package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/9/1 20:25.
 * pictures viewpager adapter
 */
public class ImageViewPagerAdapter extends PagerAdapter {

    private final static int PAGER_NUM = 3;
    private View[] mViews = new View[PAGER_NUM];
    private Context mContext;
    private List<String> mList = new ArrayList<>();
    private BitmapUtils mBitmapUtils;

    public ImageViewPagerAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
        mBitmapUtils = new BitmapUtils(mContext);
        initViews();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews[position]);
        return mViews[position];
    }

    @Override
    public int getCount() {
        return PAGER_NUM;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void initViews() {
        int picUrlNum = mList.size();
        for(int i = 0; i < PAGER_NUM;i++){
            mViews[i] = View.inflate(mContext, R.layout.item_viewpager_pictures, null);
            if(i <= picUrlNum - 1){
                ImageView imageView = (ImageView) mViews[i].findViewById(R.id.imageView_picture);
                mBitmapUtils.display(imageView, mList.get(i));
            }
        }
//        mViews[0] = View.inflate(mContext, R.layout.item_viewpager_pictures, null);
//        ImageView imageView = (ImageView) mViews[0].findViewById(R.id.imageView_picture);
//        mBitmapUtils.display(imageView, mList.get(0));
//
//        mViews[1] = View.inflate(mContext, R.layout.item_viewpager_pictures, null);
//        imageView = (ImageView) mViews[1].findViewById(R.id.imageView_picture);
//        mBitmapUtils.display(imageView, mList.get(1));
//
//        mViews[2] = View.inflate(mContext, R.layout.item_viewpager_pictures, null);
//        imageView = (ImageView) mViews[2].findViewById(R.id.imageView_picture);
//        mBitmapUtils.display(imageView, mList.get(2));
    }
}
