package com.qixingbang.qxb.adapter.equipment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.beans.equipment.Comment;

import java.util.List;

/**
 * Created by zqj on 2015/9/6 19:34.
 * 评论区 ListViewAdapter
 */
public class CommentListAdapter extends BaseInfoAdapter {

    private List<Comment> mList;

    public CommentListAdapter(Context context, List<Comment> list) {
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
            convertView = View.inflate(mContext, R.layout.item_listview_comment_third, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.textView_name);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.textView_date);
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.textView_content);
            viewHolder.portraitImageView = (RoundImageView) convertView.findViewById(R.id.imageView_portrait);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.showContent(mList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView contentTextView;
        RoundImageView portraitImageView;

        private void showContent(Comment comment) {
            nameTextView.setText(comment.getUserName());
            dateTextView.setText(comment.getCommentTime());
            contentTextView.setText(comment.getContent());
            if(comment.getUserIconUrl().isEmpty()){
                portraitImageView.setImageResource(R.drawable.xml_shape_ring_gray_inner16dp);
            }else {
                mBitmapUtils.display(portraitImageView, comment.getUserIconUrl());
            }
        }
    }
}
