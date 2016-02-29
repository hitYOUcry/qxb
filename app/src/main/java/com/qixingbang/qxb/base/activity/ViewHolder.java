package com.qixingbang.qxb.base.activity;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by Z.H. on 2015/11/19 10:45.
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private BitmapUtils mBitmapUtils;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position){
        mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
        mBitmapUtils = new BitmapUtils(context);
    }

    //获得ViewHolder对象
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position){
        if (convertView == null){
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public <T extends View>T getView(int viewId){
        View view = mViews.get(viewId);
        if (view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView(){
        return mConvertView;
    }

    public ViewHolder setText(int viewId, String text){
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public ViewHolder setImage(int viewId, String url){
        ImageView imageView = getView(viewId);
        mBitmapUtils.display(imageView, url);
        return this;
    }

    public ViewHolder setVisibility(int viewId, int visibility){
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public int getPosition(){
        return mPosition;
    }
}
