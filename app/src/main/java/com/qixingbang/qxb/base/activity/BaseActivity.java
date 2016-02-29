package com.qixingbang.qxb.base.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.qixingbang.qxb.common.utils.StatusBarUtil;

/**
 * Created by zqj on 2015/8/13.
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this);
    }
}
