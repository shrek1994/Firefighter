package com.maciejwozny.firefighter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by maciek on 20.07.17.
 */
public class DataReceiver {
    private ConnectionFactory factory = new ConnectionFactory();
    Thread subscribeThread;


    private static final String TAG = "DataReceiver";

    private void setupConnectionFactory() {
        String uri = "amqp://156.17.134.59";
        try {
            Log.d(TAG,"setAutomaticRecoveryEnabled");
            factory.setAutomaticRecoveryEnabled(false);
            Log.d(TAG,"setUri("+uri+")");
            factory.setUri(uri);
            Log.d(TAG,"DONE!");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    void subscribe(final Handler handler)
    {
        setupConnectionFactory();
    }
}
