package com.qixingbang.qxb.common.utils;

import com.qixingbang.qxb.common.application.QApplication;

import java.io.File;

/**
 * Created by zqj on 2015/11/12 19:07.
 */
public class FileUtil {

    public static final String PATH_QXB = QApplication.getInstance().getFilesDir().getAbsolutePath()
            + "/qxb/";

    private static final String PATH_QXB_TEMP = QApplication.getInstance()
            .getExternalFilesDir(null).getAbsolutePath()
            + "/qxbTemp";

    public static void init() {
        createPath(PATH_QXB);
        createPath(PATH_QXB_TEMP);
    }

    private static void createPath(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * 获取拍照的临时路径，照片拍摄结束后删除图片
     */
    public static String getTakePhotoPath() {
        return PATH_QXB_TEMP + "/takephoto.png";
    }
}
