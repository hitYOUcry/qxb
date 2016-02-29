package com.qixingbang.qxb.beans.equipment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by zqj on 2015/8/17.
 */
public class RideCyclePictureNews {
    //TODO :类名待定
    //新闻界面的数据类型 包含一张图片（BitMap），和一段相应的描述文字(String)
    private Bitmap mBitmap;
    private String mDescription;

    public RideCyclePictureNews(Bitmap bitmap, String description) {
        mBitmap = bitmap;
        mDescription = description;
    }

    public RideCyclePictureNews(Context context, int drawableResId, String description) {
        InputStream inputStream = context.getResources().openRawResource(drawableResId);
        mBitmap = BitmapFactory.decodeStream(inputStream);
        mDescription = description;

    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
