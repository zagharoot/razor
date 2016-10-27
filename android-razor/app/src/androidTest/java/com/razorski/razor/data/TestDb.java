package com.razorski.razor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.razorski.razor.FootSensorData;
import com.razorski.razor.IMUData;
import com.razorski.razor.LocationData;
import com.razorski.razor.PhoneData;
import com.razorski.razor.SensorData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class TestDb {
    public static final String TAG = TestDb.class.getSimpleName();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.razorski.razor", appContext.getPackageName());
    }

    Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        getContext().deleteDatabase(SensorDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @Test
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DataContract.SensorEntry.TABLE_NAME);

        getContext().deleteDatabase(SensorDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new SensorDbHelper(getContext()).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // Verify that the tables have been created.
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // If this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables.
        assertTrue("Error: Your database was created without both the location entry and " +
                "weather entry tables", tableNameHashSet.isEmpty());

        db.close();
    }

    private static SensorData generateFakeSensorData() {
        SensorData.Builder builder = SensorData.newBuilder();
        FootSensorData.Builder left = FootSensorData.newBuilder();
        FootSensorData.Builder right = FootSensorData.newBuilder();

        int val = 1;
        // Left foot.
        {
            IMUData.Builder imu = IMUData.newBuilder();
            imu.setAx(++val);
            imu.setAy(++val);
            imu.setAz(++val);
            imu.setGx(++val);
            imu.setGy(++val);
            imu.setGz(++val);
            imu.setYaw(++val + 0.1f);
            imu.setPitch(++val + 0.2f);
            imu.setRoll(++val + 0.3f);
            imu.setTemperature(++val);

            left.setPressureFront(++val);
            left.setImuData(imu);
            builder.setLeft(left);
        }

        // Right foot.
        {
            IMUData.Builder imu = IMUData.newBuilder();
            imu.setAx(++val);
            imu.setAy(++val);
            imu.setAz(++val);
            imu.setGx(++val);
            imu.setGy(++val);
            imu.setGz(++val);
            imu.setYaw(++val + 0.4f);
            imu.setPitch(++val + 0.5f);
            imu.setRoll(++val + 0.6f);
            imu.setTemperature(++val);

            right.setPressureFront(++val);
            right.setImuData(imu);
            builder.setRight(right);
        }

        // Phone data
        {
            PhoneData.Builder phone = PhoneData.newBuilder();
            LocationData.Builder location = LocationData.newBuilder();
            location.setLatitude(++val + 0.1);
            location.setLongitude(++val + 0.2);
            location.setAltitude(++val + 0.3);
            location.setSpeed(++val + 0.4f);
            location.setAccuracy(++val + 0.5f);

            phone.setLocationData(location);
            builder.setPhoneData(phone);
        }

        builder.setTimestampMsec(9999999L);

        return builder.build();
    }

    @Test
    public void sensorTableOperation() {
        SensorDbHelper dbHelper = new SensorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SensorData originalData = generateFakeSensorData();

        ContentValues contentValues = ProtoConverter.fromProto(originalData);

        // Insert the data into the table.
        long rowId = db.insert(DataContract.SensorEntry.TABLE_NAME, null, contentValues);
        assertTrue(rowId != -1);

        // Now read data back from database and compare with the original data.
        Cursor cursor = db.query(
                DataContract.SensorEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows.
        assertTrue( "Error: No Records returned from sensor query", cursor.moveToFirst());

        SensorData readProto = ProtoConverter.fromCursor(cursor);

        // Finally, move the cursor to demonstrate that there is only one record in the database.
        assertFalse( "Error: More than one record returned from sensor query",
                cursor.moveToNext());

        Log.d(TAG, "Original proto: " + originalData.toString());
        Assert.assertEquals(originalData, readProto);
        cursor.close();
        dbHelper.close();
    }

    @Test
    public void sensorTableNullValues() {
        SensorDbHelper dbHelper = new SensorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SensorData originalData = generateFakeSensorData();
        // Clear sensor data from left foot.
        originalData = originalData.toBuilder().clearLeft().build();

        ContentValues contentValues = ProtoConverter.fromProto(originalData);

        // Insert the data into the table.
        long rowId = db.insert(DataContract.SensorEntry.TABLE_NAME, null, contentValues);
        assertTrue(rowId != -1);

        // Now read data back from database and compare with the original data.
        Cursor cursor = db.query(
                DataContract.SensorEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows.
        assertTrue( "Error: No Records returned from sensor query", cursor.moveToFirst());

        SensorData readProto = ProtoConverter.fromCursor(cursor);

        // Finally, move the cursor to demonstrate that there is only one record in the database.
        assertFalse( "Error: More than one record returned from sensor query",
                cursor.moveToNext());

        Log.d(TAG, "Original proto: " + originalData.toString());
        Assert.assertEquals(originalData, readProto);
        cursor.close();
        dbHelper.close();
    }
}
