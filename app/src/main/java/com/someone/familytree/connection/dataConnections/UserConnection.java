package com.someone.familytree.connection.dataConnections;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.gson.Gson;
import com.someone.familytree.connection.services.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    private String userUrl = "api/user/";
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
                .url(url+ userUrl +"create")
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

            class res{
                String Uid;

                public String getUid() {
                    return Uid;
                }
                public void setUid(String uid) {
                    Uid = uid;
                }
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                if (r.isSuccessful()) {
                    assert r.body() != null;
                    res responseObj = gson.fromJson(r.body().string(), res.class);
                    UserService.setUser(user, responseObj.getUid());
                    taskCompletionSource.setResult(responseObj.getUid());
                } else {
                    taskCompletionSource.setException(new Exception("Failed to create user"));
                }
            }
        });

        return taskCompletionSource.getTask();
    }

    public Task<Object> signInUserWithEmailAndPassword(String email, String password) {
        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();

        Gson gson = new Gson();

        HashMap<String, String> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);

        RequestBody formBody = new FormBody.Builder()
                .add("user", gson.toJson(user))
                .build();

        Request request = new Request.Builder()
                .url(url+ userUrl+"signin")
                .post(formBody)
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserConnection", "Failed to sign in user", e);
                taskCompletionSource.setException(e);
            }

            class res{
                String Uid;

                public String getUid() {
                    return Uid;
                }
                public void setUid(String uid) {
                    Uid = uid;
                }
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                if (r.isSuccessful()) {
                    assert r.body() != null;
                    res responseObj = gson.fromJson(r.body().string(), res.class);
                    UserService.setUser(user, responseObj.getUid());
                    taskCompletionSource.setResult(responseObj.getUid());
                } else {
                    taskCompletionSource.setException(new Exception("Failed to sign in user"));
                }
                r.close();
            }
        });

        return taskCompletionSource.getTask();
    }
}
