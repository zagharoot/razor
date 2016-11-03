package com.razorski.razor.data;

import android.content.ContentValues;

import com.google.firebase.database.Exclude;
import com.razorski.razor.data.DataContract.RecordSessionEntry;

/**
 * Objects that will be saved into firebase database.
 * There is one class for each proto we want to save (also roughly correspond to SQL database).
 */

public class CloudProtoHelper {

    // Stores each RecordSession data. This does not include actual sensor data.
    public static class RecordSessionFB {

        private Long startTimestampMsec = null;
        private Long endTimestampMsec = null;

        public RecordSessionFB() {}

        public RecordSessionFB(ContentValues contentValues) {
            startTimestampMsec = contentValues.getAsLong(
                    RecordSessionEntry.COL_START_TIMESTAMP_MSEC);
            endTimestampMsec = contentValues.getAsLong(
                    RecordSessionEntry.COL_END_TIMESTAMP_MSEC);
        }

        public Long getStartTimestampMsec() {
            return startTimestampMsec;
        }

        public void setStartTimestampMsec(Long startTimestampMsec) {
            this.startTimestampMsec = startTimestampMsec;
        }

        public Long getEndTimestampMsec() {
            return endTimestampMsec;
        }

        public void setEndTimestampMsec(Long endTimestampMsec) {
            this.endTimestampMsec = endTimestampMsec;
        }
    }

    public static class IMUDataFB {

        public Integer getAx() {
            return ax;
        }

        public void setAx(Integer ax) {
            this.ax = ax;
        }

        public Integer getAy() {
            return ay;
        }

        public void setAy(Integer ay) {
            this.ay = ay;
        }

        public Integer getAz() {
            return az;
        }

        public void setAz(Integer az) {
            this.az = az;
        }

        public Integer getGx() {
            return gx;
        }

        public void setGx(Integer gx) {
            this.gx = gx;
        }

        public Integer getGy() {
            return gy;
        }

        public void setGy(Integer gy) {
            this.gy = gy;
        }

        public Integer getGz() {
            return gz;
        }

        public void setGz(Integer gz) {
            this.gz = gz;
        }

        public Float getYaw() {
            return yaw;
        }

        public void setYaw(Float yaw) {
            this.yaw = yaw;
        }

        public Float getPitch() {
            return pitch;
        }

        public void setPitch(Float pitch) {
            this.pitch = pitch;
        }

        public Float getRoll() {
            return roll;
        }

        public void setRoll(Float roll) {
            this.roll = roll;
        }

        public Integer getTemperature() {
            return temperature;
        }

        public void setTemperature(Integer temperature) {
            this.temperature = temperature;
        }

        @Exclude
        public boolean isAllNull() {
            return ax == null && ay == null && az == null && gx == null && gy == null && gz == null
                    && yaw == null && pitch == null && roll == null && temperature == null;
        }

        private Integer ax;
        private Integer ay;
        private Integer az;

        private Integer gx;
        private Integer gy;
        private Integer gz;

        private Float yaw;
        private Float pitch;
        private Float roll;

        private Integer temperature;

    }

    public static class FootSensorDataFB {
        public IMUDataFB getImuData() {
            return imuData;
        }

        public void setImuData(IMUDataFB imuData) {
            this.imuData = imuData;
        }

        public Integer getPressureFront() {
            return pressureFront;
        }

        public void setPressureFront(Integer pressureFront) {
            this.pressureFront = pressureFront;
        }

        private IMUDataFB imuData;
        private Integer pressureFront;

        public FootSensorDataFB(IMUDataFB imu, Integer pressureFront) {
            this.imuData = imu;
            this.pressureFront = pressureFront;
        }

        @Exclude
        public boolean isAllNull() {
            return imuData == null && pressureFront == null;
        }
    }

    public static class LocationDataFB {
        private Double latitude;
        private Double longitude;
        private Float speed;
        private Double altitude;
        private Float accuracy;

        public LocationDataFB(ContentValues values) {
            latitude = values.getAsDouble(DataContract.SensorEntry.COL_P_LOC_LAT);
            longitude = values.getAsDouble(DataContract.SensorEntry.COL_P_LOC_LONG);
            speed = values.getAsFloat(DataContract.SensorEntry.COL_P_LOC_SPEED);
            altitude = values.getAsDouble(DataContract.SensorEntry.COL_P_LOC_ALT);
            accuracy = values.getAsFloat(DataContract.SensorEntry.COL_P_LOC_ACCURACY);
        }

