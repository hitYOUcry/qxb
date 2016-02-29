package com.qixingbang.qxb.common.utils;

import android.app.Activity;
import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by zqj on 2015/12/18 16:33.
 */
public class CommonMsgHandler<T extends Activity> extends Handler {
    protected WeakReference<T> mActivity;

    public CommonMsgHandler(T activity) {
        mActivity = new WeakReference<>(activity);
    }
}
