package com.coolweather.android.utils;

/**
 * Created by Administrator on 2018/3/3 0003.
 */

public interface HttpCallbackListener {

    void onFailure(Exception e);
    void onResponse(final String response);
}
