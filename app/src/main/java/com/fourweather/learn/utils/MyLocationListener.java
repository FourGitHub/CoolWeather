package com.fourweather.learn.utils;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.fourweather.learn.View.BaseLocActivity;

/**
 * Create on 2019/04/18
 *
 * @author Four
 * @description
 */
public class MyLocationListener extends BDAbstractLocationListener {
    private static final String TAG = "MyLocationListener";
    private LocationClient mLocationClient = null;
    private BaseLocActivity mActivity;

    public MyLocationListener(BaseLocActivity activity, LocationClient locationClient) {
        mActivity = activity;
        mLocationClient = locationClient;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        double longitude = bdLocation.getLongitude(); // 经度
        double latitude = bdLocation.getLatitude();   // 纬度
        if (longitude == 4.9E-324 || latitude == 4.9E-324) {
            return;
        }
        mLocationClient.stop();
        mActivity.afterLocateSuccess(longitude, latitude);
        Log.i(TAG, "onReceiveLocation>> 经度 = " + longitude + "  纬度 = " + latitude);


    }
}
