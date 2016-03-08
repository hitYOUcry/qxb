package com.qixingbang.qxb.activity.mine.changeInfo;

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
import com.common.utils.L;
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
public class ChangePasswordAty extends BaseActivity {

    private TextView mTitleTv;
    private EditText mOldPwdEdt;
    private EditText mNewPwdEdt;
    private EditText mRepeatNewPwdEdt;
    private Button mSaveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mOldPwdEdt = (EditText) findViewById(R.id.edt_old_pwd);
        mNewPwdEdt = (EditText) findViewById(R.id.edt_new_pwd);
        mRepeatNewPwdEdt = (EditText) findViewById(R.id.edt_repeat_new_pwd);
        mSaveBtn = (Button) findViewById(R.id.btn_save_password);
    }

    @Override
    protected void initData() {
        mTitleTv.setText("更改密码");
    }

    private void initListener() {
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOldPwd();
                saveNewPwd();
            }
        });
    }

    private void saveNewPwd() {
        String newPwd = mNewPwdEdt.getText().toString();
        String repeatNewPwd = mRepeatNewPwdEdt.getText().toString();
        if (newPwd.equals(repeatNewPwd)){
            changePasswordOnServer(newPwd);
            this.finish();
        }else {
            ToastUtil.toast("两次输入的新密码不同!");
        }
    }

    private void changePasswordOnServer(String s) {
        JSONObject object = new JSONObject();
        try {
            object.put("passwd", s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUpdateUserInfo(),
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(response.toString());
                if (200 == response.optInt("result")) {
                    ToastUtil.toast("success");
                } else {
                    try {
                        ToastUtil.toast(response.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    private void checkOldPwd() {

    }
}
