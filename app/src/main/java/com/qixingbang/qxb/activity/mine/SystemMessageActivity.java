package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.common.cache.CacheSP;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class SystemMessageActivity extends BaseActivity{

    private TextView mTitleTv;
    private ImageView mBackIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mBackIv = (ImageView) findViewById(R.id.imageView_back);
        mTitleTv.setText("系统消息");
    }

    @Override
    public void initData() {
        clearHint();
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    private void clearHint() {
        CacheSP.setSystemMessageHint(false);
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
