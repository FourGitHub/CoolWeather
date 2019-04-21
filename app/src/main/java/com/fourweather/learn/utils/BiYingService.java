package com.fourweather.learn.utils;

import com.fourweather.learn.entity.BiYingEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Create on 2019/04/12
 *
 * @author Four
 * @description
 */
public interface BiYingService {
    /**
     * http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1
     */
    @GET("HPImageArchive.aspx")
    Observable<BiYingEntity> getBiYingPicInfo(@Query("format") String format, @Query("idx") int idx, @Query("n") int n);
}
