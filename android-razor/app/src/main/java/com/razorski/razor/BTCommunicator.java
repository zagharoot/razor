package com.razorski.razor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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

    // Pointer to the main UI handler, where we send connection status.
    private Handler parentHandler;

    // Objects to manage the connection to the device.
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDevice btDevice = null;

    private InputStream mmInStream = null;
    private OutputStream mmOutStream = null;

    // Object that is capable of parsing input stream to our data.
    private SensorDataStreamParser streamParser;

    // Whether we'd like to be connected to the HW.
    private boolean wantConnect = true;

    public BTCommunicator(UUID uuid_, String btAddress_, SensorDataStreamParser streamParser_, Handler parentHandler_) {
        uuid = uuid_;
        btAddress = btAddress_;
        streamParser = streamParser_;

        parentHandler = parentHandler_;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevice = btAdapter.getRemoteDevice(btAddress);
    }

    /**
     * Sets whether to connect to disconnect from hardware.
     */
    public synchronized void setConnect(boolean connect) {
        wantConnect = connect;
    }

    private void sendConnectionMessage(int message) {
        parentHandler.obtainMessage(message).sendToTarget();
    }

    /**
     * Checks the current connection status and decides whether we need to change that or not
     * based on the value of @code{wantConnect}. If the two are different, also notifies the
     * UI thread of the new status.
     * @return whether we're connected to hardware or not.
     */
    private boolean checkConnectionStatus() {
        if (wantConnect) {
            return verifyBTConnected();
        } else {
            return verifyBTClosed();
        }
    }

    /**
     * Makes sure that we're connected. If not, tries to connect.
     * @return whether we're connected or not.
     */
    private boolean verifyBTConnected() {
        if (btSocket != null && btSocket.isConnected()) {
            return true;
        }

        sendConnectionMessage(MainActivity.HW_CONNECTING);
        try {
            if (btDevice == null) {
                btDevice = btAdapter.getRemoteDevice(btAddress);
            }

            btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();

            mmInStream = btSocket.getInputStream();
            mmOutStream = btSocket.getOutputStream();
            sendConnectionMessage(MainActivity.HW_CONNECTED);
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Exception creating BT socket in BT thread: " + e.toString());
            btDevice = null;
            return false;
        }
    }

    /**
     * Verifies that we're not connected. If we are, then disconnects.
     * @return whether we're connected or not.
     */
    private boolean verifyBTClosed() {
        try {
            if (btSocket != null) {
                btSocket.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "Exception closing BT socket in BT thread: " + e.toString());
        } finally {
            sendConnectionMessage(MainActivity.HW_DISCONNECTED);
            btSocket = null;
        }
        return false;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        while(true) {
            if (checkConnectionStatus()) {
                try {
                    bytes = mmInStream.read(buffer);
                    streamParser.processData(buffer, bytes);
                } catch (IOException e) {
                    verifyBTClosed();
                }
            }
        }
    }
}
