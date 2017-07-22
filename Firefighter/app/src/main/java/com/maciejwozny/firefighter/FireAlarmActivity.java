package com.maciejwozny.firefighter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONException;

import java.io.IOException;

public class FireAlarmActivity extends AppCompatActivity {
    private static final String TAG = "FireAlarmActivity";
    private MediaPlayer mediaPlayer;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_alarm);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        showAlert(this);
        playAlarmWithRingVolume(this);
    }

    private void playAlarmWithRingVolume(Context context) {
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
            mediaPlayer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
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
                    }
                });

        if (alert == null || ! alert.isShowing()) {
            alert = builder.create();
            alert.show();
            Log.d(TAG, "alert.show()");
        }
    }
}
