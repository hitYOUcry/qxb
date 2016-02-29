package com.qixingbang.qxb.common.utils;

import android.util.Log;

import com.qixingbang.qxb.common.application.GlobalConstant;

/**
 * Created by zqj on 2015/9/23 12:00.
 */
public class LogUtil {

    public static void i(String tag, String msg) {
        if (GlobalConstant.ENABLE_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (GlobalConstant.ENABLE_LOG) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (GlobalConstant.ENABLE_LOG) {
            Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (GlobalConstant.ENABLE_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (GlobalConstant.ENABLE_LOG) {
            Log.e(tag, msg);
        }
    }


}
