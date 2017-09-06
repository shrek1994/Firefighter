package com.maciejwozny.firefighter.Model.Sms;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Mateusz on 29.08.2017.
 */

interface ISmsService {
    void onCreate();

    void onDestroy();

    int onStartCommand(Intent intent, int flags, int startId);

    @Nullable
    IBinder onBind(Intent intent);
}
