package com.someone.familytree.TreeMenu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public interface NetworkCallback {
        void onResult(boolean isOnline);
    }

    public static void checkAppOnline(Context context, NetworkCallback callback) {
        if (!isNetworkAvailable(context)) {
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(false));
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("https://www.google.com");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                connection.disconnect();
                
                boolean isOnline = responseCode == HttpURLConnection.HTTP_OK;
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(isOnline));
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onResult(false));
            }
        }).start();
    }
} 