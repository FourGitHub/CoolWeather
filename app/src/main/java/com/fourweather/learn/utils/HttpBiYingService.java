package com.fourweather.learn.utils;

import com.fourweather.learn.entity.BiYingEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create on 2019/04/12
 *
 * @author Four
 * @description  获取必应图片信息的json
 */
public class HttpBiYingService {

    private static final String BASE_URL = " http://cn.bing.com/";
    private static final String DEFAULT_FORMAT = "js";
    private static final int DEFAULT_TIMEOUT = 5;
    private static final int DEFAULT_IDX = 0;
    private static final int DEFAULT_N = 1;

    private BiYingService biYingService;
    private Retrofit mRereofit;

    private HttpBiYingService() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRereofit = new Retrofit.Builder()
                .client(clientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        biYingService = mRereofit.create(BiYingService.class);
    }

    private static class ServiceHolder {
        private static final HttpBiYingService singleton = new HttpBiYingService();
    }

    public static HttpBiYingService getInstance(){
        return ServiceHolder.singleton;
    }

    public void getBiYingPicInfo(Observer<BiYingEntity> observer) {
        biYingService.getBiYingPicInfo(DEFAULT_FORMAT, DEFAULT_IDX, DEFAULT_N)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(observer);
    }
}
