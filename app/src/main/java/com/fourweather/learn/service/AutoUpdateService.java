package com.fourweather.learn.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fourweather.learn.View.APP;
import com.fourweather.learn.entity.BiYingEntity;
import com.fourweather.learn.entity.CityWeaInfo;
import com.fourweather.learn.entity.WeatherEntity;
import com.fourweather.learn.utils.HttpBiYingService;
import com.fourweather.learn.utils.HttpUtil;
import com.fourweather.learn.utils.HttpWeatherEntity;
import com.google.gson.Gson;

import org.litepal.LitePal;

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

    private static final String TAG = "AutoUpdateService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread update = new Thread(() -> {
            updateBingPic();
            updateWeather();
        });

        update.start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int oneHour = 60 * 60 * 1000; // 这是1小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + oneHour;
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


        /* GreenDao 版本
           List<CityWeaInfo> cityWeaInfos = APP.getDaoSession().loadAll(CityWeaInfo.class);
         */
        List<CityWeaInfo> cityWeaInfos = LitePal.findAll(CityWeaInfo.class);

        // 这里我一直在考虑是否有必要检查网络是否可用，我怕从doze机制转换过来的时候，网络不及时导致更新失败
        if (cityWeaInfos.size() > 0 && HttpUtil.isNetworkEnable(APP.getContext())) {

            for (int i = 0; i < cityWeaInfos.size(); i++) {

                String cid = cityWeaInfos.get(i).getCid();
                Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                    Gson gson = new Gson();

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WeatherEntity weatherEntity) {
                        if (weatherEntity.getHeWeather6().get(0).getStatus().toLowerCase().equals("ok")) {
                            String weatherInfoJSON = gson.toJson(weatherEntity);
                            String mCid = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
                            CityWeaInfo weather = new CityWeaInfo(null, mCid, weatherInfoJSON);
                            /* GreenDao 版本
                               APP.getDaoSession().insertOrReplace(weather);
                             */
                            weather.saveOrUpdate("cid like ?", mCid);

                            Log.i(TAG, "onNext: update cid = " + mCid);
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

        if (HttpUtil.isNetworkEnable(getApplicationContext())) {
            Observer<BiYingEntity> observer = new Observer<BiYingEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BiYingEntity biYingEntity) {
                    // 接口只返回了后面的路径、前面的协议和主机需要加上去组合成一个完整的Url
                    // String bingPicUr = ("http://cn.bing.com" + biYingEntity.getImages().get(0).getUrl()).replace("1920x1080","1920x1200");
                    String bingPicUr = "http://cn.bing.com" + biYingEntity.getImages().get(0).getUrl();
                    // 请求成功，则把图片链接保存起来，备用
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(APP.getContext()).edit();
                    editor.putString("bing_pic_url", bingPicUr);
                    editor.apply();
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            };
            HttpBiYingService.getInstance().getBiYingPicInfo(observer);

        }
        /* 必应图片，郭霖版接口
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
         */

    }

    //    static class UpdateTask extends AsyncTask<Void, Void, Boolean> {
    //
    //        UpdateTask()
    //
    //        @Override
    //        protected Boolean doInBackground(Void... voids) {
    //            return null;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(Boolean aBoolean) {
    //            super.onPostExecute(aBoolean);
    //        }
    //    }


}
