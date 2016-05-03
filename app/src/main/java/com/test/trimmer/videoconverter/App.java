package com.test.trimmer.videoconverter;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by mark on 30.04.16.
 */
public class App extends Application {
    private static App mThis;

    @Override
    public void onCreate() {
        super.onCreate();
        mThis = this;
        FfmpegUtils.initFFMPEG();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static App getInstance() {
        return mThis;
    }
}
