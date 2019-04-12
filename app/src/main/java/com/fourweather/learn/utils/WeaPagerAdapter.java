package com.fourweather.learn.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.fourweather.learn.View.WeatherFragment;

import java.util.List;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description FragmentStatePagerAdapter更加适用于展示大量的或动态的item，它仅仅会save用户访问过的item的state
 * 所以相对于 FragmentPagerAdapter，它有更少的内存占用。
 */
public class WeaPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "WeaPagerAdapter";
    private List<WeatherFragment> fragmentList;

    public WeaPagerAdapter(FragmentManager fm,List<WeatherFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem: --->> position = " + position + "item's weaEntity = " +fragmentList.get(position).getWeaEntity() );
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * 重写这个方法是实现刷新的关键，它默认返回 POSITION_UNCHANGED
     * @param object
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * {@link #POSITION_NONE} if the item is no longer present.
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        // 默认返回 PagerAdapter.POSITION_UNCHANGED
        return PagerAdapter.POSITION_NONE;
    }
}
