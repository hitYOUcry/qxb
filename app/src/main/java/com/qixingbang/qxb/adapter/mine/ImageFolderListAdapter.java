package com.qixingbang.qxb.adapter.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageFolder;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Z.H. on 2015/11/13 16:33.
 */
public class ImageFolderListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<ImageFolder> mList;
    private LayoutInflater mInflater;
    private BitmapUtils mBitmapUtils;


    public interface OnImageListItemSelected{
        void OnSelected(ImageFolder folder);
    }

    public void setImageListItemSelected(OnImageListItemSelected imageListItemSelected) {
        mImageListItemSelected = imageListItemSelected;
    }

    private OnImageListItemSelected mImageListItemSelected;

    public ImageFolderListAdapter(Context context, ArrayList<ImageFolder> list){
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mBitmapUtils = new BitmapUtils(context);
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
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_listview_image_folder, null);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv_image_folder);
            holder.mTextView = (TextView) convertView.findViewById(R.id.tv_image_folder_info);
            holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //BitmapDisplayConfig config = new BitmapDisplayConfig();
        //BitmapSize size = new BitmapSize(60, 60);
        //config.setBitmapMaxSize(size);
        //mBitmapUtils.display(holder.mImageView, mList.get(position).getFirstImagePath(), config);
        //holder.mImageView.setImageResource(R.mipmap.ic_launcher);

        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mList.get(position).getFirstImagePath(), holder.mImageView);
        holder.mTextView.setText(mList.get(position).getFolderName() + " (" + mList.get(position).getCount() + ")");

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageListItemSelected.OnSelected(mList.get(position));
            }
        });

        return convertView;
    }

    class ViewHolder{
        private RelativeLayout mLayout;
        private ImageView mImageView;
        private TextView mTextView;
    }
}
