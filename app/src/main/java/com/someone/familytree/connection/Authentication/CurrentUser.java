package com.someone.familytree.connection.Authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.someone.familytree.MyApplicationContextHelper;

public class CurrentUser {
    private static CurrentUser instance = null;
    static SharedPreferences sharedPreferences = null;
    private CurrentUser() {

    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            sharedPreferences = MyApplicationContextHelper.getInstance().getAppContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                instance = new CurrentUser();
            }
        }
        return instance;
    }

    public void login(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }
}
