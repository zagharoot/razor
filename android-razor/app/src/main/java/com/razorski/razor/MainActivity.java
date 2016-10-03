package com.razorski.razor;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.razorski.razor.data.SensorDataUtils;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private Handler dataHandler;

    public static final String BT_ADDRESS = "20:15:12:08:71:82";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int RECEIVED_DATA = 1;

    SensorDataManager dataManager;
    Thread dataManagerThread;
    BTCommunicator btCommunicator;
    Thread btThread;
    SensorDataStreamParser streamParser;

    TextView sensorValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataHandler = new Handler() {
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case RECEIVED_DATA:
                        SensorData data = (SensorData) message.obj;
                        Log.d(TAG, "Received data in UI thread:" + SensorDataUtils.toString(data));
                        sensorValueTextView.setText(SensorDataUtils.toString(data));
                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };
        setContentView(R.layout.activity_main);


        dataManager = new SensorDataManager(dataHandler);
        streamParser = new SensorDataTextParser(dataManager);

        btCommunicator = new BTCommunicator(MY_UUID, BT_ADDRESS, streamParser);

        dataManagerThread = new Thread(dataManager);
        btThread = new Thread(btCommunicator);
        dataManagerThread.start();
        btThread.start();

        sensorValueTextView = (TextView) findViewById(R.id.sensorValueText);
    }
}
