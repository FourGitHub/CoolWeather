package com.fourweather.learn.utils;

import com.fourweather.learn.View.PagerFrag;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Create on 2019/04/17
 *
 * @author Four
 * @description
 */
public class SharePagerAdapter extends FragmentPagerAdapter {

    private List<PagerFrag> mPagerFragList;

    public SharePagerAdapter(FragmentManager fm, List<PagerFrag> pagerFragList) {
        super(fm);
        mPagerFragList = pagerFragList;
    }

    @Override
    public Fragment getItem(int position) {
        return mPagerFragList.get(position);
    }

    @Override
    public int getCount() {
        return mPagerFragList.size();
    }
}
