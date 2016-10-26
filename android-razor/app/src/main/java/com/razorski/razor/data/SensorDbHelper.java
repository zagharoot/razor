package com.razorski.razor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.razorski.razor.data.DataContract.SensorEntry;

/**
 *  Manages a local database for razor sensor data.
 */

public class SensorDbHelper extends SQLiteOpenHelper {

    // Increment this every time you change the schema.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "razor.db";

    public SensorDbHelper(Context context) {
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
                SensorEntry.COL_P_LOC_LAT + "REAL, " +
                SensorEntry.COL_P_LOC_LONG + "REAL, " +
                SensorEntry.COL_P_LOC_SPEED + "REAL, " +
                SensorEntry.COL_P_LOC_ALT + "REAL, " +
                SensorEntry.COL_P_LOC_ACCURACY + "REAL, " +
                SensorEntry.COL_TIMESTAMP_MSEC + "INTEGER UNIQUE NOT NULL );";

        sqLiteDatabase.execSQL(CREATE_SENSOR_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // On upgrades, we simply get rid of all the data.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SensorEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
