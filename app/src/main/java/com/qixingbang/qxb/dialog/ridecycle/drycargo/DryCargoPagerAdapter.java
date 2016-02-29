package com.qixingbang.qxb.dialog.ridecycle.drycargo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.BasePagerAdapter;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/11/4 09:18.
 */
public class DryCargoPagerAdapter extends BasePagerAdapter {

    private List<View> mViews;

    public DryCargoPagerAdapter(Context context, List<DryCargoItemBean> list) {
        super(context);
        initViews(list);
    }

    @Override
    public int getCount() {
        if (null == mViews)
            return 0;
        else
            return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    private void initViews(List<DryCargoItemBean> list) {
        mViews = new ArrayList<>();
        for (DryCargoItemBean dryCargo : list) {
            View view = View.inflate(mContext, R.layout.dialog_content_drycargo, null);

            ImageView picImageView = (ImageView) view.findViewById(R.id.imageView_pic);
            mBitmapUtils.display(picImageView, dryCargo.getLogo());

            TextView titleTextView = (TextView) view.findViewById(R.id.textView_title);
            titleTextView.setText(dryCargo.getTitle());

            TextView contentTextView = (TextView) view.findViewById(R.id.textView_content);
            contentTextView.setText(dryCargo.getContent());
            mViews.add(view);
        }
    }

}
