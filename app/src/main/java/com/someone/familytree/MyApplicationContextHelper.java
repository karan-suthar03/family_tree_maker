package com.someone.familytree;

import android.app.Application;

public class MyApplicationContextHelper extends Application {
    private static MyApplicationContextHelper instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplicationContextHelper getInstance() {
        return instance;
    }

    public android.content.Context getAppContext() {
        return getApplicationContext();
    }
}
