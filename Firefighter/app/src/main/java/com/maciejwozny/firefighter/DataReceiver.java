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
            Log.v(TAG,"setAutomaticRecoveryEnabled");
            factory.setAutomaticRecoveryEnabled(false);
            Log.v(TAG,"setUri("+uri+")");
            factory.setUri(uri);
            Log.v(TAG,"DONE!");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    void subscribe(final Handler handler)
    {
        setupConnectionFactory();
        if (subscribeThread != null) {
            subscribeThread.interrupt();
        }
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
                            Log.d(TAG,"[recv] " + message);
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (InterruptedException e) {
                        sleep(5000);
                        break;
                    } catch (Exception e) {
                        Log.w(TAG, "Connection broken: " + e.getClass().getName());
                        e.printStackTrace();
                        sleep(5000); //sleep and then try again
                    }
                }
            }
        });
        subscribeThread.start();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
