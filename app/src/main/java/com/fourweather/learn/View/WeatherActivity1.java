package com.fourweather.learn.View;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fourweather.learn.R;
import com.fourweather.learn.entity.BiYingEntity;
import com.fourweather.learn.entity.CityWeaInfo;
import com.fourweather.learn.entity.SearchedCities;
import com.fourweather.learn.entity.WeatherEntity;
import com.fourweather.learn.service.AutoUpdateService;
import com.fourweather.learn.utils.DensityUtil;
import com.fourweather.learn.utils.HttpBiYingService;
import com.fourweather.learn.utils.HttpSearchCity;
import com.fourweather.learn.utils.HttpUtil;
import com.fourweather.learn.utils.HttpWeatherEntity;
import com.fourweather.learn.utils.ToastUtil;
import com.fourweather.learn.adapter.WeaPagerAdapter;
import com.fourweather.learn.utils.WeatherHandler;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Activity主要负责创建、管理碎片实例（显示相应cid的Fragment）
 * <p>
 * 项目中关于数据库部分设计了 LitePal 和 GreenDao 两个版本，但最终使用的是LitePal
 * 是因为多次测试和修改，任然发现 GreenDao 会不定期清空CityWeaInfo表的 pos列数据，WTF ???
 * 使得我无法排序查询数据库，最终无法维护ViewPager中Frg的顺序。
 */
public class WeatherActivity1 extends BaseLocActivity {
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
    @BindView(R.id.img_loading_city)
    ImageView imgLoadingCity;


    PopupWindow popupWindow;

    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 1;
    private static final int MANAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int MAX_WEA_FRG_COUNT = 15;

    private List<WeatherFrag> mWeatherFragList = new ArrayList<>();
    private List<String> mCidList = new ArrayList<>();
    private WeaPagerAdapter mAdapter = null;
    private int curFrgPos;
    private boolean isLoadingCity;
    private WeatherHandler mWeaHandler;


    /*
     * 一个有意思的现象：轻触back键，Activity的onDestory会被调用，下次返回应用，会重新创建Activity
     *                直接按下back键回到桌面，反而只调用到onStop，符合逻辑！
     * 这是为什么呢？按照常理，应该只调用到onStop，难道是因为ViewPager的原因？*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather1);
        ButterKnife.bind(this);
        init();
        Log.i(TAG, "onCreate: -->>>");
    }

    private void init() {
        Log.i(TAG, "init: -->>>>");
        if (checkoutGPSLicence()) {
            requestGPSLicence();
        }
        TimePicker timePicker = new TimePicker(this);
        loadBingPic();
        initPopupWindow();
        setupWeatherFragmentList();
        refreshIndicator();
        mWeaHandler = new WeatherHandler(this);
        mAdapter = new WeaPagerAdapter(getSupportFragmentManager(), mWeatherFragList);
        weatherPager.setOffscreenPageLimit(0);
        weatherPager.setAdapter(mAdapter);

        /*
        需要知道的是进入APP时，并不会触发PageChange事件，只有当多个WeatherFragment左右滑动切换的时候才会触发,
        所以就需要解决一个问题：进入APP时如何初始化Activity的title
         */
        weatherPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curFrgPos = position;
                Log.i(TAG, "onPageSelected: curFrgPos = " + position);
                refreshTitle();
                refreshIndicator();
                // 选中当前frg之后，自动执行刷新逻辑
                if (mWeatherFragList.get(position) != null) {
                    mWeatherFragList.get(position).refreshWeatherEntity();
                }

