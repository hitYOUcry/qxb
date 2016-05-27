package com.qixingbang.qxb.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.MainActivity;
import com.qixingbang.qxb.activity.mine.map.MapActivity;
import com.qixingbang.qxb.activity.mine.AboutQxbActivity;
import com.qixingbang.qxb.activity.mine.FeedbackActivity;
import com.qixingbang.qxb.activity.mine.MyFavoriteActivity;
import com.qixingbang.qxb.activity.mine.MyQuestionActivity;
import com.qixingbang.qxb.activity.mine.MyReplyActivity;
import com.qixingbang.qxb.activity.mine.SettingActivity;
import com.qixingbang.qxb.activity.mine.SystemMessageActivity;
import com.qixingbang.qxb.base.activity.BaseFragment;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.common.application.GlobalConstant;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.BitmapUtil;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.common.views.CircleButton;
import com.qixingbang.qxb.dialog.TextDialog;
import com.qixingbang.qxb.server.UrlUtil;

import java.io.File;

/**
 * Created by Z.H. on 2015/8/19 9:45.
 */
public class MineFragment extends BaseFragment {

    private final String TAG = MineFragment.class.getSimpleName();

    /**
     * 我的头像
     */
    private ImageView ivHeadPortrait;
    /**
     * 顶部背景图片
     */
    private ImageView ivTopBackground;
    /**
     * 我的收藏
     */
    private Button btnFavorites;
    /**
     * 我的问题
     */
    private Button btnQuestions;
    /**
     * 我的回复
     */
    private Button btnReplies;
    /**
     * 系统消息
     */
    private Button btnSystemMessages;
    /**
     * 设置
     */
    private RelativeLayout rlSetting;
    /**
     * 关于骑行邦
     */
    private RelativeLayout rlQxb;
    /**
     * 联系我们
     */
    private RelativeLayout rlFeedback;
    /**
     * 清除缓存
     */
    private RelativeLayout rlClearCache;

    private TextView tvNickname;
    private TextView tvSex;
    private TextView tvAge;

    private ImageView ivQuestionHint;
    private ImageView ivReplyHint;
    private ImageView ivSystemHint;

    private String token;

    private UserInfoBean mUserInfoBean;
    private BitmapUtils mBitmapUtils;

