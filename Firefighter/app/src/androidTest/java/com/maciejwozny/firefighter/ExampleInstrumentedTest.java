package com.maciejwozny.firefighter;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.io.BaseEncoding;
import android.support.test.runner.AndroidJUnit4;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String FORMAT_3GPP = "3gpp"; // TODO change to SmsMessage.FORMAT_3GPP
    public static final String SUBSCRIPTION_KEY  = "subscription"; //TODO change with PhoneConstants.SUBSCRIPTION_KEY

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();


        assertEquals("com.maciejwozny.firefighter", appContext.getPackageName());
    }

    @Test
    public void test() throws Exception {

        final byte[] decodedPDU =
                new BigInteger("1100038155f50000aa0ae8329bfdbebfe56c32", 16).toByteArray();
        final SmsReceiver receiveSmsBroadcastReceiver = new SmsReceiver();
        final Intent intent = new Intent();
        intent.putExtra("format", FORMAT_3GPP);
        intent.putExtra("pdus", new Object[]{decodedPDU});
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra(SUBSCRIPTION_KEY, 1);
        receiveSmsBroadcastReceiver.onReceive(InstrumentationRegistry.getTargetContext(), intent);
    }
}
