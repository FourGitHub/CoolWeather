package com.coolweather.android.View;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.coolweather.learn.R;
import com.coolweather.android.entity.CityWeaInfo;
import com.coolweather.android.entity.WeatherEntity;
import com.coolweather.android.utils.CityManageAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 这个Activity有两个功能：1、用户可对城市Fragment排序 2、用户可删除城市
 * 而实现这两个功能只需要将用户的这些操作带来的改变及时的反应到数据库，此Activity返回后，
 * WeatherActivity1会用数据库里的信息重建城市Fragment列表。
 */
public class CityManageActivity extends AppCompatActivity {
    private static final String TAG = "CityManageActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_city_manage)
    RecyclerView recyclerCityManage;

    private List<WeatherEntity> weatherEntityList;
    private CityManageAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_city_manage);
        ButterKnife.bind(this);
        init();
    }


    /**
     * 先清空数据库，然后按照用户列表中的顺序，重建数据库
     */
    @Override
    protected void onPause() {
        super.onPause();
        APP.getDaoSession().deleteAll(CityWeaInfo.class);
        int i = weatherEntityList.size();
        Gson gson = new Gson();
        String cid;
        String weaInfoJSON;
        CityWeaInfo cityWeaInfo;
        if (i > 0) {
            for (int pos = 0; pos < i; pos++) {
                weaInfoJSON = gson.toJson(weatherEntityList.get(pos));
                cid = weatherEntityList.get(pos).getHeWeather6().get(0).getBasic().getCid();
                cityWeaInfo = new CityWeaInfo(null, cid, weaInfoJSON);
                APP.getDaoSession().insertOrReplace(cityWeaInfo);
            }
        }
    }

    private void init(){
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        initRecyclerSource();
        mAdapter = new CityManageAdapter(this, weatherEntityList);
        recyclerCityManage.setAdapter(mAdapter);
        recyclerCityManage.setLayoutManager(new LinearLayoutManager(this));

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlag, swipeFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    // 往下滑动
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(weatherEntityList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(weatherEntityList, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String cid = weatherEntityList.get(position).getHeWeather6().get(0).getBasic().getCid();
                // APP.getDaoSession().queryBuilder(CityWeaInfo.class).where(CityWeaInfoDao.Properties.Cid.eq(cid)).list();
                List<CityWeaInfo> cityWeaInfos = APP.getDaoSession().queryRaw(CityWeaInfo.class, " where cid = ?", cid);
                int i = cityWeaInfos.size();
                while (i > 0) {
                    APP.getDaoSession().delete(cityWeaInfos.get(--i));
                }
                weatherEntityList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#f8eab4"));
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        });
        mItemTouchHelper.attachToRecyclerView(recyclerCityManage);
        mAdapter.setItemTouchHelper(mItemTouchHelper);
    }

    private void initRecyclerSource(){
        weatherEntityList = new ArrayList<>();
        List<CityWeaInfo> cityWeaInfoList = APP.getDaoSession().loadAll(CityWeaInfo.class);
        int count = cityWeaInfoList.size();
        if (count > 0) {
            Gson gson = new Gson();
            for (int i = 0; i < count; i++) {
                CityWeaInfo cityWeaInfo = cityWeaInfoList.get(i);
                WeatherEntity weatherEntity = gson.fromJson(cityWeaInfo.getJsonString(), WeatherEntity.class);
                weatherEntityList.add(weatherEntity);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
