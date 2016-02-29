package com.qixingbang.qxb.adapter.communicate;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.BasePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by zqj on 2015/12/4 15:13.
 */
public class ImageViewPagerAdapter extends BasePagerAdapter {

    private List<String> mImageUrl;
    private List<View> mViewsList;
    private List<PhotoView> mPhotoViewList;
    private List<PhotoViewAttacher> mAttacher;

    public ImageViewPagerAdapter(Context context, List<String> list) {
        super(context);
        mImageUrl = list;
        initViews();
    }

    private void initViews() {
        mViewsList = new ArrayList<>();
        mPhotoViewList = new ArrayList<>();
        mAttacher = new ArrayList<>();
        for (String url : mImageUrl) {
            View view = View.inflate(mContext, R.layout.dialog_image_photoview, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
            PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
            attacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mPhotoViewList.add(photoView);
            mAttacher.add(attacher);
            mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
            mBitmapUtils.display(photoView, url);
            mViewsList.add(view);
        }
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mImageUrl.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewsList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewsList.get(position));
    }

    public void clean() {
        for (int i = 0; i < mPhotoViewList.size(); i++) {
            mPhotoViewList.set(i, null);
            mAttacher.get(i).cleanup();
        }
    }
}
