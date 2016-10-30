package com.razorski.razor;

import android.support.annotation.Nullable;

/**
 * Message class that is passed between activities and components of the program.
 */
public class EventMessage {

    // Describes what message the object contains.
    private EventType eventType;

    // Contains the sensor data if populated.
    @Nullable private SensorData sensorData = null;

    public EventMessage(EventType eventType) {
        this.eventType = eventType;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public EventType getEventType() {
        return eventType;
    }

    /**
     * Type of events that can be passed around between activities and services.
     * It is mainly used in the EventBus system to process messages.
     */
    public static enum EventType {
        // Don't use this.
        UNKNOWN,
        // We're now connected to the hardware.
        HW_CONNECTED,
        // Trying to connect to the hardware.
        HW_CONNECTING,
        // We're disconnected from the hardware (and not actively connecting).
        HW_DISCONNECTED,
        // Raw data was received from the hardware (sent by SensorDataParser when parsing is done).
        RECEIVED_RAW_DATA,
        // When a new data is received and processed, an now is ready to be shown in the UI.
        DATA_READY_FOR_UI
    }
}
