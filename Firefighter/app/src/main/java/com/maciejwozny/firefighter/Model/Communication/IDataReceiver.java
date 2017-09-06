package com.maciejwozny.firefighter.Model.Communication;

import android.os.Handler;

/**
 * Created by Mateusz on 29.08.2017.
 */

public interface IDataReceiver {
    void subscribe(final Handler handler);
    void interrupt();
}
