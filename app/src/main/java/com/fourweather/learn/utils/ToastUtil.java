package com.fourweather.learn.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/3 0003.
 */

public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String content, int duration) {
        // 避免多次点击生成多个Toast对象导致频繁弹出Toast提示框
        if (toast == null) {
            toast = Toast.makeText(context, content, duration);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
