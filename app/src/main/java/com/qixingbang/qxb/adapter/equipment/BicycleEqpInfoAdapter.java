package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.equipment.bicycleEqp.BicycleEqp;

import java.util.List;

/**
 * Created by zqj on 2015/8/31 19:14.
 */
public class BicycleEqpInfoAdapter extends BaseInfoAdapter {
    private List<BicycleEqp> mList;

    public BicycleEqpInfoAdapter(Context context, List<BicycleEqp> list) {
        super(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_listview_personequipment, null);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_bicyclePicture);
            viewHolder.modelTextView = (TextView) convertView.findViewById(R.id.textView_model);
            viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.textView_price);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 1) {
            viewHolder.layout.setBackgroundColor(mContext.
                    getResources().getColor(R.color.searchPart__black));
        } else {
            viewHolder.layout.setBackgroundColor(mContext.
                    getResources().getColor(R.color.theme_black));
        }
        BicycleEqp bicycleEqp = mList.get(position);
        mBitmapUtils.display(viewHolder.imageView, bicycleEqp.getPicUrl().get(0));
        viewHolder.modelTextView.setText(bicycleEqp.getName());
        viewHolder.priceTextView.setText("参考价位：" + bicycleEqp.getPrice() + "元");
        return convertView;
    }

    class ViewHolder {
        LinearLayout layout;
        ImageView imageView;
        TextView modelTextView;
        TextView priceTextView;
    }
}
