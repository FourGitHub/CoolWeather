package com.fourweather.learn.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourweather.learn.R;
import com.fourweather.learn.customView.SunriseView;
import com.fourweather.learn.entity.CityWeaInfo;
import com.fourweather.learn.entity.WeatherEntity;
import com.fourweather.learn.utils.HttpUtil;
import com.fourweather.learn.utils.HttpWeatherEntity;
import com.fourweather.learn.utils.MatchImageUtil;
import com.fourweather.learn.utils.ToastUtil;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
public class WeatherFrag extends Fragment {

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
    LinearLayout clothesImage;
    @BindView(R.id.air_image)
    LinearLayout airImage;
    @BindView(R.id.uv_iamge)
    LinearLayout uvIamge;
    @BindView(R.id.sports_image)
    LinearLayout sportsImage;
    @BindView(R.id.car_wash_image)
    LinearLayout carWashImage;
    @BindView(R.id.flu_image)
    LinearLayout fluImage;
    @BindView(R.id.comf_image)
    LinearLayout comfImage;
    @BindView(R.id.travel_image)
    LinearLayout travelImage;
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

    private static final String TAG = "WeatherFrag";
    private static final String FRG_POS = "frg_pos";
    // 如果数据库中没有缓存的信息，默认请求并缓存北京的天气信息
    static final String DEFAULT_CID = "CN101010100";
    static final int FLAG_INIT_DEFAULT = -1;
    private static final int HALF_HOUR = 30*60*1000;

    private static List<CityWeaInfo> mCityWeaInfoList = null;

    private Context mContext = null;
    private WeatherEntity mWeaEntity = null;
    private String mCid;
    private int mPosition;
    private long lastUpdateTime;
    Unbinder unbinder;

    public WeatherFrag() {
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
        lastUpdateTime = System.currentTimeMillis();
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
            CityWeaInfo weaInfo = mCityWeaInfoList.get(mPosition);
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
    private void requestWeatherEntity(String cid) {
        if (HttpUtil.isNetworkEnable(APP.getContext())) {
            Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onNext(WeatherEntity weatherEntity) {
                    if (weatherEntity.getHeWeather6()
                                     .get(0)
                                     .getStatus()
                                     .toLowerCase()
                                     .equals("ok")) {

                        lastUpdateTime = System.currentTimeMillis();

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
                        CityWeaInfo weather;
                        if (mPosition != FLAG_INIT_DEFAULT) {
                            weather = new CityWeaInfo((long) mPosition, mCid, weatherInfoJSON);
                        } else {
                            weather = new CityWeaInfo((long) 0, mCid, weatherInfoJSON);
                        }


                        /*
                        4、刷新成功后更换相应位置的碎片实例，这里不必要NotifyDataSetChange,因为我是直接换的碎片实例
                        下次Adapter再选中这个位置的Fragment时，已经悄然的改变为最新的实例,如果不做这一步，当下次用户重新切换
                        回来的时候，显示的将是刷新之前的旧信息，这些旧信息是在创建碎片实例时由数据库中的缓存生成的。

                        之所以这样做是为了减小性能消耗，不用频繁的NotifyDataSetChange，因为NotifyDataSetChange会
                        用数据库里的信息重新创建所有的碎片实例。
                        */
                        if (mPosition != FLAG_INIT_DEFAULT) {
                            mCityWeaInfoList.set(mPosition, weather);
                            WeatherFrag newFrg = WeatherFrag.getInstance(mPosition);
                            newFrg.mWeaEntity = weatherEntity;
                            ((WeatherActivity1) getActivity()).refreshFrgmentList(mPosition, newFrg);
                        } else {
                            mCityWeaInfoList.add(weather);
                            mPosition = 0;
                        }
                        ((WeatherActivity1) getActivity()).refreshTitle();
                        /* 使用 GreenDao版本
                           APP.getDaoSession().insertOrReplace(weather);
                         */
                        weather.setPos((long) mPosition);
                        weather.saveOrUpdate("pos = ?", mPosition + "");

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

    void refreshWeatherEntity() {
        if (System.currentTimeMillis() - lastUpdateTime > HALF_HOUR) {
            requestWeatherEntity(mCid);
        }
    }

    private void failed(String msg) {
        if (swipeRefresh != null) {
            ToastUtil.showToast(APP.getContext(), msg, Toast.LENGTH_SHORT);
            swipeRefresh.setRefreshing(false);
        }
    }

    /**
     * 更新当前碎片的视图
     */
    private void setFrgUI(WeatherEntity.HeWeather6Bean weather) {
        String degree = weather.getNow().getTmp() + "℃";
        String weatherInfo = weather.getNow().getCond_txt();
        feelTmpText.setText(String.format("体感温度: %s℃", weather.getNow().getFl()));
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        WeatherEntity.HeWeather6Bean.DailyForecastBean today = weather.getDaily_forecast().get(0);
        String sunrise = today.getSr().replace(":", ".");
        String sunset = today.getSs().replace(":", ".");
        String moonrise = today.getMr().replace(":", ".");
        String moonset = today.getMs().replace(":", ".");
        sunriseText.setText(String.format("日出 %s", sunrise));
        sunsetText.setText(String.format("日落 %s", sunset));
        startSunAnim(Float.valueOf(sunrise), Float.valueOf(sunset));
        for (WeatherEntity.HeWeather6Bean.DailyForecastBean forecast : weather.getDaily_forecast()) {

            View view = getLayoutInflater().inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCond_txt_d());
            maxText.setText(String.format("%s ℃", forecast.getTmp_max()));
            minText.setText(forecast.getTmp_min());
            forecastLayout.addView(view);
        }

        hourlyLayout.removeAllViews();
        popText.setText(String.format("降水概率: %s%%", weather.getHourly().get(0).getPop()));
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
    private void startSunAnim(float sunrise, float sunset) {
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


    static void setCityWeaInfos(List<CityWeaInfo> cityWeaInfoList) {
        mCityWeaInfoList = cityWeaInfoList;
    }

    static List<CityWeaInfo> getmCityWeaInfoList() {
        return mCityWeaInfoList;
    }

    static void addCityWeaInfoToLast(CityWeaInfo cityWeaInfo) {
        mCityWeaInfoList.add(mCityWeaInfoList.size(), cityWeaInfo);
    }

    public static WeatherFrag getInstance(int position) {
        WeatherFrag fragment = new WeatherFrag();
        Bundle bundle = new Bundle();
        bundle.putInt(FRG_POS, position);
        fragment.setArguments(bundle);
        return fragment;
    }

}
