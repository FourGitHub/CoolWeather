package com.coolweather.android.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.utils.HttpCallbackListener;
import com.coolweather.android.utils.HttpUtil;
import com.coolweather.android.utils.PCCUtil;
import com.coolweather.android.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private Context mContext;

    private CustomSwipeToRefresh swipeRefreshLayout;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>(); // 这里记得为它分配空间，不仅仅是声明！！！

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel = LEVEL_PROVINCE;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText =  view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_frg);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext().getApplicationContext();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
                swipeRefreshLayout.setRefreshing(true);
                if (HttpUtil.networkStatus(mContext)) {
                    switch (currentLevel) {
                        case LEVEL_PROVINCE:
                            queryProvinces();
                            break;
                        case LEVEL_CITY:
                            queryCities();
                            break;
                        case LEVEL_COUNTY:
                            queryCounties();
                            break;
                    }

                } else {
                    ToastUtil.showToast(mContext,"请检查您的网络连接...", Toast.LENGTH_SHORT);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    /*
                     * 选择County级后就应该加载天气信息(WeatherActivity发起络请求、解析数据并显示)，这里有两种可能：
                     * 1. 此时在MainActivity中，此时应跳转到WeatherActivity,并向它传递weatherId,
                     *    用于WeatherActivity构建发起网络请求的url
                     * 2. 此时已经在WeatherActivity中，说明此操作是用户在切换城市，自然也应该加载天气信息，不过是直接调用
                     *    WeatherActivity的requestWeather()方法
                     */
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
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
            swipeRefreshLayout.setRefreshing(false);
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            swipeRefreshLayout.setRefreshing(false);
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
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            swipeRefreshLayout.setRefreshing(false);
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
        if (HttpUtil.networkStatus(getActivity().getApplicationContext())) {
            HttpUtil.sendHttpUrlConnectionResquest(address, new HttpCallbackListener() {
                @Override
                public void onFailure(Exception e) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            closeProgressDialog();
                            ToastUtil.showToast(getContext(), "加载失败 Ծ‸Ծ", Toast.LENGTH_SHORT);
                        }
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if ("province".equals(type)) {
                                    queryProvinces();
                                } else if ("city".equals(type)) {
                                    queryCities();
                                } else if ("county".equals(type)) {
                                    queryCounties();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            ToastUtil.showToast(mContext,"请检查您的网络连接...", Toast.LENGTH_SHORT);
            swipeRefreshLayout.setRefreshing(false);
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

}
