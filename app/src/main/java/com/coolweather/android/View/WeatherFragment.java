package com.coolweather.android.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.customView.SunriseView;
import com.coolweather.android.entity.CityWeaInfo;
import com.coolweather.android.entity.WeatherEntity;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.HttpWeatherEntity;
import com.coolweather.android.utils.MatchImageUtil;
import com.coolweather.android.utils.ToastUtil;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description
 */
public class WeatherFragment extends Fragment {

    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.feel_tmp_text)
    TextView feelTmpText;
    @BindView(R.id.pop_text)
    TextView popText;
    @BindView(R.id.hourly_time_text)
    TextView hourlyTimeText;
    @BindView(R.id.hourly_weather_icon)
    ImageView hourlyWeatherIcon;
    @BindView(R.id.weather_condition_text)
    TextView weatherConditionText;
    @BindView(R.id.hourly_tmp_text)
    TextView hourlyTmpText;
    @BindView(R.id.hourly_forcast_lauout)
    HorizontalScrollView hourlyForcastLauout;
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
    @BindView(R.id.air_image)
    ImageView airImage;
    @BindView(R.id.uv_iamge)
    ImageView uvIamge;
    @BindView(R.id.sports_image)
    ImageView sportsImage;
    @BindView(R.id.car_wash_image)
    ImageView carWashImage;
    @BindView(R.id.flu_image)
    ImageView fluImage;
    @BindView(R.id.comf_image)
    ImageView comfImage;
    @BindView(R.id.travel_image)
    ImageView travelImage;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.clothes_text)
    TextView clothesText;
    @BindView(R.id.air_text)
    TextView airText;
    @BindView(R.id.uv_text)
    TextView uvText;
    @BindView(R.id.sports_text)
    TextView sportsText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.flu_text)
    TextView fluText;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.travel_text)
    TextView travelText;
    @BindView(R.id.hourly_layout)
    LinearLayout hourlyLayout;

    private static final String TAG = "WeatherFragment";
    private static final String FRG_POS = "frg_pos";
    private static final String DEFAULT_CID = "CN101010100";
    public static final int FLAG_INIT_DEFAULT = -1;
    private static List<CityWeaInfo> cityWeaInfoList = null;
    private Context mContext = null;
    private WeatherEntity mWeaEntity = null;
    private String mCid;
    private int mPosition;
    Unbinder unbinder;

    public WeatherEntity getWeaEntity() {
        return mWeaEntity;
    }

    public WeatherFragment() {
        Log.i(TAG, "构造方法 WeatherFragment() --->> : ");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: --->> mWeaEntity = " + mWeaEntity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(FRG_POS);
        }
        mContext = APP.getContext();
        Log.i(TAG, "onCreate: --->> mWeaEntity = " + mWeaEntity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        swipeRefresh.setOnRefreshListener(() -> requestWeatherEntity(mCid));
        if (mPosition == FLAG_INIT_DEFAULT) {
            requestWeatherEntity(DEFAULT_CID);
        } else {
            Gson gson = new Gson();
            CityWeaInfo weaInfo = cityWeaInfoList.get(mPosition);
            mWeaEntity = gson.fromJson(weaInfo.getJsonString(), WeatherEntity.class);
            mCid = mWeaEntity.getHeWeather6().get(0).getBasic().getCid();
            setFrgUI(mWeaEntity.getHeWeather6().get(0));
        }
        Log.i(TAG, "onCreateView: --->> mWeaEntity = " + mWeaEntity);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        Log.i(TAG, "onActivityCreated: --->> mWeaEntity = " + mWeaEntity);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: --->> mWeaEntity = " + mWeaEntity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((WeatherActivity1)mContext).getCurFrgPos() == 0 && mPosition == 0 && mWeaEntity != null) {
            String updateTime = mWeaEntity.getHeWeather6().get(0).getUpdate().getLoc().split(" ")[1];
            String titleCityName = mWeaEntity.getHeWeather6().get(0).getBasic().getLocation();
            ((WeatherActivity1)getActivity()).initTitle( updateTime,titleCityName );
        }
        Log.i(TAG, "onResume: inittitle --->> mWeaEntity = " + mWeaEntity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 有两个时机会调用此方法：
     * 1、初次安装APP，会发送请求并构造一个Fragment展示北京天气信息的碎片
     * 2、用户在当前页下拉刷新
     * <p>
     * 只要是成功完成一次request, 就更新WeatherActivity1的title
     */
    public void requestWeatherEntity(String cid) {
        if (HttpUtil.isNetworkEnable(mContext)) {
            Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(WeatherEntity weatherEntity) {
                    swipeRefresh.setRefreshing(false);
                    if (weatherEntity.getHeWeather6()
                                     .get(0)
                                     .getStatus()
                                     .toLowerCase()
                                     .equals("ok")) {
                        // 1、更新mWeaEntity
                        Gson gson = new Gson();
                        String weatherInfoJSON = gson.toJson(weatherEntity);
                        mWeaEntity = weatherEntity;
                        mCid = weatherEntity.getHeWeather6().get(0).getBasic().getCid();

                        /*
                        2、更新视图，NOTE: 现在Adapter显示的仍然是这个实例,FragmentStatePagerAdapter会在碎片
                        不可见时销毁视图，仅仅保留state,所以如果不做第四步的更换碎片实例，那么当用户切换碎片后，
                        下次在回到这个页面，将显示刷新前的数据。
                         */
                        setFrgUI(weatherEntity.getHeWeather6().get(0));

                        // 3、新增or更新数据库，同步数据库信息至最新
                        CityWeaInfo weather = new CityWeaInfo(null, mCid, weatherInfoJSON);
                        APP.getDaoSession().insertOrReplace(weather);

                        /*
                        4、刷新成功后更换相应位置的碎片实例，这里不必要NotifyDataSetChange,因为我是直接换的碎片实例
                        下次Adapter再选中这个位置的Fragment时，已经悄然的改变为最新的实例,如果不做这一步，当下次用户重新切换
                        回来的时候，显示的将是刷新之前的旧信息，这些旧信息是在创建碎片实例时由数据库中的缓存生成的。

                        之所以这样做是为了减小性能消耗，不用频繁的NotifyDataSetChange，因为NotifyDataSetChange会
                        用数据库里的信息重新创建所有的碎片实例。
                        */
                        if (mPosition != FLAG_INIT_DEFAULT) {
                            cityWeaInfoList.set(mPosition, weather);
                            WeatherFragment newFrg = WeatherFragment.getInstance(mPosition);
                            newFrg.mWeaEntity = weatherEntity;
                            ((WeatherActivity1) getActivity()).refreshFrgmentList(mPosition, newFrg);
                        }

                        // 更新 WeatherActiivty1标题栏
                        String updateTime = weatherEntity.getHeWeather6().get(0).getUpdate().getLoc().split(" ")[1];
                        String titleCityName = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
                        ((WeatherActivity1) getActivity()).refreshUpdateTime(updateTime,titleCityName);

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

                }
            };
            HttpWeatherEntity.getInstance().getWeatherEntity(observer, cid);
        } else {
            failed("请检查您的网络！");
        }

    }

    private void failed(String msg) {
        ToastUtil.showToast(mContext, msg, Toast.LENGTH_SHORT);
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 更新当前碎片的视图
     *
     * @param weather
     */
    private void setFrgUI(WeatherEntity.HeWeather6Bean weather) {
        String degree = weather.getNow().getTmp() + "℃";
        String weatherInfo = weather.getNow().getCond_txt();
        feelTmpText.setText("体感温度: " + weather.getNow().getFl() + "℃");
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        WeatherEntity.HeWeather6Bean.DailyForecastBean today = weather.getDaily_forecast().get(0);
        String sunrise = today.getSr().replace(":", ".");
        String sunset = today.getSs().replace(":", ".");
        String moonrise = today.getMr().replace(":", ".");
        String moonset = today.getMs().replace(":", ".");
        sunriseText.setText("日出 " + sunrise);
        sunsetText.setText("日落 " + sunset);
        startSunAnim(Float.valueOf(sunrise), Float.valueOf(sunset));
        for (WeatherEntity.HeWeather6Bean.DailyForecastBean forecast : weather.getDaily_forecast()) {

            View view = getLayoutInflater().inflate(R.layout.forecast_item, forecastLayout, false);
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
            View view = getLayoutInflater().inflate(R.layout.hourly_item, forecastLayout, false);
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
        for (WeatherEntity.HeWeather6Bean.LifestyleBean lifestyle : weather.getLifestyle()) {
            switch (lifestyle.getType()) {
                case "comf":
                    comfortText.setText(lifestyle.getBrf());
                    comfImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("comf"), "舒适度", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "drsg":
                    clothesText.setText(lifestyle.getBrf());
                    clothesImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("drsg"), "穿衣指数", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "flu":
                    fluText.setText(lifestyle.getBrf());
                    fluImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("flu"), "流感指数", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "sport":
                    sportsText.setText(lifestyle.getBrf());
                    sportsImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("sport"), "运动指数", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "trav":
                    travelText.setText(lifestyle.getBrf());
                    travelImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("trav"), "旅行指数", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "uv":
                    uvText.setText(lifestyle.getBrf());
                    uvIamge.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("uv"), "紫外线强度", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "cw":
                    carWashText.setText(lifestyle.getBrf());
                    carWashImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("cw"), "洗车指数", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
                case "air":
                    airText.setText(lifestyle.getBrf());
                    airImage.setOnClickListener(v -> {
                        Intent intent = ShowLifestyleActivity.sendInfo(mContext, MatchImageUtil.matchImage("air"), "空气质量", lifestyle.getBrf(), lifestyle.getTxt());
                        startActivity(intent);
                    });
                    break;
            }
        }

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

    public static void setCityWeaInfos(List<CityWeaInfo> cityWeaInfoList) {
        WeatherFragment.cityWeaInfoList = cityWeaInfoList;
    }

    public static WeatherFragment getInstance(int position) {
        Log.i(TAG, "getInstance: --->> position = " + position);
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRG_POS, position);
        fragment.setArguments(bundle);
        return fragment;
    }

}
