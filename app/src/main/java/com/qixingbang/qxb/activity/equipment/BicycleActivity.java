package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.BicycleInfoAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.equipment.bicycle.Bicycle;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.common.views.equipment.ListItemView;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zqj on 2015/8/18 21:25.
 * 二级界面--整车
 */
public class BicycleActivity extends BaseActivity implements ListItemView.ItemListListener {

    private final String TAG = BicycleActivity.class.getName();

    private final List<String> mPriceList = Arrays.asList(
            "不限", "0~1000", "1000~2000", "2000~3000", "3000~4000", "4000~5000", "5000~7000", "7000~10000", "10000+");

    private final List<String> mBrandList = new ArrayList<>();

    private final List<String> mTypeList = Arrays.asList(
            "不限", "休闲车", "公路车", "城市车", "山地车", "童车", "折叠车", "旅行车", "酷飞车", "其他");

    private ListItemView mItemListView;
    private BicycleInfoAdapter mBicycleInfoAdapter;
    private List<Bicycle> mList;

    private boolean mFirstFlag = true;

    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_itemlist);
        initView();
        initData();
    }

    @Override
    public void initView() {
        mItemListView = (ListItemView) findViewById(R.id.baseitemlist);
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        mBicycleInfoAdapter = new BicycleInfoAdapter(this, mList);
        mItemListView.setListViewAdapter(mBicycleInfoAdapter);
        mItemListView.setTabTip(getString(R.string.whole_bicycle));
        mItemListView.setItemListener(this);

        mGson = new Gson();
        getBrandList();
    }

    @Override
    public void jumpNextActivity(int position) {
        BicycleDetailsActivity.start(this, mList.get(position));
    }

    /**
     * 获取品牌列表
     */
    private void getBrandList() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBicycleBrandUrl()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBrandList.clear();
                mBrandList.add("不限");
                JSONArray jsonArray = response.optJSONArray("brands");
                mBrandList.add("美利达");
                mBrandList.add("捷安特");
                mBrandList.add("A-BIKE");
                String brandName;
                for (int i = 0; i < jsonArray.length(); i++) {
                    brandName = jsonArray.optString(i);
                    if (TextUtils.isEmpty(brandName) || brandName.equals("美利达")
                            || brandName.equals("捷安特")
                            || brandName.equals("A-BIKE")) {
                        continue;
                    }
                    mBrandList.add(jsonArray.optString(i));
                }
                //                mBrandList.add("更多");
                mItemListView.setLists(mBrandList, mTypeList, mPriceList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ResponseUtil.toastError(error);
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void sentSearchInfoToServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBicycleURL(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i(TAG, response.toString());
                        updateList(response);
                        //第一次加载的时候加入缓存
                        if (mFirstFlag) {
                            CacheSP.addEqpBicycleCache(response);
                            mFirstFlag = !mFirstFlag;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject jsonObject = CacheSP.getEqpBicycleCache();
                        if (null != jsonObject) {
                            updateList(jsonObject);
                        } else {
                            ResponseUtil.toastError(error);
                        }
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void pullToRefresh() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBicycleURL(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (200 == response.optInt("result")) {
                            List<Bicycle> list = mGson.fromJson(response.optJSONArray("bikes").toString(),
                                    new TypeToken<List<Bicycle>>() {
                                    }.getType());
                            if (list.size() == 0) {
                                ToastUtil.toast(R.string.no_more_content);
                            } else {
                                mList.addAll(list);
                                //取最后的20个计算最大id
                                mItemListView.setMaxId(Collections.max(mList.subList(mList.size() - 21 >= 0 ?
                                        mList.size() - 21 : 0, mList.size())).getBikeId());

                                mBicycleInfoAdapter.notifyDataSetChanged();
                            }
                        }
                        mItemListView.pullRefreshDone();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ResponseUtil.toastError(error);
                mItemListView.pullRefreshDone();
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    private void updateList(JSONObject response) {
        mList.clear();
        List<Bicycle> list = mGson.fromJson(response.optJSONArray("bikes").toString(),
                new TypeToken<List<Bicycle>>() {
                }.getType());
        if (list.size() > 0) {
            mList.addAll(list);
            mItemListView.setMaxId(Collections.max(mList).getBikeId());
        } else {
            ToastUtil.toast(R.string.no_related_content);
        }
        mBicycleInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 启动该Activity的方法
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, BicycleActivity.class);
        context.startActivity(intent);
    }

}
