package com.razorski.razor.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.razorski.razor.EventMessage;
import com.razorski.razor.SensorData;
import com.razorski.razor.data.SensorDataStreamParser;

import org.greenrobot.eventbus.EventBus;

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

    // Whether we'd like to be connected to the HW.
    private boolean wantConnect = true;

    public BTCommunicator(UUID uuid_, String btAddress_, SensorDataStreamParser streamParser_) {
        uuid = uuid_;
        btAddress = btAddress_;
        streamParser = streamParser_;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevice = btAdapter.getRemoteDevice(btAddress);
    }

    /**
     * Sets whether to connect to disconnect from hardware.
     */
    public synchronized void setConnect(boolean connect) {
        wantConnect = connect;
    }

    // Broadcasts connection status change on the event bus.
    private void sendConnectionMessage(EventMessage.EventType eventType) {
        EventMessage message = new EventMessage(eventType);
        EventBus.getDefault().post(message);
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

        sendConnectionMessage(EventMessage.EventType.HW_CONNECTING);
        try {
            if (btDevice == null) {
                btDevice = btAdapter.getRemoteDevice(btAddress);
            }

            btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            btSocket.connect();

            mmInStream = btSocket.getInputStream();
            mmOutStream = btSocket.getOutputStream();
            sendConnectionMessage(EventMessage.EventType.HW_CONNECTED);
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
        if (btSocket != null) {
            try {
                btSocket.close();
                btSocket = null;
            } catch (IOException e) {
                Log.d(TAG, "Exception closing BT socket in BT thread: " + e.toString());
            } finally {
                sendConnectionMessage(EventMessage.EventType.HW_DISCONNECTED);
                btSocket = null;
            }
        }
        return false;
    }

    private void sendData(SensorData sensorData) {
        // Broadcast the received data on the event bus.
        EventMessage message = new EventMessage(EventMessage.EventType.RECEIVED_RAW_DATA);
        message.setSensorData(sensorData);
        EventBus.getDefault().post(message);
    }

    @Override
    public void run() {
        while(true) {
            if (checkConnectionStatus()) {
                try {
                    SensorData sensorData = streamParser.readNext(mmInStream);
                    if (sensorData != null) {
                        sendData(sensorData);
                    }
                } catch (IOException e) {
                    verifyBTClosed();
                }
            }
        }
    }
}
