package com.maciejwozny.firefighter.Model.Communication;

import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by maciek on 20.07.17.
 */
public class DataSender implements IDataSender {
    private Integer senderCount = 0;
    private static final String TAG = "DataSender";
    private BlockingDeque queue = new LinkedBlockingDeque();
    private ConnectionFactory factory = new ConnectionFactory();
    private Thread publishThread;

    public DataSender() {
        setupConnectionFactory();
    }

    @Override
    public void sendActionResponse(Participation participation) throws JSONException {
        final String userName = "loginName";
        long dateTime = new Date().getTime();
        String nowTime = ISODateTimeFormat.dateTime().print(dateTime);

        JSONObject json = new JSONObject();
        JSONObject response = new JSONObject();

        response.put("time", dateTime);
        response.put("time (human readable)", nowTime);
        response.put("user", userName);
        response.put("participation", participation);

        json.put("response", response);

        sendResponse(json.toString());
    }


    @Override
    public void sendResponse() {
        String nowTime = ISODateTimeFormat.hourMinuteSecondMillis().print(new Date().getTime());
        senderCount++;
        sendResponse("[send-" + nowTime + "]: test " + senderCount.toString());
    }

    private void sendResponse(String txt) {
        publishMessage(txt);
        publishToAMQP();
    }

    private void publishMessage(String message) {
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
            Log.v(TAG,"setAutomaticRecoveryEnabled");
            factory.setAutomaticRecoveryEnabled(false);
            Log.v(TAG,"setUri("+uri+")");
            factory.setUri(uri);
            Log.v(TAG,"DONE!");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
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
                                ch.basicPublish("amq.fanout", "hello", null, message.getBytes());
                                Log.d(TAG, "[send] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                Log.w(TAG, "[fail] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.w(TAG, "Connection broken: " + e.getClass().getName()
                                + ", " + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            Log.w(TAG, "Sleep broken: " + e.getClass().getName() + ", " + e.getMessage());
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }
}
