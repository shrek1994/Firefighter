package com.maciejwozny.firefighter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;


public class FireAlarmActivityLegacy extends AppCompatActivity {
    private static final String TAG = "FireAlarmActivityLegacy";
    private MediaPlayer mediaPlayer;
    private AlertDialog alert;

    @Override
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_alarm);
        FlashingView flashingView = (FlashingView)findViewById(R.id.flashingView);
        flashingView.initFlashing();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        showAlert(this);
        playAlarmWithRingVolume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mediaPlayer.stop();
    }

    private void playAlarmWithRingVolume() {
        Log.d(TAG, "playAlarmWithRingVolume");
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_NORMAL);
        mediaPlayer = new MediaPlayer();
        Uri ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fire);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), ringtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
//            mediaPlayer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Context context) {
        Log.d(TAG, "show Alert");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.doYouJoinToAction);
        builder.setCancelable(true);
        builder.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        try {
                            new DataSender().sendActionResponse(Participation.yes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        FireAlarmActivityLegacy.this.finish();
                    }
                });

        builder.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        try {
                            new DataSender().sendActionResponse(Participation.no);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                        FireAlarmActivityLegacy.this.finish();
                    }
                });

        if (alert == null || ! alert.isShowing()) {
            alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
            Log.d(TAG, "alert.show()");
        }
    }
}
