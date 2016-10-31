package com.razorski.razor.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.razorski.razor.FootSensorData;
import com.razorski.razor.IMUData;
import com.razorski.razor.LocationData;
import com.razorski.razor.PhoneData;
import com.razorski.razor.RecordSession;
import com.razorski.razor.SensorData;
import com.razorski.razor.data.DataContract.SensorEntry;

/**
 * Converts data from proto to database format and vice versa.
 */

public class ProtoConverter {

    // Returns the column index given the column name and makes sure the value is not null.
    // Otherwise, returns -1.
    private static int indexAndValid(Cursor cursor, String columnName) {
        int result = cursor.getColumnIndex(columnName);
        return cursor.isNull(result) ? -1 : result;
    }

    public static RecordSession recordSessionFromCursor(Cursor cursor) {
        RecordSession.Builder builder = RecordSession.newBuilder();

        int index = indexAndValid(cursor, DataContract.RecordSessionEntry.COL_START_TIMESTAMP_MSEC);
        if (index != -1) {
            builder.setStartTimestampMsec(cursor.getLong(index));
        }

        index = indexAndValid(cursor, DataContract.RecordSessionEntry.COL_END_TIMESTAMP_MSEC);
        if (index != -1) {
            builder.setEndTimestampMsec(cursor.getLong(index));
        }
        return builder.build();
    }

