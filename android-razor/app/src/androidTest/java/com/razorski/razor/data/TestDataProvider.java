package com.razorski.razor.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.ImmutableList;
import android.support.test.runner.AndroidJUnit4;

import com.razorski.razor.RecordSession;
import com.razorski.razor.SensorData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for the content provider.
 */

@RunWith(AndroidJUnit4.class)
public class TestDataProvider {
    public static final String TAG = TestDataProvider.class.getSimpleName();

    Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        getContext().deleteDatabase(RazorDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @Test
    public void testProviderRegistry() {
        PackageManager pm = getContext().getPackageManager();

        // We define the component name based on the package name from the context and the
        // RazorDataProvider class.
        ComponentName componentName = new ComponentName(getContext().getPackageName(),
                RazorDataProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: RazorDataProvider registered with authority: " +
                    providerInfo.authority +
                    " instead of authority: " + DataContract.CONTENT_AUTHORITY,
                    providerInfo.authority, DataContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: RazorDataProvider not registered at " +
                    getContext().getPackageName(), false);
        }
    }

    @Test
    public void testGetType() {
        String type = getContext().getContentResolver().getType(
                DataContract.SensorEntry.CONTENT_URI);

        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
                DataContract.SensorEntry.CONTENT_TYPE, type);
    }

    @Test
    public void testBasicRecordSessionQuery() {
        // Insert our test records into the database.
        RazorDbHelper dbHelper = new RazorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ImmutableList<RecordSession> records = ImmutableList.of(
            TestUtils.createRecordSession(1, 3),
            TestUtils.createRecordSession(5, 6),
            TestUtils.createRecordSession(7, 9));

        for (RecordSession session : records) {
            ContentValues testValues = ProtoConverter.contentValuesFromRecordSession(session);
            // Insert the data into the table.
            long rowId = db.insert(DataContract.RecordSessionEntry.TABLE_NAME, null, testValues);
            assertTrue(rowId != -1);
        }
        db.close();

        // Test the basic content provider query.
        Cursor cursor = getContext().getContentResolver().query(
                DataContract.RecordSessionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        assertEquals(records.size(), cursor.getCount());
        for (RecordSession session : records) {
            RecordSession readData = ProtoConverter.recordSessionFromCursor(cursor);
            assertEquals(session, readData);
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Test
    public void testBasicWeatherQuery() {
        // Insert our test records into the database.
        RazorDbHelper dbHelper = new RazorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SensorData originalData = TestUtils.generateFakeSensorData();
        ContentValues testValues = ProtoConverter.contentValuesFromSensorData(originalData);
        // Insert the data into the table.
        long rowId = db.insert(DataContract.SensorEntry.TABLE_NAME, null, testValues);
        assertTrue(rowId != -1);
        db.close();

        // Test the basic content provider query.
        Cursor cursor = getContext().getContentResolver().query(
                DataContract.SensorEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        SensorData readData = ProtoConverter.sensorDataFromCursor(cursor);
        cursor.close();
        assertEquals(originalData, readData);
    }

}
