package com.razorski.razor.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            // Direct access to sensor table will return multiple items.
            case SENSOR:
                return DataContract.SensorEntry.CONTENT_TYPE;
            case RECORD_SESSION:
                return DataContract.RecordSessionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        Uri result;

        DatabaseReference reference = getReference(match);

        switch (match) {
            case RECORD_SESSION:
                CloudProtoHelper.RecordSessionFB recordSessionFB =
                        new CloudProtoHelper.RecordSessionFB(values);
                DatabaseReference newRecord = reference.push();
                newRecord.setValue(recordSessionFB);
                result = DataContract.RecordSessionEntry.uriForId(newRecord.getKey());
                break;
            case SENSOR:
                CloudProtoHelper.SensorDataFB sensorDataFB =
                        new CloudProtoHelper.SensorDataFB(values);
                DatabaseReference newSensorRecord = reference.push();
                newSensorRecord.setValue(sensorDataFB);
                result = DataContract.RecordSessionEntry.uriForId(newSensorRecord.getKey());
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (selection != null && !selection.isEmpty()) {
            throw new UnsupportedOperationException("Can't delete with selection: " + selection);
        }

        DatabaseReference reference = getReference(uriMatcher.match(uri));
        if (reference != null) {
            reference.removeValue();
        }
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

    DatabaseReference getReference(int target) {
        switch (target) {
            case RECORD_SESSION:
                return database.getReference(DataContract.RecordSessionEntry.TABLE_NAME);
            case SENSOR:
                return database.getReference(DataContract.SensorEntry.TABLE_NAME);
            default:
                return null;
        }
    }
}
