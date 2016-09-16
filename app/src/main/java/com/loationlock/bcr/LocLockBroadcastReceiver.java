package com.loationlock.bcr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.loationlock.service.LocationService;


public class LocLockBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "OnReceive", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent);
    }
}
