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

    private UUID uuid;
    private final String btAddress;
    private SensorDataStreamParser streamParser;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDevice btDevice = null;

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    private boolean autoConnect = true;

    public BTCommunicator(UUID uuid_, String btAddress_, SensorDataStreamParser streamParser_) {
        uuid = uuid_;
        btAddress = btAddress_;
        streamParser = streamParser_;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevice = btAdapter.getRemoteDevice(btAddress);
    }

    private boolean updateBTStatus() {
        if (btSocket != null && btSocket.isConnected()) {
            Log.d(TAG, "updateBTStatus is already connected");
            return true;
        }
        Log.d(TAG, "updateBTStatus is NOT already connected");

        if (!autoConnect) {
            return false;
        }

        return tryConnect();
    }

    private boolean tryConnect() {
        try {
            btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            btSocket.connect();

            mmInStream = btSocket.getInputStream();
            mmOutStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Exception creating BT socket in BT thread: " + e.toString());
            return false;
        }

        return true;
    }

    public void forceClose() {
        try {
            btSocket.close();
        } catch (IOException e) {
            Log.d(TAG, "Exception closing BT socket in BT thread: " + e.toString());
            btSocket = null;
        }
    }

    public void forceConnect() {
        autoConnect = true;
        tryConnect();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        forceConnect();

        while(true) {
            if (updateBTStatus()) {
                try {
                    bytes = mmInStream.read(buffer);
                    streamParser.processData(buffer, bytes);
                } catch (IOException e) {
                }
            }
        }
    }
}
