package com.maciejwozny.firefighter;

/**
 * Created by maciek on 25.07.17.
 */

public class Config {

    //TODO czy singleton to dobry pomysl?

    private static final Config ourInstance = new Config();

    public static Config getInstance() {
        return ourInstance;
    }

    private Config() {}

    boolean playSoundOnAlarm = true;

}
