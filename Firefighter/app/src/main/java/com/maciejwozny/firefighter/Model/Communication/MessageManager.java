package com.maciejwozny.firefighter.Model.Communication;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.maciejwozny.firefighter.BR;
import com.maciejwozny.firefighter.ViewModel.ITestActivityContract;

import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;


/**
 * Created by Mateusz on 29.08.2017.
 */

public class MessageManager extends BaseObservable implements IMessageManager, ITestActivityContract.Presenter {
    private String currentMessage;
    private IDataReceiver dataReceiver = new DataReceiver();
    private IDataSender dataSender = new DataSender();
    static final String TAG = "TestingActivity" ;
    private Context context;
    private ITestActivityContract.View testActivityView;

    public MessageManager(ITestActivityContract.View view, Context context)
    {
        this.context = context;
        this.testActivityView = view;
    }

    @Bindable
    public String getCurrentMessage()
    {
        return currentMessage;
    }

    @Override
    public void startReceiving() {
        Log.v(TAG, "startReceiving");
        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                String now = ISODateTimeFormat.hourMinuteSecondMillis().print(new Date().getTime());
                Log.d("handleMessage",
                        "[recv] "  + now + ' ' + message);
                currentMessage += "[recv-" + now +"]" + message + "\n";
                notifyPropertyChanged(BR.currentMessage);
                testActivityView.StartReceiving(currentMessage);
            }
        };
        dataReceiver.subscribe(incomingMessageHandler);
    }

    @Override
    public void sendResponse()
    {
        dataSender.sendResponse();
    }

    public void clear()
    {
        currentMessage = "";
        notifyPropertyChanged(BR.currentMessage);
    }

    public void stopReceiving()
    {
        dataReceiver.interrupt();
    }
}
