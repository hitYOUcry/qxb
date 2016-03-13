package com.qixingbang.qxb.dialog;

import android.content.Context;

/**
 * Created by zqj on 2016/3/13 14:17.
 */
public class DialogUtil {

    private static WaitingDialog mWaitDialog;

    public static void showWaitingDialog(Context context, String text) {
        if (mWaitDialog == null) {
            mWaitDialog = new WaitingDialog(context);
        }
        if (!mWaitDialog.isShowing()) {
            mWaitDialog.show();
        }
        mWaitDialog.setHintText(text);
    }

    public static void showWaitingDialog(Context context, int textRes) {
        showWaitingDialog(context, context.getString(textRes));
    }

    public static void dismissWaitingDialog() {
        if (mWaitDialog == null)
            return;
        if (mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }
}
