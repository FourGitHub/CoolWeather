package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.android.gson.MyWeatherBean;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.PCCUtil;

/**
 * 这是一个启动服务，没有和Activity绑定
 * 创建了定时任务，每隔两小时后台更新天气，其实这个更新天气并不是界面上的更新，而是缓存，用户下次刷新的时候，
 * 就直接从缓存里获取数据了，而不是联网更新！
 * 不知道定时任务算不算是service的一种保活方式！
 * Android4.4后，Alarm任务会变得不是很准确，
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
                updateBingPic();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int twoHours = 2 * 60 * 60 * 1000; // 这是2小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + twoHours;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);// 由于Doze机制，可以设置更精确一点！
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时,先从中获取weatherId,再构造请求Url,发送请求刷新天气数据
            MyWeatherBean weather = PCCUtil.handleWeatherResponse(weatherString);
            String weatherId = weather.getBasic().getCid();
            String weatherUrl = "https://free-api.heweather.net/s6/weather?" +
                    "location=" + weatherId + "&" +
                    "key=bc0418b57b2d4918819d3974ac1285d9" + "&" +
                    "lang=zh" + "&" +
                    "unit=m";

            HttpUtil.sendHttpUrlConnectionResquest(weatherUrl, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final String response) {
                    MyWeatherBean weather = PCCUtil.handleWeatherResponse(response);
                    if (weather != null && "ok".equals(weather.getStatus())) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", response);
                        editor.apply();
                    }
                }
            });

        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        if (HttpUtil.networkStatus(getApplicationContext()))
            HttpUtil.sendHttpUrlConnectionResquest(requestBingPic, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final String response) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic", response);
                    editor.apply();
                }
            });
    }

}
