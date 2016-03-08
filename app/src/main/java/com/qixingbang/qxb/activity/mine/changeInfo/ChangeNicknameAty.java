package com.qixingbang.qxb.activity.mine.changeInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeongho on 16/3/7.
 */
public class ChangeNicknameAty extends BaseActivity {

    private TextView mTitleTv;
    private EditText mNameEdt;
    private Button mSaveBtn;
    private static final int NICKNAME_RESULT_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mNameEdt = (EditText) findViewById(R.id.edt_nickname);
        mSaveBtn = (Button) findViewById(R.id.btn_save_nickname);
    }

    @Override
    protected void initData() {
        mTitleTv.setText("更改昵称");
        mNameEdt.setText(getIntent().getStringExtra("nickname"));
    }

    private void initListener() {
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNicknameOnServer(mNameEdt.getText().toString());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("nickname", mNameEdt.getText().toString());
                intent.putExtras(bundle);
                setResult(NICKNAME_RESULT_CODE, intent);
                ChangeNicknameAty.this.finish();
            }
        });
    }

    private void changeNicknameOnServer(String name) {
        JSONObject object = new JSONObject();
        try {
            object.put("nickname", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUpdateUserInfo(),
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (200 == response.optInt("result")) {
                    ToastUtil.toast("success");
                } else if (300 == response.optInt("result")) {
                    ToastUtil.toast(R.string.comment_send_failed);
                }
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
