package com.coolweather.android.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.gson.SearchedCities;
import com.coolweather.android.utils.HttpSearchCity;
import com.coolweather.android.utils.SearchCityAdapter;
import com.coolweather.android.utils.ToastUtil;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private static final int GPS_REQUEST_CODE = 1;
    @BindView(R.id.edt_search_city)
    EditText edtSearchCity;
    @BindView(R.id.indicator)
    View indicator;
    @BindView(R.id.hide_when_searching)
    RelativeLayout hideWhenSearching;
    @BindView(R.id.search_bar_layout)
    FrameLayout searchBarLayout;
    @BindView(R.id.btn_hotcity_beijing)
    Button btnHotcityBeijing;
    @BindView(R.id.btn_hotcity_shanghai)
    Button btnHotcityShanghai;
    @BindView(R.id.btn_hotcity_hangzhou)
    Button btnHotcityHangzhou;
    @BindView(R.id.btn_hotcity_shenzhen)
    Button btnHotcityShenzhen;
    @BindView(R.id.btn_hotcity_guanghzou)
    Button btnHotcityGuanghzou;
    @BindView(R.id.btn_hotcity_suzhou)
    Button btnHotcitySuzhou;
    @BindView(R.id.btn_hotcity_nanjing)
    Button btnHotcityNanjing;
    @BindView(R.id.btn_hotcity_chengdu)
    Button btnHotcityChengdu;
    @BindView(R.id.btn_hotcity_xian)
    Button btnHotcityXian;
    @BindView(R.id.btn_hotcity_chongqing)
    Button btnHotcityChongqing;
    @BindView(R.id.img_pop_whats_pos)
    ImageView imgPopWhatsCurrent;
    @BindView(R.id.btn_current_position)
    Button btnCurrentPosition;
    @BindView(R.id.recycler_city_list)
    RecyclerView recyclerCityList;
    @BindView(R.id.img_cancle_input)
    ImageView imgCancleInput;
    @BindView(R.id.search_no_result)
    RelativeLayout searchNoResult;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.btn_hotcity_wuhan)
    Button btnHotcityWuhan;
    @BindView(R.id.btn_hotcity_tianjin)
    Button btnHotcityTianjin;

    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mListener;
    private static String mProvider = LocationManager.NETWORK_PROVIDER;
    private SearchCityAdapter mAdapter;
    private AlertDialog popAboutPosition;

    //
    private Looper mLooper = Looper.myLooper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_area);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beginLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListener != null && mLocationManager != null) {
            mLocationManager.removeUpdates(mListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        // 根据输入框状态，修改搜索框中用于提示的前景布局的可见性
        edtSearchCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideWhenSearching.setVisibility(View.GONE);
            } else {
                hideWhenSearching.setVisibility(View.VISIBLE);
                imgCancleInput.setVisibility(View.GONE);
            }
        });

        // 简单输入框文本变化 并发起模糊搜索请求
        edtSearchCity.addTextChangedListener(new TextWatcher() {
            long t;
            Observer<SearchedCities> observer = new Observer<SearchedCities>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.i(TAG, "onSubscribe: --->>");

                }

                @Override
                public void onNext(SearchedCities searchedCities) {
                    if (searchedCities.getHeWeather6()
                                      .get(0)
                                      .getStatus()
                                      .toLowerCase()
                                      .equals("ok")) {
                        List<SearchedCities.HeWeather6Bean.BasicBean> cities = searchedCities.getHeWeather6()
                                                                                             .get(0)
                                                                                             .getBasic();
                        recyclerCityList.setVisibility(View.VISIBLE);
                        searchNoResult.setVisibility(View.GONE);
                        mAdapter.setmCities(cities);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.setmCities(null);
                        mAdapter.notifyDataSetChanged();
                        recyclerCityList.setVisibility(View.GONE);
                        if (edtSearchCity.getText().length() > 0)
                            searchNoResult.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG, "onNext: --->> ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(TAG, "onError: --->> ");

                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "onComplete: --->> ");

                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvHint.setVisibility(View.GONE);
                    imgCancleInput.setVisibility(View.VISIBLE);
                    recyclerCityList.setVisibility(View.VISIBLE);
                } else {
                    tvHint.setVisibility(View.VISIBLE);
                    imgCancleInput.setVisibility(View.GONE);
                    recyclerCityList.setVisibility(View.GONE);
                    searchNoResult.setVisibility(View.GONE);
                }

                if (System.currentTimeMillis() - t > 100) {
                    t = System.currentTimeMillis();
                    HttpSearchCity.getInstance()
                                  .getSearchedCity(observer, s.toString()
                                                              .trim(), 10);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 显示搜索城市结果的列表的适配器
        mAdapter = new SearchCityAdapter();
        recyclerCityList.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCityList.setLayoutManager(layoutManager);
    }


    @OnClick({R.id.btn_hotcity_beijing, R.id.btn_hotcity_shanghai, R.id.btn_hotcity_hangzhou, R.id.btn_hotcity_shenzhen, R.id.btn_hotcity_guanghzou, R.id.btn_hotcity_suzhou, R.id.btn_hotcity_nanjing, R.id.btn_hotcity_chengdu, R.id.btn_hotcity_xian, R.id.btn_hotcity_chongqing, R.id.img_pop_whats_pos, R.id.btn_current_position, R.id.btn_hotcity_wuhan, R.id.btn_hotcity_tianjin, R.id.img_cancle_input})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_hotcity_beijing:
                break;
            case R.id.btn_hotcity_shanghai:
                break;
            case R.id.btn_hotcity_hangzhou:
                break;
            case R.id.btn_hotcity_shenzhen:
                break;
            case R.id.btn_hotcity_guanghzou:
                break;
            case R.id.btn_hotcity_suzhou:
                break;
            case R.id.btn_hotcity_nanjing:
                break;
            case R.id.btn_hotcity_chengdu:
                break;
            case R.id.btn_hotcity_xian:
                break;
            case R.id.btn_hotcity_chongqing:
                break;
            case R.id.btn_hotcity_wuhan:
                break;
            case R.id.btn_hotcity_tianjin:
                break;
            case R.id.btn_current_position:
                startWeatherActivity((String)view.getTag(R.id.btn_current_position));
                break;
            case R.id.img_cancle_input:
                edtSearchCity.setText("");
                break;
            case R.id.img_pop_whats_pos:
                popAboutPos();
                break;

        }
    }

    /**
     * 弹出弹窗
     */
    private void popAboutPos() {
        if (popAboutPosition == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View aboutPosition = getLayoutInflater().inflate(R.layout.custom_dialog_about_pos, null);
            ImageView cancle = aboutPosition.findViewById(R.id.img_close_info);
            TextView setGPS = aboutPosition.findViewById(R.id.tv_go_set_gps);
            setGPS.setOnClickListener(v -> openGPS());
            builder.setCancelable(true);
            builder.setView(aboutPosition);
            popAboutPosition = builder.create();
            cancle.setOnClickListener((v) -> {
                if (popAboutPosition != null) {
                    popAboutPosition.dismiss();
                }
            });
        }

        popAboutPosition.show();

    }


    /**
     * 检查用户是否对应用授权GPS
     */
    private boolean checkoutGPSLicence(){
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查GPS是否打开
     */
    private boolean checkoutGPSIsOpen() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 跳转设置界面，提示用户打开GPS
     */
    private void openGPS() {
        if (!checkoutGPSLicence()) {
            ToastUtil.showToast(this, "您未授予应用定位权限，请先进入 「 设置 -> 应用管理 -> 应用权限 」 进行授权", Toast.LENGTH_LONG);
            return;
        }
        if (checkoutGPSIsOpen()) {
            ToastUtil.showToast(this, "您已打开GPS", Toast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST_CODE);
    }

    /**
     * 从设置界面返回之后，关闭弹窗
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                if (popAboutPosition != null) {
                    popAboutPosition.dismiss();
                }
                break;
        }
    }

    /**
     * 开始对当前设备进行定位
     */
    private void beginLocation() {
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                afterLocateSuccess();
                Log.i(TAG, "--->> onLocationChanged: " + location.getLongitude());
                Log.i(TAG, "--->> onLocationChanged: " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                mProvider = provider;
                ToastUtil.showToast(TestActivity.this, "provider = " + provider, Toast.LENGTH_SHORT);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        Log.i(TAG, "--->> beginLocation: mProvider = " + mProvider);
        mProvider = mLocationManager.getBestProvider(criteria, true);
        if (mProvider != null) {
            if (ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(TestActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                /* 这里很关键啊，首先说明问题：虽然默认进行网络定位，但是网络定位几乎不能使用，所以位置信息的来源实则还是gps，
                getLastKnownLocation()方法获取的是系统缓存中最新的位置消息，有两种可能：
                        1、别的某个应用程序获取到的(可能不够实时)
                        2、本程序刚刚获取到的最新信息(实时)
                但以上两种情况，Listener的onLocationChanged()方法都会被调用。

                我这里开启一个子线程一直循环直到获取最新的位置信息，一旦获取到就结束这个子线程
                那要是一直没有获取到最新信息怎么办呢？比如说缓存中没有位置信息，用户也没有开启gps或没收到gps信号，
                现在的处理方式就是一直循环，虽然我觉得这里有点浪费CPU资源了！！！好处就是如果用户及时开启了gps并且信号不错的情况下，
                能根据最新的位置信息更新UI

                PASS: 退一万步说，用户直接搜索不是更好更开吗....,所以我开设这个功能主要还是为了练练手....
                */
                new Thread(() -> {

                    String provider = mLocationManager.getBestProvider(criteria, true);
                    while ((mLocation = mLocationManager
                            .getLastKnownLocation(provider)) == null) {
                        provider = mLocationManager.getBestProvider(criteria, true);

                        mLocationManager
                                .requestLocationUpdates(60000, 1, criteria, mListener, mLooper);
                    }
                    afterLocateSuccess();
                    Log.i(TAG, "beginLocation: --->> provider = " + provider);
                    Log.i(TAG, "beginLocation: --->> quit");
                    Log.i(TAG, "beginLocation: --->> nowLocation" + mLocation);
                }).start();

            }
        }
        // 注释掉是因为：如果用户拒绝定位权限，那么每次都将弹出这个弹窗，体验会很不好...
//        else {
//            ToastUtil.showToast(getApplicationContext(), "定位失败...", Toast.LENGTH_SHORT);
//        }

    }

    /**
     * 定位成功后，利用经纬度信息，查询城市结果并显示到当前定位
     */
    private void afterLocateSuccess() {
        if (mLocation != null) {
            Log.i(TAG, "afterLocateSuccess: --->> ");
            double longitude = mLocation.getLongitude();
            double latitude = mLocation.getLatitude();
            Observer<SearchedCities> observer = new Observer<SearchedCities>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(SearchedCities searchedCities) {
                    if (searchedCities.getHeWeather6()
                                      .get(0)
                                      .getStatus()
                                      .toLowerCase()
                                      .equals("ok")) {
                        String location = searchedCities.getHeWeather6()
                                                        .get(0)
                                                        .getBasic().get(0).getLocation();
                        String cid = searchedCities.getHeWeather6()
                                                   .get(0)
                                                   .getBasic().get(0).getCid();
                        btnCurrentPosition.setText(location);
                        // 给btn打包，点击的时候取出cid，跳转到天气界面
                        btnCurrentPosition.setTag(R.id.btn_current_position, cid);
                    }

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };
            // 发起请求
            HttpSearchCity.getInstance().getSearchedCity(observer, longitude + "," + latitude, 1);
        }

    }

    /**
     * 请求定位权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beginLocation();
                }
                break;
        }
    }

    /**
     * 用户选择后，将所选城市天气信息保存起来
     */
    private void refreshSharedPrefrence(){

    }


    private void startWeatherActivity(String cid) {
        Intent i = new Intent(this, WeatherActivity.class);
        i.putExtra("weather_id", cid);
        startActivity(i);
    }

}