    private CacheSizeTask mCacheSizeTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("MineFragment", "onCreateView");
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    /**
     * OncreateView执行后 立即执行
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        if (QAccount.getUserInfo().isEmpty()) {
            refreshUserInfo();
        } else {
            initData();
        }
        initFloatBtn();
        mCacheSizeTask = new CacheSizeTask();
        mCacheSizeTask.execute(GlobalConstant.CACHE_PATH);
    }


    private WindowManager mWindowManager;
    CircleButton mFloatingButton;
    WindowManager.LayoutParams mLayoutParams;
    private final int TouchSlop = ViewConfiguration.get(QApplication.getInstance()).getScaledTouchSlop();

    private void initFloatBtn() {
        mWindowManager = getActivity().getWindowManager();
        mFloatingButton = new CircleButton(getActivity());
        mFloatingButton.setImageResource(R.drawable.ic_eqp_not_selected);
        //        mFloatingButton.setColor();
        mLayoutParams = new WindowManager.LayoutParams(64 * 3, 64 * 3, 0, 0, PixelFormat.TRANSLUCENT);
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 100;
        mLayoutParams.y = 300;

        mWindowManager.addView(mFloatingButton, mLayoutParams);
        mFloatingButton.setOnTouchListener(new View.OnTouchListener() {

            private int offsetX;
            private int offsetY;

            private int downX;
            private int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                boolean result = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        offsetX = mLayoutParams.x - rawX;
                        offsetY = mLayoutParams.y - rawY;
                        downX = rawX;
                        downY = rawY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mLayoutParams.x = rawX + offsetX;
                        mLayoutParams.y = rawY + offsetY;
                        mWindowManager.updateViewLayout(mFloatingButton, mLayoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(rawY - downY) > TouchSlop || Math.abs(rawX - downX) > TouchSlop) {
                            mFloatingButton.setAnimationProgress(0);
                            result = true;
                        }
                    default:
                        break;
                }
                return result;
            }
        });
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.start(mFloatingButton.getContext());
            }
        });
    }

    @Override
    public void onStart() {
        Log.d("MineFragment", "onStart");
        super.onStart();
        setHint();
        openFloatBtn();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeFloatBtn();
    }

    public void openFloatBtn() {
        if (null != mFloatingButton) {
            mFloatingButton.setVisibility(View.VISIBLE);
        }
    }

    public void closeFloatBtn() {
        if (null != mFloatingButton) {
            mFloatingButton.setVisibility(View.GONE);
        }
    }

    private void setHint() {
        //获取缓存
        Boolean myQuestionHint = CacheSP.getMyQuestionHint();
        Boolean myReplyHint = CacheSP.getMyReplyHint();
        Boolean systemMessageHint = CacheSP.getSystemMessageHint();

        if (myQuestionHint) {
            ivQuestionHint.setVisibility(View.VISIBLE);
        } else {
            ivQuestionHint.setVisibility(View.INVISIBLE);
        }

        if (myReplyHint) {
            ivReplyHint.setVisibility(View.VISIBLE);
        } else {
            ivReplyHint.setVisibility(View.INVISIBLE);
        }

        if (systemMessageHint) {
            ivSystemHint.setVisibility(View.VISIBLE);
        } else {
            ivSystemHint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initView() {
        View rootView = getView();
        ivTopBackground = (ImageView) rootView.findViewById(R.id.iv_top_bg);
        ivTopBackground.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_mine_top_bg));
        btnFavorites = (Button) rootView.findViewById(R.id.btn_favorite);
        btnFavorites.setOnClickListener(this);
        btnQuestions = (Button) rootView.findViewById(R.id.btn_question);
        btnQuestions.setOnClickListener(this);
        btnReplies = (Button) rootView.findViewById(R.id.btn_reply);
        btnReplies.setOnClickListener(this);
        btnSystemMessages = (Button) rootView.findViewById(R.id.btn_system_message);
        btnSystemMessages.setOnClickListener(this);
        rlSetting = (RelativeLayout) rootView.findViewById(R.id.rl_setting);
        rlSetting.setOnClickListener(this);
        rlQxb = (RelativeLayout) rootView.findViewById(R.id.rl_about_qxb);
        rlQxb.setOnClickListener(this);
        rlFeedback = (RelativeLayout) rootView.findViewById(R.id.rl_feedback);
        rlFeedback.setOnClickListener(this);
        rlClearCache = (RelativeLayout) rootView.findViewById(R.id.rl_clear_cache);
        rlClearCache.setOnClickListener(this);

        ivHeadPortrait = (ImageView) rootView.findViewById(R.id.iv_head_portrait);
        ivHeadPortrait.setOnClickListener(this);

        tvNickname = (TextView) rootView.findViewById(R.id.tv_nickname);
        tvSex = (TextView) rootView.findViewById(R.id.tv_sex);
        tvAge = (TextView) rootView.findViewById(R.id.tv_age);

        mBitmapUtils = new BitmapUtils(getActivity());
        //tvNickname.setTypeface(QApplication.YAHEI_FONT);
        //tvSex.setTypeface(QApplication.YAHEI_FONT);

        ivQuestionHint = (ImageView) rootView.findViewById(R.id.iv_hint_my_question);
        ivReplyHint = (ImageView) rootView.findViewById(R.id.iv_hint_my_reply);
        ivSystemHint = (ImageView) rootView.findViewById(R.id.iv_hint_system_message);


    }

    @Override
    public void initData() {
        String userInfo = QAccount.getUserInfo();
        if (userInfo.isEmpty()) {
            return;
        }
        Gson gson = new Gson();
        mUserInfoBean = gson.fromJson(userInfo, UserInfoBean.class);
        if (mUserInfoBean.result == 200) {
            mBitmapUtils.display(ivHeadPortrait, mUserInfoBean.user.icon);
            tvNickname.setText(mUserInfoBean.user.nickname);
            if (!mUserInfoBean.user.sex) {
                tvSex.setText("男");
            } else {
                tvSex.setText("女");
            }
            tvAge.setText(mUserInfoBean.user.age + "岁");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_favorite:
                switchActivity(MyFavoriteActivity.class);
                break;
            case R.id.btn_question:
                switchActivity(MyQuestionActivity.class);
                break;
            case R.id.btn_reply:
                switchActivity(MyReplyActivity.class);
                break;
            case R.id.btn_system_message:
                switchActivity(SystemMessageActivity.class);
                break;
            case R.id.rl_setting:
                if (QAccount.hasAccount()) {
                    switchActivity(SettingActivity.class, MainActivity.REQUEST_MINE_SETTING);
                } else {
                    ToastUtil.toast(R.string.not_login_yet);
                }
                break;
            case R.id.rl_about_qxb:
                switchActivity(AboutQxbActivity.class);
                break;
            case R.id.rl_feedback:
                switchActivity(FeedbackActivity.class);
                break;
            case R.id.rl_clear_cache:
                mCacheSizeTask = new CacheSizeTask();
                mCacheSizeTask.execute(GlobalConstant.CACHE_PATH);
                clearCache();
                break;
            case R.id.iv_head_portrait:
                //TODO: startAty or startAtyForResult ?
                //switchActivity(ChooseHeadPortraitActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * add by zqj ,for test.
     */
    private void clearCache() {
        final TextDialog dialog = new TextDialog(getActivity());
        dialog.show();
        dialog.setTitle(R.string.clean_cache);
        dialog.setConfirmText(R.string.clean);
        dialog.setContent("缓存文件约为" + mCacheSize);
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getActivity();
                //内存缓存大小 程序可用存储空间的1/8
                final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                final int cacheSize = maxMemory / 8;

                //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
                BitmapUtils mBitmapUtils = new BitmapUtils(context, GlobalConstant.CACHE_PATH, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
                //清图片
                mBitmapUtils.clearDiskCache();
                mBitmapUtils.clearMemoryCache();
                //清文字
                CacheSP.clear();

                ToastUtil.toast("缓存已清除");
                mCacheSizeTask = new CacheSizeTask();
                mCacheSizeTask.execute(GlobalConstant.CACHE_PATH);
                dialog.dismiss();
            }
        });
    }

    private String mCacheSize;

    class CacheSizeTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... params) {
            return FileUtil.getDirSize(new File(params[0]));
        }

        @Override
        protected void onPostExecute(Long aLong) {
            Log.i(TAG, String.valueOf(aLong) + "bytes");
            mCacheSize = FileUtil.getDirSize(aLong);
            super.onPostExecute(aLong);
        }

    }

    /**
     * 跳转aty
     *
     * @param cls
     */
    private void switchActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    private void switchActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        //这个getAc很关键 ，，不然请求码会出错
        getActivity().startActivityForResult(intent, requestCode);
    }

    public boolean refreshUserInfo() {
        String token = QAccount.getToken();
        if (token.isEmpty()) {
            return false;
        }
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
                    //                    mBitmapUtils.display(ivHeadPortrait, mUserInfoBean.user.icon);
                    QAccount.setUserInfo(result);
                    initData();
                } else if (mUserInfoBean.result == 250) {
                    //未登录
                } else {
                    //失败
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
        return true;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("MineFragment", "onHiddenChanged = " + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setHint();
        }
    }
}
