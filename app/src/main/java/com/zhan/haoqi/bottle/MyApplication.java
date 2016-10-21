package com.zhan.haoqi.bottle;

import android.app.Application;

import com.zhan.haoqi.bottle.err.CrashApplication;

/**
 * Created by zah on 2016/10/20.
 */
public class MyApplication extends Application {

    public static  MyApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        CrashApplication.getInstance(application).onCreate();
    }
}
