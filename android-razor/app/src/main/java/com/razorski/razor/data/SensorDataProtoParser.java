package com.razorski.razor.data;

import com.razorski.razor.SensorData;

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser that assumes we encode the proto on the Arduino side using proto serialization.
 */
public class SensorDataProtoParser extends SensorDataStreamParser {

    @Override
    protected SensorData readNextInternal(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        return SensorData.parseDelimitedFrom(inputStream);
    }
}
