package com.coolweather.android.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.entity.BiYing;
import com.coolweather.android.entity.CityWeaInfo;
import com.coolweather.android.entity.WeatherEntity;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.utils.DensityUtil;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.HttpWeatherEntity;
import com.coolweather.android.utils.ToastUtil;
import com.coolweather.android.utils.WeaPagerAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Activity主要负责创建、管理碎片实例（显示相应的Fragment）
 */
public class WeatherActivity1 extends AppCompatActivity {
    private static final String TAG = "WeatherActivity1";
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.img_nav)
    ImageView imgNav;
    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.dot_layout)
    LinearLayout dotLayout;
    @BindView(R.id.weather_pager)
    ViewPager weatherPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    PopupWindow popupWindow;

    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 1;
    private static final int MANAGE_ACTIVITY_REQUEST_CODE = 2;
    private List<WeatherFragment> mWeatherFragmentList = new ArrayList<>();
    private WeaPagerAdapter mAdapter = null;
    private int curFrgPos;

    public int getCurFrgPos() {
        return curFrgPos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather1);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        loadBingPic();
        initPopupWindow();
        setupWeatherFragmentList();
        refreshIndicator();
        mAdapter = new WeaPagerAdapter(getSupportFragmentManager(), mWeatherFragmentList);
        weatherPager.setAdapter(mAdapter);

        /*
        需要知道的是进入APP时，并不会触发PageChange事件，只有当多个WeatherFragment左右滑动切换的时候才会触发,
        所以就需要解决一个问题：进入APP时如何初始化Activity的title，经过Log可以发现，
         */
        weatherPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curFrgPos = position;
                refreshTitle(position);
                refreshIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: --->> page0 entity = " + mWeatherFragmentList.get(0).getWeaEntity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: --->>page0 entity = " + mWeatherFragmentList.get(0).getWeaEntity());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 启动应用的时候，由于PageChangeListener没有被触发，所以以这种方式来初始化title,
     * 调用方为position为0的WeatherFragment,调用时机为它的onResume()
     * 之所以在WeatherFragment#onResume()是为了确保相应的Fragment已经实例化好，避免NPE
     */
    public void initTitle(String updateTime, String titleCityName) {
        titleCity.setText(titleCityName);
        titleUpdateTime.setText("最近更新 " + updateTime);
    }

    private void initPopupWindow() {
        /* 加载布局 */
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pop_window, null);
        TextView tvFastCheckout = view.findViewById(R.id.tv_fast_checkout);
        TextView tvCitySearch = view.findViewById(R.id.tv_city_search);
        TextView tvCityManage = view.findViewById(R.id.tv_city_manage);
        TextView tvWeatherShare = view.findViewById(R.id.tv_weather_share);

        /* 构造PopupWindow */
        popupWindow = new PopupWindow();

        /* 配置PopupWindow */
        popupWindow.setWidth(DensityUtil.dip2px(this, 120));
        popupWindow.setHeight(DensityUtil.dip2px(this, 150));
        popupWindow.setContentView(view);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.animPopWin);
        tvFastCheckout.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
            popupWindow.dismiss();
        });

        tvCitySearch.setOnClickListener(v -> {
            startActivityForResult(new Intent(WeatherActivity1.this, SearchActivity.class), SEARCH_ACTIVITY_REQUEST_CODE);
            popupWindow.dismiss();
        });

        tvCityManage.setOnClickListener(v -> {
            startActivityForResult(new Intent(WeatherActivity1.this, CityManageActivity.class), MANAGE_ACTIVITY_REQUEST_CODE);
            popupWindow.dismiss();
        });
    }

    private void setupWeatherFragmentList() {
        List<CityWeaInfo> weaInfoList = APP.getDaoSession().loadAll(CityWeaInfo.class);
        WeatherFragment.setCityWeaInfos(weaInfoList);
        // 数据库没有，默认显示北京
        if (weaInfoList.size() == 0) {
            mWeatherFragmentList.add(WeatherFragment.getInstance(WeatherFragment.FLAG_INIT_DEFAULT));
            return;
        }
        for (int pos = 0; pos < weaInfoList.size(); pos++) {
            mWeatherFragmentList.add(WeatherFragment.getInstance(pos));
        }
    }

    @OnClick(R.id.img_nav)
    public void onViewClicked() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showPopWindow();
        }
    }

    private void showPopWindow() {
        if (popupWindow != null) {
            popupWindow.showAsDropDown(imgNav, -DensityUtil.dip2px(this, 100), 0);
        }
    }

    private void loadBingPic() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPicUrl = prefs.getString("bing_pic_url", null);
        if (bingPicUrl != null) {
            Glide.with(WeatherActivity1.this)
                 .load(bingPicUrl)
                 .placeholder(R.drawable.place_holder)
                 .error(R.drawable.place_holder)
                 .into(bingPicImg);
            return;
        }
        if (HttpUtil.isNetworkEnable(getApplicationContext())) {
            String requestBingPic = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
            HttpUtil.sendHttpUrlConnectionResquest(requestBingPic, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> Glide.with(WeatherActivity1.this)
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
                            .getDefaultSharedPreferences(WeatherActivity1.this).edit();
                    editor.putString("bing_pic_url", bingPicUr);
                    editor.apply();
                    Log.i(TAG, "onResponse: --->> biyingurl = " + bingPicUr);
                    runOnUiThread(() -> Glide.with(WeatherActivity1.this)
                                             .load(bingPicUr)
                                             .placeholder(R.drawable.place_holder)
                                             .error(R.drawable.place_holder)
                                             .into(bingPicImg));
                }
            });

        } else {
            Glide.with(WeatherActivity1.this).load(R.drawable.place_holder)
                 .into(bingPicImg);
        }
    }

    private void requestAndUpdateDB(String cid) {
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
                        String cid = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
                        // 存储or更新数据库
                        CityWeaInfo weather = new CityWeaInfo(null, cid, weatherInfoJSON);
                        APP.getDaoSession().insertOrReplace(weather);
                        refreshViewPager();
                        refreshIndicator();
                        Log.i(TAG, "onNext: --->> weatherEntity = " + weatherInfoJSON);

                    } else {
                        failed("status code error获取天气信息失败！");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    failed("error 获取天气信息失败！");
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
        ToastUtil.showToast(this, msg, Toast.LENGTH_SHORT);
    }


    /**
     * 每次有新增、删除城市的时候就会调用此方法刷新ViewPager
     */
    private void refreshViewPager() {
        mWeatherFragmentList.clear();
        setupWeatherFragmentList();
        mAdapter.notifyDataSetChanged();
        curFrgPos = mWeatherFragmentList.size() - 1;
        weatherPager.setCurrentItem(curFrgPos);
    }

    public void refreshFrgmentList(int position, WeatherFragment newFrg) {
        mWeatherFragmentList.set(position, newFrg);
    }

    /**
     * 页面切换的时候，页面指示器随着改变
     */
    private void refreshIndicator() {
        if (mWeatherFragmentList.size() == 1) {
            dotLayout.setVisibility(View.INVISIBLE);
            return;
        }
        dotLayout.setVisibility(View.VISIBLE);
        List<ImageView> dotViewsList = new ArrayList<>();
        dotLayout.removeAllViews();
        int size = mWeatherFragmentList.size();
        for (int count = 0; count < size; count++) {
            ImageView dotView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 16;
            params.rightMargin = 16;
            dotView.setLayoutParams(params);
            dotLayout.addView(dotView);
            dotViewsList.add(dotView);
        }
        for (int pos = 0; pos < size; pos++) {
            if (pos != curFrgPos) {
                dotViewsList.get(pos).setImageResource(R.drawable.ic_dot_unselected);
                continue;
            }
            dotViewsList.get(pos).setImageResource(R.drawable.ic_dot_selected);
        }

    }

    private void refreshTitle(int position) {
        WeatherEntity weatherEntity = mWeatherFragmentList.get(position).getWeaEntity();
        String updateTime = weatherEntity.getHeWeather6().get(0).getUpdate().getLoc().split(" ")[1];
        String titleCityName = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        titleUpdateTime.setText("最近更新 " + updateTime);
        titleCity.setText(titleCityName);
    }

    public void refreshUpdateTime(String updateTime, String titleCityName) {
        titleUpdateTime.setText("最近更新 " + updateTime);
        titleCity.setText(titleCityName);
    }

    /**
     * 从城市管理Activity返回之后，重建mWeatherFragmentList
     * 因为城市管理Activity里用户可能有改变顺序或者删除城市的可能性
     */
    private void afterManage() {
        mWeatherFragmentList.clear();
        setupWeatherFragmentList();
        mAdapter.notifyDataSetChanged();
        curFrgPos = 0;
        weatherPager.setCurrentItem(curFrgPos);
        refreshIndicator();
    }

    /**
     * 从SearchActivity选择城市返回后刷新数据库，重建碎片实例
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String cid = data.getStringExtra("cid");
                    requestAndUpdateDB(cid);
                }
                break;
            case MANAGE_ACTIVITY_REQUEST_CODE:
                Log.i(TAG, "onActivityResult: --->> MANAGE_ACTIVITY_REQUEST_CODE");
                afterManage();
                break;
        }
    }

}
