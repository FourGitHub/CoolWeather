package com.coolweather.android.utils;

import android.util.Log;
import android.widget.GridLayout;

import com.bumptech.glide.util.LogTime;
import com.coolweather.android.gson.SearchedCities;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description
 */
public class HttpSearchCity {
    /**
     * https://search.heweather.net/find?location=重庆&group=cn&key=e70302717c5d493499c49dd2ee6b8de2&number=20
     */
    private static final String TAG = "HttpSearchCity";
    private static final String BASE_URL = "https://search.heweather.net/";
    private static final int DEFAULT_TIMEOUT = 5;
    private static final String GROUP = "cn";
    private static final String KEY = "e70302717c5d493499c49dd2ee6b8de2";

    private Retrofit mRetrofit;
    private SearchCityService mService;

    private HttpSearchCity() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder()
                .client(clientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mService = mRetrofit.create(SearchCityService.class);
    }

    private static class HttpSearchCityHolder {
        private static final HttpSearchCity singleton = new HttpSearchCity();
    }

    public static HttpSearchCity getInstance() {
        return HttpSearchCityHolder.singleton;
    }

    public void getSearchedCity(Observer<SearchedCities> observer, String location, int number) {
        mService.getSearchedCity(location, KEY, GROUP, number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        Log.i(TAG, "getSearchedCity: --->> url = " + BASE_URL + "find?" + "location="+location+"&key="+KEY+"&group="+GROUP+"&number="+number);
    }

}
