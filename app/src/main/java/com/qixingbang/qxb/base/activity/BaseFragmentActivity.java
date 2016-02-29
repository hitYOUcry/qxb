package com.qixingbang.qxb.base.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.qixingbang.qxb.common.utils.StatusBarUtil;

/**
 * Created by zqj on 2015/8/13.
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener {
    public abstract void initView();

    public abstract void initData();

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this);
    }
}
