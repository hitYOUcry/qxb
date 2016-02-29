package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qixingbang.qxb.R;

import java.util.List;

/**
 * Created by zqj on 2015/8/27 15:52.
 */
public class FilterInfoAdapter extends BaseAdapter {

    private List<String> mList;
    private Context mContext;

    public FilterInfoAdapter(Context context) {
        mContext = context;
    }

    public FilterInfoAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    public void setInfo(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_listview_filter, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView_filter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
