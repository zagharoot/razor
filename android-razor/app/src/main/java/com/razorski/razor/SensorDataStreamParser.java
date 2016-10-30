package com.razorski.razor;

import android.support.annotation.Nullable;

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

/**
 * A parser that assumes we encode the proto on the Arduino side using proto serialization.
 */
class SensorDataProtoParser extends SensorDataStreamParser {

    @Override
    protected SensorData readNextInternal(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        return SensorData.parseDelimitedFrom(inputStream);
    }
}
