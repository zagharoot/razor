package com.razorski.razor;

import android.os.Handler;
import android.os.Looper;

/**
 * Maintains the set of received SensorData protos from the hardware.
 *
 * Note: currently, this class just passes the protos to the main UI without saving or performing
 *       any extra processing on them. This needs to change.
 */
public class SensorDataManager implements Runnable {
    private final String TAG = SensorDataManager.class.getName();

    // This is the handler from the main UI. We send our messages to it to be shown in UI.
    private Handler parentHandler;
    // Handler that runs in this thread and receives proto data from the communication thread.
    private Handler myHandler;

    public SensorDataManager(Handler parentHandler_) {
        parentHandler = parentHandler_;
    }

    /**
     * Adds the sensor data to be processed. This function will be called in communication
     * thread and NOT in our own thread.
     */
    public void addData(SensorData data) {
        myHandler.obtainMessage(MainActivity.RECEIVED_DATA, data).sendToTarget();
    }

    @Override
    public void run() {
        Looper.prepare();
        // Handler needs to be initialized in the thread this is running.
        myHandler = new Handler() {
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case MainActivity.RECEIVED_DATA:
                        SensorData data = (SensorData) message.obj;

                        // Things we want to do with the message:
                        // Right now, we just pass it along to the main UI but later we'll do more.
                        parentHandler.obtainMessage(MainActivity.RECEIVED_DATA, data)
                                .sendToTarget();
                }
                super.handleMessage(message);
            }
        };

        Looper.loop();
    }
}
