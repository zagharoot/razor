package com.razorski.razor;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Collects sensor data from the phone and populates @code{PhoneData} proto.
 */

public class PhoneSensorCollector {
    // Used for getting the location service objects.
    private Context context;

    private LocationManager locationManager = null;
    // Used for callbacks every time new location is registered with the device.
    private MyLocationListener locationListener = new MyLocationListener();

    // Minimum interval between updates from GPS in milliseconds.
    private static final int MIN_UPDATE_INTERVAL_MSEC = 500;
    // Minimum distance between updates from GPS in meters.
    private static final float MIN_UPDATE_DISTANCE = 0.2f;

    // Most recent location data.
    private Location location = null;
    // Timestamp of the last update for GPS.
    private long lastLocationUpdateTimestampMsec = 0;

    public PhoneSensorCollector(Context context_) {
        context = context_;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /** Initializes the object. Assumes permission to access data is granted.
     */
    public void init() {
        try{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_UPDATE_INTERVAL_MSEC, MIN_UPDATE_DISTANCE, locationListener);
        } catch (SecurityException e) {
            Log.d(TAG, "I still don't have access to GPS data");
        }
    }

    /**
     * Reads sensory data from the phone and returns the @code{PhoneData} proto.
     * You should call @code{init()} before calling this method.
     */
    public PhoneData readData() {
        PhoneData.Builder builder = PhoneData.newBuilder();
        LocationData locationData = readLocation();
        if (locationData != null) {
            builder.setLocationData(locationData);
        }

        return builder.build();
    }

    /**
     * Reads location data from GPS and populates the @code{LocationData} proto.
     * @return null if cannot read the data.
     */
    // TODO: We need to return null if last updated data is too stale.
    @Nullable
    private LocationData readLocation() {
        if (locationManager == null || location == null) {
            return null;
        }

        LocationData.Builder builder = LocationData.newBuilder();
        builder.setLatitude(location.getLatitude())
            .setLongitude(location.getLongitude());
        if (location.hasAccuracy()) {
            builder.setAccuracy(location.getAccuracy());
        }
        if (location.hasSpeed()) {
            builder.setSpeed(location.getSpeed());
        }
        if (location.hasAltitude()) {
            builder.setAltitude(location.getAltitude());
        }

        builder.setSpeed(10.5f);
        return builder.build();
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location_) {
            lastLocationUpdateTimestampMsec = System.currentTimeMillis();
            // First time we're reading any data.
            if (location == null) {
                location = new Location(location_);
            } else {
                location.set(location_);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
