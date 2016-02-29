package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.widget.BaseAdapter;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.common.application.QApplication;

/**
 * Created by zqj on 2015/10/29 09:33.
 */
public abstract class BaseInfoAdapter extends BaseAdapter {
    protected Context mContext;
    protected BitmapUtils mBitmapUtils;

    protected BaseInfoAdapter(Context context) {
        mContext = context;
        mBitmapUtils = QApplication.getImageLoader();
    }
}
