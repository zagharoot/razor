package com.razorski.razor.data;

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
        return String.format("{{%1$d,%2$d,%3$d},{%4$d,%5$d,%6$d}}",
                data.getLeftFoot().getTilt(),
                data.getLeftFoot().getFrontForce(),
                data.getLeftFoot().getVelocity(),
                data.getRightFoot().getTilt(),
                data.getRightFoot().getFrontForce(),
                data.getRightFoot().getVelocity());
    }
}