package com.fourweather.learn.utils;

import com.fourweather.learn.entity.SearchedCities;

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
     * 接口描述：支持模糊搜索的全球城市搜索服务，免费接口只针对全国
     *
     * 请求URL：https://search.heweather.net/find?<parameters>
     *    例如：https://search.heweather.net/find?location=重庆&group=cn&key=e70302717c5d493499c49dd2ee6b8de2&number=20
     *
     * 重点讲一下 location 参数：
     *         输入需要查询的城市名称，支持模糊搜索，可输入中文（至少一个汉字）、英文（至少2个字母）、IP地址、坐标（经度在前纬度在后，英文,分割）、ADCode
     *
     * 基于 location参数的可选值，此接口在项目中承担两个作用：「城市搜索Ac」模糊查找功能模块、自动定位模块，其中，自动定位模块「BaseLocActivity」也有
     *
     */
    @GET("find")
    Observable<SearchedCities> getSearchedCity(@Query("location") String location, @Query("key") String key, @Query("group") String group, @Query("number") int number);

}
