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
import com.qixingbang.qxb.beans.equipment.userEqp.PersonalEqp;
import com.qixingbang.qxb.common.utils.StatusBarUtil;
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
public class PersonalEqpDetailsActivity extends BaseActivity {

    private final String TAG = PersonalEqpDetailsActivity.class.getName();

    private ListItemDetailsView listItemDetailsView;
    private static PersonalEqp mPersonalEqp;

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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBodyEqpDetails(mPersonalEqp.getId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        PersonalEqp.addDetailsInfo(response.optJSONObject("bodyEqp"), mPersonalEqp);
                        listItemDetailsView.setLikeFlag(response.optInt("isLike") == 1);
                        listItemDetailsView.setFavFlag(response.optInt("isFav") == 1);
                        showPersonalEqp();
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
        listItemDetailsView.setItemId(mPersonalEqp.getId());
        listItemDetailsView.setType("bodyEqp");
        listItemDetailsView.refreshComments();
    }

    private void showPersonalEqp(){
        listItemDetailsView.setPagerAdapter(new ImageViewPagerAdapter(this,mPersonalEqp.getPicUrl()));
        listItemDetailsView.setTabTip(mPersonalEqp.getName());
        listItemDetailsView.setPrice(mPersonalEqp.getPrice());
        listItemDetailsView.setLikeNum(mPersonalEqp.getLikeCount());
        listItemDetailsView.setItemName(mPersonalEqp.getName());
        listItemDetailsView.setDescription(mPersonalEqp.getIntroduce());
        listItemDetailsView.refreshFavLike();
    }

    /**
     * start this activity
     *
     * @param context
     * @param personalEqp
     */
    public static void start(Context context, PersonalEqp personalEqp) {
        Intent intent = new Intent(context, PersonalEqpDetailsActivity.class);
        mPersonalEqp = personalEqp;
        context.startActivity(intent);
    }
}
