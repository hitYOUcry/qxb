package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.equipment.bicycle.Bicycle;

import java.util.List;

/**
 * Created by zqj on 2015/8/26 15:26.
 * 整车二级界面 自行车信息列表 适配器
 */
public class BicycleInfoAdapter extends BaseInfoAdapter {

    private List<Bicycle> mList;

    public BicycleInfoAdapter(Context context, List<Bicycle> list) {
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
            convertView = View.inflate(mContext, R.layout.item_listview_bicycleinfo, null);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_bicyclePicture);
            viewHolder.modelTextView = (TextView) convertView.findViewById(R.id.textView_model);
            viewHolder.modelDetailsTextView = (TextView) convertView.findViewById(R.id.textView_modelDetails);
            viewHolder.suitableTextView = (TextView) convertView.findViewById(R.id.textView_suitable);
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
        Bicycle bicycle = mList.get(position);
        if (bicycle.getPicUrl().size() > 0) {
            mBitmapUtils.display(viewHolder.imageView, bicycle.getPicUrl().get(0));
        }
        viewHolder.modelTextView.setText(bicycle.getModel());

        //TODO details info not complete
        viewHolder.modelDetailsTextView.setText(bicycle.getRoadType());

        viewHolder.suitableTextView.setText(bicycle.getBikeType());
        if (bicycle.getPrice() == 0) {
            viewHolder.priceTextView.setText("参考价位：----￥");
        } else {
            viewHolder.priceTextView.setText("参考价位：" + bicycle.getPrice() + "￥");
        }
        return convertView;
    }

    class ViewHolder {
        LinearLayout layout;
        ImageView imageView;
        TextView modelTextView;
        TextView modelDetailsTextView;
        TextView suitableTextView;
        TextView priceTextView;
    }
}
