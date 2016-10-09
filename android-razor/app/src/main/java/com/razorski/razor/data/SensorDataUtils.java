package com.razorski.razor.data;

import com.razorski.razor.FootSensorData;
import com.razorski.razor.IMUData;
import com.razorski.razor.SensorData;

/**
 * Utilities for working with the SensorData proto.
 */

public class SensorDataUtils {

    /**
     * Converts a proto to Text. This is necessary because the protolite library doesn't support
     * this. Because we don't want to use reflection, this needs to be updated every time we
     * update the proto definition.
     */
    public static String toString(SensorData data) {
        return "{" + toString(data.getLeft()) + "," + toString(data.getRight()) + "}";
    }

    private static String toString(FootSensorData data) {
        IMUData imu = data.getImuData();
        return String.format(
                "{%1$.2f,%2$.2f,%3$.2f,%4$.2f,%5$.2f,%6$.2f,%7$.2f,%8$.2f,%9$.2f,%10$.2f}",
                imu.getAx(), imu.getAy(), imu.getAz(), imu.getGx(), imu.getGy(), imu.getGz(),
                imu.getYaw(), imu.getPitch(), imu.getRoll(), imu.getTemperature());
    }
}
