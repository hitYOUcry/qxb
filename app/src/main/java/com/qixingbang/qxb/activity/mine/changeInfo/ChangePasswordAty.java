package com.qixingbang.qxb.activity.mine.changeInfo;

import android.os.Bundle;
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
import com.qixingbang.qxb.common.utils.SecurityUtil;
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
    private boolean flag;


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
                String oldPwd = mOldPwdEdt.getText().toString();
                if (oldPwd.equals("")){
                    ToastUtil.toast("密码输入不能为空!");
                }else {
                    if (checkOldPwd(oldPwd)){
                        saveNewPwd();
                    }
                }
            }
        });
    }

    private void saveNewPwd() {
        String newPwd = mNewPwdEdt.getText().toString();
        String repeatNewPwd = mRepeatNewPwdEdt.getText().toString();
        if (newPwd.equals("") || repeatNewPwd.equals("")){
            ToastUtil.toast("密码输入不能为空!");
            return;
        }
        if (newPwd.equals(repeatNewPwd)){
            changePasswordOnServer(newPwd);
        }else {
            ToastUtil.toast("两次输入的新密码不同!");
        }
    }

    private void changePasswordOnServer(final String s) {
        JSONObject object = new JSONObject();
        try {
            object.put("passwd", SecurityUtil.encrypt(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUpdateUserInfo(),
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (200 == response.optInt("result")) {
                    try {
                        QAccount.savePassword(SecurityUtil.encrypt(s));
                        ToastUtil.toast("密码修改成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ChangePasswordAty.this.finish();
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
                ToastUtil.toast("服务器出现问题!");
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

    private boolean checkOldPwd(String s) {

        JSONObject object = new JSONObject();
        try {
            object.put("passwd", SecurityUtil.encrypt(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getCheckPassword(),
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(response.toString());
                String message = "";
                try {
                    message = response.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (message.equals("correct")) {
                    flag = true;
                }else if (message.equals("error")){
                    ToastUtil.toast("旧密码验证失败!");
                    flag = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                flag = false;
                ToastUtil.toast("服务器出现问题!");
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
        return flag;
    }
}
