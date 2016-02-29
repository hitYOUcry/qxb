package com.qixingbang.qxb.adapter.ridecycle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.BaseInfoAdapter;
import com.qixingbang.qxb.beans.ridecycle.RideCycleBean;

import java.util.List;

/**
 * Created by lsr on 2015/9/6 19:26.
 * 整车二级界面 自行车信息列表 适配器
 */
public class RideCycleInfoAdapter extends BaseInfoAdapter {

    private List<RideCycleBean> mList;
    private Context mContext;

    public RideCycleInfoAdapter(Context context, List<RideCycleBean> list) {
        super(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.item_listview_ridecycle, null);
            viewHolder = new ViewHolder();
            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.ridecyclelinearLayout);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_ridecyclePicture);
            viewHolder.ridecycleTextView = (TextView) convertView.findViewById(R.id.textView_ridecycle);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.textView_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RideCycleBean bean = mList.get(position);
        mBitmapUtils.display(viewHolder.imageView, bean.getLogo());
        viewHolder.ridecycleTextView.setText(bean.getTitle());
        viewHolder.dateTextView.setText(bean.getCreateTime());
        return convertView;
    }

    class ViewHolder {
        LinearLayout layout;
        ImageView imageView;
        TextView ridecycleTextView;
        TextView dateTextView;
    }
}
