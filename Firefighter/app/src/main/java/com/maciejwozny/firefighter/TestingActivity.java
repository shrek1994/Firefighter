package com.maciejwozny.firefighter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.joda.time.format.ISODateTimeFormat;
import java.util.Date;

public class TestingActivity extends AppCompatActivity {
    static final String TAG = "TestingActivity" ;
    private DataReceiver dataReceiver = new DataReceiver();
    private DataSender dataSender = new DataSender();

    TextView textView;

    public void test(View v) {
        Intent intent = new Intent(this, FireAlarmActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);

        requestPermission(Manifest.permission.RECEIVE_SMS);
        requestPermission(Manifest.permission.BROADCAST_SMS);
        requestPermission(Manifest.permission.INTERNET);
        requestPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);

        Intent smsService = new Intent(this, SmsService.class);
        startService(smsService);

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
                String now = ISODateTimeFormat.hourMinuteSecondMillis().print(new Date().getTime());
                Log.d("handleMessage",
                        "[recv] "  + now + ' ' + message);
                textView.setText(textView.getText()+ "[recv-" + now +"]" + message + "\n");
                Toast.makeText(TestingActivity.this,
                        now + ' ' + message,
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
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        Log.v(TAG, "ActivityCompat.requestPermissions");
                        ActivityCompat.requestPermissions(this,
                                new String[]{permission},
                                123);
            }
        }
    }

    public void initFireAlarmActivity(View viev) {
        Context context = viev.getContext();
        Intent newIntent = new Intent(context, FireAlarmActivity.class);
        context.startActivity(newIntent);
    }

}
