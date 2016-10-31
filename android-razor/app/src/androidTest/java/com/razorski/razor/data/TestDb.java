package com.razorski.razor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.razorski.razor.RecordSession;
import com.razorski.razor.SensorData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static com.razorski.razor.data.TestUtils.generateFakeSensorData;
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
        getContext().deleteDatabase(RazorDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @Test
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DataContract.SensorEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.RecordSessionEntry.TABLE_NAME);

        getContext().deleteDatabase(RazorDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new RazorDbHelper(getContext()).getWritableDatabase();
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

    @Test
    public void recordSessionTableOperation() {
        RazorDbHelper dbHelper = new RazorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        RecordSession originalData = RecordSession.newBuilder().setStartTimestampMsec(12345L)
                .setEndTimestampMsec(12346L).build();

        ContentValues contentValues = ProtoConverter.contentValuesFromRecordSession(originalData);

        // Insert the data into the table.
        long rowId = db.insert(DataContract.RecordSessionEntry.TABLE_NAME, null, contentValues);
        assertTrue(rowId != -1);

        // Now read data back from database and compare with the original data.
        Cursor cursor = db.query(
                DataContract.RecordSessionEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows.
        assertTrue( "Error: No Records returned from sensor query", cursor.moveToFirst());

        RecordSession readProto = ProtoConverter.recordSessionFromCursor(cursor);

        // Finally, move the cursor to demonstrate that there is only one record in the database.
        assertFalse( "Error: More than one record returned from sensor query",
                cursor.moveToNext());

        Log.d(TAG, "Original proto: " + originalData.toString());
        Assert.assertEquals(originalData, readProto);
        cursor.close();
        dbHelper.close();
    }

    @Test
    public void sensorTableOperation() {
        RazorDbHelper dbHelper = new RazorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SensorData originalData = generateFakeSensorData();

        ContentValues contentValues = ProtoConverter.contentValuesFromSensorData(originalData);

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

        SensorData readProto = ProtoConverter.sensorDataFromCursor(cursor);

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
        RazorDbHelper dbHelper = new RazorDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SensorData originalData = generateFakeSensorData();
        // Clear sensor data from left foot.
        originalData = originalData.toBuilder().clearLeft().build();

        ContentValues contentValues = ProtoConverter.contentValuesFromSensorData(originalData);

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

        SensorData readProto = ProtoConverter.sensorDataFromCursor(cursor);

        // Finally, move the cursor to demonstrate that there is only one record in the database.
        assertFalse( "Error: More than one record returned from sensor query",
                cursor.moveToNext());

        Log.d(TAG, "Original proto: " + originalData.toString());
        Assert.assertEquals(originalData, readProto);
        cursor.close();
        dbHelper.close();
    }
}
