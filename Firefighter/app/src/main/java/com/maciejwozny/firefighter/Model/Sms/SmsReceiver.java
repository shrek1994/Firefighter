package com.maciejwozny.firefighter.Model.Sms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.maciejwozny.firefighter.FireAlarmActivity;
import com.maciejwozny.firefighter.R;
import com.maciejwozny.firefighter.TestingActivity;

/**
 * Created by maciek on 08.06.17.
 */
public class SmsReceiver extends BroadcastReceiver implements ISmsReceiver {
    private final static String TAG = "SmsReceiver";
    private Service service;

    public SmsReceiver(Service service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received sms");
        final Bundle bundle = intent.getExtras();
        String format = intent.getStringExtra("format");
        try {
            if (bundle != null) {
                final Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage currentMessage;
                    if (Build.VERSION.SDK_INT < 23) {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    } else {
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                    }
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

//                    Toast.makeText(service,
//                                   "senderNum: " + senderNum + ", message: " + message,
//                                   Toast.LENGTH_LONG).show();

//                    sendNotification(service, senderNum, message);

                    Intent fireAlarm = new Intent(service, FireAlarmActivity.class);
                    fireAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    fireAlarm.putExtra("MESSAGE_EXTRA", message);
                    service.startActivity(fireAlarm);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void sendNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(context, TestingActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TestingActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(1, mBuilder.build());
    }

}
