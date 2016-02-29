package com.qixingbang.qxb.activity.login;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.MainActivity;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zqj on 2015/9/11 15:51.
 * 注册
 */
public class RegisterActivity extends BaseActivity {

    private final String TAG = RegisterActivity.class.getName();

    ImageView backImageView;
    ImageView shareImageView;
    TextView tabTipTextView;

    TextView sendSuccessHint;

    EditText userCodeEdt;
    Button nextButton;

    RelativeLayout checkPartLayout;
    EditText checkNumberEdt;
    TextView resendTextView;

    boolean mFinishFlag = false;

    private Handler mResendHandler = new Handler();
    private int mResentInterval = 60;// 60s
    private final int mDelay = 1000; //1s
    private Runnable mRunnableCount = new Runnable() {
        @Override
        public void run() {
            mResentInterval--;
            resendTextView.setText(String.format(getString(R.string.resend), mResentInterval));
            if (mResentInterval > 0) {
                mResendHandler.postDelayed(mRunnableCount, mDelay);
            } else {
                mResendHandler.postDelayed(mRunnableReset, mDelay);
            }
        }
    };
    private Runnable mRunnableReset = new Runnable() {
        @Override
        public void run() {
            resendTextView.setText(R.string.resend_now);
            mResentInterval = 60;
        }
    };

    private VerifyHandler verifyHandler = new VerifyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
        initSDK();
    }

    @Override
    public void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        tabTipTextView.setText(R.string.register);

        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);

        sendSuccessHint = (TextView) findViewById(R.id.textView_sendSuccessHint);

        userCodeEdt = (EditText) findViewById(R.id.editText_phonenumber);

        nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setOnClickListener(this);

        checkPartLayout = (RelativeLayout) findViewById(R.id.layout_inputCheckPart);

        checkNumberEdt = (EditText) findViewById(R.id.editText_checknum);
        resendTextView = (TextView) findViewById(R.id.textView_resend);
        resendTextView.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    /**
     * 初始化短信SDK
     */
    private void initSDK() {
        SMSSDK.initSDK(this, "aea530fd73d0", "1e1c33d4faaa6844741b549bb15ebfa9");
        EventHandler eventHandler = new EventHandler() {
            /**
             * 在操作之后被触发
             *
             * @param event
             *            参数1
             * @param result
             *            参数2 SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.
             *            RESULT_ERROR表示操作失败
             * @param data
             *            事件操作的结果
             */
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                verifyHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
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
            case R.id.button_next:
                if (mFinishFlag) {
                    startVerify();
                } else {
                    clickedAtFirstStage();
                }
                break;
            case R.id.textView_resend:
                if (mResentInterval == 60) {
                    mResendHandler.postDelayed(mRunnableCount, mDelay);
                    startVerify();
                } else {
                    ToastUtil.toast("not yet!!");
                }
                break;
            default:
                break;
        }
    }

    private void clickedAtFirstStage() {
        if (userCodeEdt.getText().length() < 11) {
            ToastUtil.toast("手机号不对！");
            return;
        }
        SMSSDK.getVerificationCode("86", userCodeEdt.getText().toString());
        mFinishFlag = true;
        userCodeEdt.setVisibility(View.INVISIBLE);
        checkPartLayout.setVisibility(View.VISIBLE);
        sendSuccessHint.setVisibility(View.VISIBLE);
        sendSuccessHint.setText(getString(R.string.checknum_send_success) + userCodeEdt.getText().toString());
        ObjectAnimator animator = ObjectAnimator.ofFloat(sendSuccessHint, "alpha", 0f, 1f, 1f, 1f, 0f);
        animator.setDuration(4000);//4s
        animator.start();
        mResendHandler.postDelayed(mRunnableCount, mDelay);
    }

    private void startVerify() {
        SMSSDK.submitVerificationCode("86", userCodeEdt.getText().toString(),
                checkNumberEdt.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    class VerifyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    ToastUtil.toast("提交验证码成功");
                    registerToServer();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    ToastUtil.toast("验证码已经发送");
                } else {
                    ((Throwable) data).printStackTrace();
                }
            } else {
                ToastUtil.toast("验证码不对");
            }
        }
    }

    private void registerToServer() {
        String userCode = userCodeEdt.getText().toString();
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            jsonObject.put("nickname", userCode);
            jsonObject.put("passwd", userCode);
            jsonObject.put("age", 20);
            jsonObject.put("sex", 0);
            jsonObject.put("birthday", format.format(new Date(System.currentTimeMillis())));
            jsonObject.put("phone", userCode);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUserRegisterUrl(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int result = response.optInt("result");
                        if (200 == result) {
                            login();
                        } else if (300 == result) {
                            ToastUtil.toast(response.optString("message"));
                        } else if (350 == result) {
                            ToastUtil.toast(R.string.alreay_register);
                            login();
                        }

                        //
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

    private void login() {
        final String userCode = userCodeEdt.getText().toString();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userCode", userCode);
            jsonObject.put("passwd", userCode);
        } catch (JSONException e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getUserLoginUrl(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        QAccount.saveToken(response.optString("token"));
                        //TODO 密码 信息需要确认 是否  这样生成
                        QAccount.saveLoginInfo(userCode, userCode);
                        MainActivity.start(RegisterActivity.this);
                        RegisterActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = error.networkResponse.statusCode;
                        if (300 == statusCode) {
                            ToastUtil.toast(R.string.login_error);
                        } else {
                            ResponseUtil.toastError(error);
                        }
                    }
                });
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    /**
     * start
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}
