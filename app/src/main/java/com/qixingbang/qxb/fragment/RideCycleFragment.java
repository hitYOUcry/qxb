package com.qixingbang.qxb.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.ridecycle.CommonDetailsActivity;
import com.qixingbang.qxb.activity.ridecycle.DryCargoDetailsActivity;
import com.qixingbang.qxb.adapter.ridecycle.RideCycleViewPagerAdapter;
import com.qixingbang.qxb.base.activity.BaseFragment;
import com.qixingbang.qxb.beans.ridecycle.RideCycleBean;
import com.qixingbang.qxb.beans.ridecycle.Type;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by lsr on 2015/8/23 19:45.
 */
public class RideCycleFragment extends BaseFragment implements RideCycleViewPagerAdapter.ListViewListener, ViewPager.OnPageChangeListener {

    private final String TAG = RideCycleFragment.class.getName();
    RelativeLayout ridecyclenews;
    View newsHintView;
    RelativeLayout ridecyclecare;
    View careHintView;
    RelativeLayout ridecyclestr;
    View strHintView;
    RelativeLayout ridecycledry;
    View dryHintView;

    RideCycleViewPagerAdapter mAdapter;
    ViewPager mViewPager;

    private final static String ACTION_NEW = "new";
    private final static String ACTION_MORE = "more";
    private final static String ACTION_UPDATE = "update";

    private Type mType = Type.NEWS;
    private String mAction = ACTION_NEW;
    private int mSearchInfoId = 0;

