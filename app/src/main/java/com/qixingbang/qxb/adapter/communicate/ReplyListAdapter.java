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
import com.qixingbang.qxb.beans.ReplyBean;

import java.util.List;

/**
 * Created by zqj on 2015/12/2 20:35.
 */
public class ReplyListAdapter extends BaseInfoAdapter {

    public interface OnElementClickListener {
        void likeClicked(int likeFlagBeforeClick, int answerId, Runnable successRunnable);

        //使用dialog方式预览
        void imageClicked(int index, List<String> picUrls);

        //使用动画方式预览（暂定使用动画）
        void imageClicked(ImageView clickedView, String picUrl);

    }

    private List<ReplyBean> mList;

    private final int TYPE_ANSWER_TEXT = 0;
    private final int TYPE_ANSWER_PIC = 1;
    private final int TYPE_TITLE_TEXT = 2;
    private final int TYPE_TITLE_PIC = 3;


    private OnElementClickListener mElementListener;

    public ReplyListAdapter(List<ReplyBean> list, Context context, OnElementClickListener listener) {
        super(context);
        mList = list;
        mElementListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReplyBean item = mList.get(position);
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (null == convertView) {
            viewHolder = new ViewHolder();
            if (type == TYPE_ANSWER_TEXT || type == TYPE_TITLE_TEXT) {
                convertView = View.inflate(mContext, R.layout.item_listview_communicate_reply, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_listview_communicate_reply_image, null);
                viewHolder.pic[0] = (ImageView) convertView.findViewById(R.id.imageView_one);
                viewHolder.pic[1] = (ImageView) convertView.findViewById(R.id.imageView_two);
                viewHolder.pic[2] = (ImageView) convertView.findViewById(R.id.imageView_three);
            }
            viewHolder.headPortrait = (RoundImageView) convertView.findViewById(R.id.imageView_portrait);
            viewHolder.nickname = (TextView) convertView.findViewById(R.id.textView_nickname);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textView_date);
            viewHolder.content = (TextView) convertView.findViewById(R.id.textView_content);
            viewHolder.likeIcon = (ImageView) convertView.findViewById(R.id.imageView_like);
            viewHolder.likeNum = (TextView) convertView.findViewById(R.id.textView_likeNum);
            if (type == TYPE_TITLE_TEXT || type == TYPE_TITLE_PIC) {
                viewHolder.likeIcon.setVisibility(View.INVISIBLE);
                viewHolder.likeNum.setVisibility(View.INVISIBLE);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (type == TYPE_TITLE_TEXT || type == TYPE_ANSWER_TEXT) {
            viewHolder.showTextContent(item);
        } else {
            viewHolder.showImageContent(item);
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            if (mList.get(position).getPicList().isEmpty()) {
                return TYPE_TITLE_TEXT;
            } else {
                return TYPE_TITLE_PIC;
            }
        } else {
            if (mList.get(position).getPicList().isEmpty()) {
                return TYPE_ANSWER_TEXT;
            } else {
                return TYPE_ANSWER_PIC;
            }
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getAnswererId();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    class ViewHolder {
        private RoundImageView headPortrait;
        private TextView nickname;
        private TextView date;
        private TextView content;

        private ImageView likeIcon;
        private TextView likeNum;
        private ImageView[] pic = new ImageView[3];


        public void showTextContent(final ReplyBean item) {
            if (!TextUtils.isEmpty(item.getAnsererIcon())) {
                mBitmapUtils.display(headPortrait, item.getAnsererIcon());
            }
            nickname.setText(item.getAnswererName());
            date.setText(item.getAnswerTime());
            content.setText(item.getContent());
            if (1 == item.getIsLike()) {
                likeIcon.setImageResource(R.drawable.ic_common_like_red);
            } else {
                likeIcon.setImageResource(R.drawable.ic_common_like_gray);
            }
            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mElementListener.likeClicked(item.getIsLike(), item.getAnswerId(), new Runnable() {
                        @Override
                        public void run() {
                            if (item.getIsLike() == 0) {
                                likeIcon.setImageResource(R.drawable.ic_common_like_red);
                                likeNum.setText(String.valueOf(Integer.valueOf(likeNum.getText().toString()) + 1));
                                item.setIsLike(1);
                            } else {
                                likeIcon.setImageResource(R.drawable.ic_common_like_gray);
                                likeNum.setText(String.valueOf(Integer.valueOf(likeNum.getText().toString()) - 1));
                                item.setIsLike(0);
                            }
                        }
                    });
                }
            });
            likeNum.setText(String.valueOf(item.getLikeCount()));
        }

        public void showImageContent(final ReplyBean item) {
            showTextContent(item);
            List<String> urlList = item.getPicList();
            for (int i = 0; i < urlList.size(); i++) {
                mBitmapUtils.display(pic[i], urlList.get(i));
                final int index = i;
                pic[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                        mElementListener.imageClicked(index, item.getPicList());
                        mElementListener.imageClicked(pic[index], item.getPicList().get(index));
                    }
                });
            }
        }

        public void clear() {
            if (null != headPortrait) {
                headPortrait.setImageResource(R.drawable.ic_common_pic_none);
            }
            for (int i = 0; i < pic.length; i++) {
                if (null != pic[i]) {
                    pic[i].setImageResource(R.drawable.ic_common_pic_none);
                }
            }
            if (null != nickname) {
                nickname.setText(" ");
            }
            if (null != date) {
                date.setText(" ");
            }
            if (null != content) {
                content.setText(" ");
            }
            if (null != likeIcon) {
                likeIcon.setImageResource(R.drawable.ic_common_like_gray);
            }
            if (null != likeNum) {
                likeNum.setText(" ");
            }
        }
    }
}
