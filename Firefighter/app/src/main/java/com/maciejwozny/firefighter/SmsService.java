package com.maciejwozny.firefighter;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by maciek on 20.07.17.
 */

public class SmsService  extends Service {
    private static final String TAG = "SmsService";
    private SmsReceiver smsReceiver;
    private IntentFilter intentFilter;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "service creating");
        Toast.makeText(this, "service creating", Toast.LENGTH_SHORT).show();

        smsReceiver = new SmsReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "service onDestroy");
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service starting");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
