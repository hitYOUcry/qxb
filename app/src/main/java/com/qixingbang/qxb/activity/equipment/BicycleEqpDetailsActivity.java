package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.ImageViewPagerAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.bicycleEqp.BicycleEqp;
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
public class BicycleEqpDetailsActivity extends BaseActivity {

    private final String TAG = BicycleEqpDetailsActivity.class.getName();

    private ListItemDetailsView listItemDetailsView;
    private static BicycleEqp mBicycleEqp;

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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBikeEqpDetails(mBicycleEqp.getId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        BicycleEqp.addDetailsInfo(response.optJSONObject("bikeEqp"), mBicycleEqp);
                        listItemDetailsView.setLikeFlag(response.optInt("isLike") == 1);
                        listItemDetailsView.setFavFlag(response.optInt("isFav") == 1);
                        showBicycleEqp();
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
        listItemDetailsView.setItemId(mBicycleEqp.getId());
        listItemDetailsView.setType("bikeEqp");
        listItemDetailsView.refreshComments();
    }

    private void showBicycleEqp() {
        listItemDetailsView.setPagerAdapter(new ImageViewPagerAdapter(this,mBicycleEqp.getPicUrl()));
        listItemDetailsView.setTabTip(mBicycleEqp.getName());
        listItemDetailsView.setPrice(mBicycleEqp.getPrice());
        listItemDetailsView.setLikeNum(mBicycleEqp.getLikeCount());
        listItemDetailsView.setItemName(mBicycleEqp.getName());
        listItemDetailsView.setDescription(mBicycleEqp.getIntroduce());
        listItemDetailsView.refreshFavLike();
        listItemDetailsView.setLikeNum(mBicycleEqp.getLikeCount());
    }

    /**
     * start this activity
     *
     * @param context
     * @param bicycleEqp
     */
    public static void start(Context context, BicycleEqp bicycleEqp) {
        Intent intent = new Intent(context, BicycleEqpDetailsActivity.class);
        mBicycleEqp = bicycleEqp;
        context.startActivity(intent);
    }
}
