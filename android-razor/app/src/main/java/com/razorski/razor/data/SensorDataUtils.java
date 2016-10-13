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
//        return "{" + toString(data.getLeft()) + "," + toString(data.getRight()) + "}";
        return toString(data.getLeft());
    }

    private static String toString(FootSensorData data) {
        IMUData imu = data.getImuData();
        return String.format(
                "A: {%1$d,%2$d,%3$d}\n" +
                "G: {%4$d,%5$d,%6$d}\n" +
                "P: {%7$.2f,%8$.2f,%9$.2f}\n" + "T: {%10$d}\n" +
                "Pr: {%11$d}",
                imu.getAx(), imu.getAy(), imu.getAz(), imu.getGx(), imu.getGy(), imu.getGz(),
                imu.getYaw(), imu.getPitch(), imu.getRoll(), imu.getTemperature(), data.getPressureFront());

/*        return String.format(
                "{%1$d,%2$d,%3$d,%4$d,%5$d,%6$d,%7$.2f,%8$.2f,%9$.2f,%10$d}",
                imu.getAx(), imu.getAy(), imu.getAz(), imu.getGx(), imu.getGy(), imu.getGz(),
                imu.getYaw(), imu.getPitch(), imu.getRoll(), imu.getTemperature());
*/    }
}
