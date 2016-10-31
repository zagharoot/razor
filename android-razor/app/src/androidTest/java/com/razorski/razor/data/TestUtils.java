package com.razorski.razor.data;

import com.razorski.razor.FootSensorData;
import com.razorski.razor.IMUData;
import com.razorski.razor.LocationData;
import com.razorski.razor.PhoneData;
import com.razorski.razor.RecordSession;
import com.razorski.razor.SensorData;

/**
 * Utility functions for tests.
 */

public class TestUtils {

    public static RecordSession createRecordSession(long start, long end) {
        return RecordSession.newBuilder().setStartTimestampMsec(start)
                .setEndTimestampMsec(end).build();
    }

    public static SensorData generateFakeSensorData() {
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

}
