package com.someone.familytree.connection.Authentication;

import com.google.android.gms.tasks.Task;
import com.someone.familytree.connection.Connection;

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
}
