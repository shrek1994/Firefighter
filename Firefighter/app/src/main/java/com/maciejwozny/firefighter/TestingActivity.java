package com.maciejwozny.firefighter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maciejwozny.firefighter.Model.Communication.MessageManager;
import com.maciejwozny.firefighter.Model.Sms.SmsService;
import com.maciejwozny.firefighter.ViewModel.ITestActivityContract;
import com.maciejwozny.firefighter.databinding.ActivityMainBinding;

public class TestingActivity extends AppCompatActivity implements ITestActivityContract.View {
    private final String TAG  = "TAG";

    public void test(View v) {
        Intent intent = new Intent(this, FireAlarmActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MessageManager messageManager = new MessageManager(this, getApplicationContext());
        binding.setMessageManager(messageManager);

        requestPermission(Manifest.permission.RECEIVE_SMS);
        requestPermission(Manifest.permission.BROADCAST_SMS);
        requestPermission(Manifest.permission.INTERNET);
        requestPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);

        Intent smsService = new Intent(this, SmsService.class);
        startService(smsService);

        messageManager.startReceiving();
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

    @Override
    public void StartReceiving(String currentMessage) {
        Toast.makeText(this, currentMessage, Toast.LENGTH_LONG).show();
    }
}
