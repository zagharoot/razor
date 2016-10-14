package com.razorski.razor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Collects sensor data from the phone and populates @code{PhoneData} proto.
 */

public class PhoneSensorCollector {
    private Context context;
    private LocationManager locationManager;
    private MyLocationListener locationListener = new MyLocationListener();

    // Most recent location data.
    private Location location;


    public PhoneSensorCollector(Context context_) {
        context = context_;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Ey baba no permission");
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    /**
     * Reads sensory data from the phone and returns the @code{PhoneData} proto.
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
    @Nullable
    private LocationData readLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Need to request user for permission and handle results.
            Log.d(TAG, "No GPS permission, need to get it from user.");
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
            Log.d(TAG, "Received new location data");
            location = location_;
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
