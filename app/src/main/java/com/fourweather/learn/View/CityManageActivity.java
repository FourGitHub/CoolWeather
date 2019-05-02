package com.fourweather.learn.View;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fourweather.learn.R;
import com.fourweather.learn.entity.CityWeaInfo;
import com.fourweather.learn.entity.WeatherEntity;
import com.fourweather.learn.adapter.CityManageAdapter;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.img_add_city)
    ImageView imgAddCity;
    @BindView(R.id.img_toolbar_title_back)
    ImageView imgToolbarTitleBack;
    @BindView(R.id.add_city_item_container)
    LinearLayout addCityItemContainer;
    @BindView(R.id.outer_container)
    LinearLayout outerContainer;

    private List<WeatherEntity> weatherEntityList;
    private View addCityItemView;
    private CityManageAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private boolean isAddCityItemOpen = false;

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
     * <p>
     * BUG：如果用户在此界面直接杀死程序，则用户的更改来不及写入数据库
     */
    @Override
    protected void onPause() {
        super.onPause();
        int i = weatherEntityList.size();
        Gson gson = new Gson();
        String weaInfoJSON;
        if (i > 0) {
            for (int pos = 0; pos < i; pos++) {
                weaInfoJSON = gson.toJson(weatherEntityList.get(pos));
                String cid = weatherEntityList.get(pos).getHeWeather6().get(0).getBasic().getCid();
                CityWeaInfo cityWeaInfo = new CityWeaInfo((long) pos, cid, weaInfoJSON);
                /*GreenDao 版本A
                 APP.getDaoSession().insertOrReplace(cityWeaInfo);
                 */
                cityWeaInfo.saveOrUpdate("cid like ?", cid);
            }
        }

    }


    private void init() {
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
                    // 往下移动
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
                if (mAdapter.getItemCount() <= 1) {
                    return;
                }
                int position = viewHolder.getAdapterPosition();
                String cid = weatherEntityList.get(position).getHeWeather6().get(0).getBasic().getCid();
                /* 使用GreenDao版本
                List<CityWeaInfo> cityWeaInfos = APP.getDaoSession().queryRaw(CityWeaInfo.class, " where cid = ?", cid);
                「 or 」
                List<CityWeaInfo> cityWeaInfos = APP.getDaoSession().queryBuilder(CityWeaInfo.class).where(CityWeaInfoDao.Properties.Cid.eq(cid)).list();

                List<CityWeaInfo> cityWeaInfos = LitePal.where("cid = ?",cid).find(CityWeaInfo.class);
                if (cityWeaInfos.size() > 0) {
                    // 由于 cid 为 @unique,所以数据库中最多只有一项
                    APP.getDaoSession().delete(cityWeaInfos.get(0));
                }
                 */
                LitePal.deleteAll(CityWeaInfo.class, "cid like ?", cid);
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
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#aa87cefa"));
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

    private void initRecyclerSource() {
        weatherEntityList = new ArrayList<>();
        /* 使用GreenDao的版本
           List<CityWeaInfo> cityWeaInfoList =  APP.getDaoSession().queryBuilder(CityWeaInfo.class).orderAsc(CityWeaInfoDao.Properties.Pos).list();
         */
        List<CityWeaInfo> cityWeaInfoList = LitePal.order("pos asc").find(CityWeaInfo.class);
        int count = cityWeaInfoList.size();
        Gson gson = new Gson();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                CityWeaInfo cityWeaInfo = cityWeaInfoList.get(i);
                WeatherEntity weatherEntity = gson.fromJson(cityWeaInfo.getJsonString(), WeatherEntity.class);
                weatherEntityList.add(weatherEntity);
                Log.i(TAG, "initRecyclerSource: -->>> DBpos = " + cityWeaInfo.getPos());
            }
        }
    }

    private void initAddCityItemView() {
        addCityItemView = getLayoutInflater().inflate(R.layout.recycler_city_manage_item_addcity, addCityItemContainer, false);
        addCityItemView.setOnClickListener(v -> {
            Intent intent = new Intent(CityManageActivity.this, SearchLocActivity.class);
            // 因为是从 CityManageActivity.this 启动 SearchLocActivity.class
            // 所以，SearchLocActivity 的 parentAc 不是 WeatherAc
            intent.putExtra("parentAcIsWeatherAc", false);
            startActivity(intent);
            finish();
        });
    }


    @Override
    // WeatherActivity1 # startActivityForResult(...)
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @OnClick({R.id.img_toolbar_title_back, R.id.img_add_city})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_toolbar_title_back:
                finish();
                break;
            case R.id.img_add_city:
                if (addCityItemView == null) {
                    initAddCityItemView();
                }
                if (isAddCityItemOpen) {
                    addCityItemContainer.removeView(addCityItemView);
                    isAddCityItemOpen = false;
                } else {
                    addCityItemContainer.addView(addCityItemView, 0);
                    isAddCityItemOpen = true;
                }
                break;
        }
        imgAddCity.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imgAddCity.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imgAddCity.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).rotationBy(45);
    }

}
