package com.razorski.razor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.razorski.razor.EventMessage;
import com.razorski.razor.PhoneSensorCollector;
import com.razorski.razor.SensorData;
import com.razorski.razor.data.SensorDataProtoParser;
import com.razorski.razor.data.SensorDataStreamParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

/**
 * The service that does most of the work for razor. I.e. record data received from bluetooth
 * and pass it along to main UI.
 */

public class DataService extends Service {

    // TODO: These are hardcoded, need to change.
    public static final String BT_ADDRESS = "20:15:12:08:71:82";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Pointer to objects that get and process data in other threads.
    private BTCommunicator btCommunicator;
    private Thread btThread;
    private SensorDataStreamParser streamParser;
    private PhoneSensorCollector phoneSensorCollector;

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        // Set up the class for collecting data off of the phone.
        phoneSensorCollector = new PhoneSensorCollector(getBaseContext());
        phoneSensorCollector.init();

        streamParser = new SensorDataProtoParser();

        btCommunicator = new BTCommunicator(MY_UUID, BT_ADDRESS, streamParser);
        btThread = new Thread(btCommunicator);
        btThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final EventMessage.EventType command = EventMessage.EventType.valueOf(
                intent.getStringExtra(EventMessage.EVENT_TYPE_KEY));

        switch (command) {
            case CONNECT_HW:
                btCommunicator.setConnect(true);
                break;
            case DISCONNECT_HW:
                btCommunicator.setConnect(false);
                stopSelf();
                break;
            case START_RECORDING:
                startRecording();
                break;
            case STOP_RECORDING:
                stopRecording();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // This handles processing of received sensor data in another working thread.
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

    // Starts a recording session.
    private void startRecording() {
        // TODO: implement.
    }

    // Stops a recording session.
    private void stopRecording() {
        // TODO: implement.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
