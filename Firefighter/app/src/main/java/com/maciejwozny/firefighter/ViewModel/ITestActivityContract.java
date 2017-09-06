package com.maciejwozny.firefighter.ViewModel;

/**
 * Created by Mateusz on 29.08.2017.
 */

public interface ITestActivityContract {
    public interface Presenter {
        void startReceiving();
        void stopReceiving();
        void sendResponse();
        void clear();
    }
    public interface View {
        void StartReceiving(String currentMessage);
    }
}
