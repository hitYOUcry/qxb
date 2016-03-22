package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.L;
import com.common.utils.T;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.communicate.ReplyActivity;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.base.activity.CommonAdapter;
import com.qixingbang.qxb.base.activity.ViewHolder;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.communicate.CommunicateBean;
import com.qixingbang.qxb.beans.mine.myReply.MyReplyBean;
import com.qixingbang.qxb.beans.mine.myReply.MyReplyList;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.UrlUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class MyReplyActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.imageView_back)
    ImageView mImageViewBack;
    private TextView tvTitle;
    private PullToRefreshListView ptrReply;
    /**
     * 回答的实体
     */
    private ArrayList<MyReplyBean> mReplyBeans;
    private CommonAdapter<MyReplyBean> mAdapter;
    private String token;

    private MyReplyList mMyReplyList;
    private static int REQUEST_REPLY_OK = 0x01;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_REPLY_OK) {
                mAdapter = new CommonAdapter<MyReplyBean>(getApplicationContext(), mReplyBeans,
                        R.layout.item_listview_my_question_and_reply) {
                    @Override
                    protected void convert(ViewHolder helper, MyReplyBean item) {
                        helper.setImage(R.id.iv_head_portrait, item.question.asker.icon);
                        helper.setText(R.id.tv_nickname, item.question.asker.nickname);
                        helper.setText(R.id.tv_date, item.question.createTime);
                        helper.setText(R.id.tv_thumb_number, item.question.answerCount + "");
                        helper.setText(R.id.tv_content, item.question.content);
                        helper.setVisibility(R.id.ll_pic, View.GONE);
                        if (item.question.pic1 != null) {
                            helper.setVisibility(R.id.ll_pic, View.VISIBLE);
                            helper.setImage(R.id.iv_first, item.question.pic1);
                            if (item.question.pic2 != null) {
                                helper.setImage(R.id.iv_second, item.question.pic2);
                            }
                            if (item.question.pic3 != null) {
                                helper.setImage(R.id.iv_third, item.question.pic3);
                            }
                        }
                    }
                };

                ptrReply.setAdapter(mAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reply);
        ButterKnife.bind(this);

        token = QAccount.getToken();

        initView();
        initData();
        initListener();
    }


    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.textView_tabTip);
        ptrReply = (PullToRefreshListView) findViewById(R.id.lv_reply);
        mReplyBeans = new ArrayList<MyReplyBean>();
        mHandler.sendEmptyMessage(REQUEST_REPLY_OK);
        mMyReplyList = new MyReplyList();
        ptrReply.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    public void initData() {
        tvTitle.setText(R.string.my_reply);
        initListView();
        clearHint();
    }

    private void clearHint() {
        CacheSP.setMyReplyHint(false);
    }

    private void initListView() {
        getMyReplyListFromServer(0);
    }

    private void initListener() {
        ptrReply.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                int answerId = mReplyBeans.get(mReplyBeans.size() - 1).answerId;
                getMyReplyListFromServer(answerId);
            }
        });
        ptrReply.setOnItemClickListener(this);
    }

    private void getMyReplyListFromServer(int answerId) {
        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        HttpUtils httpUtils = new HttpUtils();
        String myQuestionListUrl = UrlUtil.getMyReplyList(answerId);
        httpUtils.send(HttpRequest.HttpMethod.POST, myQuestionListUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.d(responseInfo.result);
                Gson gson = new Gson();
                mMyReplyList = gson.fromJson(responseInfo.result, MyReplyList.class);
                int resultCode = mMyReplyList.result;
                if (resultCode == 200) {
                    if (mMyReplyList.answer.size() == 0){
                        ToastUtil.toast("暂无更多回复");
                    }else {
                        mReplyBeans.addAll(mMyReplyList.answer);
                        mAdapter.notifyDataSetChanged();
                    }
                    ptrReply.onRefreshComplete();
                } else if (resultCode == 300) {
                    T.show(MyReplyActivity.this, "获取失败", Toast.LENGTH_SHORT);
                } else if (resultCode == 250) {
                    T.show(MyReplyActivity.this, "未登录", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }


    //点击返回
    @OnClick(R.id.imageView_back)
    public void back() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyReplyBean replyBean = mReplyBeans.get(position - 1);
        CommunicateBean bean = new CommunicateBean();
        bean.setQuestionId(replyBean.question.questionId);
        bean.setContent(replyBean.question.content);
        bean.setLikeCount(replyBean.question.likeCount + "");
        bean.setAnswerCount(replyBean.question.answerCount + "");
        bean.setCreateTime(replyBean.question.createTime);
        bean.getAsker().setIcon(replyBean.question.asker.icon);
        bean.getAsker().setNickname(replyBean.question.asker.nickname);
        bean.getAsker().setUserId(replyBean.question.asker.userId);
        if (replyBean.question.pic1 != null) {
            bean.setPic1(replyBean.question.pic1);
        }
        if (replyBean.question.pic2 != null) {
            bean.setPic1(replyBean.question.pic2);
        }
        if (replyBean.question.pic3 != null) {
            bean.setPic1(replyBean.question.pic3);
        }
        ReplyActivity.start(this, bean);
    }
}
