package com.qixingbang.qxb.common.views.equipment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.adapter.equipment.CommentListAdapter;
import com.qixingbang.qxb.adapter.equipment.ImageViewPagerAdapter;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.Comment;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.share.ShareUtil;
import com.qixingbang.qxb.common.utils.DensityUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.common.utils.ViewUtil;
import com.qixingbang.qxb.dialog.DialogUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqj on 2015/9/30 14:37.
 */
public class ListItemDetailsView extends LinearLayout implements View.OnClickListener, ViewPager.OnPageChangeListener {

    public ListItemDetailsView(Context context) {
        super(context);
        init(context);
    }

    public ListItemDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListItemDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


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
     * 内容描述
     */
    LinearLayout layoutShowMore;
    TextView descriptionTextView;
    TextView showMoreTextView;
    ImageView showMoreImageView;
    private boolean mMoreConfigFlag = true;

    /**
     * 查看评论部分
     */
    ListView commentListView;
    private CommentListAdapter mCommentAdapter;
    private List<Comment> mCommentList;
    private List<Comment> mTempCommentsList;
    TextView showMoreComment;

    /**
     * 评论
     */
    EditText commentEditText;
    TextView submitComment;
    private Context mContext;
    private int mMaxCommentId;
    private int mCommentNums;

    /**
     * 类型和id信息
     */
    private String mType;
    private int mItemId;

