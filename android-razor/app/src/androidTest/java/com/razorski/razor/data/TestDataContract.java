package com.razorski.razor.data;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.razorski.razor.SensorData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Tests for the DataContract class.
 */

@RunWith(AndroidJUnit4.class)
public class TestDataContract {

    // Tests whether we correctly encode/decode URI for sensor reading.
    @Test
    public void sensorReadingUriTest() {
        SensorData sensorData = TestUtils.generateFakeSensorData();

        Uri readingUri = DataContract.SensorEntry.uriForSensorReading(sensorData);
        assertNotNull("Error: Null Uri returned.", readingUri);

        SensorData readData = DataContract.SensorEntry.getReadingFromUri(readingUri);

        assertEquals(sensorData, readData);
    }

    @Test
    public void timestampUriTest() {
        long timestamp = 123456789;

        Uri uri = DataContract.SensorEntry.uriForTimestamp(timestamp);

        long readTimeStamp = DataContract.SensorEntry.timestampFromUri(uri);
        assertEquals(timestamp, readTimeStamp);
    }
}
