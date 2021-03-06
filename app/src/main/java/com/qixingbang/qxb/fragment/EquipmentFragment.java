package com.qixingbang.qxb.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.equipment.BicycleActivity;
import com.qixingbang.qxb.activity.equipment.BicycleEqpActivity;
import com.qixingbang.qxb.activity.equipment.BicyclePartsActivity;
import com.qixingbang.qxb.activity.equipment.PersonalEqpActivity;
import com.qixingbang.qxb.activity.ridecycle.CommonDetailsActivity;
import com.qixingbang.qxb.base.activity.BaseFragment;
import com.qixingbang.qxb.beans.ridecycle.RideCycleBean;
import com.qixingbang.qxb.common.application.GlobalConstant;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zqj on 2015/8/17.
 * 四大块之一 ：装备
 */
public class EquipmentFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private final static int DELAY = 3000;
    private final static int INDICATOR_ONE = 0;
    private final static int INDICATOR_TWO = 1;
    private final static int INDICATOR_THREE = 2;

    private int mCurrentPosition = 0;

    // 新闻区显示
    ViewPager viewPager;

    // 新闻区页面指示
    ImageView indicatorOne;
    ImageView indicatorTwo;
    ImageView indicatorThree;

    // 主四大块点击区域
    RelativeLayout bicycleArea;
    RelativeLayout userEquipmentArea;
    RelativeLayout bicycleEquipmentArea;
    RelativeLayout bicyclePartsArea;

