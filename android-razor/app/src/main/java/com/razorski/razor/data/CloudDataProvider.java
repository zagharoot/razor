package com.razorski.razor.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorski.razor.RecordSession;
import com.razorski.razor.SensorData;

import static com.razorski.razor.data.RazorDataProvider.RECORD_SESSION;
import static com.razorski.razor.data.RazorDataProvider.SENSOR;

/**
 * A content provider that uses google cloud to serve data.
 */

public class CloudDataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private FirebaseDatabase database = null;

    // Direct simple access to sensor table.
    private static final int SENSOR = 100;
    // Direct simple access to record-session table.
    private static final int RECORD_SESSION = 200;

    @Override
    public boolean onCreate() {
        database = FirebaseDatabase.getInstance();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_SENSOR, SENSOR);
        matcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_RECORD_SESSION,
                RECORD_SESSION);

        return matcher;
    }

    DatabaseReference getReference(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case RECORD_SESSION:
                return database.getReference(DataContract.RecordSessionEntry.TABLE_NAME);
            case SENSOR:
                return database.getReference(DataContract.SensorEntry.TABLE_NAME);
            default:
                return null;
        }
    }
}
