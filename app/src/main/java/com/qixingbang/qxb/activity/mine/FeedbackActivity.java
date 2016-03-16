package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class FeedbackActivity extends BaseActivity{

    private TextView mTitleTv;
    private ImageView mBackIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mBackIv = (ImageView) findViewById(R.id.imageView_back);
        mTitleTv.setText("意见反馈");
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.imageView_back:
                this.finish();
                break;
        }
    }
}