                /* 调试信息：查看城市Frg在ViewPager中的pos
                 List<CityWeaInfo> cityWeaInfos = WeatherFrag.getmCityWeaInfoList();
                Gson gson = new Gson();
                WeatherEntity weatherEntity;
                for (int i = 0; i < cityWeaInfos.size(); i++) {
                    weatherEntity = gson.fromJson(cityWeaInfos.get(i).getJsonString(), WeatherEntity.class);
                    Log.i(TAG, "onPageSelected: listPos = " + i + weatherEntity.getHeWeather6().get(0).getBasic().getLocation());
                }
                 */
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(imgLoadingCity, "rotation", 360);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setDuration(1000);
        rotateAnim.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent: -->>>>");
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("is", false) && intent.getStringExtra("cid") != null) {
            requestAndUpdateDB(intent.getStringExtra("cid"), false);
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: -->>>>");
        super.onStop();
        if (drawerLayout != null && drawerLayout.isAttachedToWindow()) {
            drawerLayout.closeDrawers();
        }

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void afterLocateSuccess(double longitude, double latitude) {
        super.afterLocateSuccess(longitude, latitude);
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
                    if (!mCidList.contains(cid)) {
                        new AlertDialog.Builder(WeatherActivity1.this).setPositiveButton("确定", (dialog, which) -> {
                            requestAndUpdateDB(cid, true);
                            dialog.cancel();
                        }).setNegativeButton("取消", (dialog, which) -> dialog.cancel()).setCancelable(true).setMessage("您目前位于『" + location + "』\n" + "是否获取当地天气信息？").show();
                    }
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        HttpSearchCity.getInstance().getSearchedCity(observer, longitude + "," + latitude, 1);
    }

    /* 由于我的 Title属于Activity部分，不属于Frg部分，所以需要通过监听 ViewPager 的滑动 并 更改Ac的Title
       但是，启动应用的时候，PageChangeListener没有被触发，所以使用这种方式来初始化Ac的Title */
    private void initTitle(CityWeaInfo firstWeaInfo) {
        Log.i(TAG, "initTitle: ---->>>> curPos = " + curFrgPos);
        WeatherEntity weatherEntity = new Gson().fromJson(firstWeaInfo.getJsonString(), WeatherEntity.class);
        String updateTime = weatherEntity.getHeWeather6().get(0).getUpdate().getLoc().split(" ")[1];
        String titleCityName = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        titleCity.setText(titleCityName);
        titleUpdateTime.setText(String.format("最近更新 %s", updateTime));
    }

    private void initPopupWindow() {
        /* 加载布局 */
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pop_window, null);
//        TextView tvFastCheckout = view.findViewById(R.id.tv_fast_checkout);
        TextView tvCitySearch = view.findViewById(R.id.tv_city_search);
        TextView tvCityManage = view.findViewById(R.id.tv_city_manage);
        TextView tvWeatherShare = view.findViewById(R.id.tv_weather_share);
        TextView tvSechedule = view.findViewById(R.id.tv_sechedule_manage);

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

        tvCitySearch.setOnClickListener(v -> {

            Intent intent = new Intent(WeatherActivity1.this, SearchLocActivity.class);
            intent.putExtra("parentAcIsWeatherAc", true);
            startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
        });

        tvCityManage.setOnClickListener(v -> {
            startActivityForResult(new Intent(WeatherActivity1.this, CityManageActivity.class), MANAGE_ACTIVITY_REQUEST_CODE);
        });

        tvWeatherShare.setOnClickListener(v -> {
            /* Intent传递的 cid 之所以不使用 mWeatherFragList.get(curPos).getmCid 的返回值
             * 是因为当从「城市管理Ac」返回后，afterManage方法会「clear mWeatherFragList」,所以...NPE.
             * 但是也会「clear mCidList」*/
            Intent intent = new Intent(WeatherActivity1.this, ShareActivity.class);
            intent.putExtra(ShareActivity.SHARE_CID, mCidList.get(curFrgPos));
            startActivity(intent);
        });

        tvSechedule.setOnClickListener(v->{
            Intent intent = new Intent(WeatherActivity1.this, ScheduleActivity.class);
            startActivity(intent);
        });
    }


    /* 重建碎片，并且保证 mWeatherFragList 和 mCidList 元素之间是一一对应的关系
     * 既然是一一对应关系，为什么不采用 Map 结构呢？因为。。。。。。。 */
    private void setupWeatherFragmentList() {
        mWeatherFragList.clear();
        mCidList.clear();
        /* GreenDao 版本
           List<CityWeaInfo> mCityWeaInfoList = APP.getDaoSession().queryBuilder(CityWeaInfo.class).orderAsc(CityWeaInfoDao.Properties.Pos).list();
         */

        List<CityWeaInfo> mCityWeaInfoList = LitePal.order("pos asc").find(CityWeaInfo.class);
        //        if (mCityWeaInfoList.size() != 0) {
        //            while (mCityWeaInfoList.get(0).getPos() == null) {
        //                mCityWeaInfoList = APP.getDaoSession().queryBuilder(CityWeaInfo.class).orderAsc(CityWeaInfoDao.Properties.Pos).list();
        //            }
        //        }

        //        List<CityWeaInfo> mCityWeaInfoList = APP.getDaoSession().loadAll(CityWeaInfo.class);
        WeatherFrag.setCityWeaInfos(mCityWeaInfoList);
        // 数据库没有，默认显示北京
        if (mCityWeaInfoList.size() == 0) {
            mWeatherFragList.add(WeatherFrag.getInstance(WeatherFrag.FLAG_INIT_DEFAULT));
            mCidList.add(WeatherFrag.DEFAULT_CID);
            return;
        }

        for (int pos = 0; pos < mCityWeaInfoList.size(); pos++) {
            if (pos == 0) {
                initTitle(mCityWeaInfoList.get(0));
            }
            mWeatherFragList.add(WeatherFrag.getInstance(pos));
            mCidList.add(mCityWeaInfoList.get(pos).getCid());
            // GreenDao版本，由于pos列数据擦除问题，当时的想法是：每次创建 mWeatherFragList 时，都为数据库中pos列重新赋值，结果并没什么卵用
            //            CityWeaInfo cityWeaInfo = mCityWeaInfoList.get(pos);
            //            Log.i(TAG, "setupWeatherFragmentList: -->>> DBpos = " + cityWeaInfo.getPos());
            //            cityWeaInfo.setPos((long) pos);
            //            APP.getDaoSession().insertOrReplace(cityWeaInfo);
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
            Glide.with(WeatherActivity1.this).load(bingPicUrl).animate(R.anim.glide_pic_enter).error(R.drawable.place_holder).into(bingPicImg);
            // 之前在这里就return的原因是：如果后台服务成功的更新了bing_pic_url....,那么就不必再重新请求了
            // 但是，因为 Andorid 8.0 后的后台限制，导致后台服务不能启动，所以...我注释掉了这里的return
            // return;
        }

        if (HttpUtil.isNetworkEnable(getApplicationContext())) {
            Observer<BiYingEntity> observer = new Observer<BiYingEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(BiYingEntity biYingEntity) {
                    // 接口只返回了后面的路径、前面的协议和主机需要加上去组合成一个完整的Url
                    String bingPicUr = "http://cn.bing.com" + biYingEntity.getImages().get(0).getUrl();
                    // 请求成功，则把图片链接保存起来，备用
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity1.this).edit();
                    editor.putString("bing_pic_url", bingPicUr);
                    editor.apply();
                    Log.i(TAG, "onResponse: --->> biyingurl = " + bingPicUr);

                    Glide.with(WeatherActivity1.this).load(bingPicUr).animate(R.anim.glide_pic_enter).error(R.drawable.place_holder).into(bingPicImg);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            };
            HttpBiYingService.getInstance().getBiYingPicInfo(observer);

        } else {
            Glide.with(WeatherActivity1.this).load(R.drawable.place_holder).animate(R.anim.glide_pic_enter).into(bingPicImg);
        }
    }


    public void requestAndUpdateDB(String cid, boolean isFromAutoLoc) {
        // 如果选择的城市已经在已有的城市列表中，则直接跳转到对应的 Frg
        if (mCidList.contains(cid)) {
            curFrgPos = mCidList.indexOf(cid);

            // setCurrentItem 会触发 onPageSelected回调
            weatherPager.setCurrentItem(curFrgPos);
            return;
        }

        if (mWeatherFragList.size() >= MAX_WEA_FRG_COUNT) {
            ToastUtil.showToast(APP.getContext(), "您只能最多同时关注 15 个城市的天气信息", Toast.LENGTH_SHORT);
            return;
        }

        if (isLoadingCity) {
            ToastUtil.showToast(APP.getContext(), "正在加载上一个城市天气信息", Toast.LENGTH_SHORT);
            return;
        }

        isLoadingCity = true;
        imgLoadingCity.setVisibility(View.VISIBLE);
        // 如果...不在...,那么数据库里面也不会有，因为mCidList是根据数据库里的最新信息创建的
        // 所以，这时候需要请求并保存天气信息（到数据库）
        if (HttpUtil.isNetworkEnable(this)) {
            Observer<WeatherEntity> observer = new Observer<WeatherEntity>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(WeatherEntity weatherEntity) {
                    isLoadingCity = false;
                    imgLoadingCity.setVisibility(View.GONE);
                    if (weatherEntity.getHeWeather6().get(0).getStatus().toLowerCase().equals("ok")) {

                        // 用返回的WeatherEntity,取出cid,两者再包装一个CityWeaInfo对象添加到数据库
                        Gson gson = new Gson();
                        String weatherInfoJSON = gson.toJson(weatherEntity);
                        String cid = weatherEntity.getHeWeather6().get(0).getBasic().getCid();
                        CityWeaInfo cityWeaInfo = new CityWeaInfo((long) WeatherFrag.getmCityWeaInfoList().size(), cid, weatherInfoJSON);
                        /* GreenDao 版本
                           APP.getDaoSession().insertOrReplace(cityWeaInfo);
                         */
                        if (isFromAutoLoc) {
                            // 如果是自动定位获取到的位置，且不在已有城市列表里，说明用户可能到了另外的城市，现在，自动将当前城市放在第一个展示页
                            /* 这里之前的设计是：直接把新的Frg插入mWeatherFragList头部，然后notifyDataSetChanged
                                              结果发现会抛出：java.lang.IllegalStateException: Can't change tag of fragment WeatherFrag{...}
                                              该非法状态异常是说不能修改Fragment的Tag（Tag和ViewPager的缓存机制紧密相连）
                                              因为pos = 0 位置的Frg，已经在 FragmentPagerAdapter#instantiateItem方法中设置过了*/
                            List<CityWeaInfo> cityWeaInfos = LitePal.order("pos asc").find(CityWeaInfo.class);
                            CityWeaInfo weaInfo;
                            for (int pos = 0; pos < cityWeaInfos.size(); pos++) {
                                weaInfo = cityWeaInfos.get(pos);
                                weaInfo.setPos((long) (pos + 1));
                                weaInfo.save();
                            }
                            cityWeaInfo.setPos((long) 0);
                            cityWeaInfo.saveOrUpdate("cid like ?", cid);
                            reInitWeaFrgPager();
                        } else {
                            // 否则，说明是用户新增关注了已管理城市之外的其他城市，这时候，放到最后一个展示页即可
                            curFrgPos = mWeatherFragList.size();
                            WeatherFrag.addCityWeaInfoToLast(cityWeaInfo);
                            cityWeaInfo.saveOrUpdate("cid like ?", cid);
                            mWeatherFragList.add(curFrgPos, WeatherFrag.getInstance(curFrgPos));
                            mAdapter.notifyDataSetChanged();
                            mCidList.add(curFrgPos, cid);
                            weatherPager.setCurrentItem(curFrgPos);
                            refreshTitle();
                        }

                    } else {
                        failed("status code error 获取天气信息失败！");
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
            failed("网络连接状态异常 Ծ‸Ծ");
        }
    }

    private void failed(String msg) {
        isLoadingCity = false;
        imgLoadingCity.setVisibility(View.GONE);
        ToastUtil.showToast(APP.getContext(), msg, Toast.LENGTH_SHORT);
    }

    public void refreshFrgmentList(int position, WeatherFrag newFrg) {
        mWeatherFragList.set(position, newFrg);
    }

    /**
     * 页面切换的时候，页面指示器随着改变
     */
    private void refreshIndicator() {
        if (mWeatherFragList.size() == 1) {
            dotLayout.setVisibility(View.INVISIBLE);
            return;
        }
        dotLayout.setVisibility(View.VISIBLE);
        List<ImageView> dotViewsList = new ArrayList<>();
        dotLayout.removeAllViews();
        int size = mWeatherFragList.size();
        for (int count = 0; count < size; count++) {
            ImageView dotView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

    public void refreshTitle() {
        Log.i(TAG, "refreshTitle: ---->>>> curPos = " + curFrgPos);
        String weaJSONString = WeatherFrag.getmCityWeaInfoList().get(curFrgPos).getJsonString();
        Gson gson = new Gson();
        WeatherEntity weatherEntity = gson.fromJson(weaJSONString, WeatherEntity.class);
        String updateTime = weatherEntity.getHeWeather6().get(0).getUpdate().getLoc().split(" ")[1];
        String titleCityName = weatherEntity.getHeWeather6().get(0).getBasic().getLocation();
        titleUpdateTime.setText(String.format("最近更新 %s", updateTime));
        titleCity.setText(titleCityName);
    }

    /**
     * 从城市管理Activity返回 / 定位之后，重建mWeatherFragmentList
     * 1、因为城市管理Activity里用户可能有改变顺序或者删除城市的可能性
     * 2、定位后，将把定位城市展示在Pager首页,如果只是简单的: 添加到mWeatherFragmentList头部 ->
     * -> weatherPager.notifyDataSetChanged -> curPos = 0 -> weatherPager.setCurrentItem(curFrgPos)
     * 会抛出 java.lang.IllegalStateException: Can't change tag of fragment WeatherFrag {...}
     */
    private void reInitWeaFrgPager() {
        curFrgPos = 0;
        setupWeatherFragmentList();
        mAdapter.notifyDataSetChanged();
        weatherPager.setCurrentItem(curFrgPos);
        refreshIndicator();
    }


    /**
     * 从SearchActivity选择城市返回后刷新数据库，重建碎片实例
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult -->>>> : ");
        switch (requestCode) {
            case SEARCH_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String cid = data.getStringExtra("cid");
                    requestAndUpdateDB(cid, false);
                }
                break;
            case MANAGE_ACTIVITY_REQUEST_CODE:
                reInitWeaFrgPager();
                break;
        }
    }

    /**
     * 检查用户是否对应用授权GPS
     */
    private boolean checkoutGPSLicence() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestGPSLicence() {
        ActivityCompat.requestPermissions(WeatherActivity1.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


}

