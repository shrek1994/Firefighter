package com.maciejwozny.firefighter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by maciek on 08.06.17.
 */

public class SmsReceiver extends BroadcastReceiver {
    final static String TAG = "SmsReceiver";

    public static void notify(
            Context context,
            int id,
            int titleResId,
            int textResId,
            PendingIntent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(titleResId);
        String text = context.getString(textResId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker(title)
                .setContentIntent(intent);
        notificationManager.notify(id, builder.build());
    }

    //==========================================================================


    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received sms");
//        Toast.makeText(context, "received sms", Toast.LENGTH_SHORT).show();
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                    // Show Alert
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: "+ senderNum + ", message: " + message, duration);
//                    toast.show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

}
