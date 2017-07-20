package com.maciejwozny.firefighter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by maciek on 20.07.17.
 */

public class DataSender {
    private static final String TAG = "DataSender";
    private BlockingDeque queue = new LinkedBlockingDeque();
    private ConnectionFactory factory = new ConnectionFactory();


    public void sendResponse(Context context) {
        setupConnectionFactory();
        publishMessage("hello jakubie! trzecia proba!");
        publishToAMQP(context);
    }

    void publishMessage(String message) {
        try {
            Log.d(TAG,"[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    public void publishToAMQP(Context context) {
        Thread publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            String message = (String) queue.takeFirst();
                            try {
                                ch.basicPublish("", "hello", null, message.getBytes());
                                Log.d(TAG, "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                Log.d(TAG, "[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d(TAG, "Connection broken: " + e.getClass().getName()
                                + ", " + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            Log.d(TAG, "Sleep broken: " + e.getClass().getName() + ", " + e.getMessage());
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }
}