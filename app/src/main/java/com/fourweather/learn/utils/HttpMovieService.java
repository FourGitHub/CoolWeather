package com.fourweather.learn.utils;

import com.fourweather.learn.entity.MovieEntity;

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
 * @description 练习Retrofit + RxJava + Gson用的, 与本项目逻辑无关！
 */
public class HttpMovieService {
    private static final String BASE_URL = "https://api.douban.com/v2/movie/";
    private static final int DEFAULT_TIMEOUT = 5;

    private MovieService movieService;
    private Retrofit mRetrofit;

    private HttpMovieService() {
        OkHttpClient.Builder clientBiulder = new OkHttpClient.Builder();
        clientBiulder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder().client(clientBiulder.build())
                                          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                          .addConverterFactory(GsonConverterFactory.create())
                                          .baseUrl(BASE_URL)
                                          .build();
        movieService = mRetrofit.create(MovieService.class);
    }

    private static class HttpMovieHolder {
        private static final HttpMovieService singleton = new HttpMovieService();
    }

    public static HttpMovieService getInstance() {
        return HttpMovieHolder.singleton;
    }

    public void getTopMovie(Observer<MovieEntity> observer, int start, int count) {
        movieService.getTopMovie(start, count)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
    }


}
