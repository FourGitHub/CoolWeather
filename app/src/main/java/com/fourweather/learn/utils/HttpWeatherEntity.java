package com.fourweather.learn.utils;

import com.fourweather.learn.entity.WeatherEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create on 2019/01/25
 *
 * @author Four
 * @description
 */
public class HttpWeatherEntity {
    private static final String BASE_URL = "https://free-api.heweather.net/s6/";
    private static final String USER_NAME = "HE1802011110181104";
    private static final String SECRET = "e70302717c5d493499c49dd2ee6b8de2";
    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit mRetrofit;
    private WeatherEntityService mService;

    private HttpWeatherEntity() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder()
                .client(clientBuilder.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = mRetrofit.create(WeatherEntityService.class);
    }

    private static class HttpWeatherEntityHolder {
        private final static HttpWeatherEntity singleton = new HttpWeatherEntity();
    }

    public static HttpWeatherEntity getInstance() {
        return HttpWeatherEntityHolder.singleton;
    }

    public void getWeatherEntity(Observer<WeatherEntity> observer, String cid) {
        String t = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> params = new HashMap<>();
        params.put("location", cid);
        params.put("username", "HE1802011110181104");
        params.put("t", t);
        String sign = null;
        try {
            sign = SignatureUtil
                    .getSignature(params, HttpWeatherEntity.SECRET);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mService.getWeatherEntity(cid, USER_NAME, t, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
