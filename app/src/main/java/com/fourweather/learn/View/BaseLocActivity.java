package com.fourweather.learn.View;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fourweather.learn.utils.MyLocationListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Create on 2019/04/18
 *
 * @author Four
 * @description 这个BaseAc主要是提供一个定位功能，关于定位我使用的是开源框架 「PermissionsDispatcher」
 * <p>
 * 系统原生权限请求核心API:
 * AppCompat.checkSelfPermission()
 * AppCompat.requestPermissions()
 * ActivityCompat.shouldShowRequestPermissionRationale()
 * onRequestPermissionsResult()
 */
@RuntimePermissions
public class BaseLocActivity extends AppCompatActivity {
    private static final String TAG = "BaseLocActivity";
    protected LocationClient mLocationClient = null;
    protected MyLocationListener mLocationListener = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configLocating();
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void configLocating() {
        mLocationClient = new LocationClient(APP.getContext());
        mLocationListener = new MyLocationListener(this, mLocationClient);
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();


        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);


        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；（按照接口参数要求，此项目必须选则这个）
        //BD09：百度墨卡托坐标；
        option.setCoorType("BD09ll");

        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(1000);


        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);


        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setLocationNotify(false);

        option.setIgnoreKillProcess(false);


        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BaseLocActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /*
    如果发现 @OnPermissionDenied() 标记的方法为 unused 状态，那么需要 「build project」或者 修改「@NeedsPermission(...) 中的内容后再「build project」

    目前测试发现：1、当不同 @OnPermissionDenied(...) 回调方法的...属于一组危险权限时，只有一个回调方法会被回调
                    例如 @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION) 和 @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
                    自会回调其中一个方法
               2、@NeedsPermission(...) 权限被拒绝后，会回掉 @OnPermissionDenied(...) ，但是要求 ... 严格一致，不能是包含关系
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void onUserDeniedFineLoc() {
    }


    /*
    用户之前拒绝了应用的权限申请，应用再次请求时调用
     */
    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void onShowRationaleFineLoc(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("同意", (dialog, which) -> request.proceed())
                .setNegativeButton("拒绝", (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage("为了给您提供更好的服务，应用将申请使用定位权限")
                .show();
    }

    /*
    用户点击了不再提示，并拒绝了应用权限申请，应用再次请求时调用
    我的处理：不再给与用户提示
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void onNeverAskFineLoc() {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", (dialog, which) -> {
                    // 打开系统应用设置
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    dialog.cancel();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                .setCancelable(false)
                .setMessage("您已经禁止了定位权限,是否现在去开启")
                .show();
    }

    public void afterLocateSuccess(double longitude, double latitude) {

    }
}
