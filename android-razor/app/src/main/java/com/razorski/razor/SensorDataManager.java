package com.razorski.razor;

import android.os.Looper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Maintains the set of received SensorData protos from the hardware.
 *
 * Note: currently, this class just passes the protos to the main UI without saving or performing
 *       any extra processing on them. This needs to change.
 */
public class SensorDataManager implements Runnable {
    private final String TAG = SensorDataManager.class.getName();

    private PhoneSensorCollector phoneSensorCollector;

    public SensorDataManager(PhoneSensorCollector phoneSensorCollector_) {
        phoneSensorCollector = phoneSensorCollector_;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(EventMessage message) {
        switch (message.getEventType()) {
            case RECEIVED_RAW_DATA:
                SensorData data = message.getSensorData();

                // Things we want to do with the message:
                // Right now, we just pass it along to the main UI but later we'll do more.
                data = data.toBuilder().setPhoneData(phoneSensorCollector.readData()).build();

                // Broadcast the processed data for the main UI.
                EventMessage relayMessage =
                        new EventMessage(EventMessage.EventType.DATA_READY_FOR_UI);
                relayMessage.setSensorData(data);
                EventBus.getDefault().post(relayMessage);
        }
    }

    @Override
    public void run() {
        EventBus.getDefault().register(this);
        Looper.prepare();
        Looper.loop();
    }

    public void startRecordingSession() {

    }

    public void stopRecordingSession() {

    }
}
