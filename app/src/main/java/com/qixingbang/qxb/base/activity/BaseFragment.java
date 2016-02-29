package com.qixingbang.qxb.base.activity;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by zqj on 2015/8/17.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    public abstract void initView();

    public abstract void initData();

    @Override
    public void onClick(View v) {
    }
}
