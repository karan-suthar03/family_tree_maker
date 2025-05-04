package com.someone.familytree.connection.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserService {
    public static SharedPreferences sharedPreferences = null;

    public static void initializeSharedPreferences(Context applicationContext) {
        if (sharedPreferences == null) {
            sharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        }
    }
    public static void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("username");
        editor.remove("password");
        editor.apply();
    }

    public static void setUser(HashMap<String, String> user, String Uid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.get("username"));
        editor.putString("password", user.get("password"));
        editor.putString("Uid", Uid);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public static String getUid() {
        return sharedPreferences.getString("Uid", null);
    }
}
