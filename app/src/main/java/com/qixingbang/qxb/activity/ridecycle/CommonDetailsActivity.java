package com.qixingbang.qxb.activity.ridecycle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.adapter.equipment.CommentListAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.Comment;
import com.qixingbang.qxb.beans.ridecycle.RideCycleBean;
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
 * @author lsr
 * @date 2015-10-07
 * description: RideCycle Part news,care and strategy details Activity
 */
public class CommonDetailsActivity extends BaseActivity {

    private final String TAG = CommonDetailsActivity.class.getName();

    private static RideCycleBean mSelected;
    WebView webView;
    private Handler mHandler;
    /**
     * title
     */
    TextView tabTipTextView;
    ImageView backImageView;
    ImageView shareImageView;
    /**
     * content title
     */
    TextView contentTitle;
    TextView createTime;
    /**
     * 收藏点赞部分
     */
    TextView likeNumTextView;
    ImageView likeImageView;
    ImageView favoritesImageView;
    boolean mFavoritesFlag = false;
    boolean mLikeFlag = false;
    /**
     * 查看评论部分
     */
    ListView commentListView;
    private CommentListAdapter mCommentAdapter;
    private List<Comment> mCommentList;
    TextView showMoreComment;

    /**
     * 提交评论
     */
    EditText commentEditText;
    TextView submitComment;
    private int mMaxCommentId;
    private int mCommentNums;

    private Runnable mRefreshCommentListTask = new Runnable() {
        @Override
        public void run() {
            getCommentsFromServer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_cycle_common_details);
        initView();
        initData();
        refreshFavlike();
    }

    public void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setVisibility(View.VISIBLE);
        shareImageView.setOnClickListener(this);

        switch (mSelected.getType()) {
            case NEWS:
                tabTipTextView.setText(R.string.ridecycle_newstxt);
                break;
            case CARE:
                tabTipTextView.setText(R.string.ridecycle_caretxt);
                break;
            case STRATEGY:
                tabTipTextView.setText(R.string.ridecycle_strtxt);
                break;
            default:
                break;
        }

        contentTitle = (TextView) findViewById(R.id.textView_content_title);
        contentTitle.setText(mSelected.getTitle());
        createTime = (TextView) findViewById(R.id.textView_create_time);
        createTime.setText(mSelected.getCreateTime());

        favoritesImageView = (ImageView) findViewById(R.id.imageView_favorites);
        favoritesImageView.setOnClickListener(this);
        likeImageView = (ImageView) findViewById(R.id.imageView_like);
        likeImageView.setOnClickListener(this);
        likeNumTextView = (TextView) findViewById(R.id.textView_likeNum);

        //评论列表
        commentListView = (ListView) findViewById(R.id.listView_comments);
        showMoreComment = (TextView) findViewById(R.id.textView_showMoreComment);
        showMoreComment.setOnClickListener(this);
        //评论编辑框
        commentEditText = (EditText) findViewById(R.id.editText_comment);
        //提交
        submitComment = (TextView) findViewById(R.id.textView_submitComment);
        submitComment.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.news_webView);
        webView.getSettings().setDefaultTextEncodingName("utf-8");//设置默认为utf-8

    }

    public void initData() {
        mHandler = new Handler();
        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentListAdapter(this, mCommentList);
        commentListView.setAdapter(mCommentAdapter);
        getDetailfavlike();
        getCommentsFromServer();
        getNewsFromServer();


    }

    /**
     * 处理点击事件
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_showMoreComment:
                moreComments();
                break;
            case R.id.textView_submitComment:
                submitComment();
                break;
            case R.id.imageView_favorites:
                favClicked();
                break;
            case R.id.imageView_like:
                likeClicked();
                break;
            case R.id.imageView_share:
                ShareUtil.share(this,mSelected.getTitle());
                break;
            default:
                break;
        }
    }

    private void refreshFavlike() {
        likeNumTextView.setText(mSelected.getLikeCount() + "");
    }

    private void getDetailfavlike() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                UrlUtil.getfavlikeDetails(mSelected.getType().toString(), mSelected.getArticleId()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mLikeFlag = response.optInt("isLike") == 1;
                        mFavoritesFlag = response.optInt("isFav") == 1;
                        mSelected.setLikeCount(response.optInt("likeCount"));
                        //点赞收藏信息 更新
                        favoriteChanged();
                        likeChanged();

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
            JsonObjectRequest request = new JsonObjectRequest(method,
                    UrlUtil.getLikeUrl(mSelected.getType().toString(), mSelected.getArticleId()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response.optInt("result") == 200) {
                                if (mLikeFlag) {
                                    mSelected.setLikeCount(mSelected.getLikeCount() - 1);
                                } else {
                                    mSelected.setLikeCount(mSelected.getLikeCount() + 1);
                                }
                                likeNumTextView.setText(mSelected.getLikeCount() + "");
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
            JsonObjectRequest request = new JsonObjectRequest(method,
                    UrlUtil.getFavUrl(mSelected.getType().toString(), mSelected.getArticleId()),
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
        JSONObject jsonObject = Comment.getSubmitCommentJSON(mSelected.getArticleId(), content, mSelected.getType().toString());
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
        mCommentList.addAll(mSelected.getCommentLists());
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                UrlUtil.getObtainCommentsUrl(mSelected.getType().toString(), mSelected.getArticleId(), mMaxCommentId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mSelected.setCommentLists(Comment.fromJsonArray(response.optJSONArray("comments")));
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

    private void getNewsFromServer() {
        StringRequest request = new StringRequest(Request.Method.POST, UrlUtil.getNewsDetailsUrl(mSelected.getArticleId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        webView.loadData(response, "text/html; charset=utf-8", null);
                        webView.getSettings().setBuiltInZoomControls(true); //显示放大缩小 controler
                        webView.getSettings().setSupportZoom(true); //可以缩放
                        webView.setSaveEnabled(true);
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

    public static void start(Context context, RideCycleBean selected) {
        Intent intent = new Intent(context, CommonDetailsActivity.class);
        mSelected = selected;
        context.startActivity(intent);
    }
}