package com.coolweather.android.utils;

import com.coolweather.android.gson.SearchedCities;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description
 */
public interface SearchCityService {
    /**
     * https://search.heweather.net/find?location=重庆&group=cn&key=e70302717c5d493499c49dd2ee6b8de2&number=20
     * 根据用户输入内容进行模糊查找
     */
    @GET("find")
    Observable<SearchedCities> getSearchedCity(@Query("location") String location, @Query("key") String key, @Query("group") String group, @Query("number") int number);

    /**
     * https://search.heweather.net/find?location=106.60,29.53&key=e70302717c5d493499c49dd2ee6b8de2&group=cn
     * 根据当前经纬度进行搜索,注意：经度在前纬度在后
     */
    @GET("find")
    Observable<SearchedCities> getCurrentCity(@Query("location") String location,@Query("key") String key,@Query("group") String group);
}
