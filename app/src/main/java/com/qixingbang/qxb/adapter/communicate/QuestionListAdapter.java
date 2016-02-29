package com.qixingbang.qxb.adapter.communicate;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.adapter.equipment.BaseInfoAdapter;
import com.qixingbang.qxb.beans.communicate.CommunicateBean;

import java.util.List;

/**
 * Created by cr30 on 2015/9/6.
 */
public class QuestionListAdapter extends BaseInfoAdapter {

    private final static int WITH_PICS = 0;
    private final static int WITHOUT_PICS = 1;


    private List<CommunicateBean> mList;


    public QuestionListAdapter(Context context, List<CommunicateBean> list) {
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (!mList.get(position).getPicList().isEmpty())
            return WITH_PICS;
        else
            return WITHOUT_PICS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int viewType = getItemViewType(position);
        CommunicateBean item = mList.get(position);
        if (null == convertView) {
            viewHolder = new ViewHolder();
            if (viewType == WITHOUT_PICS) {
                convertView = View.inflate(mContext, R.layout.item_listview_communicate, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_listview_communicate_image, null);
                viewHolder.pic[0] = (ImageView) convertView.findViewById(R.id.imageView_one);
                viewHolder.pic[1] = (ImageView) convertView.findViewById(R.id.imageView_two);
                viewHolder.pic[2] = (ImageView) convertView.findViewById(R.id.imageView_three);
            }
            viewHolder.headPortrait = (RoundImageView) convertView.findViewById(R.id.imageView_communicate_head);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_cmct_title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textView_date);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tv_cmct_content);
            viewHolder.answerNum = (TextView) convertView.findViewById(R.id.tv_cmct_notenum);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.clear();
        if (viewType == WITH_PICS) {
            viewHolder.showContentWithPics(item);
        } else {
            viewHolder.showContentWithOutPics(item);
        }
        return convertView;
    }

    private class ViewHolder {
        private RoundImageView headPortrait;
        private TextView title;
        private TextView date;
        private TextView content;
        private TextView answerNum;
        private ImageView[] pic = new ImageView[3];

        public void showContentWithOutPics(CommunicateBean item) {
            if (!TextUtils.isEmpty(item.getAskerIcon())) {
                mBitmapUtils.display(headPortrait, item.getAskerIcon());
            } else {
                headPortrait.setImageResource(R.drawable.ic_communicate_head);
            }
//            title.setText(item.getTitle());
            title.setText(item.getAskerName());
            date.setText(item.getCreateTime());
            content.setText(item.getContent());
            answerNum.setText(item.getAnswerCount());
        }

        private void showContentWithPics(CommunicateBean item) {
            showContentWithOutPics(item);
            List<String> picList = item.getPicList();
            for (int i = 0; i < picList.size(); i++) {
                mBitmapUtils.display(pic[i], picList.get(i));
            }
        }

        public void clear() {
            if (null != headPortrait) {
                headPortrait.setImageResource(R.drawable.ic_common_pic_none);
            }
            for (int i = 0; i < pic.length; i++) {
                if (null != pic[i]) {
                    pic[i].setImageResource(R.drawable.xml_common_transparent);
                }
            }
        }
    }


}


