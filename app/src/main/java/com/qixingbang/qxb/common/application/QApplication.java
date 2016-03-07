package com.qixingbang.qxb.common.application;

import android.app.Application;
import android.app.Notification;
import android.os.Environment;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.common.utils.FileUtil;

import org.xutils.x;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;


/**
 * Created by zqj on 2015/8/25 19:07.
 */
public class QApplication extends Application {

    private static QApplication instance;

    public static QApplication getInstance() {
        return instance;
    }

    private static BitmapUtils mBitmapUtils;

    public static BitmapUtils getImageLoader() {
        return mBitmapUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //极光初始化
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);

        initJPushNotification();

        initDB();
        FileUtil.init();
        initBitmapUtils();

        x.Ext.init(this);
        x.Ext.setDebug(true);
//        ShareSDK.initSDK(this);
    }

    private void initBitmapUtils() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        //磁盘高速缓存路径 (磁盘高速缓存大小10MB)
        String mCachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? getExternalCacheDir().getPath() :
                        getCacheDir().getPath();
        mBitmapUtils = new BitmapUtils(this, mCachePath, cacheSize, GlobalConstant.DISK_CACHE_SIZE);
    }

    private void initJPushNotification() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.drawable.ic_mine_logo;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  // 设置为铃声与震动都要
        JPushInterface.setPushNotificationBuilder(1, builder);
    }


    /**
     * 关闭推送
     */
    public static void stopJPush() {
        JPushInterface.stopPush(instance);
    }

    /**
     * 开启推送
     */
    public static void resumeJPush() {
        JPushInterface.resumePush(instance);
    }

    /**
     * 创建数据库
     */
    private void initDB() {
    }
}
