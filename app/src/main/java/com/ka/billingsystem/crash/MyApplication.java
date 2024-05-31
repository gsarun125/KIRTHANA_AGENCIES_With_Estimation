package com.ka.billingsystem.crash;

import android.app.Application;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();

        // Set the uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
    }
}
