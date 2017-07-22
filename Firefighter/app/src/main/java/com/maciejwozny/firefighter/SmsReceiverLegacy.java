package com.maciejwozny.firefighter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by maciek on 08.06.17.
 */

public class SmsReceiverLegacy extends BroadcastReceiver {
    private final static String TAG = "SmsReceiverLegacy";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received sms Legacy");
        Toast.makeText(context, "received sms Legacy", Toast.LENGTH_SHORT).show();
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: " + senderNum + ", message: " + message, duration);
                    toast.show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

}
