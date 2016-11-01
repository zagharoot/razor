package com.razorski.razor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.razorski.razor.data.SensorDataUtils;
import com.razorski.razor.service.DataService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    // Pointer to my UI elements.
    private TextView sensorValueTextView;
    private ProgressBar progressBar;
    private CheckBox connectionCheckBox;
    private Switch recordSwitch;
    private NavigationView navigationView;

    // Authentication stuff.
    private FirebaseAuth firebaseAuth;
    private ProfileTracker profileTracker;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage message) {
        switch (message.getEventType()) {
            case DATA_READY_FOR_UI:
                SensorData data = message.getSensorData();
                sensorValueTextView.setText(SensorDataUtils.toString(data));
                break;
            case HW_CONNECTED:
                if (!connectionCheckBox.getText().equals("Connected")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    connectionCheckBox.setText("Connected");
                    connectionCheckBox.setChecked(true);
                    recordSwitch.setVisibility(View.VISIBLE);
                }
                break;
            case HW_CONNECTING:
                if (!connectionCheckBox.getText().equals("Connecting...")) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.animate();
                    connectionCheckBox.setText("Connecting...");
                    connectionCheckBox.setChecked(false);
                    recordSwitch.setChecked(false);
                    recordSwitch.setVisibility(View.INVISIBLE);
                }
                break;
            case HW_DISCONNECTED:
                if (!connectionCheckBox.getText().equals("Disconnected")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    connectionCheckBox.setText("Disconnected");
                    connectionCheckBox.setChecked(false);
                    recordSwitch.setChecked(false);
                    recordSwitch.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        setupAuthenticationProcess();

        // Assign views to variables.
        createViewVariables();

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Make sure we have permission, and once we're clear, call checkPermissionAndRun().
        MainActivityPermissionsDispatcher.checkPermissionAndRunWithCheck(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    void startRecording() {
        Toast.makeText(getBaseContext(), "Starting to record", Toast.LENGTH_SHORT).show();
        sendCommandToService(EventMessage.EventType.START_RECORDING);
    }

    void stopRecording() {
        Toast.makeText(getBaseContext(), "Stopped recording", Toast.LENGTH_SHORT).show();
        sendCommandToService(EventMessage.EventType.STOP_RECORDING);
    }

    /**
     * initializes the view variables in this activity.
     */
    private void createViewVariables() {
        sensorValueTextView = (TextView) findViewById(R.id.sensorValueText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recordSwitch = (Switch) findViewById(R.id.record_switch);
        recordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Click on the login button in the navigation view will take us to the login page.
        Button loginButton = (Button) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_bar_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        connectionCheckBox = (CheckBox) findViewById(R.id.connectionStatus);
        // Clicking on the connected checkbox will toggle connect/disconnect from HW.
        connectionCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (connectionCheckBox.isChecked()) {
                    sendCommandToService(EventMessage.EventType.CONNECT_HW);
                } else {
                    sendCommandToService(EventMessage.EventType.DISCONNECT_HW);
                }
            }
        });
    }

    // Delegates the permission handling to generated method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    private void sendCommandToService(EventMessage.EventType command) {
        Intent intent = new Intent(getApplicationContext(), DataService.class);
        intent.putExtra(EventMessage.EVENT_TYPE_KEY, command.toString());
        startService(intent);
    }

    /**
     * This code runs once it is verified that the program has permission to all its resources.
     * The annotation makes sure the permissions are requested if not already.
     * This code will not run if the user denies permission.
     */
    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_ADMIN})
    protected void checkPermissionAndRun() {
        sendCommandToService(EventMessage.EventType.CONNECT_HW);
    }

    @OnShowRationale({android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_ADMIN})
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // TODO: Take care of business here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getOrder()) {
            case 0:
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), RecordSessionsActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    private void setupAuthenticationProcess() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                // We have a profile.
                if (newProfile != null) {
                    // We also have token.
                    if (AccessToken.getCurrentAccessToken() != null) {
                        performLogin();
                    }
                } else {
                    performLogout();
                }
            }
        };
        profileTracker.startTracking();
    }

    /**
     * Logs in the user. Assumes that AccessToken and Profile data are available from facebook.
     */
    private void performLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        if (token == null || profile == null) {
            return;
        }

        // Pass facebook token to firebase for actual signing in.
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Also update the profile pic and name of the user in the navigation drawer.
        View header = navigationView.getHeaderView(0);
        Button login = (Button) header.findViewById(R.id.nav_bar_login_button);
        NetworkImageView profileImage = (NetworkImageView) header.findViewById(R.id.nav_bar_profile_pic);
        TextView userName = (TextView) header.findViewById(R.id.nav_bar_user_name);

        login.setText("LOGOUT");
        userName.setText(profile.getName());
        profileImage.setImageUrl(profile.getProfilePictureUri(1000, 1000).toString(),
                VolleySingleton.getInstance().getImageLoader());
    }

    /**
     * Signs user out.
     */
    private void performLogout() {
        Toast.makeText(getBaseContext(), "You're now logged out", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();

        View header = navigationView.getHeaderView(0);
        Button login = (Button) header.findViewById(R.id.nav_bar_login_button);
        ImageView profileImage = (ImageView) header.findViewById(R.id.nav_bar_profile_pic);
        TextView userName = (TextView) header.findViewById(R.id.nav_bar_user_name);

        login.setText("LOGIN");
        userName.setText("");
    }
}
