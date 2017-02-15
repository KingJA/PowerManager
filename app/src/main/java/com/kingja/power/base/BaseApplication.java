package com.kingja.power.base;

import android.app.Application;
import android.content.Context;
public class BaseApplication extends Application {
    private static Context mAppContext;
    public static String service_uuid="0000fff0-0000-1000-8000-00805f9b34fb";
    public static String write_uuid;
    public static String read_uuid;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();

    }

    public static Context getContext() {
        return mAppContext;
    }


}