//    // 搜索框和图标
//    EditText searchEditText;
//    TextView searchConfirmTextView;

    private NewsViewPagerAdapter mAdapter;
    private List<RideCycleBean> mNewsList;

    // 循环切换ViewPager
    Handler mHandler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCurrentPosition++;
            mCurrentPosition = mCurrentPosition % 3;
            indicatorClicked();
            mHandler.postDelayed(runnable, DELAY);
        }
    };

    private Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        onClick(indicatorOne);
        mHandler.postDelayed(runnable, DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == mNewsList) {
            initData();
        }
    }

    @Override
    public void initView() {
        View view = getView();
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(EquipmentFragment.this);

        indicatorOne = (ImageView) view.findViewById(R.id.imageView_dot1);
        indicatorOne.setOnClickListener(this);
        indicatorTwo = (ImageView) view.findViewById(R.id.imageView_dot2);
        indicatorTwo.setOnClickListener(this);
        indicatorThree = (ImageView) view.findViewById(R.id.imageView_dot3);
        indicatorThree.setOnClickListener(this);

        bicycleArea = (RelativeLayout) view.findViewById(R.id.relativeLayout_bicycle);
        bicycleArea.setOnClickListener(this);

        userEquipmentArea = (RelativeLayout) view.findViewById(R.id.relativeLayout_userEquipment);
        userEquipmentArea.setOnClickListener(this);

        bicycleEquipmentArea = (RelativeLayout) view.findViewById(R.id.relativeLayout_bicycleEquipment);
        bicycleEquipmentArea.setOnClickListener(this);

        bicyclePartsArea = (RelativeLayout) view.findViewById(R.id.relativeLayout_bicycleParts);
        bicyclePartsArea.setOnClickListener(this);

//        searchEditText = (EditText) view.findViewById(R.id.editText_search);
//        searchConfirmTextView = (TextView) view.findViewById(R.id.textView_search_confirm);
//        searchConfirmTextView.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getNewsInfoFromServer();
    }

    private void getNewsInfoFromServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, UrlUtil.getHomePageUrl(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mNewsList = gson.fromJson(response.optJSONArray("homePage").toString(), new TypeToken<List<RideCycleBean>>() {
                        }.getType());
                        mAdapter = new NewsViewPagerAdapter(mNewsList);
                        viewPager.setAdapter(mAdapter);
                        CacheSP.addEqpNewsCache(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject response = CacheSP.getEqpNewsCache();
                        if (null != response) {
                            mNewsList = gson.fromJson(response.optJSONArray("homePage").toString(), new TypeToken<List<RideCycleBean>>() {
                            }.getType());
                            mAdapter = new NewsViewPagerAdapter(mNewsList);
                            viewPager.setAdapter(mAdapter);
                        } else {
                            ResponseUtil.toastError(error);
                        }
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_dot1:
                mCurrentPosition = INDICATOR_ONE;
                indicatorClicked();
                break;
            case R.id.imageView_dot2:
                mCurrentPosition = INDICATOR_TWO;
                indicatorClicked();
                break;
            case R.id.imageView_dot3:
                mCurrentPosition = INDICATOR_THREE;
                indicatorClicked();
                break;
            case R.id.relativeLayout_bicycle:
                BicycleActivity.start(getActivity());
                break;
            case R.id.relativeLayout_userEquipment:
                PersonalEqpActivity.start(getActivity());
                break;
            case R.id.relativeLayout_bicycleEquipment:
                BicycleEqpActivity.start(getActivity());
                break;
            case R.id.relativeLayout_bicycleParts:
                BicyclePartsActivity.start(getActivity());
                break;
//            case R.id.textView_search_confirm:
//                //TODO
//                ToastUtil.toast("Searching " + searchEditText.getText().toString());
//                break;
            default:
                break;
        }

    }

    private void indicatorClicked() {
        if (null != viewPager) {
            viewPager.setCurrentItem(mCurrentPosition);
        }
        refreshIndicator();
    }

    private void refreshIndicator() {
        indicatorOne.setImageResource(R.drawable.xml_shape_circle_dot_gray737373);
        indicatorTwo.setImageResource(R.drawable.xml_shape_circle_dot_gray737373);
        indicatorThree.setImageResource(R.drawable.xml_shape_circle_dot_gray737373);
        switch (mCurrentPosition) {
            case INDICATOR_ONE:
                indicatorOne.setImageResource(R.drawable.xml_shape_circle_dot_white);
                break;
            case INDICATOR_TWO:
                indicatorTwo.setImageResource(R.drawable.xml_shape_circle_dot_white);
                break;
            case INDICATOR_THREE:
                indicatorThree.setImageResource(R.drawable.xml_shape_circle_dot_white);
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position % NewsViewPagerAdapter.NEWS_NUM;
        refreshIndicator();
    }

    private class NewsViewPagerAdapter extends PagerAdapter {

        public final static int NEWS_NUM = 3;
        private View[] mViews = new View[NEWS_NUM];
        private List<RideCycleBean> mNewsContent;
        private BitmapUtils mBitmapUtils;

        public NewsViewPagerAdapter(List<RideCycleBean> newsContent) {
            initBitmapUtils();
            mNewsContent = newsContent;
            calculateViews();
        }

        private void initBitmapUtils() {
            Context mContext = EquipmentFragment.this.getActivity();
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;

            //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
            String mCachePath =
                    Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                            !Environment.isExternalStorageRemovable() ? mContext.getExternalCacheDir().getPath() :
                            mContext.getCacheDir().getPath();
            mBitmapUtils = new BitmapUtils(mContext, mCachePath, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
        }

        @Override
        public int getCount() {
            return NEWS_NUM;
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

        private void calculateViews() {
            for (int i = 0; i < NEWS_NUM; i++) {
                View view = View.inflate(getActivity(), R.layout.item_viewpager_homepage, null);

                final int position = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDetailsActivity.start(getActivity(), mNewsContent.get(position));
                    }
                });
                ImageView contentImage = (ImageView) view.findViewById(R.id.imageView);
                mBitmapUtils.display(contentImage, mNewsContent.get(i).getLogo());
                TextView contentText = (TextView) view.findViewById(R.id.textView_newsText);
                contentText.setText(mNewsContent.get(i).getTitle());
                mViews[i] = view;
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
