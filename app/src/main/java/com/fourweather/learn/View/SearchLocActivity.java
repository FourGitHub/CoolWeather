package com.fourweather.learn.View;

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

import com.fourweather.learn.R;
import com.fourweather.learn.entity.SearchedCities;
import com.fourweather.learn.utils.HttpSearchCity;
import com.fourweather.learn.utils.SearchCityAdapter;
import com.fourweather.learn.utils.ToastUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 定位方案设计了两个版本：1、Android 原生定位方式；2、第三方百度定位SDK；
 * Android 原生定位方式：仅支持 GPS 和 网络，
 */
public class SearchLocActivity extends BaseLocActivity {
    private static final String TAG = "SearchLocActivity";
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
    @BindView(R.id.img_titleback)
    ImageView imgTitleback;

    /* Android 原生定位相关的五个变量（第三方定位方案在其继承的父类 --- BaseLocActivity里实现）*/
    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mListener;
    private static String mProvider = LocationManager.NETWORK_PROVIDER;
    // Looper是主线程的Looper,用于LocationManager实现回调
    private Looper mLooper = Looper.myLooper();


    private SearchCityAdapter mAdapter;
    private AlertDialog popAboutPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_area);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Android 原生定位方式版本 */
        //        beginLocation();
        mLocationClient.restart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListener != null && mLocationManager != null) {
            mLocationManager.removeUpdates(mListener);
        }
    }

    private void init() {
        /* Android 原生定位方式版本 */
        //        if (checkoutGPSLicence()) {
        //            requestGPSLicence();
        //        }

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
                    if (searchedCities.getHeWeather6().get(0).getStatus().toLowerCase().equals("ok")) {
                        List<SearchedCities.HeWeather6Bean.BasicBean> cities = searchedCities.getHeWeather6().get(0).getBasic();
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

            /*
            随着输入状态的改变，控制相应组件的显示或隐藏
             */
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
                    HttpSearchCity.getInstance().getSearchedCity(observer, s.toString().trim(), 10);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 列表的初始化配置
        mAdapter = new SearchCityAdapter(this);
        recyclerCityList.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCityList.setLayoutManager(layoutManager);
    }


    @OnClick({R.id.img_titleback, R.id.btn_hotcity_beijing, R.id.btn_hotcity_shanghai, R.id.btn_hotcity_hangzhou, R.id.btn_hotcity_shenzhen, R.id.btn_hotcity_guanghzou, R.id.btn_hotcity_suzhou, R.id.btn_hotcity_nanjing, R.id.btn_hotcity_chengdu, R.id.btn_hotcity_xian, R.id.btn_hotcity_chongqing, R.id.img_pop_whats_pos, R.id.btn_current_position, R.id.btn_hotcity_wuhan, R.id.btn_hotcity_tianjin, R.id.img_cancle_input})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_titleback:
                finish();
                break;
            case R.id.btn_hotcity_beijing:
                updateWeatherActivity("CN101010100");
                break;
            case R.id.btn_hotcity_shanghai:
                updateWeatherActivity("CN101020100");
                break;
            case R.id.btn_hotcity_hangzhou:
                updateWeatherActivity("CN101210101");
                break;
            case R.id.btn_hotcity_shenzhen:
                updateWeatherActivity("CN101280601");
                break;
            case R.id.btn_hotcity_guanghzou:
                updateWeatherActivity("CN101280101");
                break;
            case R.id.btn_hotcity_suzhou:
                updateWeatherActivity("CN101190401");
                break;
            case R.id.btn_hotcity_nanjing:
                updateWeatherActivity("CN101190101");
                break;
            case R.id.btn_hotcity_chengdu:
                updateWeatherActivity("CN101270101");
                break;
            case R.id.btn_hotcity_xian:
                updateWeatherActivity("CN101110101");
                break;
            case R.id.btn_hotcity_chongqing:
                updateWeatherActivity("CN101040100");
                break;
            case R.id.btn_hotcity_wuhan:
                updateWeatherActivity("CN101200101");
                break;
            case R.id.btn_hotcity_tianjin:
                updateWeatherActivity("CN101030100");
                break;
            case R.id.btn_current_position:
                if (view.getTag(R.id.btn_current_position) != null) {
                    updateWeatherActivity((String) view.getTag(R.id.btn_current_position));
                } else {
                    ToastUtil.showToast(APP.getContext(), "定位中... 请您稍等！", Toast.LENGTH_SHORT);
                }
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
     * 「当前定位」旁边的❓，点击弹出弹窗
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
     * 检查用户是否对应用授权GPS
     */
    private boolean checkoutGPSLicence() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 跳转设置界面，提示用户打开GPS
     */
    private void openGPS() {
        if (checkoutGPSLicence()) {
            ToastUtil.showToast(APP.getContext(), "您未授予应用定位权限，请先进入 「 设置 -> 应用管理 -> 应用权限 」 进行授权", Toast.LENGTH_LONG);
            return;
        }
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST_CODE);
    }

    /**
     * 开始对当前设备进行定位
     */
    /* Android 原生定位方式版本 */
    private void beginLocation() {
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    mLocation = location;
                    afterLocateSuccess(mLocation.getLongitude(), mLocation.getLatitude());
                    Log.i(TAG, "--->> onLocationChanged: longitude = " + location.getLongitude() + "  latitude = " + location.getLatitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                mProvider = provider;
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        /*
        这里需要注意一点，如果用户没有授权GPS，那么无论如何都会返回null,这也是为什么我在init()方法中首先判断用户是否授权的原因
        强行让用户授权有点耍流氓的感觉，但是原生的网络定位几乎不能用，所以只能让用户授权GPS，这样如果用户开启了GPS并能接受到信号的话就可以对用户进行定位。
        */
        mProvider = mLocationManager.getBestProvider(criteria, true);
        Log.i(TAG, "--->> beginLocation: mProvider = " + mProvider);
        if (mProvider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestGPSLicence();

            } else {
                /*
                这里很关键啊，首先说明问题：虽然默认进行网络定位，但是网络定位几乎不能使用，所以位置信息的来源实则还是gps，
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
                    while ((mLocation = mLocationManager.getLastKnownLocation(provider)) == null) {
                        provider = mLocationManager.getBestProvider(criteria, true);

                        mLocationManager.requestLocationUpdates(2000, 1, criteria, mListener, mLooper);
                    }
                    afterLocateSuccess(mLocation.getLongitude(), mLocation.getLatitude());
                    Log.i(TAG, "beginLocation: --->> provider = " + provider);
                    Log.i(TAG, "beginLocation: --->> nowLocation" + mLocation);
                    Log.i(TAG, "beginLocation: --->> quit");
                }).start();

            }
        }
        // 注释掉是因为：如果用户拒绝定位权限，那么每次都将弹出这个弹窗，体验会很不好...，与其弹出定位失败，不如把锅甩给用户，那就是在init()方法中强行让用户授权，直到同意为止，哈哈哈哈！！！
        //        else {
        //            ToastUtil.showToast(getApplicationContext(), "定位失败...", Toast.LENGTH_SHORT);
        //        }

    }

    private void requestGPSLicence() {
        ActivityCompat.requestPermissions(SearchLocActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    /**
     * 请求定位权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //                    beginLocation();
                }
                break;
        }
    }

    public void updateWeatherActivity(String cid) {
        if (getIntent().getBooleanExtra("parentAcIsWeatherAc", false)) {
            Intent i = new Intent();
            i.putExtra("cid", cid);
            setResult(RESULT_OK, i);
        } else {
            Intent i = new Intent(this, WeatherActivity1.class);
            i.putExtra("is", true);
            i.putExtra("cid", cid);
            startActivity(i);
        }
        finish();
    }

    /**
     * 定位成功后，利用经纬度信息，查询城市结果并显示到当前定位
     */
    @Override
    public void afterLocateSuccess(double longitude, double latitude) {
        Observer<SearchedCities> observer = new Observer<SearchedCities>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SearchedCities searchedCities) {
                if (searchedCities.getHeWeather6().get(0).getStatus().toLowerCase().equals("ok")) {
                    SearchedCities.HeWeather6Bean.BasicBean cityInfo = searchedCities.getHeWeather6().get(0).getBasic().get(0);
                    String location = cityInfo.getLocation() + ", " + cityInfo.getAdmin_area() + ", " + cityInfo.getCnty();
                    String cid = searchedCities.getHeWeather6().get(0).getBasic().get(0).getCid();
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
