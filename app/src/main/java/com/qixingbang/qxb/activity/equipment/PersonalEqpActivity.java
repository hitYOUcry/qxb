package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.PersonalEqpInfoAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.equipment.userEqp.PersonalEqp;
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
 * 二级页面--人身装备
 */
public class PersonalEqpActivity extends BaseActivity implements ListItemView.ItemListListener {

    private final static String TAG = PersonalEqpActivity.class.getName();

    private final List<String> mPriceList = Arrays.asList(
            "不限", "0~100", "100~200", "200~300", "300~500","500+");

    private final List<String> mBrandList = new ArrayList<>();

    private final List<String> mTypeList = Arrays.asList(
            "不限", "S级", "A级", "B级", "AC级", "D级", "E级", "F级");

    private ListItemView mItemListView;
    PersonalEqpInfoAdapter mPersonalEqpInfoAdapter;
    private List<PersonalEqp> mList;

    private boolean mFirstFlag = true;

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
        mPersonalEqpInfoAdapter = new PersonalEqpInfoAdapter(this, mList);
        mItemListView.setListViewAdapter(mPersonalEqpInfoAdapter);
        getBrandList();
        mItemListView.setTabTip(getString(R.string.user_equipment));
        mItemListView.setItemListener(this);
    }

    /**
     * 获取品牌列表
     */
    private void getBrandList() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBodyEqpBrandUrl()
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
    public void jumpNextActivity(int position) {
        PersonalEqpDetailsActivity.start(this, mList.get(position));
    }

    @Override
    public void sentSearchInfoToServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBodyEqpListUrl(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i(TAG, response.toString());
                        updateList(response);
                        if (mFirstFlag) {
                            CacheSP.addEqpPersonalEqpCache(response);
                            mFirstFlag = !mFirstFlag;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject response = CacheSP.getEqpPersonalEqpCache();
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBodyEqpListUrl(),
                mItemListView.getSearchJsonObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.i(TAG, response.toString());
                        List<PersonalEqp> list = PersonalEqp.fromJsonArray(response.optJSONArray("bodyEqps"));
                        if (list.size() == 0) {
                            ToastUtil.toast(R.string.no_more_content);
                        } else {
                            mList.addAll(list);

                            //取最后的20个计算最大id
                            mItemListView.setMaxId(Collections.max(mList.subList(mList.size() - 21 >= 0 ?
                                    mList.size() - 21 : 0, mList.size())).getId());
                            mPersonalEqpInfoAdapter.notifyDataSetChanged();
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
        List<PersonalEqp> list = PersonalEqp.fromJsonArray(response.optJSONArray("bodyEqps"));
        if (list.size() > 0) {
            mList.addAll(list);
            mItemListView.setMaxId(Collections.max(mList).getId());
        } else {
            ToastUtil.toast(R.string.no_related_content);
        }
        mPersonalEqpInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 启动该Activity的方法
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, PersonalEqpActivity.class);
        context.startActivity(intent);
    }
}