    /**
     * 所需共同变量
     */
    private int mPrice;
    private int mLikeNum;
    private String mItemName;
    private String mDescription;


    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.common_itemlist_details, this);
        initView();
        initData();
    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);

        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);
        shareImageView.setVisibility(View.VISIBLE);

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);

        picturesViewPager = (ViewPager) findViewById(R.id.viewPager_pictures);
        picturesViewPager.addOnPageChangeListener(this);

        indicatorOne = (ImageView) findViewById(R.id.imageView_dot1);
        indicatorOne.setOnClickListener(this);
        indicatorTwo = (ImageView) findViewById(R.id.imageView_dot2);
        indicatorTwo.setOnClickListener(this);
        indicatorThree = (ImageView) findViewById(R.id.imageView_dot3);
        indicatorThree.setOnClickListener(this);

        refreshIndicator();

        priceTextView = (TextView) findViewById(R.id.textView_referPrice);

        favoritesImageView = (ImageView) findViewById(R.id.imageView_favorites);
        favoritesImageView.setOnClickListener(this);

        likeImageView = (ImageView) findViewById(R.id.imageView_like);
        likeImageView.setOnClickListener(this);

        likeNumTextView = (TextView) findViewById(R.id.textView_likeNum);

        modelNameTextView = (TextView) findViewById(R.id.textView_model);

        showMoreTextView = (TextView) findViewById(R.id.textView_showMore);
        showMoreImageView = (ImageView) findViewById(R.id.imageView_more_configuration);
        descriptionTextView = (TextView) findViewById(R.id.textView_description);
        layoutShowMore = (LinearLayout) findViewById(R.id.layout_more_configuration);
        layoutShowMore.setOnClickListener(this);

        commentListView = (ListView) findViewById(R.id.listView_comments);
        showMoreComment = (TextView) findViewById(R.id.textView_showMoreComment);
        showMoreComment.setOnClickListener(this);

        commentEditText = (EditText) findViewById(R.id.editText_comment);
        submitComment = (TextView) findViewById(R.id.textView_submitComment);
        submitComment.setOnClickListener(this);
    }

    private void initData() {
        mCommentList = new ArrayList<>();
        mTempCommentsList = new ArrayList<>();
        mCommentAdapter = new CommentListAdapter(mContext, mCommentList);
        commentListView.setAdapter(mCommentAdapter);
        ViewUtil.adjustListViewHeight(commentListView);
    }

    public void setTabTip(String title) {
        tabTipTextView.setText(title);
        tabTipTextView.setHorizontallyScrolling(true);
    }

    public void setPagerAdapter(ImageViewPagerAdapter adapter) {
        mAdapter = adapter;
        picturesViewPager.setAdapter(mAdapter);
        picturesViewPager.setOffscreenPageLimit(mAdapter.getCount());
    }

    public void setPrice(int price) {
        mPrice = price;
        priceTextView.setText(mPrice + "￥");
    }

    public void setLikeNum(int likeNum) {
        mLikeNum = likeNum;
        likeNumTextView.setText(String.valueOf(mLikeNum));
    }

    public void setItemName(String name) {
        mItemName = name;
        modelNameTextView.setText(mItemName);
    }

    public void setDescription(String description) {
        mDescription = description;
        descriptionTextView.setText(mDescription);
        //        Rect rect = new Rect();
        //        descriptionTextView.getGlobalVisibleRect(rect);
        //        if (rect.height() < height_180dp) {
        //            layoutShowMore.setVisibility(GONE);
        //        }
        int line = descriptionTextView.getLineCount();
        if (line <= 10) {
            layoutShowMore.setVisibility(GONE);
        }
    }

    public void setItemId(int id) {
        mItemId = id;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setFavFlag(boolean isFav) {
        mFavoritesFlag = isFav;
    }

    public void setLikeFlag(boolean isLike) {
        mLikeFlag = isLike;
    }

    public void refreshFavLike() {
        likeChanged();
        favoriteChanged();
    }

    public void refreshComments() {
        getCommentsFromServer();
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position % 3;
        refreshIndicator();
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
                ((Activity) mContext).finish();
                break;
            case R.id.imageView_share:
                ShareUtil.share(mContext);
                break;
            case R.id.imageView_favorites:
                favClicked();
                break;
            case R.id.imageView_like:
                likeClicked();
                break;
            case R.id.layout_more_configuration:
                configToggle();
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
            JsonObjectRequest request = new JsonObjectRequest(method, UrlUtil.getFavUrl(mType, mItemId),
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
            RequestUtil.getInstance().addToRequestQueue(request, mContext.getClass().getName());
        } else {
            LoginActivity.start(mContext);
            //            ((Activity) mContext).finish();
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
            JsonObjectRequest request = new JsonObjectRequest(method, UrlUtil.getLikeUrl(mType, mItemId),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.optInt("result") == 200) {
                                if (mLikeFlag) {
                                    mLikeNum = mLikeNum - 1;
                                } else {
                                    mLikeNum = mLikeNum + 1;
                                }
                                likeNumTextView.setText(mLikeNum + "");
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
            RequestUtil.getInstance().addToRequestQueue(request, mContext.getClass().getName());
        } else {
            LoginActivity.start(mContext);
            //            ((Activity) mContext).finish();
        }
    }

    /**
     * 点赞切换
     */
    private void likeChanged() {
        if (mLikeFlag) {
            likeImageView.setImageResource(R.drawable.ic_common_like_red);
            //            ToastUtil.toast(R.string.like_success);
        } else {
            likeImageView.setImageResource(R.drawable.ic_common_like_gray);
            //            ToastUtil.toast(R.string.like_cancel);
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
     * 配置列表的打开与收起
     */
    private static int height_180dp = DensityUtil.dip2px(QApplication.getInstance(), 180.0f);

    private void configToggle() {
        ViewGroup.LayoutParams lp = descriptionTextView.getLayoutParams();
        if (mMoreConfigFlag) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            showMoreTextView.setText(R.string.show_less);
            showMoreImageView.setImageResource(R.drawable.ic_common_back_to_top);
        } else {
            lp.height = height_180dp;
            showMoreTextView.setText(R.string.show_more);
            showMoreImageView.setImageResource(R.drawable.ic_eqp_show_more);
        }
        descriptionTextView.setLayoutParams(lp);
        mMoreConfigFlag = !mMoreConfigFlag;
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
     * 加载更多评论
     */
    private void moreComments() {
        if (!showMoreComment.getText().toString().equals(mContext.getString(R.string.no_comment))) {
            getCommentsFromServer();
        }
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        if (!QAccount.hasAccount()) {
            ToastUtil.toast("Login Please!");
            LoginActivity.start(mContext);
            return;
        }
        String content = commentEditText.getText().toString();
        if (content.isEmpty()) {
            return;
        }
        DialogUtil.showWaitingDialog(mContext, R.string.sending);
        JSONObject jsonObject = Comment.getSubmitCommentJSON(mItemId, content, mType);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getSendCommentUrl(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (200 == response.optInt("result")) {
                            mMaxCommentId = 0;
                            commentEditText.setText("");
                            commentEditText.clearFocus();
                            getCommentsFromServer();
                        } else if (300 == response.optInt("result")) {
                            ToastUtil.toast(R.string.comment_send_failed);
                            DialogUtil.dismissWaitingDialog();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                        DialogUtil.dismissWaitingDialog();
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
        mCommentList.addAll(mTempCommentsList);
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getObtainCommentsUrl(mType, mItemId, mMaxCommentId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTempCommentsList = Comment.fromJsonArray(response.optJSONArray("comments"));
                        refreshCommentList();
                        DialogUtil.dismissWaitingDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                        DialogUtil.dismissWaitingDialog();
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
        RequestUtil.getInstance().addToRequestQueue(request);
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

}
