package com.razorski.razor.data;

import com.google.firebase.database.Exclude;
import com.razorski.razor.FootSensorData;
import com.razorski.razor.IMUData;
import com.razorski.razor.LocationData;
import com.razorski.razor.PhoneData;
import com.razorski.razor.RecordSession;
import com.razorski.razor.RunSession;
import com.razorski.razor.SensorData;

/**
 * Contains representation of our protos in firebase.
 * Firebase wants simple classes, but proto classes have tons of overhead, so we can't just easily
 * store them in firebase database.
 * We have one class for each proto message here, and unfortunately needs to be in sync with our
 * proto, sensor-data.proto.
 */
public class FirebaseDataProtos {

    public static class IMUDataFB {
        public Integer ax;
        public Integer ay;
        public Integer az;
        
        public Integer gx;
        public Integer gy;
        public Integer gz;
        
        public Float yaw;
        public Float pitch;
        public Float roll;
        
        public Integer temperature;

        public IMUDataFB() {}

        public IMUDataFB(IMUData imuData) {
            if (imuData.hasAx()) {
                ax = imuData.getAx();
            }
            if (imuData.hasAy()) {
                ay = imuData.getAy();
            }
            if (imuData.hasAz()) {
                az = imuData.getAz();
            }
            if (imuData.hasGx()) {
                gx = imuData.getGx();
            }
            if (imuData.hasGy()) {
                gy = imuData.getGy();
            }
            if (imuData.hasGz()) {
                gz = imuData.getGz();
            }
            if (imuData.hasYaw()) {
                yaw = imuData.getYaw();
            }
            if (imuData.hasPitch()) {
                pitch = imuData.getPitch();
            }
            if (imuData.hasRoll()) {
                roll = imuData.getRoll();
            }
            if (imuData.hasTemperature()) {
                temperature = imuData.getTemperature();
            }
        }

        @Exclude
        public IMUData.Builder toProto() {
            IMUData.Builder builder = IMUData.newBuilder();
            if (ax != null) {
                builder.setAx(ax);
            }
            if (ay != null) {
                builder.setAy(ay);
            }
            if (az != null) {
                builder.setAz(az);
            }
            if (gx != null) {
                builder.setGx(gx);
            }
            if (gy != null) {
                builder.setGy(gy);
            }
            if (gz != null) {
                builder.setGz(gz);
            }
            if (yaw != null) {
                builder.setYaw(yaw);
            }
            if (pitch != null) {
                builder.setPitch(pitch);
            }
            if (roll != null) {
                builder.setRoll(roll);
            }
            if (temperature != null) {
                builder.setTemperature(temperature);
            }

            return builder;
        }
    }

    public static class FootSensorDataFB {
        public IMUDataFB imuData;
        public Integer pressureFront;

        public FootSensorDataFB() {}

        public FootSensorDataFB(FootSensorData protoData) {
            if (protoData.hasPressureFront()) {
                pressureFront = protoData.getPressureFront();
            }
            if (protoData.hasImuData()) {
                imuData = new IMUDataFB(protoData.getImuData());
            }
        }

        @Exclude
        public FootSensorData.Builder toProto() {
            FootSensorData.Builder builder = FootSensorData.newBuilder();

            if (imuData != null) {
                builder.setImuData(imuData.toProto());
            }
            if (pressureFront != null) {
                builder.setPressureFront(pressureFront);
            }
            return builder;
        }
    }

    public static class LocationDataFB {
        public Double latitude;
        public Double longitude;
        public Float speed;
        public Double altitude;
        public Float accuracy;

        public LocationDataFB() {}

        public LocationDataFB(LocationData protoData) {
            if (protoData.hasLatitude()) {
                latitude = protoData.getLatitude();
            }
            if (protoData.hasLongitude()) {
                longitude = protoData.getLongitude();
            }
            if (protoData.hasLatitude()) {
                speed = protoData.getSpeed();
            }
            if (protoData.hasLatitude()) {
                altitude = protoData.getAltitude();
            }
            if (protoData.hasLatitude()) {
                accuracy = protoData.getAccuracy();
            }
        }

