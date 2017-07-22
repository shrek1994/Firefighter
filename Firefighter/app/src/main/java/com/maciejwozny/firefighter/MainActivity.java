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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
        requestPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);

        Intent intent = new Intent(this, SmsService.class);
        startService(intent);

//        showAlert(this);
//        Log.d(TAG, "sendNotification");
//        sendNotification();

        startReceiving(null);

        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.recevingButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startReceiving(buttonView);
                } else {
                    stopReceiving(buttonView);
                }
            }
        });
    }

    public void sendData(View v) {
        dataSender.sendResponse();
    }

    public void startReceiving(View v) {
        Log.v(TAG, "startReceiving");
        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                Date now = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss.SSS aa");
                Log.d("handleMessage", "[r] " + ft.format(now) + ' ' + message);
                textView.setText(textView.getText()+ "[r-" + ft.format(now) +"]"+message + "\n");
                Toast.makeText(MainActivity.this,
                        ft.format(now) + ' ' + message,
                        Toast.LENGTH_LONG).show();
            }
        };
        dataReceiver.subscribe(incomingMessageHandler);
    }

    public void stopReceiving(View v) {
        Log.v(TAG, "stopReceiving");
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

            Log.v(TAG, "ActivityCompat.shouldShowRequestPermissionRationale");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

            } else {
                Log.v(TAG, "ActivityCompat.requestPermissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        123);
            }
        }
    }

    public static void sendNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text);
        Intent resultIntent = new Intent(context, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
        mNotificationManager.notify(1, mBuilder.build());
    }

}
