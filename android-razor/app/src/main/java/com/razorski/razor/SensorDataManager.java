package com.razorski.razor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Maintains a set of unrendered SensorData protos received from the hardware.
 */

public class SensorDataManager implements Runnable {
    private final String TAG = SensorDataManager.class.getName();
    private Handler parentHandler;
    private Handler myHandler;

    public SensorDataManager(Handler parentHandler_) {
        parentHandler = parentHandler_;
    }

    /**
     * Adds the sensor data to be processed. This function will be called in clients threads and
     * NOT in the manager thread.
     */
    public void addData(SensorData data) {
        Log.d(TAG, "DM addData is called from " + android.os.Process.myTid());
        myHandler.obtainMessage(MainActivity.RECEIVED_DATA, data).sendToTarget();
    }

    @Override
    public void run() {
        Log.d(TAG, "DM thread starting at " + android.os.Process.myTid());
        Looper.prepare();
        // Handler needs to be initialized in the thread this is running.
        myHandler = new Handler() {
            public void handleMessage(android.os.Message message) {
                Log.d(TAG, "handle message called in DM thread");
                switch (message.what) {
                    case MainActivity.RECEIVED_DATA:
                        Log.d(TAG, "Received data in DM thread");
                        SensorData data = (SensorData) message.obj;

                        // Things we want to do with the message:
                        // Right now, we just pass it along to the main UI but later we'll do more.
                        parentHandler.obtainMessage(MainActivity.RECEIVED_DATA, data)
                                .sendToTarget();

                        Log.d(TAG, "Processed data in DM thread");
                }
                super.handleMessage(message);
            }
        };

        Looper.loop();

    }
}
