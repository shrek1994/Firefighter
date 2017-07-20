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
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        AMQP.Queue.DeclareOk q = channel.queueDeclare();
                        channel.queueBind(q.getQueue(), "amq.fanout", "hello");
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(q.getQueue(), true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Log.d(TAG,"[r] " + message);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.d(TAG, "Connection broken: " + e1.getClass().getName());
                        e1.printStackTrace();
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }
}
