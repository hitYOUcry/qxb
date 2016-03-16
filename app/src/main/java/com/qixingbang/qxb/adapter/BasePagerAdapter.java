package com.qixingbang.qxb.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.common.application.GlobalConstant;

/**
 * Created by zqj on 2015/12/4 15:14.
 */
public abstract class BasePagerAdapter extends PagerAdapter {
    protected BitmapUtils mBitmapUtils;
    protected Context mContext;

    public BasePagerAdapter(Context context) {
        mContext = context;
        //内存缓存大小 程序可用存储空间的1/8
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
        String mCachePath = mContext.getCacheDir().getPath();
        mBitmapUtils = new BitmapUtils(mContext, mCachePath, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
    }
}
