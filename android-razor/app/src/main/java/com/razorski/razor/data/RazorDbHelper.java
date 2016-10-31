package com.razorski.razor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.razorski.razor.data.DataContract.RecordSessionEntry;
import com.razorski.razor.data.DataContract.SensorEntry;

/**
 *  Manages a local database for razor sensor data.
 */

public class RazorDbHelper extends SQLiteOpenHelper {

    // Increment this every time you change the schema.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "razor.db";

    public RazorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the sensor table.
        final String CREATE_SENSOR_TABLE_QUERY = "CREATE TABLE " + SensorEntry.TABLE_NAME + " (" +
                SensorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SensorEntry.COL_L_AX + " INTEGER, " +
                SensorEntry.COL_L_AY + " INTEGER, " +
                SensorEntry.COL_L_AZ + " INTEGER, " +
                SensorEntry.COL_L_GX + " INTEGER, " +
                SensorEntry.COL_L_GY + " INTEGER, " +
                SensorEntry.COL_L_GZ + " INTEGER, " +
                SensorEntry.COL_L_YAW + " REAL, " +
                SensorEntry.COL_L_PITCH + " REAL, " +
                SensorEntry.COL_L_ROLL + " REAL, " +
                SensorEntry.COL_L_TEMP + " INTEGER, " +
                SensorEntry.COL_L_PRESSURE + " INTEGER, " +
                SensorEntry.COL_R_AX + " INTEGER, " +
                SensorEntry.COL_R_AY + " INTEGER, " +
                SensorEntry.COL_R_AZ + " INTEGER, " +
                SensorEntry.COL_R_GX + " INTEGER, " +
                SensorEntry.COL_R_GY + " INTEGER, " +
                SensorEntry.COL_R_GZ + " INTEGER, " +
                SensorEntry.COL_R_YAW + " REAL, " +
                SensorEntry.COL_R_PITCH + " REAL, " +
                SensorEntry.COL_R_ROLL + " REAL, " +
                SensorEntry.COL_R_TEMP + " INTEGER, " +
                SensorEntry.COL_R_PRESSURE + " INTEGER, " +
                SensorEntry.COL_P_LOC_LAT + " REAL, " +
                SensorEntry.COL_P_LOC_LONG + " REAL, " +
                SensorEntry.COL_P_LOC_SPEED + " REAL, " +
                SensorEntry.COL_P_LOC_ALT + " REAL, " +
                SensorEntry.COL_P_LOC_ACCURACY + " REAL, " +
                SensorEntry.COL_TIMESTAMP_MSEC + " BIGINT UNIQUE NOT NULL );";
        sqLiteDatabase.execSQL(CREATE_SENSOR_TABLE_QUERY);

        final String INDEX_SENSOR_QUERY = "CREATE INDEX SENSOR_TIMESTAMP_IDX ON " +
                SensorEntry.TABLE_NAME + "(" + SensorEntry.COL_TIMESTAMP_MSEC + ");";
        sqLiteDatabase.execSQL(INDEX_SENSOR_QUERY);

        final String CREATE_RECORD_SESSION_TABLE_QUERY = "CREATE TABLE " +
                RecordSessionEntry.TABLE_NAME + " (" +
                RecordSessionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecordSessionEntry.COL_START_TIMESTAMP_MSEC + " BIGINT UNIQUE NOT NULL, " +
                RecordSessionEntry.COL_END_TIMESTAMP_MSEC + " BIGINT UNIQUE NOT NULL );";
        sqLiteDatabase.execSQL(CREATE_RECORD_SESSION_TABLE_QUERY);

        final String INDEX_RECORD_SESSION_QUERY = "CREATE INDEX RECORD_SESSION_TIMESTAMP_IDX ON " +
                RecordSessionEntry.TABLE_NAME + "(" + RecordSessionEntry.COL_START_TIMESTAMP_MSEC +
                ");";
        sqLiteDatabase.execSQL(INDEX_RECORD_SESSION_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // On upgrades, we simply get rid of all the data.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SensorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecordSessionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
