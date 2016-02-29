package com.qixingbang.qxb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Z.H. on 2015/9/23 13:59.
 */

public class SplashActivity extends Activity {

    private final String TAG = SplashActivity.class.getName();

    private final String TOKEN = "token";

    private static final int REQUEST_USER_INFO = 0x01;
    private static final int REQUEST_HOMEPAGE = 0x02;
    /*private Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (QAccount.isFirstOpen()) {
                //goto Welcome
                WelcomeActivity.start(SplashActivity.this);
            } else {
                if (QAccount.hasAccount()) {
                    verifyLoginInfo();
                } else {
                    MainActivity.start(SplashActivity.this);
                }

            }
        }
    };*/
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REQUEST_USER_INFO:
                    getUserInfo();
                    break;
                case REQUEST_HOMEPAGE:
                    try {
                        Thread.sleep(900);
                        MainActivity.start(SplashActivity.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //mHandler.postDelayed(runnable, 1000);

        if (QAccount.isFirstOpen()){
            WelcomeActivity.start(SplashActivity.this);
            finish();
        }else {
            //获取token
            verifyLoginInfo();
        }
    }

    private void getUserInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = QAccount.getToken();
                String detailsUrl = UrlUtil.getUserDetails();
                RequestParams params = new RequestParams();
                params.addHeader("authorization", token);
                HttpUtils httpUtils = new HttpUtils();
                httpUtils.send(HttpRequest.HttpMethod.POST, detailsUrl, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Gson gson = new Gson();
                        UserInfoBean mUserInfoBean = gson.fromJson(responseInfo.result, UserInfoBean.class);
                        if (mUserInfoBean.result == 200){
                            QAccount.setUserInfo(result);
                        }else if (mUserInfoBean.result == 250){
                            //未登录
                        }else {
                            //失败
                        }
                        mHandler.sendEmptyMessage(REQUEST_HOMEPAGE);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        }).start();
    }


    private void verifyLoginInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUserLoginUrl(), QAccount.getLoginInfo(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                QAccount.saveToken(response.optString(TOKEN));
//                                MainActivity.start(SplashActivity.this);
//                                SplashActivity.this.finish();
                                //获取用户信息
                                mHandler.sendEmptyMessage(REQUEST_USER_INFO);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (null != error.networkResponse) {
                                    int statusCode = error.networkResponse.statusCode;
                                    if (300 == statusCode) {
                                        ToastUtil.toast(R.string.login_error);
                                    } else {
                                        ResponseUtil.toastError(error);
                                    }
                                } else {
                                    ResponseUtil.toastError(error);
                                }
                                //TODO 登录失败也可以 进入mainAC,
                                // 目前 登录不成功会清除缓存的 用户信息，需要手动登录（逻辑还需要完善）
                                //                        QAccount.clear();
//                                ToastUtil.toast("未能自动登录，请手动登录");
                                MainActivity.start(SplashActivity.this);
                                SplashActivity.this.finish();
                            }
                        });
                RequestUtil.getInstance().addToRequestQueue(request, TAG);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}
