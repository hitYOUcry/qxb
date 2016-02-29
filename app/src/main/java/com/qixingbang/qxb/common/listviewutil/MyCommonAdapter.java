package com.qixingbang.qxb.common.listviewutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by cr30 on 2015/9/24.
 */


public abstract class MyCommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mlayoutId;

    public MyCommonAdapter(Context context,List<T> datas,int layoutId) {
        this.mContext=context;
        this.mInflater=LayoutInflater.from(context);
        this.mDatas=datas;
        this.mlayoutId=layoutId;
    }


    public int getCount() {
        // TODO 自动生成的方法存根
        return mDatas.size();
    }


    public T getItem(int position) {
        // TODO 自动生成的方法存根
        return mDatas.get(position);
    }


    public long getItemId(int position) {
        // TODO 自动生成的方法存根
        return position;
    }


    public  View getView(int position, View convertView, ViewGroup parent) {
//			每次使用时进行初始化viewholder
        MyViewHold holder= MyViewHold.get(mContext, position, convertView, parent,
                mlayoutId);
        convert(holder, getItem(position));

        return holder.getconvert();
    }
    public abstract void convert(MyViewHold holder,T t);
}
