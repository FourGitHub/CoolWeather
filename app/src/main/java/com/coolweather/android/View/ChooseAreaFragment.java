package com.coolweather.android.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.entity.City;
import com.coolweather.android.entity.County;
import com.coolweather.android.entity.Province;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.PCCUtil;
import com.coolweather.android.utils.ToastUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.back_button)
    Button backButton;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.swipe_refresh_frg)
    SwipeRefreshLayout swipeRefreshFrg;
    Unbinder unbinder;

    private Context mContext;

    private ProgressDialog progressDialog;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>(); // 这里记得为它分配空间，不仅仅是声明！！！

    // 省列表
    private List<Province> provinceList;

    // 市列表
    private List<City> cityList;

    // 县列表
    private List<County> countyList;

    // 选中的省份
    private Province selectedProvince;

    // 选中的城市
    private City selectedCity;

    // 当前选中的级别
    private int currentLevel = LEVEL_PROVINCE;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ----> ViewGroup = " + container);
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        adapter = new ArrayAdapter<>(mContext, R.layout.recycler_city_list_item1, dataList);
        listView.setAdapter(adapter);

        // 为展示省市县的列表设置监听
        swipeRefreshFrg.setOnRefreshListener(() -> {
            swipeRefreshFrg.setColorSchemeResources(R.color.colorPrimaryDark);
            swipeRefreshFrg.setRefreshing(true);
            if (HttpUtil.isNetworkEnable(mContext)) {
                switch (currentLevel) {
                    case LEVEL_PROVINCE:
                        queryAndShowProvinces();
                        break;
                    case LEVEL_CITY:
                        queryAndShowCities();
                        break;
                    case LEVEL_COUNTY:
                        queryAndShowCounties();
                        break;
                }

            } else {
                ToastUtil.showToast(mContext, "请检查您的网络连接...", Toast.LENGTH_SHORT);
                swipeRefreshFrg.setRefreshing(false);
            }

        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryAndShowCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryAndShowCounties();
            } else if (currentLevel == LEVEL_COUNTY) {
                /*
                 * 选择County级后就应该加载天气信息(WeatherActivity发起络请求、解析数据并显示)，这里有两种可能：
                 * 1. 此时在MainActivity中，此时应跳转到WeatherActivity,并向它传递weatherId,
                 *    用于WeatherActivity构建发起网络请求的url
                 * 2. 此时已经在WeatherActivity中，说明此操作是用户在切换城市，自然也应该加载天气信息，不过是直接调用
                 *    WeatherActivity的requestWeather()方法
                 */
                String weatherId = countyList.get(position).getWeatherId();
                WeatherActivity activity = (WeatherActivity) getActivity();
                activity.drawerLayout.closeDrawers();
                activity.requestWeatherEntity(weatherId);

            }
        });

        // 默认显示最高级别的省级列表
        queryAndShowProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryAndShowProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        // 查询数据库
        if (provinceList.size() > 0) {
            // 清空当前碎片ListView中显示的数据，并重新将数据库中保存的数据添加到ListView
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
            swipeRefreshFrg.setRefreshing(false);
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryAndShowCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                          .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            swipeRefreshFrg.setRefreshing(false);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryAndShowCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId()))
                            .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            swipeRefreshFrg.setRefreshing(false);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        if (HttpUtil.isNetworkEnable(getActivity().getApplicationContext())) {
            HttpUtil.sendHttpUrlConnectionResquest(address, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    getActivity().runOnUiThread(() -> {
                        swipeRefreshFrg.setRefreshing(false);
                        closeProgressDialog();
                        ToastUtil.showToast(getContext(), "加载失败 Ծ‸Ծ", Toast.LENGTH_SHORT);
                    });
                }

                /**
                 * 根据查询的类型type,以便于正确匹配解析对应的类型
                 */
                @Override
                public void onResponse(final String response) {
                    boolean result = false;
                    if ("province".equals(type)) {
                        result = PCCUtil.handleProvinceResponse(response);
                    } else if ("city".equals(type)) {
                        result = PCCUtil.handleCityResponse(response, selectedProvince.getId());
                    } else if ("county".equals(type)) {
                        result = PCCUtil.handleCountyResponse(response, selectedCity.getId());
                    }
                    if (result) {
                        getActivity().runOnUiThread(() -> {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryAndShowProvinces();
                            } else if ("city".equals(type)) {
                                queryAndShowCities();
                            } else if ("county".equals(type)) {
                                queryAndShowCounties();
                            }
                        });
                    }
                }
            });
        } else {
            ToastUtil.showToast(mContext, "请检查您的网络连接...", Toast.LENGTH_SHORT);
            swipeRefreshFrg.setRefreshing(false);
            closeProgressDialog();
        }

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());

            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                if (currentLevel == LEVEL_COUNTY) {
                    queryAndShowCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryAndShowProvinces();
                }
                break;
        }
    }
}
