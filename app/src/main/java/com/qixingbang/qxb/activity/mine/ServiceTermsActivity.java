package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.server.UrlUtil;

/**
 * Created by Jeongho on 16/3/21.
 */
public class ServiceTermsActivity extends BaseActivity{

    private TextView mTitleTv;
    private WebView mContentWv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_terms);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mContentWv = (WebView) findViewById(R.id.wv_terms);
        mTitleTv.setText(R.string.service_terms);
    }

    @Override
    protected void initData() {
        mContentWv.loadUrl(UrlUtil.getServiceTerms());
    }
}