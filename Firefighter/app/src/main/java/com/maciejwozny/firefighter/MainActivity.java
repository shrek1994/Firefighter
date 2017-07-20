package com.maciejwozny.firefighter;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity" ;
    private DataReceiver dataReceiver = new DataReceiver();
    private DataSender dataSender = new DataSender();

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);

        requestPermission(Manifest.permission.RECEIVE_SMS);
        requestPermission(Manifest.permission.BROADCAST_SMS);
        requestPermission(Manifest.permission.INTERNET);

        Intent intent = new Intent(this, SmsService.class);
        startService(intent);

//        showAlert();
//        Log.d(TAG, "sendNotification");
//        sendNotification();

    }

    public void sendData(View v) {
        dataSender.sendResponse();
    }

    public void startReceiving(View v) {
        Log.d(TAG, "startReceiving");
        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                Date now = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
                Log.d("handleMessage", "[r] " + ft.format(now) + ' ' + message);
                textView.setText(textView.getText() + message + "\n");
                Toast.makeText(MainActivity.this,
                        ft.format(now) + ' ' + message,
                        Toast.LENGTH_LONG).show();
            }
        };
        dataReceiver.subscribe(incomingMessageHandler);
    }

    public void stopReceiving(View v) {
        Log.d(TAG, "stopReceiving");
        dataReceiver.subscribeThread.interrupt();
    }

    public void clear(View v) {
        textView.setText("");
    }
    private void requestPermission(String permission) {
        Log.d(TAG, "ContextCompat.checkSelfPermission for " + permission);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "ActivityCompat.shouldShowRequestPermissionRationale");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

            } else {
                Log.d(TAG, "ActivityCompat.requestPermissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        123);
            }
        }
    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(001, mBuilder.build());
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bierzesz udział w akcji?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }
}
