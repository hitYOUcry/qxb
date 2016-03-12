package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class SystemMessageActivity extends BaseActivity{

    private TextView mTitleTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        initView();
        initData();
    }

    @Override
    public void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mTitleTv.setText("系统消息");
    }

    @Override
    public void initData() {

    }
}
