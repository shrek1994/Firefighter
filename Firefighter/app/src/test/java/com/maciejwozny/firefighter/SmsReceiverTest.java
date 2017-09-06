package com.maciejwozny.firefighter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.maciejwozny.firefighter.Model.Sms.ISmsReceiver;
import com.maciejwozny.firefighter.Model.Sms.SmsReceiver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;

import static org.mockito.Mockito.*;

/**
 * Created by maciek on 25.07.17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SmsMessage.class, SmsReceiver.class, Toast.class})
public class SmsReceiverTest {
    private final String format = "extraFormat";
    private final String smsBody = "hello world!";
    private final String smsAddress = "+48123456789";
    private final byte[] decodedPDU =
            new BigInteger("1100038155f50000aa0ae8329bfdbebfe56c32", 16).toByteArray();
    private final Object[] pdus = new Object[]{decodedPDU};

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock Service service;
    @Mock Context context;
    @Mock Intent smsIntent;
    @Mock Bundle bundle;
    @Mock SmsMessage smsMessage;
    @Mock Toast toast;
    @Mock Intent nextIntent;

    private ISmsReceiver receiveSmsBroadcastReceiver;

    @Before
    public void before() {
        receiveSmsBroadcastReceiver = new SmsReceiver(service);
    }

    @Test
    public void shouldStartFireAlarmActivityWhenReceiveCorrectSms() throws Exception {

        when(smsIntent.getStringExtra("format")).thenReturn(format);
        when(smsIntent.getExtras()).thenReturn(bundle);

        when(bundle.get("pdus")).thenReturn(pdus);

        PowerMockito.mockStatic(SmsMessage.class);
        PowerMockito.when(SmsMessage.createFromPdu((byte[])pdus[0])).thenReturn(smsMessage);

        when(smsMessage.getDisplayMessageBody()).thenReturn(smsBody);
        when(smsMessage.getDisplayOriginatingAddress()).thenReturn(smsAddress);

        PowerMockito.whenNew(Intent.class).withArguments(service, FireAlarmActivity.class).thenReturn(nextIntent);

        receiveSmsBroadcastReceiver.onReceive(context, smsIntent);

        verify(nextIntent).putExtra("MESSAGE_EXTRA", smsBody);
        verify(service).startActivity(nextIntent);

    }

}