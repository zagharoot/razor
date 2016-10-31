package com.razorski.razor.data;

import android.support.annotation.Nullable;

import com.razorski.razor.SensorData;

import java.io.IOException;
import java.io.InputStream;

/**
 * An abstract class that is capable of parsing data stream as they are transmitted over BT
 * and generating SensorData proto.
 */
public abstract class SensorDataStreamParser {

    public SensorDataStreamParser() {}

    public SensorData readNext(InputStream inputStream)  throws IOException {
        SensorData data = readNextInternal(inputStream);
        if (data != null) {
            // Add timestamp to the data.
            data = data.toBuilder().setTimestampMsec(System.currentTimeMillis()).build();
        }

        return data;
    }

    @Nullable
    abstract protected SensorData readNextInternal(InputStream inputStream) throws IOException;
}

