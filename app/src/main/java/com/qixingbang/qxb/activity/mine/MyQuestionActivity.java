package com.qixingbang.qxb.activity.mine;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
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
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.beans.mine.myQuestion.MyQuestionList;
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
public class MyQuestionActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.textView_tabTip)
    TextView tvTitle;
    @Bind(R.id.lv_question)
    PullToRefreshListView mPullToRefreshListView;
    /**
     * 问题的实体
     */
    private ArrayList<CommunicateBean> mQuestionBeans;
    private CommonAdapter<CommunicateBean> mAdapter;
    private String token;

    private MyQuestionList mMyQuestionList;

    private static int REQUEST_QUESTION_OK = 0x01;

    private ProgressDialog mProgressDialog;

    private String mHead;
    private String mNickname;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_QUESTION_OK) {
                mProgressDialog.dismiss();
                mAdapter = new CommonAdapter<CommunicateBean>(getApplicationContext(), mQuestionBeans, R.layout.item_listview_my_question_and_reply) {
                    @Override
                    protected void convert(ViewHolder helper, CommunicateBean item) {
                        int replyCount = Integer.parseInt(item.getAnswerCount());
                        helper.setText(R.id.tv_content, item.getContent());
                        helper.setText(R.id.tv_date, item.getCreateTime());
                        helper.setText(R.id.tv_thumb_number, item.getAnswerCount() + "");
                        helper.setText(R.id.tv_nickname, mNickname);
                        helper.setImage(R.id.iv_head_portrait, mHead);
                        helper.setVisibility(R.id.ll_pic, View.GONE);
                        if (item.getPic1() != null){
                            helper.setVisibility(R.id.ll_pic, View.VISIBLE);
                            helper.setImage(R.id.iv_first, item.getPic1());
                            if (item.getPic2() != null){
                                helper.setImage(R.id.iv_second, item.getPic2());
                            }
                            if (item.getPic3() != null){
                                helper.setImage(R.id.iv_third, item.getPic3());
                            }
                        }
                    }
                };
                mPullToRefreshListView.setAdapter(mAdapter);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question);
        ButterKnife.bind(this);

        token = QAccount.getToken();

        initView();
        initData();
        initListener();
    }

    @Override
    public void initView() {
        mQuestionBeans = new ArrayList<CommunicateBean>();
        mHandler.sendEmptyMessage(REQUEST_QUESTION_OK);
        mMyQuestionList = new MyQuestionList();

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中......");
        mProgressDialog.show();
    }

    @Override
    public void initData() {
        tvTitle.setText(R.string.my_question);
        getHeadAndNickname();
        getMyQuestionListFromServer(0);
        clearHint();
    }

    private void clearHint() {
        CacheSP.setMyQuestionHint(false);
    }

    private void getHeadAndNickname() {
        String userInfo = QAccount.getUserInfo();
        Gson gson = new Gson();
        UserInfoBean userInfoBean = gson.fromJson(userInfo, UserInfoBean.class);
        if (userInfoBean.result == 200) {
            mHead = userInfoBean.user.icon;
            mNickname = userInfoBean.user.nickname;
        }
    }

    public void initListener(){
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getMyQuestionListFromServer(mQuestionBeans.size());
            }
        });

        mPullToRefreshListView.setOnItemClickListener(this);
    }

    private void getMyQuestionListFromServer(int questionCount) {
        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        HttpUtils httpUtils = new HttpUtils();
        String myQuestionListUrl = UrlUtil.getMyQuestionList(questionCount);
        httpUtils.send(HttpRequest.HttpMethod.POST, myQuestionListUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.d(responseInfo.result);
                Gson gson = new Gson();
                mMyQuestionList = gson.fromJson(responseInfo.result, MyQuestionList.class);
                int resultCode = mMyQuestionList.result;
                if (resultCode == 200) {
                    if (mMyQuestionList.questions.size() == 0) {
                        ToastUtil.toast("暂无更多提问");
                    }else {
                        mQuestionBeans.addAll(mMyQuestionList.questions);
                        mAdapter.notifyDataSetChanged();
                    }
                    mPullToRefreshListView.onRefreshComplete();
                } else if (resultCode == 300) {
                    T.show(MyQuestionActivity.this, "获取失败", Toast.LENGTH_SHORT);
                } else if (resultCode == 250) {
                    T.show(MyQuestionActivity.this, "未登录", Toast.LENGTH_SHORT);
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

    //点击分享
    @OnClick(R.id.imageView_share)
    public void share() {
        T.show(this, "clicked share part", Toast.LENGTH_SHORT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CommunicateBean bean = mQuestionBeans.get(position - 1);
        bean.getAsker().setIcon(mHead);
        ReplyActivity.start(this, bean);
    }
}
