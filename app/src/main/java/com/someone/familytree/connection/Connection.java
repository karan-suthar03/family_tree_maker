package com.someone.familytree.connection;

import com.google.android.gms.tasks.Task;
import com.someone.familytree.connection.dataConnections.DatabaseConnection;
import com.someone.familytree.connection.dataConnections.UserConnection;

import okhttp3.OkHttpClient;

public class Connection {
    private static Connection instance = null;
    private static final String url = "https://19e1-152-58-17-93.ngrok-free.app/";
    private static OkHttpClient client = new OkHttpClient();
    private static UserConnection userConnection;
    private static DatabaseConnection databaseConnection;


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

    public DatabaseConnection databaseConnection() {
        if (databaseConnection == null) {
            databaseConnection = DatabaseConnection.getInstance(url, client);
        }
        return databaseConnection;
    }

    public Task<Object> getAllTrees() {
        return databaseConnection().getAllTrees();
    }

    public Task<Object> updateAllTreesOnServer(String jsonToSend) {
        return databaseConnection().updateAllTreesOnServer(jsonToSend);
    }

    public Task<Object> updateMetaData() {
        return databaseConnection().updateMetaData();
    }
}
