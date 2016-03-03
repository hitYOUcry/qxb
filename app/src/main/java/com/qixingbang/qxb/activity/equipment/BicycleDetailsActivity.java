package com.qixingbang.qxb.activity.equipment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.adapter.equipment.CommentListAdapter;
import com.qixingbang.qxb.adapter.equipment.ConfigurationListAdapter;
import com.qixingbang.qxb.adapter.equipment.ImageViewPagerAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.Comment;
import com.qixingbang.qxb.beans.equipment.Equipment;
import com.qixingbang.qxb.beans.equipment.bicycle.Bicycle;
import com.qixingbang.qxb.beans.equipment.bicycle.ConfigItem;
import com.qixingbang.qxb.common.share.ShareUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.common.utils.ViewUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqj on 2015/9/1 14:47.
 * 三级页面--整车详情
 */
public class BicycleDetailsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private final String TAG = BicycleDetailsActivity.class.getName();

    private final static int INDICATOR_ONE = 0;
    private final static int INDICATOR_TWO = 1;
    private final static int INDICATOR_THREE = 2;

    private int mCurrentPosition = INDICATOR_ONE;

    /**
     * 标题栏
     */
    ImageView backImageView;
    ImageView shareImageView;
    TextView tabTipTextView;

    /**
     * 图片展示区
     */
    ImageView indicatorOne;
    ImageView indicatorTwo;
    ImageView indicatorThree;
    ViewPager picturesViewPager;
    private ImageViewPagerAdapter mAdapter;

    /**
     * 价格名称部分
     */
    TextView priceTextView;
    TextView likeNumTextView;
    ImageView likeImageView;
    ImageView favoritesImageView;
    TextView modelNameTextView;
    boolean mFavoritesFlag = false;
    boolean mLikeFlag = false;

    /**
     * 颜色尺寸部分
     */
    TextView bicycleColorTextView;
    TextView bicycleSizeTextView;

    /**
     * 配置部分
     */
    ListView configurationListView;
    LinearLayout layoutMoreConfig;
    TextView configHintTextView;
    ImageView configHintImageView;
    private ConfigurationListAdapter mConfigAdapter;
    private List<ConfigItem> mConfigList = new ArrayList<>();
    private List<ConfigItem> listTotal = new ArrayList<>();
    private List<ConfigItem> listShort = new ArrayList<>();
    private boolean mMoreConfigFlag = true;

    /**
     * 查看评论部分
     */
    ListView commentListView;
    private CommentListAdapter mCommentAdapter;
    private List<Comment> mCommentList;
    TextView showMoreComment;

    /**
     * 评论
     */
    EditText commentEditText;
    TextView submitComment;
    private int mMaxCommentId;
    private int mCommentNums;


    private static Bicycle mBicycle;
    private static int mBikeId;

    private Runnable mRefreshCommentListTask = new Runnable() {
        @Override
        public void run() {
            getCommentsFromServer();
        }
    };

    private Handler mHandler;

    private Gson mGson;

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicycle_details);
        initView();
        initData();
        refreshBicycleInfo();
    }

    @Override
    public void initView() {

        rootView = findViewById(R.id.rootView);
        //返回
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);

        //分享
        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);
        shareImageView.setVisibility(View.VISIBLE);

        //标题
        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);

        //自行车图
        picturesViewPager = (ViewPager) findViewById(R.id.viewPager_pictures);
        picturesViewPager.addOnPageChangeListener(this);

        indicatorOne = (ImageView) findViewById(R.id.imageView_dot1);
        indicatorOne.setOnClickListener(this);
        indicatorTwo = (ImageView) findViewById(R.id.imageView_dot2);
        indicatorTwo.setOnClickListener(this);
        indicatorThree = (ImageView) findViewById(R.id.imageView_dot3);
        indicatorThree.setOnClickListener(this);
        refreshIndicator();

        //价格
        priceTextView = (TextView) findViewById(R.id.textView_referPrice);

        //收藏
        favoritesImageView = (ImageView) findViewById(R.id.imageView_favorites);
        favoritesImageView.setOnClickListener(this);

        //点赞
        likeImageView = (ImageView) findViewById(R.id.imageView_like);
        likeImageView.setOnClickListener(this);
        likeNumTextView = (TextView) findViewById(R.id.textView_likeNum);

        //车名
        modelNameTextView = (TextView) findViewById(R.id.textView_model);

        //颜色
        bicycleColorTextView = (TextView) findViewById(R.id.textView_bicycleColor);
        //尺寸
        bicycleSizeTextView = (TextView) findViewById(R.id.textView_bicycleSize);
        //配置列表
        configurationListView = (ListView) findViewById(R.id.listView_configuration);
        configHintTextView = (TextView) findViewById(R.id.textView_showMore);
        configHintImageView = (ImageView) findViewById(R.id.imageView_more_configuration);

        layoutMoreConfig = (LinearLayout) findViewById(R.id.layout_more_configuration);
        layoutMoreConfig.setOnClickListener(this);
        //评论列表
        commentListView = (ListView) findViewById(R.id.listView_comments);
        showMoreComment = (TextView) findViewById(R.id.textView_showMoreComment);
        showMoreComment.setOnClickListener(this);
        //评论编辑框
        commentEditText = (EditText) findViewById(R.id.editText_comment);
        //提交
        submitComment = (TextView) findViewById(R.id.textView_submitComment);
        submitComment.setOnClickListener(this);

    }

    @Override
    public void initData() {
        mGson = new Gson();
        mHandler = new Handler();

        mConfigList = new ArrayList<>();
        mConfigAdapter = new ConfigurationListAdapter(this, mConfigList);
        configurationListView.setAdapter(mConfigAdapter);
        ViewUtil.adjustListViewHeight(configurationListView);

        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentListAdapter(this, mCommentList);
        commentListView.setAdapter(mCommentAdapter);
        getDetailInfoFromServer();
        getCommentsFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_dot1:
                mCurrentPosition = INDICATOR_ONE;
                indicatorClicked();
                break;
            case R.id.imageView_dot2:
                mCurrentPosition = INDICATOR_TWO;
                indicatorClicked();
                break;
            case R.id.imageView_dot3:
                mCurrentPosition = INDICATOR_THREE;
                indicatorClicked();
                break;
            case R.id.imageView_back:
                finish();
                break;
            case R.id.imageView_share:
                ShareUtil.share(this);
                break;
            case R.id.imageView_favorites:
                favClicked();
                break;
            case R.id.imageView_like:
                likeClicked();
                break;
            case R.id.layout_more_configuration:
                configToggle(mMoreConfigFlag);
                mMoreConfigFlag = !mMoreConfigFlag;
                break;
            case R.id.textView_showMoreComment:
                moreComments();
                break;
            case R.id.textView_submitComment:
                submitComment();
                break;
            default:
                break;
        }
    }

    /**
     * 从后台获取自行车的详细信息
     */
    private void getDetailInfoFromServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getBicycleDetails(mBicycle.getBikeId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mBicycle = mGson.fromJson(response.optJSONObject("bike").toString(),
                                Bicycle.class);
                        mBicycle.setBikeId(mBikeId);
                        mLikeFlag = response.optInt("isLike") == 1;
                        mFavoritesFlag = response.optInt("isFav") == 1;
                        //点赞收藏信息 更新
                        favoriteChanged();
                        likeChanged();

                        //配置列表更新
                        refreshConfigList();

                        //自信车详细信息更新
                        refreshBicycleInfo();

                        //配图显示
                        //TODO notifyDataChanged Failed so init pic adapter here
                        mAdapter = new ImageViewPagerAdapter(BicycleDetailsActivity.this, mBicycle.getPicUrl());
                        picturesViewPager.setAdapter(mAdapter);
                        picturesViewPager.setOffscreenPageLimit(mAdapter.getCount());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (QAccount.hasAccount()) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    /**
     * 自行车基本信息刷新
     */
    private void refreshBicycleInfo() {
        tabTipTextView.setText(mBicycle.getModel());
        priceTextView.setText(mBicycle.getPrice() + "￥");
        likeNumTextView.setText(mBicycle.getLikeCount() + "");
        modelNameTextView.setText(mBicycle.getModel());
        bicycleColorTextView.setText(mBicycle.getColor());
        bicycleSizeTextView.setText(mBicycle.getSize());
    }


    /**
     * 点击收藏
     */
    private void favClicked() {
        if (QAccount.hasAccount()) {
            int method;
            if (mFavoritesFlag) {
                method = Request.Method.DELETE;
            } else {
                method = Request.Method.POST;
            }
            JsonObjectRequest request = new JsonObjectRequest(method, UrlUtil.getFavUrl("bike", mBicycle.getBikeId()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.optInt("result") == 200) {
                                mFavoritesFlag = !mFavoritesFlag;
                                favoriteChanged();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ResponseUtil.toastError(error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                }
            };
            RequestUtil.getInstance().addToRequestQueue(request, TAG);
        } else {
            LoginActivity.start(this);
            //need not finish;
            //            finish();
        }
    }

    /**
     * 点击点赞
     */
    private void likeClicked() {
        if (QAccount.hasAccount()) {
            int method;
            if (mLikeFlag) {
                method = Request.Method.DELETE;
            } else {
                method = Request.Method.POST;
            }
            JsonObjectRequest request = new JsonObjectRequest(method, UrlUtil.getLikeUrl("bike", mBicycle.getBikeId()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.optInt("result") == 200) {
                                if (mLikeFlag) {
                                    mBicycle.setLikeCount(mBicycle.getLikeCount() - 1);
                                } else {
                                    mBicycle.setLikeCount(mBicycle.getLikeCount() + 1);
                                }
                                likeNumTextView.setText(mBicycle.getLikeCount() + "");
                                mLikeFlag = !mLikeFlag;
                                likeChanged();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ResponseUtil.toastError(error);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                }
            };
            RequestUtil.getInstance().addToRequestQueue(request, TAG);
        } else {
            LoginActivity.start(this);
            //need not finish;
            //            finish();
        }
    }

    /**
     * 点赞切换
     */
    private void likeChanged() {
        if (mLikeFlag) {
            likeImageView.setImageResource(R.drawable.ic_common_like_red);
        } else {
            likeImageView.setImageResource(R.drawable.ic_common_like_gray);
        }

    }


    /**
     * 收藏切换
     */
    private void favoriteChanged() {
        if (mFavoritesFlag) {
            favoritesImageView.setImageResource(R.drawable.ic_common_favorites_red);
            //            ToastUtil.toast(R.string.favorites_success);
        } else {
            favoritesImageView.setImageResource(R.drawable.ic_common_favorites_gray);
            //            ToastUtil.toast(R.string.favorites_cancel);
        }
    }

    /**
     * 配置列表刷新
     */
    private void refreshConfigList() {
        listShort.clear();
        listTotal.clear();
        ConfigItem configItem = new ConfigItem("frame", mBicycle.getFrame());
        listTotal.add(configItem);
        if (null != mBicycle.getFrontFork()) {
            configItem = new ConfigItem("frontFork", mBicycle.getFrontFork().getName());
            listTotal.add(configItem);
        }
        if (null != mBicycle.getLever()) {
            configItem = new ConfigItem("lever", mBicycle.getLever().getName());
            listTotal.add(configItem);
        }
        if (null != mBicycle.getBrake()) {
            configItem = new ConfigItem("brake", mBicycle.getBrake().getName());
            listTotal.add(configItem);
        }
        if (null != mBicycle.getCassettes()) {
            configItem = new ConfigItem("cassettes", mBicycle.getCassettes().getName());
            listTotal.add(configItem);
        }

        configItem = new ConfigItem("outerTire", mBicycle.getOuterTire());
        listTotal.add(configItem);

        if (null != mBicycle.getWheelSystem()) {
            configItem = new ConfigItem("wheelSystem", mBicycle.getWheelSystem().getName());
            listTotal.add(configItem);
        }
        if (listTotal.size() <= 4) {
            layoutMoreConfig.setVisibility(View.GONE);
            listShort.addAll(listTotal);
        } else {
            listShort.addAll(listTotal.subList(0, 4));
        }
        mConfigList.clear();
        mConfigList.addAll(listShort);
        mConfigAdapter.notifyDataSetChanged();
        ViewUtil.adjustListViewHeight(configurationListView);
        refreshBicycleInfo();
    }

    /**
     * 配置列表的打开与收起
     *
     * @param moreConfig
     */
    private void configToggle(boolean moreConfig) {
        mConfigList.clear();
        if (moreConfig) {
            mConfigList.addAll(listTotal);
            configHintImageView.setImageResource(R.drawable.ic_common_collapse);
            configHintTextView.setText(R.string.show_less);
        } else {
            mConfigList.addAll(listShort);
            configHintImageView.setImageResource(R.drawable.ic_eqp_show_more);
            configHintTextView.setText(R.string.show_more);
        }
        ViewUtil.adjustListViewHeight(configurationListView);
    }

    /**
     * 加载更多评论
     */
    private void moreComments() {
        if (!showMoreComment.getText().toString().equals(getString(R.string.no_comment))) {
            getCommentsFromServer();
        }
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        if (!QAccount.hasAccount()) {
            ToastUtil.toast("Login Please!");
            LoginActivity.start(this);
            return;
        }
        String content = commentEditText.getText().toString();
        if (content.isEmpty()) {
            return;
        }
        JSONObject jsonObject = Comment.getSubmitCommentJSON(mBicycle.getBikeId(), content, Equipment.BIKE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getSendCommentUrl(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (200 == response.optInt("result")) {
                            mMaxCommentId = 0;
                            mHandler.postDelayed(mRefreshCommentListTask, 1000);
                            commentEditText.setText("");
                            commentEditText.clearFocus();
                        } else if (300 == response.optInt("result")) {
                            ToastUtil.toast(R.string.comment_send_failed);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", QAccount.getToken());
                return headers;
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request);
    }

    /**
     * 刷新评论列表
     */
    private void refreshCommentList() {
        if (mMaxCommentId == 0) {
            mCommentList.clear();
        }
        mCommentNums = mCommentList.size();
        mCommentList.addAll(mBicycle.getComments());
        if (mCommentList.size() > 0 && mCommentNums != mCommentList.size()) {
            mMaxCommentId = mCommentList.get(mCommentList.size() - 1).getId();
            mCommentAdapter.notifyDataSetChanged();
            ViewUtil.adjustListViewHeight(commentListView);
            showMoreComment.setText(R.string.show_more_comment);
            mCommentNums = mCommentList.size();
        } else {
            showMoreComment.setText(R.string.no_comment);
        }
    }

    /**
     * 从后台获取评论列表
     */
    private void getCommentsFromServer() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getObtainCommentsUrl(Equipment.BIKE, mBicycle.getBikeId(), mMaxCommentId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mBicycle.setComments(Comment.fromJsonArray(response.optJSONArray("comments")));
                        refreshCommentList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (QAccount.hasAccount()) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", QAccount.getToken());
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request, TAG);
    }

    /**
     * viewpager indicator切换
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position % 3;
        refreshIndicator();
    }

    /**
     * viewpager indicator 点击动作
     */
    private void indicatorClicked() {
        if (null != picturesViewPager) {
            picturesViewPager.setCurrentItem(mCurrentPosition);
        }
        refreshIndicator();
    }

    /**
     * viewpager indicator 刷新显示
     */
    private void refreshIndicator() {
        indicatorOne.setImageResource(R.drawable.xml_shape_circle_dot_gray_light);
        indicatorTwo.setImageResource(R.drawable.xml_shape_circle_dot_gray_light);
        indicatorThree.setImageResource(R.drawable.xml_shape_circle_dot_gray_light);
        switch (mCurrentPosition) {
            case INDICATOR_ONE:
                indicatorOne.setImageResource(R.drawable.xml_shape_circle_dot_gray_dark);
                break;
            case INDICATOR_TWO:
                indicatorTwo.setImageResource(R.drawable.xml_shape_circle_dot_gray_dark);
                break;
            case INDICATOR_THREE:
                indicatorThree.setImageResource(R.drawable.xml_shape_circle_dot_gray_dark);
                break;
        }
    }


    /**
     * Unused PagerChangeListener functions
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //    private void share() {
    //        OnekeyShare oks = new OnekeyShare();
    //        //关闭sso授权
    //        oks.disableSSOWhenAuthorize();
    //        oks.setTheme(OnekeyShareTheme.CLASSIC);
    ////        oks.setSilent(true);
    //        oks.setSilent(false);
    //        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
    //        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
    //        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用(QQ系必填)
    //        oks.setTitle(getString(R.string.title_activity_main));
    //        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
    //        oks.setTitleUrl(GlobalConstant.QXB_WEBSITE);
    //        // text是分享文本，所有平台都需要这个字段
    //        oks.setText(getString(R.string.share_text));
    //        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
    //        oks.setImageUrl(GlobalConstant.SHARE_IMG);
    //        // url仅在微信（包括好友和朋友圈）中使用
    //        oks.setUrl(GlobalConstant.QXB_WEBSITE);
    //        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
    //        //        oks.setComment("我是测试评论文本");
    //        // site是分享此内容的网站名称，仅在QQ空间使用
    //        oks.setSite(getString(R.string.app_name));
    //        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
    //        oks.setSiteUrl(GlobalConstant.QXB_WEBSITE);
    ////        oks.setViewToShare(rootView);
    //        // 启动分享GUI
    //        oks.show(this);
    //    }

    /**
     * start this activity
     *
     * @param context
     * @param bicycle
     */
    public static void start(Context context, Bicycle bicycle) {
        Intent intent = new Intent(context, BicycleDetailsActivity.class);
        mBicycle = bicycle;
        mBikeId = mBicycle.getBikeId();
        context.startActivity(intent);
    }

    public static void start(Context context, int bikeId) {
        Intent intent = new Intent(context, BicycleDetailsActivity.class);
        mBikeId = bikeId;
        mBicycle = new Bicycle(bikeId);
        context.startActivity(intent);
    }
}
