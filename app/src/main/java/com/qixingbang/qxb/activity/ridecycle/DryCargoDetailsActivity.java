package com.qixingbang.qxb.activity.ridecycle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;
import com.qixingbang.qxb.common.views.ridecycle.DryCargoView;
import com.qixingbang.qxb.dialog.ridecycle.drycargo.DryCargoDialog;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by zqj on 2015/11/3 09:36.
 */
public class DryCargoDetailsActivity extends BaseActivity implements DryCargoView.DryCargoItemClickedListener {

    TextView tabTipTextView;
    ScrollView scrollView;
    LinearLayout linearLayout;

    ImageView backImageView;

    private List<DryCargoItemBean> mDryCargoList;
    private static int mItemId;

    private TreeMap<String, List<DryCargoItemBean>> mDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drycargo);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        tabTipTextView.setText(R.string.ridecycle_drytxt);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.relativeLayout);
    }


    @Override
    protected void initData() {
        mDataMap = new TreeMap<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getDryCargoDetailsUrl(mItemId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        mDryCargoList = gson.fromJson(response.optJSONArray("dryCargos").toString(),
                                new TypeToken<List<DryCargoItemBean>>() {
                                }.getType());
                        sortData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                });

        RequestUtil.getInstance().addToRequestQueue(request);

    }

    @Override
    public void dryCargoItemClicked(DryCargoItemBean dryCargo) {
        int position = mDryCargoList.indexOf(dryCargo);
        DryCargoDialog dryCargoDialog = new DryCargoDialog(this, mDryCargoList);
        dryCargoDialog.show();
        dryCargoDialog.setIndex(position);
    }

    private void sortData() {
        if (null != mDataMap && null != mDryCargoList && !mDryCargoList.isEmpty()) {
            mDataMap.clear();
            for (DryCargoItemBean item : mDryCargoList) {
                String firstWord = item.getFirstWord();
                if (mDataMap.containsKey(firstWord)) {
                    mDataMap.get(firstWord).add(item);
                } else {
                    List<DryCargoItemBean> tempList = new ArrayList<>();
                    tempList.add(item);
                    mDataMap.put(firstWord, tempList);
                }
            }
        }
        addViews();
    }

    private void addViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Iterator<String> iterator = mDataMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            DryCargoView dryCargoView = new DryCargoView(this, mDataMap.get(key), key);
            dryCargoView.setLayoutParams(layoutParams);
            dryCargoView.setItemClickedListener(this);
            linearLayout.addView(dryCargoView);
        }

        /**
         * if data is more enough bottom click back
         */
        if (mDataMap.keySet().size() > 6 || mDryCargoList.size() > 16) {
            View view = this.getLayoutInflater().inflate(R.layout.common_back_to_top, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.scrollTo(0, 0);
                }
            });
            linearLayout.addView(view);
        }
    }

    public static void start(Context context, int id) {
        Intent intent = new Intent(context, DryCargoDetailsActivity.class);
        mItemId = id;
        context.startActivity(intent);
    }
}
