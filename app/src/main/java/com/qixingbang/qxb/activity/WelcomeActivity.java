package com.qixingbang.qxb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.common.views.transfomer.ZoomOutPageTransformer;

/**
 * Created by Z.H. on 2015/9/23 13:57.
 */
public class WelcomeActivity extends BaseActivity {
    private ViewPager vpWelcome;
    private LayoutInflater mInflater;

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
        WelcomePageAdapter mPagerAdapter = new WelcomePageAdapter(this,
                new int[]{R.drawable.ic_welcomepage_1, R.drawable.ic_welcomepage_2, R.drawable.ic_welcomepage_3},
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.start(WelcomeActivity.this);
                        finish();
                    }
                });
        vpWelcome.setAdapter(mPagerAdapter);
        vpWelcome.setPageTransformer(false, new ZoomOutPageTransformer());
    }

    class WelcomePageAdapter extends PagerAdapter {

        private int[] mPicResIds;
        private View[] mViews;

        private Context mContext;
        private View.OnClickListener mLastPageListener;

        public WelcomePageAdapter(Context context, int[] picResIds, View.OnClickListener lastPagerListener) {
            mPicResIds = picResIds;
            mContext = context;
            mLastPageListener = lastPagerListener;
            initChildViews();
        }

        private void initChildViews() {
            int num = mPicResIds.length;
            mViews = new View[num];
            for (int i = 0; i < num; i++) {
                View view = View.inflate(mContext, R.layout.welcome_1, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                imageView.setBackgroundResource(mPicResIds[i]);
                if (i == num - 1) {
                    view.setOnClickListener(mLastPageListener);
                }
                mViews[i] = view;
            }
        }

        @Override
        public int getCount() {
            return mPicResIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews[position]);
            return mViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews[position]);
        }
    }

    /**
     *
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }
}
