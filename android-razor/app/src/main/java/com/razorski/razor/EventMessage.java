package com.razorski.razor;

import android.support.annotation.Nullable;

/**
 * Message class that is passed between activities and components of the program.
 */
public class EventMessage {

    // Describes what message the object contains.
    private EventType eventType;

    // This is just to put an event type into an intent (you have to put key/value pair and this
    // will be used as the key).
    public static final String EVENT_TYPE_KEY = "EventType";

    // Contains the sensor data if populated.
    @Nullable private SensorData sensorData = null;

    public EventMessage(EventType eventType) {
        this.eventType = eventType;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    @Nullable
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
    public enum EventType {
        // Don't use this.
        UNKNOWN,

        // Connection status report:
        // We're now connected to the hardware.
        HW_CONNECTED,
        // Trying to connect to the hardware.
        HW_CONNECTING,
        // We're disconnected from the hardware (and not actively connecting).
        HW_DISCONNECTED,

        // Commands from UI:
        // Connect to the device.
        CONNECT_HW,
        // Disconnect from the device (Also stops recording if it is on).
        DISCONNECT_HW,
        // Start recording data.
        START_RECORDING,
        // Stop recording data (does not disconnect from device).
        STOP_RECORDING,

        // Data Transfers:
        // Raw data was received from the hardware (sent by SensorDataParser when parsing is done).
        RECEIVED_RAW_DATA,
        // When a new data is received and processed, an now is ready to be shown in the UI.
        DATA_READY_FOR_UI
    }
}
