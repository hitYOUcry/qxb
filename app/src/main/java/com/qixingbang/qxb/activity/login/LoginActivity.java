package com.qixingbang.qxb.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.common.application.GlobalConstant;
import com.qixingbang.qxb.common.utils.CommonMsgHandler;
import com.qixingbang.qxb.common.utils.SecurityUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.dialog.WaitingDialog;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by zqj on 2015/9/11 10:39.
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements PlatformActionListener {

    private final String TAG = LoginActivity.class.getName();

    ImageView backImageView;
    ImageView shareImageView;
    TextView tabTipTextView;

    EditText userNameEdt;
    EditText passWordEdt;

    Button loginBtn;
    Button registerBtn;

    ImageView loginByQQ;
    ImageView loginBySina;
    ImageView loginByWechat;

    WaitingDialog waitingDialog;
    private JSONObject registerInfoJsonObject;


    static class MessageHandler extends CommonMsgHandler<LoginActivity> {
        private static final int AUTHORIZED = 0x01;
        private static final int AUTHORIZE_ERROR = 0x02;
        private static final int AUTHORIZE_CANCEL = 0x03;

        MessageHandler(LoginActivity activity) {
            super(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            switch (msg.what) {
                case AUTHORIZED:
                    activity.register();
                    break;
                case AUTHORIZE_ERROR:
                    activity.authorizeError();
                    break;
                case AUTHORIZE_CANCEL:
                    activity.authorizeCancel();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    CommonMsgHandler mHandler = new MessageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    @Override
    public void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        tabTipTextView.setText(R.string.login);

        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);

        userNameEdt = (EditText) findViewById(R.id.editText_username);
        passWordEdt = (EditText) findViewById(R.id.editText_password);

        loginBtn = (Button) findViewById(R.id.button_login);
        loginBtn.setOnClickListener(this);
        registerBtn = (Button) findViewById(R.id.button_register);
        registerBtn.setOnClickListener(this);

        loginByQQ = (ImageView) findViewById(R.id.imageView_qq);
        loginByQQ.setOnClickListener(this);
        loginBySina = (ImageView) findViewById(R.id.imageView_weibo);
        loginBySina.setOnClickListener(this);
        loginByWechat = (ImageView) findViewById(R.id.imageView_wechat);
        loginByWechat.setOnClickListener(this);

        waitingDialog = new WaitingDialog(this);

    }

    @Override
    public void initData() {
        ShareSDK.initSDK(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_back:
                finish();
                break;
            case R.id.imageView_share:
                //TODO
                break;
            case R.id.button_login:
                login();
                break;
            case R.id.button_register:
                RegisterActivity.start(this);
                break;
            case R.id.imageView_qq:
                Platform qqPlat = ShareSDK.getPlatform(this, QZone.NAME);
                authorize(qqPlat);
                break;
            case R.id.imageView_weibo:
                Platform weiboPlat = ShareSDK.getPlatform(this, SinaWeibo.NAME);
                authorize(weiboPlat);
                break;
            case R.id.imageView_wechat:
                Platform weChat = ShareSDK.getPlatform(this, Wechat.NAME);
                authorize(weChat);
                break;
            default:
                break;
        }
    }

    private final String USER_CODE = "userCode";
    private final String PASSWORD = "passwd";
    private final String TOKEN = "token";

    private void login() {
        String id = userNameEdt.getText().toString();
        String passwd = passWordEdt.getText().toString();
        if (TextUtils.isEmpty(passwd) || TextUtils.isEmpty(id)) {
            ToastUtil.toast(R.string.input_error);
            return;
        }
        login(id, passwd);
    }

    private void login(final String userCode, final String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USER_CODE, userCode);
            jsonObject.put(PASSWORD, SecurityUtil.encrypt(password));
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUserLoginUrl(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        QAccount.saveToken(response.optString(TOKEN));
                        try {
                            QAccount.saveLoginInfo(userCode, SecurityUtil.encrypt(password));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "save login info failed!!");
                        }
                        getUserInfo();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse && 300 == error.networkResponse.statusCode) {
                            ToastUtil.toast(R.string.login_error);
                        } else {
                            ResponseUtil.toastError(error);
                        }
                        dismissWaitingDialog();
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    private void getUserInfo() {
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
                if (mUserInfoBean.result == 200) {
                    QAccount.setUserInfo(result);
                    setResult(RESULT_OK);
                } else if (mUserInfoBean.result == 250) {
                    //未登录
                } else {
                    //失败
                }
                dismissWaitingDialog();
                //                MainActivity.start(LoginActivity.this);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                dismissWaitingDialog();
            }
        });

    }

    /**
     * 第三方登录，验证,注册和登录
     *
     * @param plat
     */
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }
        if (null != waitingDialog && !waitingDialog.isShowing()) {
            waitingDialog.show();
            waitingDialog.setHintText(R.string.connect_platform);
        }
        //        if (plat.isAuthValid()) {
        //            String userId = plat.getDb().getUserId();
        //            if (userId != null) {
        //                registerInfoJsonObject = getPlatformRegisterInfo(plat);
        //                register();
        //                return;
        //            }
        //        }
        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        registerInfoJsonObject = getPlatformRegisterInfo(platform);
        mHandler.sendEmptyMessage(MessageHandler.AUTHORIZED);
    }

    public void register() {
        waitingDialog.setHintText("正在登录");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUserRegisterUrl(), registerInfoJsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int result = response.optInt("result");
                        if (200 == result || 350 == result) {
                            try {
                                login(registerInfoJsonObject.optString("userCode"), SecurityUtil.decrypt(registerInfoJsonObject.optString("passwd")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (300 == result) {
                            ToastUtil.toast(response.optString("message"));
                            dismissWaitingDialog();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        mHandler.sendEmptyMessage(MessageHandler.AUTHORIZE_ERROR);
        platform.removeAccount();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        mHandler.sendEmptyMessage(MessageHandler.AUTHORIZE_CANCEL);
        platform.removeAccount();
    }

    public void authorizeError() {
        dismissWaitingDialog();
        ToastUtil.toast("Connect selected platform failed,try later.");
    }

    public void authorizeCancel() {
        dismissWaitingDialog();
    }

    private void dismissWaitingDialog() {
        if (null != waitingDialog && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    private JSONObject getPlatformRegisterInfo(Platform platform) {
        JSONObject jsonObject = new JSONObject();
        PlatformDb db = platform.getDb();
        try {
            jsonObject.put("icon", db.getUserIcon());
            jsonObject.put("userCode", db.getUserId());
            jsonObject.put("passwd", SecurityUtil.encrypt(GlobalConstant.COMMON_PASSWORD));
            jsonObject.put("nickname", db.getUserName());
            jsonObject.put("age", 20);
            jsonObject.put("birthday", "2000-1-1");
            if (!TextUtils.isEmpty(db.getUserGender())) {
                if (db.getUserGender().equals("m")) {
                    jsonObject.put("sex", 0);
                } else {
                    jsonObject.put("sex", 1);
                }
            } else {
                jsonObject.put("sex", 1);
            }

        } catch (JSONException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * start this activity
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
