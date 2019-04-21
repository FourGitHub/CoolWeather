package com.fourweather.learn.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.fourweather.learn.View.APP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    /**
     * Android原生方式发起网络请求，请求方式 : GET
     *
     * @param address  请求地址
     * @param listener 自定义的回调接口，用于向主线程返回请求结果
     */
    public static void sendHttpUrlConnectionResquest(final String address, final HttpCallbackListener listener) {

        if (!HttpUtil.isNetworkEnable(APP.getContext())) {
           listener.onFailure(new Exception("Network state Exception"));
            return;
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setConnectTimeout(8 * 1000);
                connection.setReadTimeout(8 * 1000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    is = connection.getInputStream();
                    os = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len ;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    String response = os.toString();
                    if (listener != null) {
                        listener.onResponse(response);
                        Log.i(TAG, "-->>response:" + response);
                    }
                }

            } catch (Exception e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * @return 发起网络请求之前检查网络状态是否可用
     */
    public static boolean isNetworkEnable(Context context) {
        boolean isNetworkEnable = false;
        int checkTimes = 5;
        ConnectivityManager manager = null;
        while (checkTimes-- > 0 && manager == null) {
            manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            isNetworkEnable = networkInfo != null && networkInfo.isAvailable();
        }
        return isNetworkEnable;
    }
}
