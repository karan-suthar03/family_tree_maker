package com.someone.familytree.connection;

import com.google.android.gms.tasks.Task;

public class MyDatabase {
    private static MyDatabase instance;

    private MyDatabase() {

    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Task<Object> getAllTrees() {
        Connection connection = Connection.getInstance();
        return connection.getAllTrees();
    }

    public Task<Object> updateAllTreesOnServer(String jsonToSend) {
        Connection connection = Connection.getInstance();
        return connection.updateAllTreesOnServer(jsonToSend);
    }

    public Task<Object> updateMetaData() {
        Connection connection = Connection.getInstance();
        return connection.updateMetaData();
    }
}
