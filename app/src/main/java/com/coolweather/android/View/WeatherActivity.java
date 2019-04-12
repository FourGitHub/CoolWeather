package com.coolweather.android.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.customView.SunriseView;
import com.coolweather.android.entity.BiYing;
import com.coolweather.android.entity.CityWeaInfo;
import com.coolweather.android.entity.WeatherEntity;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.utils.DensityUtil;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.HttpWeatherEntity;
import com.coolweather.android.utils.MatchImageUtil;
import com.coolweather.android.utils.ToastUtil;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @BindView(R.id.img_nav)
    ImageView navButton;
    @BindView(R.id.title_city)
    TextView titleCity;
//    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.hourly_time_text)
    TextView hourlyTimeText;
    @BindView(R.id.hourly_weather_icon)
    ImageView hourlyWeatherIcon;
    @BindView(R.id.weather_condition_text)
    TextView weatherConditionText;
    @BindView(R.id.hourly_tmp_text)
    TextView hourlyTmpText;
    @BindView(R.id.hourly_layout)
    LinearLayout hourlyLayout;
    @BindView(R.id.hourly_forcast_lauout)
    HorizontalScrollView hourlyForcastLauout;
    @BindView(R.id.feel_tmp_text)
    TextView feelTmpText;
    @BindView(R.id.pop_text)
    TextView popText;
    @BindView(R.id.append_now_layout)
    LinearLayout appendNowLayout;
    @BindView(R.id.sun)
    SunriseView sun;
    @BindView(R.id.sunrise_text)
    TextView sunriseText;
    @BindView(R.id.sunset_text)
    TextView sunsetText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.clothes_image)
    ImageView clothesImage;
    @BindView(R.id.clothes_text)
    TextView clothesText;
    @BindView(R.id.clothes_layout)
    CardView clothesLayout;
    @BindView(R.id.air_image)
    ImageView airImage;
    @BindView(R.id.air_text)
    TextView airText;
    @BindView(R.id.air_layout)
    CardView airLayout;
    @BindView(R.id.uv_iamge)
    ImageView uvIamge;
    @BindView(R.id.uv_text)
    TextView uvText;
    @BindView(R.id.uv_layout)
    CardView uvLayout;
    @BindView(R.id.sports_image)
    ImageView sportsImage;
    @BindView(R.id.sports_text)
    TextView sportsText;
    @BindView(R.id.sports_layout)
    CardView sportsLayout;
    @BindView(R.id.car_wash_image)
    ImageView carWashImage;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.car_wash_layout)
    CardView carWashLayout;
    @BindView(R.id.flu_image)
    ImageView fluImage;
    @BindView(R.id.flu_text)
    TextView fluText;
    @BindView(R.id.flu_layout)
    CardView fluLayout;
    @BindView(R.id.comf_image)
    ImageView comfImage;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.comf_layout)
    CardView comfLayout;
    @BindView(R.id.travel_image)
    ImageView travelImage;
    @BindView(R.id.travel_text)
    TextView travelText;
    @BindView(R.id.travel_layout)
    CardView travelLayout;
    @BindView(R.id.weather_layout)
    NestedScrollView weatherLayout;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private static final int REQUEST_CODE = 1;
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        initComponentsAndStartWork();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    private void initComponentsAndStartWork() {
        swipeRefresh.setOnRefreshListener(() ->
                requestWeatherEntity(mWeatherId));
        /* 对应方式一，随着项目的升级，修改为了用GreenDAO存储
        String weatherInfoJSON = getCachedWeather();
        */
        List<CityWeaInfo> weathers = APP.getDaoSession().loadAll(CityWeaInfo.class);
        String weatherInfoJSON = null;
        if (weathers.size() > 0) {
            weatherInfoJSON = weathers.get(0).getJsonString();
        }
        if (weatherInfoJSON != null) {
            // 如果有缓存的天气信息，将它作为天气首页展示
            Gson gson = new Gson();
            WeatherEntity weatherEntity = gson.fromJson(weatherInfoJSON, WeatherEntity.class);
            mWeatherId = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
            showWeatherInfo(weatherEntity.getHeWeather6().get(0));
            loadBingPic();
            Log.i(TAG, "initComponentsAndStartWork: --->> 缓存 = " + weatherInfoJSON);
            return;
        } else {
            // 没有缓存信息，默认显示北京
            mWeatherId = "CN101010100";
        }
        requestWeatherEntity(mWeatherId);

    }

    /**
     * @return SharedPreference存储的WeatherEntity的JSON字符串
     */
    private String getCachedWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("weather", null);
    }

    /**
     * 根据城市的天气代码获取天气信息
     */
    public void requestWeatherEntity(String cid) {
        loadBingPic();
        if (HttpUtil.isNetworkEnable(this)) {
            Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(WeatherEntity weatherEntity) {
                    if (weatherEntity.getHeWeather6()
                                     .get(0)
                                     .getStatus()
                                     .toLowerCase()
                                     .equals("ok")) {
                        Gson gson = new Gson();
                        String weatherInfoJSON = gson.toJson(weatherEntity);
                        mWeatherId = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
                        showWeatherInfo(weatherEntity.getHeWeather6().get(0));
                        /* 查询成功就保存起来
                        1. 使用sharedPreference:
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", weatherInfoJSON);
                        editor.apply();

                        2. 使用GreenDAO:
                        */
                        CityWeaInfo weather = new CityWeaInfo(Long.valueOf(0), mWeatherId, weatherInfoJSON);
                        APP.getDaoSession().insertOrReplace(weather);

                        Log.i(TAG, "onNext: --->> weatherEntity = " + weatherInfoJSON);
                    } else {
                        failed("获取天气信息失败！");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    failed("获取天气信息失败！");
                }

                @Override
                public void onComplete() {
                    swipeRefresh.setRefreshing(false);

                }
            };
            HttpWeatherEntity.getInstance().getWeatherEntity(observer, cid);
        } else {
            failed("请检查您的网络！");
        }

    }

    private void failed(String msg) {
        ToastUtil.showToast(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 加载必应每日一图,《第一行代码》给的 今日必应 接口
     * 接口地址以及参数：http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1
     */
    private void loadBingPic() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPicUrl = prefs.getString("bing_pic_url", null);
        if (bingPicUrl != null) {
            Glide.with(WeatherActivity.this)
                 .load(bingPicUrl)
                 .placeholder(R.drawable.place_holder)
                 .error(R.drawable.place_holder)
                 .into(bingPicImg);
            return;
        }
        if (HttpUtil.isNetworkEnable(this)) {
            String requestBingPic = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
            HttpUtil.sendHttpUrlConnectionResquest(requestBingPic, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Glide.with(WeatherActivity.this)
                                             .load(R.drawable.place_holder)
                                             .into(bingPicImg));
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final String response) {
                    // 接口只返回了后面的路径、前面的协议和主机需要加上去组合成一个完整的Url
                    String bingPicUr = "http://cn.bing.com" + new Gson()
                            .fromJson(response, BiYing.class).getImages().get(0).getUrl();
                    // 请求成功，则把图片链接保存起来，备用
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic_url", bingPicUr);
                    editor.apply();
                    Log.i(TAG, "onResponse: --->> biyingurl = " + bingPicUr);
                    runOnUiThread(() -> Glide.with(WeatherActivity.this)
                                             .load(bingPicUr)
                                             .placeholder(R.drawable.place_holder)
                                             .error(R.drawable.place_holder)
                                             .into(bingPicImg));
                }
            });

        } else {
            Glide.with(WeatherActivity.this).load(R.drawable.place_holder)
                 .into(bingPicImg);
        }
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(WeatherEntity.HeWeather6Bean weather) {
        String cityName = weather.getBasic().getLocation();
//        String updateTime = weather.getUpdate().getLoc().split(" ")[1];
        String degree = weather.getNow().getTmp() + "℃";
        String weatherInfo = weather.getNow().getCond_txt();
        feelTmpText.setText("体感温度: " + weather.getNow().getFl() + "℃");
        titleCity.setText(cityName);
//        titleUpdateTime.setText("最近更新: " + updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        WeatherEntity.HeWeather6Bean.DailyForecastBean today = weather.getDaily_forecast().get(0);
        String sunrise = today.getSr().replace(':', '.');
        String sunset = today.getSs().replace(':', '.');
        String moonrise = today.getMr().replace(':', '.');
        String moonset = today.getMs().replace(':', '.');
        sunriseText.setText("日出 " + sunrise);
        sunsetText.setText("日落 " + sunset);
        startSunAnim(Float.valueOf(sunrise), Float.valueOf(sunset));
        for (WeatherEntity.HeWeather6Bean.DailyForecastBean forecast : weather.getDaily_forecast()) {

            View view = LayoutInflater.from(this)
                                      .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond_txt_d());
            maxText.setText(forecast.getTmp_max());
            minText.setText(forecast.getTmp_min());
            forecastLayout.addView(view);
        }

        hourlyLayout.removeAllViews();
        popText.setText("降水概率: " + weather.getHourly().get(0).getPop() + "%");
        for (WeatherEntity.HeWeather6Bean.HourlyBean hourly : weather.getHourly()) {
            View view = LayoutInflater.from(this)
                                      .inflate(R.layout.hourly_item, hourlyLayout, false);
            TextView hourlyTimeText = view.findViewById(R.id.hourly_time_text);
            ImageView hourlyWeatherIcon = view.findViewById(R.id.hourly_weather_icon);
            TextView hourlyWeatherConditionText = view
                    .findViewById(R.id.weather_condition_text);
            TextView hourlyTmpText = view.findViewById(R.id.hourly_tmp_text);
            hourlyTimeText.setText(hourly.getTime().split(" ")[1]);
            hourlyWeatherIcon
                    .setImageResource(MatchImageUtil.matchWeatherIocnCode(hourly.getCond_code()));
            hourlyWeatherConditionText.setText(hourly.getCond_txt().split("/")[0]);
            hourlyTmpText.setText(hourly.getTmp());
            hourlyLayout.addView(view);
        }

        /*
         * 这里的SharedPreferences 用于缓存接口中关于 lifestyle 各个板块的详细信息，当用户点击
         * WeatherActivity 中展示lifestyle模块的ImageView时，打开另一个活动展示lifestyle详细信息
         * 我觉得这样写一定程度上能简化代码，而不是每次跳转活动的时候，又重新解析一遍。
         */
        SharedPreferences share;
        SharedPreferences.Editor editor;
        for (WeatherEntity.HeWeather6Bean.LifestyleBean lifestyle : weather.getLifestyle()) {
            switch (lifestyle.getType()) {
                case "comf":
                    comfortText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("comf_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("comf_brf", lifestyle.getBrf());
                    editor.putString("comf_txt", lifestyle.getTxt());
                    editor.putString("comf_type", lifestyle.getType());
                    editor.apply();// 线程不安全，但效率高
                    break;
                case "drsg":
                    clothesText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("drsg_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("drsg_brf", lifestyle.getBrf());
                    editor.putString("drsg_txt", lifestyle.getTxt());
                    editor.putString("drsg_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "flu":
                    fluText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("flu_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("flu_brf", lifestyle.getBrf());
                    editor.putString("flu_txt", lifestyle.getTxt());
                    editor.putString("flu_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "sport":
                    sportsText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("sport_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("sport_brf", lifestyle.getBrf());
                    editor.putString("sport_txt", lifestyle.getTxt());
                    editor.putString("sport_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "trav":
                    travelText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("trav_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("trav_brf", lifestyle.getBrf());
                    editor.putString("trav_txt", lifestyle.getTxt());
                    editor.putString("trav_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "uv":
                    uvText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("uv_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("uv_brf", lifestyle.getBrf());
                    editor.putString("uv_txt", lifestyle.getTxt());
                    editor.putString("uv_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "cw":
                    carWashText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("cw_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("cw_brf", lifestyle.getBrf());
                    editor.putString("cw_txt", lifestyle.getTxt());
                    editor.putString("cw_type", lifestyle.getType());
                    editor.apply();
                    break;
                case "air":
                    airText.setText(lifestyle.getBrf());
                    share = getSharedPreferences("air_data", MODE_PRIVATE);
                    editor = share.edit();
                    editor.putString("air_brf", lifestyle.getBrf());
                    editor.putString("air_txt", lifestyle.getTxt());
                    editor.putString("air_type", lifestyle.getType());
                    editor.apply();
                    break;
                default:
                    break;
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


    /**
     * 传入日出日落时间，计算动画中此时太阳所在的位置。
     */
    public void startSunAnim(float sunrise, float sunset) {
        Calendar calendarNow = Calendar.getInstance();
        int hour = calendarNow.get(Calendar.HOUR_OF_DAY);
        int minute = calendarNow.get(Calendar.MINUTE);
        float now = Float.valueOf(String.format("%02d.%02d", hour, minute));
        if (now < sunrise) {
            sun.sunAnim(0);
        } else if (now > sunset) {
            sun.sunAnim(1);
        } else {
            sun.sunAnim((now - sunrise) / (sunset - sunrise));
        }
    }

    @OnClick({R.id.img_nav, R.id.clothes_image, R.id.air_image, R.id.uv_iamge, R.id.sports_image, R.id.car_wash_image, R.id.flu_image, R.id.comf_image, R.id.travel_image})
    public void onViewClicked(View view) {
        SharedPreferences prefs;
        Intent intent;
        switch (view.getId()) {
            case R.id.img_nav:
                showPopWindow();
                break;
            case R.id.clothes_image:
                prefs = getSharedPreferences("drsg_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("drsg_type", " ")),
                        "穿衣指数",
                        prefs.getString("drsg_brf", " "),
                        prefs.getString("drsg_txt", " "));
                startActivity(intent);
                break;
            case R.id.air_image:
                prefs = getSharedPreferences("air_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("air_type", " ")),
                        "空气质量",
                        prefs.getString("air_brf", " "),
                        prefs.getString("air_txt", " "));
                startActivity(intent);
                break;
            case R.id.uv_iamge:
                prefs = getSharedPreferences("uv_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("uv_type", " ")),
                        "紫外线强度",
                        prefs.getString("uv_brf", " "),
                        prefs.getString("uv_txt", " "));
                startActivity(intent);
                break;
            case R.id.sports_image:
                prefs = getSharedPreferences("sport_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("sport_type", " ")),
                        "运动指数",
                        prefs.getString("sport_brf", " "),
                        prefs.getString("sport_txt", " "));
                startActivity(intent);
                break;
            case R.id.car_wash_image:
                prefs = getSharedPreferences("cw_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("cw_type", " ")),
                        "洗车指数",
                        prefs.getString("cw_brf", " "),
                        prefs.getString("cw_txt", " "));
                startActivity(intent);
                break;
            case R.id.flu_image:
                prefs = getSharedPreferences("flu_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("flu_type", " ")),
                        "流感指数",
                        prefs.getString("flu_brf", " "),
                        prefs.getString("flu_txt", " "));
                startActivity(intent);
                break;
            case R.id.comf_image:
                prefs = getSharedPreferences("comf_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("comf_type", " ")),
                        "舒适度指数",
                        prefs.getString("comf_brf", " "),
                        prefs.getString("comf_txt", " "));
                startActivity(intent);
                break;
            case R.id.travel_image:
                prefs = getSharedPreferences("trav_data", MODE_PRIVATE);
                intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                        MatchImageUtil.matchImage(prefs.getString("trav_type", " ")),
                        "旅行指数",
                        prefs.getString("trav_brf", " "),
                        prefs.getString("trav_txt", " "));
                startActivity(intent);
                break;
        }
    }

    private void showPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pop_window, null);
        TextView tvFastCheckout = view.findViewById(R.id.tv_fast_checkout);
        TextView tvCitySearch = view.findViewById(R.id.tv_city_search);
        TextView tvCityManage = view.findViewById(R.id.tv_city_manage);
        TextView tvWeatherShare = view.findViewById(R.id.tv_weather_share);
        PopupWindow popupWindow = new PopupWindow();
        popupWindow.setWidth(DensityUtil.dip2px(this, 120));
        popupWindow.setHeight(DensityUtil.dip2px(this, 150));
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.animPopWin);
        popupWindow.showAsDropDown(navButton, -DensityUtil.dip2px(this, 105),0);

        tvFastCheckout.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
            popupWindow.dismiss();
        });
        tvCitySearch.setOnClickListener(v -> {
            startActivityForResult(new Intent(WeatherActivity.this, SearchActivity.class),REQUEST_CODE);
            popupWindow.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mWeatherId = data.getStringExtra("weatherId");
                    requestWeatherEntity(mWeatherId);
                }
        }
    }
}
