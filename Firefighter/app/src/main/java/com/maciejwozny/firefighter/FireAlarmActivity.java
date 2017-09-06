package com.maciejwozny.firefighter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.maciejwozny.firefighter.Model.Communication.DataSender;
import com.maciejwozny.firefighter.Model.Communication.Participation;

import org.json.JSONException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FireAlarmActivity extends Activity {
    private static final String TAG = "FireAlarmActivity";
    private MediaPlayer mediaPlayer;
    private AlertDialog alert;

    @Override
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fire_alarm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
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
            mediaPlayer.start();
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
                        FireAlarmActivity.this.finish();
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
                        FireAlarmActivity.this.finish();
                    }
                });

        if (alert == null || ! alert.isShowing()) {
            alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
            alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            Log.d(TAG, "alert.show()");
        }
    }


}
