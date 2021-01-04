package com.dibomart.dibomart.app;

import android.app.Application;
import android.content.Context;

import com.onesignal.OneSignal;

public class ApplicationClass extends Application {

    ApplicationClass mInstance;
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
