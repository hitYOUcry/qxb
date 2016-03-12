package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class FeedbackActivity extends BaseActivity{

    private TextView mTitleTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initData();
    }

    @Override
    public void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mTitleTv.setText("意见反馈");
    }

    @Override
    public void initData() {

    }
}
