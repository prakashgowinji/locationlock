package com.loationlock.bcr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;

import com.loationlock.data.FirebaseHelper;
import com.loationlock.data.LocationLoc;

import loationlock.com.locpoller.LocationPollerResult;


public class LocLockBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        LocationPollerResult locationResult = new LocationPollerResult(b);
        Location location = locationResult.getLocation();
        if (location != null) {
            pushLocation(context, location.getLatitude(),
                    location.getLongitude(),
                    location.getAltitude(), Long.valueOf(String.valueOf(location.getTime())),
                    location.getProvider(), location.getSpeed());
        }
    }

    private String getDeviceID(Context context) {
        final String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    private void pushLocation(Context context, double latitude, double longitude,
                              double altitude, long time, String provider, double speed) {
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.init();
        LocationLoc location = new LocationLoc(getDeviceID(context), latitude, longitude, altitude, time, provider, speed);
        firebaseHelper.pushLocationInfo(location);
    }
}
