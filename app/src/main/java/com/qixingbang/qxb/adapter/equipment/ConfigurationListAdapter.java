package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.equipment.bicycle.ConfigItem;

import java.util.List;

/**
 * Created by zqj on 2015/9/6 09:44.
 */
public class ConfigurationListAdapter extends BaseAdapter {

    private List<ConfigItem> mList;
    private Context mContext;

    public ConfigurationListAdapter(Context context, List<ConfigItem> list) {
        mContext = context;
        mList = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_listview_configuration, null);
        TextView itemName = (TextView) view.findViewById(R.id.textView_itemName);
        TextView itemContent = (TextView) view.findViewById(R.id.textVIew_itemContent);
        ImageView showDetails = (ImageView) view.findViewById(R.id.imageView_itemMore);

        itemName.setText(mList.get(position).getName());
        itemContent.setText(mList.get(position).getContent().getName());
        if (mList.get(position).getContent().getHasDetail()) {
            showDetails.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
