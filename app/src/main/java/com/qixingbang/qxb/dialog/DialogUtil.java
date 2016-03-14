package com.qixingbang.qxb.dialog;

import android.content.Context;
import android.view.View;

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

    public static void showTextDialog(Context context, int titleRes,
                                      int contentRes, View.OnClickListener sureListener) {
        TextDialog textDialog = new TextDialog(context);
        textDialog.show();
        textDialog.setTitle(titleRes);
        textDialog.setContent(contentRes);
        textDialog.setConfirmListener(sureListener);
    }

    public static void showTextDialog(Context context, int titleRes,
                                      String content, View.OnClickListener sureListener) {
        TextDialog textDialog = new TextDialog(context);
        textDialog.show();
        textDialog.setTitle(titleRes);
        textDialog.setContent(content);
        textDialog.setConfirmListener(sureListener);
    }

    public static void showTextDialog(Context context, int titleRes,
                                      String content,int sureTextRes, View.OnClickListener sureListener) {
        TextDialog textDialog = new TextDialog(context);
        textDialog.show();
        textDialog.setTitle(titleRes);
        textDialog.setContent(content);
        textDialog.setConfirmText(sureTextRes);
        textDialog.setConfirmListener(sureListener);
    }

}