    public static SensorData sensorDataFromCursor(Cursor cursor) {
        SensorData.Builder builder = SensorData.newBuilder();

        int index = indexAndValid(cursor, SensorEntry.COL_TIMESTAMP_MSEC);
        if (index != -1) {
            builder.setTimestampMsec(cursor.getLong(index));
        }

        // Set all the data related to left foot.
        {
            FootSensorData.Builder left = FootSensorData.newBuilder();
            IMUData.Builder imu = IMUData.newBuilder();

            index = indexAndValid(cursor, SensorEntry.COL_L_AX);
            if (index != -1) {
                imu.setAx(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_AY);
            if (index != -1) {
                imu.setAy(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_AZ);
            if (index != -1) {
                imu.setAz(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_GX);
            if (index != -1) {
                imu.setGx(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_GY);
            if (index != -1) {
                imu.setGy(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_GZ);
            if (index != -1) {
                imu.setGz(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_YAW);
            if (index != -1) {
                imu.setYaw(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_PITCH);
            if (index != -1) {
                imu.setPitch(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_ROLL);
            if (index != -1) {
                imu.setRoll(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_TEMP);
            if (index != -1) {
                imu.setTemperature(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_L_PRESSURE);
            if (index != -1) {
                left.setPressureFront(cursor.getInt(index));
            }

            // Set data.
            if (!imu.build().equals(IMUData.getDefaultInstance())) {
                left.setImuData(imu);
            }

            if (!left.build().equals(FootSensorData.getDefaultInstance())) {
                builder.setLeft(left);
            }
        }

        // Set all the data related to right foot.
        {
            FootSensorData.Builder right = FootSensorData.newBuilder();
            IMUData.Builder imu = IMUData.newBuilder();

            index = indexAndValid(cursor, SensorEntry.COL_R_AX);
            if (index != -1) {
                imu.setAx(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_AY);
            if (index != -1) {
                imu.setAy(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_AZ);
            if (index != -1) {
                imu.setAz(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_GX);
            if (index != -1) {
                imu.setGx(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_GY);
            if (index != -1) {
                imu.setGy(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_GZ);
            if (index != -1) {
                imu.setGz(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_YAW);
            if (index != -1) {
                imu.setYaw(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_PITCH);
            if (index != -1) {
                imu.setPitch(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_ROLL);
            if (index != -1) {
                imu.setRoll(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_TEMP);
            if (index != -1) {
                imu.setTemperature(cursor.getInt(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_R_PRESSURE);
            if (index != -1) {
                right.setPressureFront(cursor.getInt(index));
            }

            // Set data.
            if (!imu.build().equals(IMUData.getDefaultInstance())) {
                right.setImuData(imu);
            }

            if (!right.build().equals(FootSensorData.getDefaultInstance())) {
                builder.setRight(right);
            }
        }

        // Set all the data related to phone sensors.
        {
            PhoneData.Builder phone = PhoneData.newBuilder();
            LocationData.Builder location = LocationData.newBuilder();

            index = indexAndValid(cursor, SensorEntry.COL_P_LOC_LAT);
            if (index != -1) {
                location.setLatitude(cursor.getDouble(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_P_LOC_LONG);
            if (index != -1) {
                location.setLongitude(cursor.getDouble(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_P_LOC_ALT);
            if (index != -1) {
                location.setAltitude(cursor.getDouble(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_P_LOC_SPEED);
            if (index != -1) {
                location.setSpeed(cursor.getFloat(index));
            }

            index = indexAndValid(cursor, SensorEntry.COL_P_LOC_ACCURACY);
            if (index != -1) {
                location.setAccuracy(cursor.getFloat(index));
            }

            if (!location.build().equals(LocationData.getDefaultInstance())) {
                phone.setLocationData(location);
            }
            if (!phone.build().equals(PhoneData.getDefaultInstance())) {
                builder.setPhoneData(phone);
            }
        }

        return builder.build();
    }

    public static ContentValues contentValuesFromRecordSession(RecordSession protoData) {
        ContentValues result = new ContentValues();

        if (protoData.hasStartTimestampMsec()) {
            result.put(DataContract.RecordSessionEntry.COL_START_TIMESTAMP_MSEC,
                    protoData.getStartTimestampMsec());
        }

        if (protoData.hasEndTimestampMsec()) {
            result.put(DataContract.RecordSessionEntry.COL_END_TIMESTAMP_MSEC,
                    protoData.getEndTimestampMsec());
        }

        return result;
    }

    public static ContentValues contentValuesFromSensorData(SensorData protoData) {
        ContentValues result = new ContentValues();

        // Data at the SensorData level.
        if (protoData.hasTimestampMsec()) {
            result.put(SensorEntry.COL_TIMESTAMP_MSEC, protoData.getTimestampMsec());
        }

        // All the data coming from the left foot.
        {
            FootSensorData left = protoData.getLeft();
            IMUData imu = left.getImuData();
            if (imu.hasAx()) {
                result.put(SensorEntry.COL_L_AX, imu.getAx());
            }
            if (imu.hasAy()) {
                result.put(SensorEntry.COL_L_AY, imu.getAy());
            }
            if (imu.hasAz()) {
                result.put(SensorEntry.COL_L_AZ, imu.getAz());
            }
            if (imu.hasGx()) {
                result.put(SensorEntry.COL_L_GX, imu.getGx());
            }
            if (imu.hasGy()) {
                result.put(SensorEntry.COL_L_GY, imu.getGy());
            }
            if (imu.hasGz()) {
                result.put(SensorEntry.COL_L_GZ, imu.getGz());
            }
            if (imu.hasYaw()) {
                result.put(SensorEntry.COL_L_YAW, imu.getYaw());
            }
            if (imu.hasPitch()) {
                result.put(SensorEntry.COL_L_PITCH, imu.getPitch());
            }
            if (imu.hasRoll()) {
                result.put(SensorEntry.COL_L_ROLL, imu.getRoll());
            }
            if (imu.hasTemperature()) {
                result.put(SensorEntry.COL_L_TEMP, imu.getTemperature());
            }

            if (left.hasPressureFront()) {
                result.put(SensorEntry.COL_L_PRESSURE, left.getPressureFront());
            }
        }

        // All the data coming from the right foot.
        {
            FootSensorData right = protoData.getRight();
            IMUData imu = right.getImuData();
            if (imu.hasAx()) {
                result.put(SensorEntry.COL_R_AX, imu.getAx());
            }
            if (imu.hasAy()) {
                result.put(SensorEntry.COL_R_AY, imu.getAy());
            }
            if (imu.hasAz()) {
                result.put(SensorEntry.COL_R_AZ, imu.getAz());
            }
            if (imu.hasGx()) {
                result.put(SensorEntry.COL_R_GX, imu.getGx());
            }
            if (imu.hasGy()) {
                result.put(SensorEntry.COL_R_GY, imu.getGy());
            }
            if (imu.hasGz()) {
                result.put(SensorEntry.COL_R_GZ, imu.getGz());
            }
            if (imu.hasYaw()) {
                result.put(SensorEntry.COL_R_YAW, imu.getYaw());
            }
            if (imu.hasPitch()) {
                result.put(SensorEntry.COL_R_PITCH, imu.getPitch());
            }
            if (imu.hasRoll()) {
                result.put(SensorEntry.COL_R_ROLL, imu.getRoll());
            }
            if (imu.hasTemperature()) {
                result.put(SensorEntry.COL_R_TEMP, imu.getTemperature());
            }

            if (right.hasPressureFront()) {
                result.put(SensorEntry.COL_R_PRESSURE, right.getPressureFront());
            }
        }

        // All the data coming from phone.
        {
            PhoneData phone = protoData.getPhoneData();
            LocationData location = phone.getLocationData();

            if (location.hasLatitude()) {
                result.put(SensorEntry.COL_P_LOC_LAT, location.getLatitude());
            }
            if (location.hasLongitude()) {
                result.put(SensorEntry.COL_P_LOC_LONG, location.getLongitude());
            }
            if (location.hasAltitude()) {
                result.put(SensorEntry.COL_P_LOC_ALT, location.getAltitude());
            }
            if (location.hasSpeed()) {
                result.put(SensorEntry.COL_P_LOC_SPEED, location.getSpeed());
            }
            if (location.hasAccuracy()) {
                result.put(SensorEntry.COL_P_LOC_ACCURACY, location.getAccuracy());
            }
        }

        return result;
    }
}
