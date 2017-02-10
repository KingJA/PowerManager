package com.kingja.power.base;

import android.app.Application;
import android.content.Context;
public class BaseApplication extends Application {
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();

    }

    public static Context getContext() {
        return mAppContext;
    }


}
