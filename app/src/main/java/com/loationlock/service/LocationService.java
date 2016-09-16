package com.loationlock.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.loationlock.R;
import com.loationlock.data.FirebaseHelper;
import com.loationlock.data.LocationLoc;

public class LocationService extends Service implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    //private final Context mContext;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2000; // 2000 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 60; // 1 hour

    // Declaring a LocationLoc Manager
    protected LocationManager locationManager;

    FirebaseHelper firebaseHelper;


    public LocationService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showToast("onStartCommand()");
        System.out.println("XXX onStartCommand()");
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    public Location getLocation() {
        showToast("getLocation()");
        System.out.println("XXX getLocation()");
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    System.out.println("XXX isGPSEnabled");
                    //if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                    //}
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        try {
            if (locationManager != null) {
                if (hasLocationPermission()) {
                    locationManager.removeUpdates(LocationService.this);
                } else {
                    requestLocationPermission();
                }
            }
        } catch (SecurityException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void requestLocationPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions((AppCompatActivity) getApplicationContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    private boolean hasLocationPermission() {
        String callPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), callPermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        showToast(R.string.location_changed
                + "Lat: " + location.getLatitude()
                + " Lon: " + location.getLongitude()
                + " Alt: " + location.getAltitude()
                + "Time: " + location.getTime()
                + "\n Provider: " + location.getProvider());
        pushLocation(location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(), Long.valueOf(String.valueOf(location.getTime())),
                location.getProvider());
    }

    /**
     * Showing a toast message, using the Main thread
     */
    private void showToast(final String message) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private String getDeviceID() {
        final String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        System.out.println("XXX DeviceId: " + deviceId);
        return deviceId;
    }

    private void pushLocation(double latitude, double longitude, double altitude, long time, String provider) {
        System.out.println("latitude = [" + latitude + "], longitude = [" + longitude + "], altitude = [" + altitude + "], time = [" + time + "]");
        LocationLoc location = new LocationLoc(getDeviceID(), latitude, longitude, altitude, time, provider);
        firebaseHelper.pushLocationInfo(location);
    }

    @Override
    public void onRequestPermissionsResult(@NonNull int requestCode, @NonNull String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                try {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeUpdates(LocationService.this);

                    } else {
                        showToast(getString(R.string.location_permission_denied));
                    }
                } catch (SecurityException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
