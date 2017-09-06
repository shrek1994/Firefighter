package com.maciejwozny.firefighter.Model.Communication;

import org.json.JSONException;

/**
 * Created by Mateusz on 29.08.2017.
 */

public interface IDataSender {
    void sendActionResponse(Participation participation) throws JSONException;
    void sendResponse();
}
