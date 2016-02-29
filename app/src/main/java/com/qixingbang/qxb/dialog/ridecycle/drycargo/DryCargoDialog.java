package com.qixingbang.qxb.dialog.ridecycle.drycargo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;

import java.util.List;

/**
 * Created by zqj on 2015/11/3 17:15.
 */
public class DryCargoDialog extends Dialog {

    private Context mContext;
    private ImageView closeImageView;
    private ViewPager mViewPager;
    private DryCargoPagerAdapter mAdapter;

    private List<DryCargoItemBean> mDryCargoList;


    public DryCargoDialog(Context context, List<DryCargoItemBean> list) {
        super(context);
        mContext = context;
        mDryCargoList = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_drycargo);

        closeImageView = (ImageView) findViewById(R.id.imageView_close);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new DryCargoPagerAdapter(mContext, mDryCargoList);
        mViewPager.setAdapter(mAdapter);
    }

    public void setIndex(int index) {
        mViewPager.setCurrentItem(index);
    }
}
