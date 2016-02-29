package com.qixingbang.qxb.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.qixingbang.qxb.common.application.QApplication;

import java.io.InputStream;

/**
 * Created by zh on 2015/8/25 19:05.
 */
public class BitmapUtil {
    /**
     * 压缩图片 防止OOM
     *
     * @param resId
     * @return
     */
    public static Bitmap compressBitmap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //获取资源图片
        InputStream is = QApplication.getInstance().getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * @param bitmap
     * @param newWidth
     * @return
     * @title: resizeBitmap
     * @description: 图像尺寸调整函数
     * @author: ZQJ
     * @date: 2015年6月27日 下午4:56:20
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float temp = ((float) height) / ((float) width);
        int newHeight = (int) ((newWidth) * temp);
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }
}
