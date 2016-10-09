package com.razorski.razor;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * An abstract class that is capable of parsing data stream as they are transmitted over BT
 * and generating SensorData proto.
 */
public abstract class SensorDataStreamParser {
    private SensorDataManager dataManager;

    public SensorDataStreamParser(SensorDataManager dataManager_)
    {
        dataManager = dataManager_;
    }

    public void readNext(InputStream inputStream)  throws IOException {
        SensorData data = readNextInternal(inputStream);
        if (data != null) {
            dataManager.addData(data);
        }
    }

    @Nullable
    abstract protected SensorData readNextInternal(InputStream inputStream) throws IOException;
}

/**
 * A parser that assumes we encode the proto on the Arduino side using proto serialization.
 */
class SensorDataProtoParser extends SensorDataStreamParser {

    public SensorDataProtoParser(SensorDataManager dataManager_)
    {
        super(dataManager_);
    }

    @Override
    protected SensorData readNextInternal(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        try {
            return SensorData.parseDelimitedFrom(inputStream);
        } catch (IOException e) {
            return null;
        }
    }
}
