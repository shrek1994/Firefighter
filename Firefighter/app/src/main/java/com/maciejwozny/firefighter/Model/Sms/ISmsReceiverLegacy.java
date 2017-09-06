package com.maciejwozny.firefighter.Model.Sms;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Mateusz on 29.08.2017.
 */

public interface ISmsReceiverLegacy {
    void onReceive(Context context, Intent intent);
}
