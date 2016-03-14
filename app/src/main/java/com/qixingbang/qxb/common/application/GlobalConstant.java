package com.qixingbang.qxb.common.application;

/**
 * Created by zqj on 2015/9/23 12:05.
 */
public class GlobalConstant {

    //开启或关闭日志（调试期间开启，发布时关闭）
    public static final boolean ENABLE_LOG = true;
    public static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;//10MB 磁盘缓存
    public static final String QXB_WEBSITE = "www.njhwt.com";
    public static final String CACHE_PATH =
            QApplication.getInstance().getCacheDir().getPath();
    public static final String COMMON_PASSWORD = "888888";
}
