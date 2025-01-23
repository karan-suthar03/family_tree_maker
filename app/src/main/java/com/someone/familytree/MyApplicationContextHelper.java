package com.someone.familytree;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.someone.familytree.connection.services.UserService;

public class MyApplicationContextHelper extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        // Ensure the context is initialized on app start
        Log.d("MyApplicationContextHelper", "onCreate");
        UserService.initializeSharedPreferences(getApplicationContext());
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    public static void setContext(Context applicationContext) {
        // Only set the context once to avoid re-setting
        if (instance == null) {
            instance = (Application) applicationContext;
        }
    }

    public Context getAppContext() {
        return instance.getApplicationContext();
    }
}
