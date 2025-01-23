package com.someone.familytree.connection.Authentication;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.someone.familytree.MyApplicationContextHelper;
import com.someone.familytree.connection.Connection;
import com.someone.familytree.connection.services.UserService;

public class Authentication {
    private static Authentication instance = null;

    private Authentication() {

    }

    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    public CurrentUser getCurrentUser() {
        return CurrentUser.getInstance();
    }

    public Task<Object> createUserWithEmailAndPassword(String email, String password) {
        return Connection.getInstance().userConnection().createUserWithEmailAndPassword(email, password);
    }

    public void logout() {
        if(CurrentUser.getInstance() != null) {
            CurrentUser.getInstance().logout();
        }
        UserService.logout();
    }

    public Task<Object> signInUserWithEmailAndPassword(String email, String password) {
        return Connection.getInstance().userConnection().signInUserWithEmailAndPassword(email, password);
    }
}
