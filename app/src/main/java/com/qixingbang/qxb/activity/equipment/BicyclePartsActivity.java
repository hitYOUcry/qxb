package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.BicyclePartsInfoAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.equipment.accesory.Accessory;
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
 * Created by zqj on 2015/8/19 19:45.
 * 二级页面--零部件装备
 */
public class BicyclePartsActivity extends BaseActivity implements ListItemView.ItemListListener {

    private final static String TAG = BicyclePartsActivity.class.getName();

    private final List<String> mPriceList = Arrays.asList(
            "不限", "0~500", "500~1000", "1000~1500", "1500~2000", "2000~3000", "3000+");

    private final List<String> mBrandList = new ArrayList<>();

    private final List<String> mTypeList = new ArrayList<>();

    private ListItemView mItemListView;
    BicyclePartsInfoAdapter mBicyclePartsInfoAdapter;
    private List<Accessory> mList;

    private boolean mFirstFlag = true;

    private Gson mGson = new Gson();

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
        mBicyclePartsInfoAdapter = new BicyclePartsInfoAdapter(this, mList);
        mItemListView.setListViewAdapter(mBicyclePartsInfoAdapter);
        getBrandList();
        getTypeList();
        mItemListView.setTabTip(getString(R.string.bicycle_parts));
        mItemListView.setItemListener(this);
        mItemListView.setLists(mBrandList, mTypeList, mPriceList);
    }

    /**
     * 获取品牌列表
     */
    private void getBrandList() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikePartsBrand()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mBrandList.clear();
                mBrandList.add("不限");
                JSONArray jsonArray = response.optJSONArray("brands");
                for (int i = 0; i < jsonArray.length(); i++) {
                    mBrandList.add(jsonArray.optString(i));
                }
                //                mBrandList.add("更多");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ResponseUtil.toastError(error);
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    private void getTypeList() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikeAccessoryType(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTypeList.clear();
                        mTypeList.add("不限");
                        JSONArray jsonArray = response.optJSONArray("types");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            mTypeList.add(jsonArray.optString(i));
                        }
                        //                mBrandList.add("更多");
//                        mItemListView.setLists(mBrandList, mTypeList, mPriceList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void jumpNextActivity(int position) {
        BicyclePartsDetailsActivity.start(this, mList.get(position));
    }

    @Override
    public void sentSearchInfoToServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikePartsList(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i(TAG, response.toString());
                        updateList(response);
                        if (mFirstFlag) {
                            CacheSP.addEqpBicyclePartsCache(response);
                            mFirstFlag = !mFirstFlag;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject response = CacheSP.getEqpBicyclePartsCache();
                if (null != response) {
                    updateList(response);
                } else {
                    ResponseUtil.toastError(error);
                }
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void pullToRefresh() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikePartsList(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (200 == response.optInt("result")) {
                            LogUtil.i(TAG, response.toString());
                            //                        List<Accessory> list = Accessory.fromJsonArray(response.optJSONArray("accessories"));
                            List<Accessory> list = mGson.fromJson(response.optJSONArray("accessories").toString(),
                                    new TypeToken<List<Accessory>>() {
                                    }.getType());
                            if (list.size() == 0) {
                                ToastUtil.toast(R.string.no_more_content);
                            } else {
                                mList.addAll(list);

                                //取最后的20个计算最大id
                                mItemListView.setMaxId(Collections.max(mList.subList(mList.size() - 21 >= 0 ?
                                        mList.size() - 21 : 0, mList.size())).getAccessoryId());
                                mBicyclePartsInfoAdapter.notifyDataSetChanged();
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
        List<Accessory> list = mGson.fromJson(response.optJSONArray("accessories").toString(),
                new TypeToken<List<Accessory>>() {
                }.getType());
        if (list.size() > 0) {
            mList.addAll(list);
            mItemListView.setMaxId(Collections.max(mList).getAccessoryId());
        } else {
            ToastUtil.toast(R.string.no_related_content);
        }
        mBicyclePartsInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 启动该Activity的方法
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, BicyclePartsActivity.class);
        context.startActivity(intent);
    }
}
