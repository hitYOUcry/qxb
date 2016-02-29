package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.ImageViewPagerAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.accesory.Accessory;
import com.qixingbang.qxb.common.views.equipment.ListItemDetailsView;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqj on 2015/9/1 14:47.
 * 三级页面--整车详情
 */
public class BicyclePartsDetailsActivity extends BaseActivity {

    private final String TAG = BicyclePartsDetailsActivity.class.getName();

    private ListItemDetailsView listItemDetailsView;
    private static Accessory mAccessory;
    private static int mAccessoryId;

    private Gson mGson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_itemlist_details);
        initView();
        initData();
        setTypeInfo();
    }

    @Override
    public void initView() {
        listItemDetailsView = (ListItemDetailsView) findViewById(R.id.list_item_details);
    }

    @Override
    public void initData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikePartsDetails(mAccessory.getAccessoryId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Accessory.addDetailsInfo(response.optJSONObject("accessory"), mBicycleParts);
                        mAccessory = mGson.fromJson(response.optJSONObject("accessory").toString(),Accessory.class);
                        listItemDetailsView.setLikeFlag(response.optInt("isLike") == 1);
                        listItemDetailsView.setFavFlag(response.optInt("isFav") == 1);
                        showBicycleParts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (QAccount.hasAccount()) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    private void setTypeInfo() {
        listItemDetailsView.setItemId(mAccessory.getAccessoryId());
        listItemDetailsView.setType("accessory");
        listItemDetailsView.refreshComments();
    }

    private void showBicycleParts() {
        listItemDetailsView.setPagerAdapter(new ImageViewPagerAdapter(this, mAccessory.getPicList()));
        listItemDetailsView.refreshFavLike();
        listItemDetailsView.setTabTip(mAccessory.getName());
        listItemDetailsView.setPrice(mAccessory.getPrice());
        listItemDetailsView.setLikeNum(mAccessory.getLikeCount());
        listItemDetailsView.setItemName(mAccessory.getName());
        listItemDetailsView.setDescription(mAccessory.getIntroduce());
    }

    /**
     * start this activity
     *
     * @param context
     * @param accessory
     */
    public static void start(Context context, Accessory accessory) {
        Intent intent = new Intent(context, BicyclePartsDetailsActivity.class);
        mAccessory = accessory;
        mAccessoryId = accessory.getAccessoryId();
        context.startActivity(intent);
    }

    public static void start(Context context, int accessoryId) {
        Intent intent = new Intent(context, BicyclePartsDetailsActivity.class);
        mAccessoryId = accessoryId;
        mAccessory = new Accessory(mAccessoryId);
        context.startActivity(intent);
    }
}
