package com.razorski.razor.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Specifies the contract for the content provider.
 */
public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.razorski.razor";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths:

    // Stores individual sensor readings for users.
    public static final String PATH_SENSOR = "sensor";
    // Stores sessions. Each session is a group of related sensor readings. E.g. a ski run from
    // top of the trail to the bottom.
    public static final String PATH_SESSION = "session";

    // Contract for the 'sensor' table.
    public static final class SensorEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SENSOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SENSOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SENSOR;

        // Table name.
        public static final String TABLE_NAME = "sensor";

        // Columns related to the data from left foot.
        public static final String COL_L_AX = "left_ax";
        public static final String COL_L_AY = "left_ay";
        public static final String COL_L_AZ = "left_az";
        public static final String COL_L_GX = "left_gx";
        public static final String COL_L_GY = "left_gy";
        public static final String COL_L_GZ = "left_gz";
        public static final String COL_L_YAW = "left_yaw";
        public static final String COL_L_PITCH = "left_pitch";
        public static final String COL_L_ROLL = "left_roll";
        public static final String COL_L_TEMP = "left_temp";
        public static final String COL_L_PRESSURE = "left_pressure";

        // Columns related to the data from right foot.
        public static final String COL_R_AX = "right_ax";
        public static final String COL_R_AY = "right_ay";
        public static final String COL_R_AZ = "right_az";
        public static final String COL_R_GX = "right_gx";
        public static final String COL_R_GY = "right_gy";
        public static final String COL_R_GZ = "right_gz";
        public static final String COL_R_YAW = "right_yaw";
        public static final String COL_R_PITCH = "right_pitch";
        public static final String COL_R_ROLL = "right_roll";
        public static final String COL_R_TEMP = "right_temp";
        public static final String COL_R_PRESSURE = "right_pressure";

        // Columns related to the data from the phone.
        public static final String COL_P_LOC_LAT = "phone_loc_lat";
        public static final String COL_P_LOC_LONG = "phone_loc_long";
        public static final String COL_P_LOC_SPEED = "phone_loc_speed";
        public static final String COL_P_LOC_ALT = "phone_loc_alt";
        public static final String COL_P_LOC_ACCURACY = "phone_loc_accuracy";

        // Timestamp of when the sensor was read in millis.
        public static final String COL_TIMESTAMP_MSEC = "timestamp_msec";

        public static Uri buildLocationUri(long timestamp) {
            return ContentUris.withAppendedId(CONTENT_URI, timestamp);
        }

        public static long getTimestampFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
}
