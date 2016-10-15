package com.razorski.razor;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.razorski.razor.data.SensorDataUtils;

import java.util.UUID;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
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
    private SensorDataManager dataManager;
    private Thread dataManagerThread;
    private BTCommunicator btCommunicator;
    private Thread btThread;
    private SensorDataStreamParser streamParser;
    private PhoneSensorCollector phoneSensorCollector;

    // Pointer to my UI elements.
    private TextView sensorValueTextView;
    private ProgressBar progressBar;
    private CheckBox connectionCheckBox;

    /**
     * This code runs once it is verified that the program has permission to all its resources.
     * The annotation makes sure the permissions are requested if not already.
     * This code will not run if the user denies permission.
     */
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION,
                      Manifest.permission.BLUETOOTH_ADMIN})
    protected void checkPermissionAndRun() {
        // Set up the class for collecting data off of the phone.
        phoneSensorCollector = new PhoneSensorCollector(getBaseContext());

        dataManager = new SensorDataManager(dataHandler, phoneSensorCollector);
        streamParser = new SensorDataProtoParser(dataManager);

        // Start the data manager thread. This doesn't do much on its own until the bluetooth thread
        // is also running.
        dataManagerThread = new Thread(dataManager);
        dataManagerThread.start();

        phoneSensorCollector.init();
        btCommunicator = new BTCommunicator(MY_UUID, BT_ADDRESS, streamParser, dataHandler);
        // Clicking on the connected checkbox will toggle connect/disconnect from HW.
        connectionCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btCommunicator.setConnect(connectionCheckBox.isChecked());
            }
        });

        btThread = new Thread(btCommunicator);
        btThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataHandler = new Handler() {
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case RECEIVED_DATA:
                        SensorData data = (SensorData) message.obj;
                        sensorValueTextView.setText(SensorDataUtils.toString(data));
                        break;
                    case HW_CONNECTED:
                        if (!connectionCheckBox.getText().equals("Connected")) {
                            progressBar.setVisibility(View.INVISIBLE);
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
                            progressBar.setVisibility(View.INVISIBLE);
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

        // Assign views to variables.
        sensorValueTextView = (TextView) findViewById(R.id.sensorValueText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connectionCheckBox = (CheckBox) findViewById(R.id.connectionStatus);

        // Make sure we have permission, and once we're clear, call checkPermissionAndRun().
        MainActivityPermissionsDispatcher.checkPermissionAndRunWithCheck(this);
    }

    // Delegates the permission handling to generated method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN})
    void showRationaleForPermissions(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("Razor cannot work without having access to location and bluetooth.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();
            }
        }).show();
    }
}
