package com.kingja.power.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kingja.power.util.Constants;

public class App extends Application {
    private static Context mAppContext;
    public static String service_uuid="0000fff0-0000-1000-8000-00805f9b34fb";
    public static String write_uuid;
    public static String read_uuid;
    private static SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        mSharedPreferences = getSharedPreferences(Constants.APPLICATION_NAME,
                MODE_PRIVATE);
    }

    public static Context getContext() {
        return mAppContext;
    }
    public static SharedPreferences getSP() {
        return mSharedPreferences;
    }

}
