package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.common.utils.L;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class FeedbackActivity extends BaseActivity{

    private TextView mTitleTv;
    private ImageView mBackIv;
    private EditText mFeedbackContentEdt;
    private Button mSubmitBtn;
    private String mFeedbackContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mBackIv = (ImageView) findViewById(R.id.imageView_back);
        mFeedbackContentEdt = (EditText) findViewById(R.id.edt_feedback);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mTitleTv.setText("意见反馈");
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.imageView_back:
                this.finish();
                break;
            case R.id.btn_submit:
                mFeedbackContent = mFeedbackContentEdt.getText().toString();
                if (mFeedbackContent != null) {
                    submitFeedback(mFeedbackContent);
                }
                break;
        }
    }

    private void submitFeedback(String feedback) {
        JSONObject object = new JSONObject();
        try {
            object.put("suggestion", feedback);
            L.d(feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getFeedback(),
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(response.toString());
                if (200 == response.optInt("result")) {
                    FeedbackActivity.this.finish();
                } else {

                }
                L.d(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                ResponseUtil.toastError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authorization", QAccount.getToken());
                return headers;
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request);
    }
}
