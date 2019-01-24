package com.coolweather.android.utils;

import com.coolweather.android.gson.MovieEntity;

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
public class HttpWeatherMethod {
    private static final String BASE_URL = "https://api.douban.com/v2/movie/";
    private static final int DEFAULT_TIMEOUT = 5;

    private MovieService movieService;
    private Retrofit mRetrofit;

    private HttpWeatherMethod() {
        OkHttpClient.Builder clientBiulder = new OkHttpClient.Builder();
        clientBiulder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder().client(clientBiulder.build())
                                          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                          .addConverterFactory(GsonConverterFactory.create())
                                          .baseUrl(BASE_URL)
                                          .build();
        movieService = mRetrofit.create(MovieService.class);
    }

    private static class HttpWeaMetHolder {
        private static final HttpWeatherMethod singleton = new HttpWeatherMethod();
    }

    public static HttpWeatherMethod getHttpWeatherMethod() {
        return HttpWeaMetHolder.singleton;
    }

    public void getTopMovie(Observer<MovieEntity> observer, int start, int count) {
        movieService.getTopMovie(start, count)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
    }


}
