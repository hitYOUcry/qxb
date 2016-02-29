package com.qixingbang.qxb.base.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Z.H. on 2015/11/19 11:04.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mLayoutInflater;
    protected Context mContext;
    protected List<T> mData;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> data, int itemLayoutId){
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mData = data;
        mItemLayoutId = itemLayoutId;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    protected abstract void convert(ViewHolder helper, T item);

    private final ViewHolder getViewHolder(int position, View convertView, ViewGroup parent){
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }
}
