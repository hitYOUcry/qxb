package com.qixingbang.qxb.activity.mine.changeInfo;

import android.os.Bundle;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

/**
 * Created by Jeongho on 16/3/7.
 */
public class ChangePasswordAty extends BaseActivity {

    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
    }

    @Override
    protected void initData() {
        mTitleTv.setText("更改密码");
    }

    private void initListener() {

    }
}
