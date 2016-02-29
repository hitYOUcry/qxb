package com.qixingbang.qxb.server;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;

/**
 * Created by zqj on 2015/9/23 12:30.
 */
public class ResponseUtil {
    private static final String TAG = ResponseUtil.class.getName();

    public static void toastError(VolleyError error) {
        LogUtil.e(TAG, error.toString());
        if (isNetworkProblem(error)) {
            ToastUtil.toast(R.string.network_error);
        } else {
            ToastUtil.toast(R.string.server_error);
        }
    }

    private static boolean isNetworkProblem(VolleyError error) {
        return (error instanceof TimeoutError) || (error instanceof NetworkError)
                || (error instanceof NoConnectionError);
    }
}