        @Exclude
        public LocationData.Builder toProto() {
            LocationData.Builder builder = LocationData.newBuilder();
            if (latitude != null) {
                builder.setLatitude(latitude);
            }
            if (longitude != null) {
                builder.setLongitude(longitude);
            }
            if (speed != null) {
                builder.setSpeed(speed);
            }
            if (altitude != null) {
                builder.setAltitude(altitude);
            }
            if (accuracy != null) {
                builder.setAccuracy(accuracy);
            }

            return builder;
        }
    }

    public static class PhoneDataFB {
        public LocationDataFB locationData;

        public PhoneDataFB() {}

        public PhoneDataFB(PhoneData protoData) {
            if (protoData.hasLocationData()) {
                locationData = new LocationDataFB(protoData.getLocationData());
            }
        }

        @Exclude
        public PhoneData.Builder toProto() {
            PhoneData.Builder builder = PhoneData.newBuilder();

            if (locationData != null) {
                builder.setLocationData(locationData.toProto());
            }
            return builder;
        }
    }

    public static class SensorDataFB {
        public FootSensorDataFB left;
        public FootSensorDataFB right;
        public Long timestampMsec;
        public PhoneDataFB phoneData;

        public SensorDataFB() {}

        public SensorDataFB(SensorData protoData) {
            if (protoData.hasTimestampMsec()) {
                timestampMsec = protoData.getTimestampMsec();
            }
            if (protoData.hasLeft()) {
                left = new FootSensorDataFB(protoData.getLeft());
            }
            if (protoData.hasRight()) {
                right = new FootSensorDataFB(protoData.getRight());
            }
            if (protoData.hasPhoneData()) {
                phoneData = new PhoneDataFB(protoData.getPhoneData());
            }
        }

        @Exclude
        public SensorData.Builder toProto() {
            SensorData.Builder builder = SensorData.newBuilder();

            if (timestampMsec != null) {
                builder.setTimestampMsec(timestampMsec);
            }
            if (left != null) {
                builder.setLeft(left.toProto());
            }
            if (right != null) {
                builder.setRight(right.toProto());
            }
            if (phoneData != null) {
                builder.setPhoneData(phoneData.toProto());
            }
            return builder;
        }
    }

    public static class RunSessionFB {
        public Long startTimestampMsec;
        public Long endTimestampMsec;

        public RunSessionFB() {}

        public RunSessionFB(RunSession protoData) {
            if (protoData.hasStartTimestampMsec()) {
                startTimestampMsec = protoData.getStartTimestampMsec();
            }
            if (protoData.hasEndTimestampMsec()) {
                endTimestampMsec = protoData.getEndTimestampMsec();
            }
        }

        @Exclude
        public RunSession.Builder toProto() {
            RunSession.Builder builder = RunSession.newBuilder();

            if (startTimestampMsec != null) {
                builder.setStartTimestampMsec(startTimestampMsec);
            }
            if (endTimestampMsec != null) {
                builder.setEndTimestampMsec(endTimestampMsec);
            }
            return builder;
        }
    }

    public static class RecordSessionFB {
        public Long startTimestampMsec;
        public Long endTimestampMsec;

        public RecordSessionFB() {}

        public RecordSessionFB(RecordSession protoData) {
            if (protoData.hasStartTimestampMsec()) {
                startTimestampMsec = protoData.getStartTimestampMsec();
            }
            if (protoData.hasEndTimestampMsec()) {
                endTimestampMsec = protoData.getEndTimestampMsec();
            }
        }

        @Exclude
        public RecordSession.Builder toProto() {
            RecordSession.Builder builder = RecordSession.newBuilder();

            if (startTimestampMsec != null) {
                builder.setStartTimestampMsec(startTimestampMsec);
            }
            if (endTimestampMsec != null) {
                builder.setEndTimestampMsec(endTimestampMsec);
            }
            return builder;
        }
    }
}
