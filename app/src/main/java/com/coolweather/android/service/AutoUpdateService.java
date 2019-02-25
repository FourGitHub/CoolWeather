package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.android.View.APP;
import com.coolweather.android.entity.CityWeaInfo;
import com.coolweather.android.entity.WeatherEntity;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.HttpWeatherEntity;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
        Thread update = new Thread(() -> {
            updateWeather();
            updateBingPic();
        });
        update.start();
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
        List<CityWeaInfo> cityWeaInfos = APP.getDaoSession().loadAll(CityWeaInfo.class);
        // 这里没有检查网络是否可用，我怕从doze机制转换过来的时候，网络不及时导致更新失败
        if (cityWeaInfos.size() > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < cityWeaInfos.size(); i++) {
                String cid = cityWeaInfos.get(i).getCid();
                Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeatherEntity weatherEntity) {
                        if (weatherEntity.getHeWeather6()
                                         .get(0)
                                         .getStatus()
                                         .toLowerCase()
                                         .equals("ok")) {
                            String weatherInfoJSON = gson.toJson(weatherEntity);
                            String mCid = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
                            CityWeaInfo weather = new CityWeaInfo(null, mCid, weatherInfoJSON);
                            APP.getDaoSession().insertOrReplace(weather);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                };
                HttpWeatherEntity.getInstance().getWeatherEntity(observer, cid);
            }

        }
    }


    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        if (HttpUtil.isNetworkEnable(getApplicationContext()))
            HttpUtil.sendHttpUrlConnectionResquest(requestBingPic, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final String response) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic_url", response);
                    editor.apply();
                }
            });
    }

}
