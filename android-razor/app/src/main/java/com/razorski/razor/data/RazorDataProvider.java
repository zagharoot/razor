package com.razorski.razor.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Content provider for data in Razor.
 */

public class RazorDataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private SensorDbHelper dbHelper;

    // Different type of URIs we support:

    // Direct simple access to sensor table.
    static final int SENSOR = 100;

    private static final SQLiteQueryBuilder sensorQueryBuilder;

    static{
        sensorQueryBuilder = new SQLiteQueryBuilder();
        sensorQueryBuilder.setTables(DataContract.SensorEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_SENSOR, SENSOR);
        return matcher;
    }

    Cursor getSensorData(Uri uri, String[] projection, String sortOrder) {
        return sensorQueryBuilder.query(dbHelper.getReadableDatabase(), projection, "", null, "",
                "", sortOrder);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SensorDbHelper(getContext());
        return true;
    }

    // Given a URI returns what type of cursor it'll be bound to (item or list of items).
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            // Direct access to sensor table will return multiple items.
            case SENSOR:
                return DataContract.SensorEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor result;
        switch (uriMatcher.match(uri)) {
            case SENSOR:
            {
                result = getSensorData(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri result;

        switch (match) {
            case SENSOR:
                long _id = db.insert(DataContract.SensorEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    result = DataContract.SensorEntry.uriForId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement.
        return 0;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Implement.
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case SENSOR:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DataContract.SensorEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
