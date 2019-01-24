package com.coolweather.android.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.db.BiYing;
import com.coolweather.android.gson.MyWeatherBean;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.sunriseView.SunriseView;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.MatchImageUtil;
import com.coolweather.android.utils.PCCUtil;
import com.coolweather.android.utils.SignatureUtil;
import com.coolweather.android.utils.ToastUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";

    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private SunriseView mSunriseView;
    private TextView sunriseText;
    private TextView sunsetText;

    private Button navButton;

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;

    private TextView windScText;
    private TextView windSpdText;
    private TextView windDirText;

    private TextView pressText;
    private TextView humidityText;

    private TextView clothesText;
    private TextView comfortText;
    private TextView uvText;
    private TextView sportsText;
    private TextView carWashText;
    private TextView feelTmpText;
    private TextView fluText;
    private TextView popText;
    private TextView airText;
    private TextView travelText;

    private LinearLayout forecastLayout;
    private LinearLayout hourlyLayout;

    private ImageView comfImageView;
    private ImageView drsgImageView;
    private ImageView fluImageView;
    private ImageView sportImageView;
    private ImageView travImageView;
    private ImageView uvImageView;
    private ImageView cwImageView;
    private ImageView airImageView;

    private ImageView bingPicImg;

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
        initComponentsAndStartWork();

    }

    private void initComponentsAndStartWork() {
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        hourlyLayout = findViewById(R.id.hourly_layout);

        clothesText = findViewById(R.id.clothes_text);
        comfortText = findViewById(R.id.comfort_text);
        uvText = findViewById(R.id.uv_text);
        sportsText = findViewById(R.id.sports_text);
        carWashText = findViewById(R.id.car_wash_text);
        feelTmpText = findViewById(R.id.feel_tmp_text);
        popText = findViewById(R.id.pop_text);
        airText = findViewById(R.id.air_text);
        travelText = findViewById(R.id.travel_text);
        fluText = findViewById(R.id.flu_text);

        windScText = findViewById(R.id.wind_sc_text);
        windSpdText = findViewById(R.id.wind_spd_text);
        windDirText = findViewById(R.id.wind_dir_text);

        pressText = findViewById(R.id.press_text);
        humidityText = findViewById(R.id.humidity_text);

        sunriseText = findViewById(R.id.sunrise_text);
        sunsetText = findViewById(R.id.sunset_text);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);


        comfImageView = findViewById(R.id.comf_image);
        drsgImageView = findViewById(R.id.clothes_image);
        fluImageView = findViewById(R.id.flu_image);
        sportImageView = findViewById(R.id.sports_image);
        travImageView = findViewById(R.id.travel_image);
        uvImageView = findViewById(R.id.uv_iamge);
        cwImageView = findViewById(R.id.car_wash_image);
        airImageView = findViewById(R.id.air_image);
        setOnClickListenerForLifestyle();

        mSunriseView = findViewById(R.id.sun);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据，并取出weatherId以便发送请求刷新天气信息
            MyWeatherBean weather = PCCUtil.handleWeatherResponse(weatherString);
            mWeatherId = weather.getBasic().getCid();
            showWeatherInfo(weather);
        } else {
            // 无缓存时(用户切换新城市)去服务器查询天气信息
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        // 设置监听器，下拉刷新时，重新请求天气信息
        swipeRefresh.setOnRefreshListener(() -> requestWeather(mWeatherId));
        navButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        // 检测有无图片链接缓存。
        String bingPicUrl = prefs.getString("bing_pic_url", null);
        if (bingPicUrl != null) {
            Glide.with(this).load(bingPicUrl).placeholder(R.drawable.place_holder).into(bingPicImg);
        } else {
            loadBingPic();
        }

    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        // sign 请求更安全
        long t = System.currentTimeMillis();
        HashMap<String, String> params = new HashMap<>();
        params.put("location", weatherId);
        params.put("username", "HE1802011110181104");
        params.put("t", String.valueOf(t));
        String sign = null;
        try {
            sign = SignatureUtil
                    .getSignature(params, "e70302717c5d493499c49dd2ee6b8de2");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String weatherUrlSign = "https://free-api.heweather.net/s6/weather?" +
                "location=" + weatherId + "&" +
                "username=HE1802011110181104" + "&" +
                "t=" + t + "&" +
                "sign=" + sign;

        Log.i(TAG, "-->>weatherUrlSign = : " + weatherUrlSign);

        if (HttpUtil.networkStatus(getApplicationContext())) {
            HttpUtil.sendHttpUrlConnectionResquest(weatherUrlSign, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        ToastUtil
                                .showToast(getApplicationContext(), "获取天气信息失败 Ծ‸Ծ", Toast.LENGTH_SHORT);
                        swipeRefresh.setRefreshing(false);
                    });
                }

                @Override
                public void onResponse(final String response) {
                    // 把请求返回的JSON数据解析封装到JavaBean --- MyWeatherBean 中
                    final MyWeatherBean weather = PCCUtil.handleWeatherResponse(response);
                    runOnUiThread(() -> {
                        if (weather != null && "ok".equals(weather.getStatus())) {
                            // 请求成功则把天气数据缓存起来，备用
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", response);
                            editor.apply();
                            mWeatherId = weather.getBasic().getCid();
                            showWeatherInfo(weather);
                        } else {
                            ToastUtil
                                    .showToast(getApplicationContext(), "获取天气信息失败 Ծ‸Ծ", Toast.LENGTH_SHORT);
                        }
                        swipeRefresh.setRefreshing(false);
                    });
                }
            });
        } else {
            ToastUtil.showToast(getApplicationContext(), "请检查您的网络连接...", Toast.LENGTH_SHORT);
            swipeRefresh.setRefreshing(false);
        }
        loadBingPic();

    }

    /**
     * 加载必应每日一图,《第一行代码》给的 今日必应 接口
     * 接口地址以及参数：http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1
     */
    private void loadBingPic() {
        String requestBingPic = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        if (HttpUtil.networkStatus(getApplicationContext())) {
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
            ToastUtil.showToast(getApplicationContext(), "请检查您的网络连接...", Toast.LENGTH_SHORT);

        }
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(MyWeatherBean weather) {

        String cityName = weather.getBasic().getLocation();
        String updateTime = weather.getUpdate().getLoc().split(" ")[1];
        String degree = weather.getNow().getTmp() + "℃";
        String weatherInfo = weather.getNow().getCond_txt();
        String windScInfo = "风力  " + weather.getNow().getWind_sc();
        String windDirInfo = "风向  " + weather.getNow().getWind_dir();
        String windSpdInfo = "风速  " + weather.getNow().getWind_spd() + " km/h";
        String humidityInfo = weather.getNow().getHum();
        String pressInfo = weather.getNow().getPres() + "hpa";
        String feelTmp = "体感温度  " + weather.getNow().getFl() + "℃";

        windDirText.setText(windDirInfo);
        windScText.setText(windScInfo);
        windSpdText.setText(windSpdInfo);
        humidityText.setText(humidityInfo);
        pressText.setText(pressInfo);
        feelTmpText.setText(feelTmp);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        MyWeatherBean.DailyForecastBean today = weather.getDaily_forecast().get(0);
        sunrise = today.getSr().replace(':', '.');
        sunset = today.getSs().replace(':', '.');
        moonrise = today.getMr().replace(':', '.');
        moonset = today.getMs().replace(':', '.');
        sunriseText.setText("日出 " + sunrise);
        sunsetText.setText("日落 " + sunset);
        startSunAnim(Float.valueOf(sunrise), Float.valueOf(sunset));
        for (MyWeatherBean.DailyForecastBean forecast : weather.getDaily_forecast()) {

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
        popText.setText("降水概率  " + weather.getHourly().get(0).getPop() + "%");
        for (MyWeatherBean.HourlyBean hourly : weather.getHourly()) {
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
        for (MyWeatherBean.LifestyleBean lifestyle : weather.getLifestyle()) {
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
            mSunriseView.sunAnim(0);
        } else if (now > sunset) {
            mSunriseView.sunAnim(1);
        } else {
            mSunriseView.sunAnim((now - sunrise) / (sunset - sunrise));
        }
    }

    private void setOnClickListenerForLifestyle() {
        comfImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("comf_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("comf_type", " ")),
                    "舒适度指数",
                    prefs.getString("comf_brf", " "),
                    prefs.getString("comf_txt", " "));
            startActivity(intent);
        });
        drsgImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("drsg_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("drsg_type", " ")),
                    "穿衣指数",
                    prefs.getString("drsg_brf", " "),
                    prefs.getString("drsg_txt", " "));
            startActivity(intent);
        });
        fluImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("flu_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("flu_type", " ")),
                    "流感指数",
                    prefs.getString("flu_brf", " "),
                    prefs.getString("flu_txt", " "));
            startActivity(intent);
        });
        sportImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("sport_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("sport_type", " ")),
                    "运动指数",
                    prefs.getString("sport_brf", " "),
                    prefs.getString("sport_txt", " "));
            startActivity(intent);
        });
        travImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("trav_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("trav_type", " ")),
                    "旅行指数",
                    prefs.getString("trav_brf", " "),
                    prefs.getString("trav_txt", " "));
            startActivity(intent);
        });
        uvImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("uv_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("uv_type", " ")),
                    "紫外线强度",
                    prefs.getString("uv_brf", " "),
                    prefs.getString("uv_txt", " "));
            startActivity(intent);
        });
        cwImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("cw_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("cw_type", " ")),
                    "洗车指数",
                    prefs.getString("cw_brf", " "),
                    prefs.getString("cw_txt", " "));
            startActivity(intent);
        });
        airImageView.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("air_data", MODE_PRIVATE);
            Intent intent = ShowLifestyleActivity.sendInfo(getApplicationContext(),
                    MatchImageUtil.matchImage(prefs.getString("air_type", " ")),
                    "空气质量",
                    prefs.getString("air_brf", " "),
                    prefs.getString("air_txt", " "));
            startActivity(intent);
        });
    }

}
