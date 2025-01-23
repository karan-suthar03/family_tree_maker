package com.someone.familytree.connection.Authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.someone.familytree.MyApplicationContextHelper;
import com.someone.familytree.connection.services.UserService;

import java.util.HashMap;

public class CurrentUser {
    private static CurrentUser instance = null;
    private CurrentUser() {

    }

    static CurrentUser getInstance() {
        if (instance == null) {
            boolean isLoggedIn = UserService.isLoggedIn();
            if (isLoggedIn) {
                instance = new CurrentUser();
            }
        }
        return instance;
    }

    void logout() {
        UserService.logout();
        instance = null;
    }

    public String getUid() {
        if (instance == null) {
            return null;
        }
        return UserService.getUid();
    }
}
