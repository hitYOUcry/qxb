package com.qixingbang.qxb.common.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.qixingbang.qxb.R;
import com.qixingbang.qxblibrary.statusbar.SystemBarTintManager;

/**
 * Created by zqj on 2015/8/13.
 */
public class StatusBarUtil {
    public static void setStatusBar(Activity activity) {
        setSystemBar(activity, R.color.theme_black);
    }

    private static void setSystemBar(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(params);
            SystemBarTintManager manager = new SystemBarTintManager(activity);
            manager.setStatusBarTintEnabled(true);
            manager.setTintColor(activity.getResources().getColor(colorId));
        }
    }
}
