package com.qixingbang.qxb.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.BitmapUtil;
import com.qixingbang.qxb.server.UrlUtil;

/**
 * Created by Z.H. on 2015/8/19 9:45.
 */
public class MineFragment extends BaseFragment {

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

    private String token;

    private UserInfoBean mUserInfoBean;
    private BitmapUtils mBitmapUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MineFragment", "onCreate");
    }

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
        initData();
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
    }

    @Override
    public void initData() {
        String userInfo = QAccount.getUserInfo();
        Gson gson = new Gson();
        mUserInfoBean = gson.fromJson(userInfo, UserInfoBean.class);
        if (mUserInfoBean.result == 200) {
            mBitmapUtils.display(ivHeadPortrait, mUserInfoBean.user.icon);
            tvNickname.setText(mUserInfoBean.user.nickname);
            if (mUserInfoBean.user.sex == false) {
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
                switchActivity(SettingActivity.class, MainActivity.REQUEST_MINE_SETTING);
                break;
            case R.id.rl_about_qxb:
                switchActivity(AboutQxbActivity.class);
                break;
            case R.id.rl_feedback:
                switchActivity(FeedbackActivity.class);
                break;
            case R.id.rl_clear_cache:
                Toast.makeText(getActivity(), "清除缓存", Toast.LENGTH_SHORT).show();
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
        Context context = getActivity();
        //内存缓存大小 程序可用存储空间的1/8
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
        String mCachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();
        BitmapUtils mBitmapUtils = new BitmapUtils(context, mCachePath, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
        //清图片
        mBitmapUtils.clearDiskCache();
        mBitmapUtils.clearMemoryCache();
        //清文字
        CacheSP.clear();
    }

    /**
     * 跳转aty
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

    public void refreshUserInfo() {
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
                    mBitmapUtils.display(ivHeadPortrait, mUserInfoBean.user.icon);
                    QAccount.setUserInfo(result);
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
    }


}
