package com.coolweather.android.utils;

import com.coolweather.android.entity.WeatherEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Create on 2019/01/25
 *
 * @author Four
 * @description
 */
public interface WeatherEntityService {
    /**
     * https://free-api.heweather.net/s6/weather?location=CN101040100&username=HE1802011110181104&t=1548389316151&sign=9hYzgJvCJ26OFAZ5132SAQ==
     */

    @GET("weather")
    Observable<WeatherEntity> getWeatherEntity(@Query("location") String location,@Query("username") String username, @Query("t") String t, @Query("sign") String sign);
}
