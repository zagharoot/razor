package com.razorski.razor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Manages the connection to the bluetooth hardware and send/receive raw data.
 */

public class BTCommunicator implements Runnable {
    private final String TAG = BTCommunicator.class.getName();

    // Parameters needed to create bluetooth connection to the device.
    private UUID uuid;
    private final String btAddress;

    // Objects to manage the connection to the device.
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDevice btDevice = null;

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    // Object that is capable of parsing input stream to our data.
    private SensorDataStreamParser streamParser;

    // Whether we should automatically reconnect if the connection is lost.
    private boolean autoConnect = true;

    public BTCommunicator(UUID uuid_, String btAddress_, SensorDataStreamParser streamParser_) {
        uuid = uuid_;
        btAddress = btAddress_;
        streamParser = streamParser_;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevice = btAdapter.getRemoteDevice(btAddress);
    }

    /**
     * Forcibly connects to the bluetooth, also sets the autoConnect to true.
     */
    public void forceConnect() {
        autoConnect = true;
        tryConnect();
    }

    public void forceClose() {
        autoConnect = false;
        tryClose();
    }

    /**
     * Tries to close the connection.
     */
    private void tryClose() {
        try {
            btSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "Exception closing BT socket in BT thread: " + e.toString());
        } finally {
            btSocket = null;
        }
    }

    /**
     * Makes sure the connection is still active.
     * <p>If connection is lost and auto connect is true, tries to reconnect.</p>
     * <p>Returns true if at the end we have a connection.</p>
     */
    private boolean verifyBTConnected() {
        // We're already connected.
        if (btSocket != null && btSocket.isConnected()) {
            return true;
        }

        Log.d(TAG, "updateBTStatus is NOT already connected");
        // Sorry, we're just not connected and don't want to connect either.
        if (!autoConnect) {
            return false;
        }

        boolean connected = tryConnect();
        return connected;
    }

    /**
     * Tries to establish bluetooth connection and returns true if succeeds.
     * @return
     */
    private boolean tryConnect() {
        try {
            if (btDevice == null) {
                btDevice = btAdapter.getRemoteDevice(btAddress);
            }

            btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();

            mmInStream = btSocket.getInputStream();
            mmOutStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Exception creating BT socket in BT thread: " + e.toString());
            btDevice = null;
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        forceConnect();

        while(true) {
            if (verifyBTConnected()) {
                try {
                    bytes = mmInStream.read(buffer);
                    streamParser.processData(buffer, bytes);
                } catch (IOException e) {
                    tryClose();
                }
            }
        }
    }
}
