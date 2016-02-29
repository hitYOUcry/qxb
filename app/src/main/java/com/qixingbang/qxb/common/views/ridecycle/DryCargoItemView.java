package com.qixingbang.qxb.common.views.ridecycle;

import android.content.Context;
import android.os.Environment;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;
import com.qixingbang.qxb.common.application.GlobalConstant;

/**
 * Created by zqj on 2015/11/3 11:02.
 */
public class DryCargoItemView extends RelativeLayout {
    /**
     * custom part;
     */
    private Context mContext;
    private DryCargoItemBean dryCargo;
    private TextView dryCargoTxv;
    private RoundImageView dryCargoPicImv;
    private BitmapUtils mBitmapUtils;

    public DryCargoItemView(Context context, DryCargoItemBean itemBean) {
        super(context);
        dryCargo = itemBean;
        mContext = context;
        //内存缓存大小 程序可用存储空间的1/8
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
        String mCachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? mContext.getExternalCacheDir().getPath() :
                        mContext.getCacheDir().getPath();
        mBitmapUtils = new BitmapUtils(mContext, mCachePath, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.item_brand_view, this);
        dryCargoTxv = (TextView) findViewById(R.id.textView_brandName);
        dryCargoTxv.setText(dryCargo.getTitle());
        dryCargoPicImv = (RoundImageView) findViewById(R.id.imageView_brandPic);
        mBitmapUtils.display(dryCargoPicImv, dryCargo.getLogo());
    }


}
