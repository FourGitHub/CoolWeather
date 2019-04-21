package com.fourweather.learn.utils;

import android.os.Handler;
import android.os.Message;

import com.fourweather.learn.View.WeatherActivity1;

import java.lang.ref.WeakReference;

/**
 * Create on 2019/04/14
 *
 * @author Four
 * @description
 */
public class  WeatherHandler extends Handler {
    private WeakReference<WeatherActivity1> weakWeaAc ;

    public WeatherHandler(WeatherActivity1 weatherActivity) {
        weakWeaAc = new WeakReference<>(weatherActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        final WeatherActivity1 weaAc = weakWeaAc.get();
        switch (msg.what) {
            case 0:

        }
    }
}
