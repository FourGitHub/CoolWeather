package com.fourweather.learn.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.fourweather.learn.View.APP;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Create on 2019/04/20
 *
 * @author Four
 * @description
 */
public class SloveConflictDrawerLayout extends DrawerLayout {
    public SloveConflictDrawerLayout(@NonNull Context context) {
        this(context,null);
    }

    public SloveConflictDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(APP.getContext()).getScaledTouchSlop();
    }

    public SloveConflictDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private float mTouchSlop;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        float curMotionX = ev.getX();
        float curMotionY = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float deltax = curMotionX - mLastMotionX;
                float deltaY = curMotionY = mLastMotionY;
                if (Math.abs(deltax) > Math.abs(deltaY)) {
                    isIntercept = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }

        mLastMotionX = curMotionX;
        mLastMotionY = curMotionY;

        return isIntercept;
//        return super.onInterceptTouchEvent(ev);
    }
}
