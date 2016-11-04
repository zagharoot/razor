package com.razorski.razor.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Defines the structure of the firebase database tree.
 * This needs to be in sync with the rules we define on the server, so don't just change stuff here.
 */

public class FirebaseContract {
    public static final String USERS_PATH = "users";
    public static final String RECORD_SESSION_PATH = "record-sessions";
    public static final String RUN_SESSION_PATH = "run-sessions";
    public static final String SENSOR_PATH = "sensors";

    private static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    private static String getUsername() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getUserRef() {
        return getDatabase().getReference(USERS_PATH + "/" + getUsername());
    }

    public static DatabaseReference getRecordSessionsRef() {
        return getDatabase().getReference(USERS_PATH + "/" + getUsername() + "/" +
                RECORD_SESSION_PATH);
    }

    public static DatabaseReference getRunSessionsRef() {
        return getDatabase().getReference(USERS_PATH + "/" + getUsername() + "/" +
                RUN_SESSION_PATH);
    }

    public static DatabaseReference getSensorsRef() {
        return getDatabase().getReference(USERS_PATH + "/" + getUsername() + "/" + SENSOR_PATH);
    }
}
