package com.razorski.razor;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.razorski.razor.data.SensorDataUtils;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    // TODO: These are hardcoded, need to change.
    public static final String BT_ADDRESS = "20:15:12:08:71:82";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Message IDs that we handle here.
    public static final int RECEIVED_DATA = 1;
    public static final int HW_CONNECTED = 2;
    public static final int HW_CONNECTING = 3;
    public static final int HW_DISCONNECTED = 4;

    // Handler of messages.
    private Handler dataHandler;

    // Pointer to objects that get and process data in other threads.
    SensorDataManager dataManager;
    Thread dataManagerThread;
    BTCommunicator btCommunicator;
    Thread btThread;
    SensorDataStreamParser streamParser;

    // Pointer to my UI elements.
    TextView sensorValueTextView;
    ProgressBar progressBar;
    CheckBox connectionCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataHandler = new Handler() {
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case RECEIVED_DATA:
                        SensorData data = (SensorData) message.obj;
//                        Log.d(TAG, "Received data in UI thread:" + SensorDataUtils.toString(data));
                        sensorValueTextView.setText(SensorDataUtils.toString(data));
                        break;
                    case HW_CONNECTED:
                        if (!connectionCheckBox.getText().equals("Connected")) {
                            progressBar.setVisibility(View.GONE);
                            connectionCheckBox.setText("Connected");
                            connectionCheckBox.setChecked(true);
                        }
                        break;
                    case HW_CONNECTING:
                        if (!connectionCheckBox.getText().equals("Connecting...")) {
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.animate();
                            connectionCheckBox.setText("Connecting...");
                            connectionCheckBox.setChecked(false);
                        }
                        break;
                    case HW_DISCONNECTED:
                        if (!connectionCheckBox.getText().equals("Disconnected")) {
                            progressBar.setVisibility(View.GONE);
                            connectionCheckBox.setText("Disconnected");
                            connectionCheckBox.setChecked(false);
                        }
                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };
        setContentView(R.layout.activity_main);


        dataManager = new SensorDataManager(dataHandler);
        streamParser = new SensorDataProtoParser(dataManager);

        btCommunicator = new BTCommunicator(MY_UUID, BT_ADDRESS, streamParser, dataHandler);

        dataManagerThread = new Thread(dataManager);
        btThread = new Thread(btCommunicator);
        dataManagerThread.start();
        btThread.start();

        sensorValueTextView = (TextView) findViewById(R.id.sensorValueText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connectionCheckBox = (CheckBox) findViewById(R.id.connectionStatus);

        // Clicking on the connected checkbox will toggle connect/disconnect from HW.
        connectionCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btCommunicator.setConnect(connectionCheckBox.isChecked());
            }
        });
    }
}
