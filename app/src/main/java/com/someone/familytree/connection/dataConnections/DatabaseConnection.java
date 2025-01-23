package com.someone.familytree.connection.dataConnections;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.someone.familytree.connection.Authentication.Authentication;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DatabaseConnection {

    private static DatabaseConnection instance = null;
    private static String url;
    private static String TreeUrl = "api/tree/";
    private static OkHttpClient client;

    public DatabaseConnection(String url, OkHttpClient client) {
        DatabaseConnection.url = url;
        DatabaseConnection.client = client;
    }

    public static DatabaseConnection getInstance(String url, OkHttpClient client) {
        if (instance == null) {
            instance = new DatabaseConnection(url, client);
        }
        return instance;
    }

    public Task<Object> getAllTrees() {
        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();

        String myUid = Authentication.getInstance().getCurrentUser().getUid();
        Log.d("DatabaseConnection", "getAllTrees: myUid: " + myUid);

        Request request = new Request.Builder()
                .url(url + TreeUrl+"all"+"?uid="+myUid)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        Log.d("DatabaseConnection", "getAllTrees: request: " + request);

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                taskCompletionSource.setException(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    taskCompletionSource.setResult(response.body().string());
                } else {
                    taskCompletionSource.setException(new Exception("Failed to get trees"));
                }
            }
        });
        return taskCompletionSource.getTask();
    }
}
