package com.qixingbang.qxb.common.views.ridecycle;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;
import com.qixingbang.qxb.common.application.QApplication;

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

    public DryCargoItemView(Context context, DryCargoItemBean itemBean) {
        super(context);
        dryCargo = itemBean;
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.item_brand_view, this);
        dryCargoTxv = (TextView) findViewById(R.id.textView_brandName);
        dryCargoTxv.setText(dryCargo.getTitle());
        dryCargoPicImv = (RoundImageView) findViewById(R.id.imageView_brandPic);
        QApplication.getImageLoader()
                .configDefaultBitmapMaxSize(150,150)
                .display(dryCargoPicImv, dryCargo.getLogo());
        QApplication.getImageLoader()
                .configDefaultBitmapMaxSize(0, 0);
    }


}
