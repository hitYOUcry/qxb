package com.qixingbang.qxb.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.MainActivity;
import com.qixingbang.qxb.activity.communicate.AskActivity;
import com.qixingbang.qxb.activity.communicate.ReplyActivity;
import com.qixingbang.qxb.adapter.communicate.QuestionListAdapter;
import com.qixingbang.qxb.base.activity.BaseFragment;
import com.qixingbang.qxb.beans.communicate.CommunicateBean;
import com.qixingbang.qxb.common.cache.CacheSP;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} suass.
 */
public class CommunicateFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    private final String TAG = CommunicateFragment.class.getName();

    private final static String ACTION_NEW = "new";
    private final static String ACTION_MORE = "more";
    private final static String ACTION_UPDATE = "update";

    private RelativeLayout askRelativeLayout;
    private PullToRefreshListView questionListView;
    private List<CommunicateBean> mQuestionList;
    private QuestionListAdapter mQuestionListAdapter;
    private int mSearchInfoId;

    private String mAction = ACTION_NEW;

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_communicate, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
        getQuestionsFromServer();
    }

    @Override
    public void initView() {
        View mView = getView();
        askRelativeLayout = (RelativeLayout) mView.findViewById(R.id.rl_ask_cmct);
        questionListView = (PullToRefreshListView) mView.findViewById(R.id.listView_communicate);
    }

    @Override
    public void initData() {
        mContext = getActivity();
        mQuestionList = new ArrayList<>();
        mQuestionListAdapter = new QuestionListAdapter(mContext, mQuestionList);
        questionListView.setAdapter(mQuestionListAdapter);
        questionListView.setOnItemClickListener(this);
        questionListView.setOnRefreshListener(this);
        questionListView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout endLabels = questionListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(mContext.getString(R.string.load_more));// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel(mContext.getString(R.string.loading));// 刷新时
        endLabels.setReleaseLabel(mContext.getString(R.string.begin_load));// 下来达到一定距离时，显示的提示

        askRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent askQuestion = new Intent(mContext, AskActivity.class);
                getActivity().startActivityForResult(askQuestion, MainActivity.REQUEST_COMMUNICATE_ASK);
            }
        });
    }

    private void update(List<CommunicateBean> list) {
        if (null == list || list.size() == 0) {
            ToastUtil.toast(R.string.no_more_content);
        } else {
            List<CommunicateBean> tempList = new ArrayList<>();
            tempList.addAll(mQuestionList);
            mQuestionList.clear();
            mQuestionList.addAll(list);
            mQuestionList.addAll(tempList);
            mQuestionListAdapter.notifyDataSetChanged();
        }
        questionListView.onRefreshComplete();
    }

    private void more(List<CommunicateBean> list) {
        if (null == list || list.size() == 0) {
            ToastUtil.toast(R.string.no_more_content);
        } else {
            mQuestionList.addAll(list);
            mQuestionListAdapter.notifyDataSetChanged();
        }
        questionListView.onRefreshComplete();

    }

    //    从服务器获取question,缓存
    public void getQuestionsFromServer() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                UrlUtil.getObtainQuestionUrl(mAction, mSearchInfoId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optInt("result") == 200) {
                            Gson gson = new Gson();
                            List<CommunicateBean> list = gson.fromJson(response.optJSONArray("questions").toString(),
                                    new TypeToken<List<CommunicateBean>>() {
                                    }.getType());
                            if (mAction.equals(ACTION_UPDATE)) {
                                update(list);
                            } else {
                                more(list);
                                if (mAction.equals(ACTION_NEW)) {
                                    CacheSP.addCommunicateQuestionsCache(response);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mAction.equals(ACTION_NEW)) {
                            JSONObject response = CacheSP.getCommunicateQuestionsCache();
                            if (null != response) {
                                Gson gson = new Gson();
                                List<CommunicateBean> list = gson.fromJson(response.optJSONArray("questions").toString(),
                                        new TypeToken<List<CommunicateBean>>() {
                                        }.getType());
                                more(list);
                            } else {
                                ResponseUtil.toastError(error);
                            }
                        } else {
                            ResponseUtil.toastError(error);
                        }
                        if (questionListView.isRefreshing()) {
                            questionListView.onRefreshComplete();
                        }
                    }
                });

        RequestUtil.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        ReplyActivity.start(mContext, mQuestionList.get(position - 1));
        ReplyActivity.start(getActivity(), mQuestionList.get(position - 1), MainActivity.REQUEST_COMMUNICATE_REPLAY);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mAction = ACTION_UPDATE;
        if (mQuestionList.size() > 0) {
            mSearchInfoId = Collections.max(mQuestionList).getQuestionId();
        } else {
            mSearchInfoId = 0;
        }
        getQuestionsFromServer();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mAction = ACTION_MORE;
        if (mQuestionList.size() > 0) {
            mSearchInfoId = Collections.min(mQuestionList).getQuestionId();
        } else {
            mSearchInfoId = 0;
        }
        getQuestionsFromServer();
    }

    /**
     * while go back from reply activity and user submit comment success ,this function called.
     */
    public void refreshQuestionsList() {
        mAction = ACTION_NEW;
        mQuestionList.clear();
        mSearchInfoId = 0;
        getQuestionsFromServer();
    }
}
