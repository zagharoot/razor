package com.razorski.razor;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.MatchResult;

import static java.lang.Math.max;

/**
 * An abstract class that is capable of parsing data stream as they are transmitted over BT
 * and generating SensorData proto.
 */
public abstract class SensorDataStreamParser {
    private SensorDataManager dataManager;
    private Queue<SensorData> dataToProcess = new ArrayBlockingQueue<SensorData>(5);

    public SensorDataStreamParser(SensorDataManager dataManager_)
    {
        dataManager = dataManager_;
    }

    /**
     * Processes new incoming data, possibly notifying the corresponding task of the new sensory
     * data if complete data for new data has arrived.
     */
    public void processData(byte[] buffer, int length) {
        processDataInternal(buffer, length);
        while (!dataToProcess.isEmpty()) {
            dataManager.addData(dataToProcess.remove());
        }
    }

    protected void addData(SensorData data) {
        dataToProcess.add(data);
    }

    /**
     * Processes new arrival data from buffer.
     *
     * This is the function that children need to override.
     */
    protected abstract void processDataInternal(byte[] buffer, int length);
}

/**
 * Assumes the data is being transmitted as text representation of the proto.
 * TODO: This is a stupid solution that is dependant on the proto definition. Rewrite to send/parse
 *       proto binary serialization.
 *
 * The text should be of the format {{x,y,z} {x,y,z}} for left foot and right foot data.
 */
class SensorDataTextParser extends SensorDataStreamParser {
    private static final String TAG = SensorDataTextParser.class.getName();

    private StringBuilder messageBuilder = new StringBuilder();
    private int braceBalance = 0;
    private int maxBraces = 0;

    public SensorDataTextParser(SensorDataManager task_) {
        super(task_);
    }

    private void reset() {
        maxBraces = 0;
        braceBalance = 0;
        messageBuilder.delete(0, messageBuilder.length());
    }

    @Nullable
    @Override
    protected void processDataInternal(byte[] buffer, int length) {
        if (length <= 0) {
            return;
        }

        for (int i = 0; i < length; ++i) {
            char ch = (char) buffer[i];
            if (ch == '{') {
                braceBalance++;
                maxBraces = max(maxBraces, braceBalance);
            } else if (ch == '}') {
                braceBalance--;
            }

            messageBuilder.append(ch);

            // Bad input, wipe out everything and start over.
            if (braceBalance < 0) {
                Log.d(TAG, "bad input data: " + messageBuilder.toString());

                reset();
                continue;
            }

            // Message was received.
            if (braceBalance == 0) {
                if (maxBraces == 2) {
                    SensorData data = parseMessage(messageBuilder.toString());
                    if (data != null) {
                        addData(data);
                    }
                }
                reset();
            }

        }
        return;
    }

    /**
     * Tries to parse the message. Note: message should represent the proto otherwise null
     * will be returned.
     */
    @Nullable
    private SensorData parseMessage(String message) {
        Log.d(TAG, "parsing message in parser thread: " + message);
        Scanner s = new Scanner(message);
        s.findInLine("\\{\\{(\\d+),(\\d+),(\\d+)\\}, \\{(\\d+),(\\d+),(\\d+)\\}\\}");
        MatchResult result = s.match();

        if (result.groupCount() == 6) {
            SensorData.Builder builder = SensorData.newBuilder();
            FootData.Builder footBuilder = FootData.newBuilder();
            footBuilder.setTilt(Integer.valueOf(result.group(1)));
            footBuilder.setFrontForce(Integer.valueOf(result.group(2)));
            footBuilder.setVelocity(Integer.valueOf(result.group(3)));
            builder.setLeftFoot(footBuilder.build());

            footBuilder.setTilt(Integer.valueOf(result.group(4)));
            footBuilder.setFrontForce(Integer.valueOf(result.group(5)));
            footBuilder.setVelocity(Integer.valueOf(result.group(6)));
            builder.setRightFoot(footBuilder.build());
            s.close();

            return builder.build();
        }
        return null;
    }
}
