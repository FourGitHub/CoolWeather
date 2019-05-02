package com.fourweather.learn.adapter;

import android.util.Log;
import android.view.ViewGroup;

import com.fourweather.learn.View.WeatherFrag;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description FragmentStatePagerAdapter更加适用于展示大量的或动态的item，它仅仅会save用户访问过的item的state
 * 所以相对于 FragmentPagerAdapter，它有更少的内存占用。
 */
public class WeaPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "WeaPagerAdapter";
    private List<WeatherFrag> fragmentList;

    public WeaPagerAdapter(FragmentManager fm, List<WeatherFrag> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * 当调用 FragmentPagerAdapter#notifyDataSetChanged后，Fragment的View不更新解决方案
     *
     * 重写这个方法是实现刷新的关键，它默认返回 POSITION_UNCHANGED
     * @param object
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * {@link #POSITION_NONE} if the item is no longer present.
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        super.getItemPosition(object);
        // 默认返回 PagerAdapter.POSITION_UNCHANGED
        return PagerAdapter.POSITION_NONE;
    }
}
