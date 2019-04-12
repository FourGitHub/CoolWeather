package com.fourweather.learn.utils;

import com.fourweather.learn.entity.MovieEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description 练习Retrofit + RxJava + Gson用的, 与本项目逻辑无关！
 */
public interface MovieService {

    /**
     * @return https://api.douban.com/v2/movie/top250?start=0&count=10
     */
    @GET("top250")
    Observable<MovieEntity> getTopMovie(@Query("start") int start, @Query("count") int count);
}
