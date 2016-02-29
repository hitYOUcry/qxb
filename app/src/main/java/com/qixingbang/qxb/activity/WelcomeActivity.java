package com.qixingbang.qxb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.mine.MyFavoritePagerAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;

import java.util.ArrayList;

/**
 * Created by Z.H. on 2015/9/23 13:57.
 */
public class WelcomeActivity extends BaseActivity {
    private ViewPager vpWelcome;
    private MyFavoritePagerAdapter mPagerAdapter;
    private LayoutInflater mInflater;
    private ArrayList<View> mViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        vpWelcome = (ViewPager) findViewById(R.id.vp_welcome);
        mInflater = getLayoutInflater();

    }

    @Override
    protected void initData() {
        View view1 = mInflater.inflate(R.layout.welcome_1, null);
        View view2 = mInflater.inflate(R.layout.welcome_2, null);
        View view3 = mInflater.inflate(R.layout.welcome_3, null);
        mViewList = new ArrayList<View>();
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        view3.setOnClickListener(this);
        mPagerAdapter = new MyFavoritePagerAdapter(this, mViewList);
        vpWelcome.setAdapter(mPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == mViewList.get(2)) {
            MainActivity.start(this);
            finish();
        }
    }

    /**
     *
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
        QAccount.appStart();
    }
}