    private List<List<RideCycleBean>> mContentLists;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ridecycle, container, false);
    }

    /**
     * OncreateView执行后 立即执行
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        initView();
        initData();

        /**
         * start
         */
        refreshList();
        refreshHintBar();
    }

    @Override
    public void initView() {
        View view = getView();
        ridecyclenews = (RelativeLayout) view.findViewById(R.id.relativeLayout_ridecycle_news);
        ridecyclenews.setOnClickListener(this);

        ridecyclecare = (RelativeLayout) view.findViewById(R.id.relativeLayout_ridecycle_care);
        ridecyclecare.setOnClickListener(this);

        ridecyclestr = (RelativeLayout) view.findViewById(R.id.relativeLayout_ridecycle_str);
        ridecyclestr.setOnClickListener(this);

        ridecycledry = (RelativeLayout) view.findViewById(R.id.relativeLayout_ridecycle_dry);
        ridecycledry.setOnClickListener(this);

        newsHintView = view.findViewById(R.id.view_filterHint_news);
        careHintView = view.findViewById(R.id.view_filterHint_care);
        strHintView = view.findViewById(R.id.view_filterHint_str);
        dryHintView = view.findViewById(R.id.view_filterHint_dry);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(RideCycleViewPagerAdapter.NUM);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void initData() {
        mContentLists = new ArrayList<>();
        for (int i = 0; i < RideCycleViewPagerAdapter.NUM; i++) {
            mContentLists.add(new ArrayList<RideCycleBean>());
        }
        mAdapter = new RideCycleViewPagerAdapter(getActivity(), mContentLists);
        mAdapter.setListViewListener(this);

        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayout_ridecycle_news:
                mType = Type.NEWS;
                break;
            case R.id.relativeLayout_ridecycle_care:
                mType = Type.CARE;
                break;
            case R.id.relativeLayout_ridecycle_str:
                mType = Type.STRATEGY;
                break;
            case R.id.relativeLayout_ridecycle_dry:
                mType = Type.DRY_CARGO;
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(mType.getIndex());
    }

    @Override
    public void onItemClick(View view, int position, Type type) {
        view.setBackgroundResource(R.color.black_242424);
        // pullToRefreshListView position begin with 1;
        position = position - 1;
        RideCycleBean selected = mContentLists.get(type.getIndex()).get(position);
        selected.setType(type);
        int articleId = selected.getArticleId();
        switch (type) {
            case NEWS:
                CommonDetailsActivity.start(mContext, selected);
                break;
            case CARE:
                CommonDetailsActivity.start(mContext, selected);
                break;
            case STRATEGY:
                CommonDetailsActivity.start(mContext, selected);
                break;
            case DRY_CARGO:
                DryCargoDetailsActivity.start(mContext, articleId);
                break;
            default:
                break;
        }

    }

    @Override
    public void onPullDownRefresh(Type type) {
        mType = type;
        List<RideCycleBean> list = mContentLists.get(type.getIndex());
        if (null != list && list.size() > 0) {
            mSearchInfoId = Collections.max(list).getArticleId();
        }
        mAction = ACTION_UPDATE;
        refreshList();
    }

    @Override
    public void onPullUpToRefresh(Type type) {
        mType = type;
        List<RideCycleBean> list = mContentLists.get(type.getIndex());
        if (null != list && list.size() > 0) {
            mSearchInfoId = Collections.min(list).getArticleId();
        }
        mAction = ACTION_MORE;
        refreshList();
    }

    @Override
    public void onPageSelected(int position) {
        mType = Type.get(position);
        if (mContentLists.get(mType.getIndex()).isEmpty()) {
            mAction = ACTION_NEW;
            mSearchInfoId = 0;
            refreshList();
        }
        refreshHintBar();
    }

    private void more(List<RideCycleBean> list) {
        if (null == list || list.size() == 0) {
            ToastUtil.toast(R.string.no_more_content);
        } else {
            mContentLists.get(mType.getIndex()).addAll(list);
            mAdapter.dataSetChanged(mType.getIndex());
        }
        mAdapter.refreshComplete(mType.getIndex());
    }

    private void update(List<RideCycleBean> list) {
        if (null == list || list.size() == 0) {
            ToastUtil.toast(R.string.no_more_content);
        } else {
            mContentLists.get(mType.getIndex()).clear();
            mContentLists.get(mType.getIndex()).addAll(list);
            mAdapter.dataSetChanged(mType.getIndex());
        }
        mAdapter.refreshComplete(mType.getIndex());
    }

    private void refreshList() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getRideCycleListUrl(mType.toString(), mAction,
                mSearchInfoId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        List<RideCycleBean> list = gson.fromJson(response.optJSONArray("articles").toString(),
                                new TypeToken<List<RideCycleBean>>() {
                                }.getType());
                        if (mAction == ACTION_UPDATE) {
                            update(list);
                        } else {
                            more(list);
                            if (mAction == ACTION_NEW) {
                                CacheSP.addRideCycleCache(response, mType);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mAction == ACTION_NEW) {
                            JSONObject response = CacheSP.getRideCycleCache(mType);
                            if (null != response) {
                                Gson gson = new Gson();
                                List<RideCycleBean> list = gson.fromJson(response.optJSONArray("articles").toString(),
                                        new TypeToken<List<RideCycleBean>>() {
                                        }.getType());
                                more(list);
                            } else {
                                ResponseUtil.toastError(error);
                            }
                        } else {
                            ResponseUtil.toastError(error);
                        }
                        mAdapter.refreshComplete(mType.getIndex());
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    private void refreshHintBar() {
        Resources res = getResources();
        newsHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        careHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        strHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        dryHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        ridecyclenews.setBackgroundColor(res.getColor(R.color.theme_black));
        ridecyclecare.setBackgroundColor(res.getColor(R.color.theme_black));
        ridecyclestr.setBackgroundColor(res.getColor(R.color.theme_black));
        ridecycledry.setBackgroundColor(res.getColor(R.color.theme_black));
        switch (mType) {
            case NEWS:
                newsHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                ridecyclenews.setBackgroundColor(res.getColor(R.color.black_242424));
                break;
            case CARE:
                careHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                ridecyclecare.setBackgroundColor(res.getColor(R.color.black_242424));
                break;
            case STRATEGY:
                strHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                ridecyclestr.setBackgroundColor(res.getColor(R.color.black_242424));
                break;
            case DRY_CARGO:
                dryHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                ridecycledry.setBackgroundColor(res.getColor(R.color.black_242424));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
