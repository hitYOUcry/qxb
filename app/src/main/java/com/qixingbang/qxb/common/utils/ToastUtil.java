package com.qixingbang.qxb.common.utils;

import android.widget.Toast;

import com.qixingbang.qxb.common.application.QApplication;

/**
 * Created by zqj on 2015/8/25 19:10.
 */
public class ToastUtil {
    public static void toast(int resId) {
        Toast.makeText(QApplication.getInstance(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void toast(CharSequence text) {
        Toast.makeText(QApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

}
