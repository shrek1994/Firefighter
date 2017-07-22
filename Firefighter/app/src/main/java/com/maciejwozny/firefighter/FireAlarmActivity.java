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

import java.io.IOException;

public class FireAlarmActivity extends AppCompatActivity {
    private static final String TAG = "FireAlarmActivity";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_alarm);
        playAlarmWithRingVolume(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAlert(this);
    }

    private void playAlarm(Context context) {
        Log.d(TAG, "play Alarm");
        mediaPlayer = MediaPlayer.create(context, R.raw.fire);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void playAlarmWithRingVolume(Context context) {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_NORMAL);
        MediaPlayer mp = new MediaPlayer();
        Uri ringtoneUri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.fire);
//        Uri ringtoneUri = RingtoneManager.getDefaultUri(R.raw.fire);
        try
        {
            mp.setDataSource(getApplicationContext(), ringtoneUri);
            mp.setAudioStreamType(AudioManager.STREAM_RING);
            mp.prepare();
            mp.start();
        }
        catch(Exception e)
        {
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
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