        @Exclude
        public boolean isAllNull() {
            return latitude == null && longitude == null && speed == null && altitude == null
                    && accuracy == null;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Float getSpeed() {
            return speed;
        }

        public void setSpeed(Float speed) {
            this.speed = speed;
        }

        public Double getAltitude() {
            return altitude;
        }

        public void setAltitude(Double altitude) {
            this.altitude = altitude;
        }

        public Float getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Float accuracy) {
            this.accuracy = accuracy;
        }
    }

    public static class PhoneDataFB {
        private LocationDataFB locationData;

        public PhoneDataFB(ContentValues values) {
            locationData = new LocationDataFB(values);

            if (locationData.isAllNull()) {
                locationData = null;
            }
        }

        @Exclude
        public boolean isAllNull() {
            return locationData == null;
        }

        public LocationDataFB getLocationData() {
            return locationData;
        }

        public void setLocationData(LocationDataFB locationData) {
            this.locationData = locationData;
        }
    }

    public static class SensorDataFB {
        private FootSensorDataFB left;
        private FootSensorDataFB right;
        private Long timestampMsec;
        private PhoneDataFB phoneData;

        public SensorDataFB(ContentValues values) {
            timestampMsec = values.getAsLong(DataContract.SensorEntry.COL_TIMESTAMP_MSEC);
            phoneData = new PhoneDataFB(values);
            if (phoneData.isAllNull()) {
                phoneData = null;
            }

            // Set the values for left foot.
            IMUDataFB leftImu = new IMUDataFB();
            leftImu.setAx(values.getAsInteger(DataContract.SensorEntry.COL_L_AX));
            leftImu.setAy(values.getAsInteger(DataContract.SensorEntry.COL_L_AY));
            leftImu.setAz(values.getAsInteger(DataContract.SensorEntry.COL_L_AX));
            leftImu.setGx(values.getAsInteger(DataContract.SensorEntry.COL_L_GX));
            leftImu.setGy(values.getAsInteger(DataContract.SensorEntry.COL_L_GY));
            leftImu.setGz(values.getAsInteger(DataContract.SensorEntry.COL_L_GZ));
            leftImu.setPitch(values.getAsFloat(DataContract.SensorEntry.COL_L_PITCH));
            leftImu.setYaw(values.getAsFloat(DataContract.SensorEntry.COL_L_YAW));
            leftImu.setRoll(values.getAsFloat(DataContract.SensorEntry.COL_L_ROLL));
            leftImu.setTemperature(values.getAsInteger(DataContract.SensorEntry.COL_L_TEMP));
            if (leftImu.isAllNull()) {
                leftImu = null;
            }

            left = new FootSensorDataFB(leftImu,
                    values.getAsInteger(DataContract.SensorEntry.COL_L_PRESSURE));
            if (left.isAllNull()) {
                left = null;
            }

            // Set the values for right foot.
            IMUDataFB rightImu = new IMUDataFB();
            rightImu.setAx(values.getAsInteger(DataContract.SensorEntry.COL_R_AX));
            rightImu.setAy(values.getAsInteger(DataContract.SensorEntry.COL_R_AY));
            rightImu.setAz(values.getAsInteger(DataContract.SensorEntry.COL_R_AX));
            rightImu.setGx(values.getAsInteger(DataContract.SensorEntry.COL_R_GX));
            rightImu.setGy(values.getAsInteger(DataContract.SensorEntry.COL_R_GY));
            rightImu.setGz(values.getAsInteger(DataContract.SensorEntry.COL_R_GZ));
            rightImu.setPitch(values.getAsFloat(DataContract.SensorEntry.COL_R_PITCH));
            rightImu.setYaw(values.getAsFloat(DataContract.SensorEntry.COL_R_YAW));
            rightImu.setRoll(values.getAsFloat(DataContract.SensorEntry.COL_R_ROLL));
            rightImu.setTemperature(values.getAsInteger(DataContract.SensorEntry.COL_R_TEMP));
            if (rightImu.isAllNull()) {
                rightImu = null;
            }

            right = new FootSensorDataFB(rightImu,
                    values.getAsInteger(DataContract.SensorEntry.COL_R_PRESSURE));
            if (right.isAllNull()) {
                right = null;
            }
        }

        public FootSensorDataFB getLeft() {
            return left;
        }

        public void setLeft(FootSensorDataFB left) {
            this.left = left;
        }

        public FootSensorDataFB getRight() {
            return right;
        }

        public void setRight(FootSensorDataFB right) {
            this.right = right;
        }

        public Long getTimestampMsec() {
            return timestampMsec;
        }

        public void setTimestampMsec(Long timestampMsec) {
            this.timestampMsec = timestampMsec;
        }

        public PhoneDataFB getPhoneData() {
            return phoneData;
        }

        public void setPhoneData(PhoneDataFB phoneData) {
            this.phoneData = phoneData;
        }
    }

}
