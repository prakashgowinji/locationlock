package com.loationlock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loationlock.bcr.LocLockBroadcastReceiver;

import loationlock.com.locpoller.LocationPoller;
import loationlock.com.locpoller.LocationPollerParameter;

public class MainActivity extends AppCompatActivity {
    private static final int PERIOD = 1000 * 60 * 30;    // 30 minutes
    private PendingIntent pendingIntent = null;
    private AlarmManager alarmManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent i = new Intent(this, LocationPoller.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(new Intent(this, LocLockBroadcastReceiver.class));
        parameter.setProviders(new String[]{LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER}); // try GPS and fall back to NETWORK_PROVIDER
        parameter.setTimeout(60000);
        i.putExtras(bundle);


        pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                PERIOD,
                pendingIntent);

        Toast.makeText(this, "Location polling every 30 minutes begun", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void stopAlarm(View v) {
        alarmManager.cancel(pendingIntent);
        finish();
    }
}
