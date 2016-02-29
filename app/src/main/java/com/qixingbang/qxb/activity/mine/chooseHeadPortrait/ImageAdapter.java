package com.qixingbang.qxb.activity.mine.chooseHeadPortrait;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.ChooseHeadPortraitActivity;
import com.qixingbang.qxb.activity.mine.ClipHeadPortraitActivity;

import java.util.ArrayList;

/**
 * 图片GridView的适配器
 * Created by Z.H. on 2015/9/8 9:20.
 */
public class ImageAdapter extends BaseAdapter {

    private ArrayList<ImageModel> imageList;
    private LayoutInflater inflater;
    private Context mContext;

    public ImageAdapter(ArrayList<ImageModel> imageList, Context context) {
        this.imageList = imageList;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //设置最近的99张图片
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //加载item
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_head_portrait_choose_gridview, parent, false);
            holder = new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mImageView.setImageResource(R.drawable.ic_common_pic_none);

        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(imageList.get(position).path, holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipHeadPortraitActivity.start((Activity) mContext, imageList.get(position).path,
                        ChooseHeadPortraitActivity.REQUEST_CLIP);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView mImageView;
    }
}
