package com.qixingbang.qxb.server;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.utils.LogUtil;

/**
 * Created by zqj on 2015/9/23 11:50.
 * 发请求工具类
 */
public class RequestUtil {

    private String TAG_REQUEST = RequestUtil.class.getName();

    //singleton
    private static RequestUtil instance;

    //http request queue in volley
    private RequestQueue mRequestQueue;

    public static RequestUtil getInstance() {
        if (null == instance) {
            synchronized (RequestUtil.class) {
                if (null == instance) {
                    instance = new RequestUtil();
                }
            }
        }
        return instance;
    }

    private RequestUtil() {
        mRequestQueue = Volley.newRequestQueue(QApplication.getInstance());
    }

    /**
     * 发送http请求
     *
     * @param request
     * @param <T>     请求体类型
     */
    public <T> void addToRequestQueue(Request<T> request) {
        addToRequestQueue(request, null);
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG_REQUEST : tag);
        mRequestQueue.add(request);
        LogUtil.i(TAG_REQUEST, "url:" + request.getUrl());
    }

    /**
     * 取消请求（根据tag信息取消）
     *
     * @param tag
     */
    public void cancelPendingRequests(String tag) {
        mRequestQueue.cancelAll(tag);
    }
}
