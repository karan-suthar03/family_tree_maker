package com.someone.familytree.connection;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.gson.Gson;
import com.someone.familytree.database.User;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserConnection {
    private static UserConnection instance = null;
    private String url;
    private String UserUrl = "api/user";
    private OkHttpClient client;


    private UserConnection(String url, OkHttpClient client) {
        this.url = url;
        this.client = client;
    }

    public static UserConnection getInstance(String url, OkHttpClient client) {
        if (instance == null) {
            instance = new UserConnection(url, client);
        }
        return instance;
    }

    public Task<Object> createUserWithEmailAndPassword(String email, String password) {

        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();

        Gson gson = new Gson();

        HashMap<String, String> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);

        RequestBody formBody = new FormBody.Builder()
                .add("user", gson.toJson(user))
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.23.21:8080/api/user/create")
                .post(formBody)
                .addHeader("Content-Type", "application/json")
                .build();


        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserConnection", "Failed to create user", e);
                taskCompletionSource.setException(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    taskCompletionSource.setResult(null);
                } else {
                    taskCompletionSource.setException(new Exception("Failed to create user"));
                }
            }
        });

        return taskCompletionSource.getTask();
    }
}
