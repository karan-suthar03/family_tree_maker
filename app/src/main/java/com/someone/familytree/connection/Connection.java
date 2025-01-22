package com.someone.familytree.connection;

import okhttp3.OkHttpClient;

public class Connection {
    private static Connection instance = null;
    private String url = "https://192.168.23.21:8080";
    private static OkHttpClient client = new OkHttpClient();
    public UserConnection userConnection;

    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    public UserConnection userConnection() {
        if (userConnection == null) {
            userConnection = UserConnection.getInstance(url, client);
        }
        return userConnection;
    }
}
