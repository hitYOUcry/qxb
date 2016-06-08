package com.qixingbang.qxb.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.mine.map.RideInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zqj on 2016/6/7 16:51.
 */
public class RideHistoryDrawLayout extends LinearLayout {

    public interface ItemClickListener {
        void onItemClicked(RideInfo rideInfo);
    }

    public RideHistoryDrawLayout(Context context) {
        super(context);
        init();
    }

    public RideHistoryDrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RideHistoryDrawLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    ListView listView;
    RideHistoryAdapter mAdapter;
    List<RideInfo> mRideInfos;

    ItemClickListener mListener;

    public void setRideInfo(List<RideInfo> infos) {
        mRideInfos.clear();
        mRideInfos.addAll(infos);
        mAdapter.notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    private void init() {
        inflate(getContext(), R.layout.activity_ride_history, this);
        initViews();
        initData();
    }

    private void initData() {
        mRideInfos = new ArrayList<>();
        mAdapter = new RideHistoryAdapter(mRideInfos);
        listView.setAdapter(mAdapter);
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListener) {
                    mListener.onItemClicked(mRideInfos.get(position));
                }
            }
        });
    }

    private class RideHistoryAdapter extends BaseAdapter {

        List<RideInfo> mData;

        public RideHistoryAdapter(List<RideInfo> lists) {
            mData = lists;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_listview_ride_history, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.showRideInfo(mData.get(position));
            return convertView;
        }

        class ViewHolder {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            TextView timeTxv;
            TextView startPointTxv;
            TextView endPointTxv;
            TextView durationTxv;

            public ViewHolder(View view) {
                timeTxv = (TextView) view.findViewById(R.id.textView_time);
                startPointTxv = (TextView) view.findViewById(R.id.textView_startPoint);
                endPointTxv = (TextView) view.findViewById(R.id.textView_endPoint);
                durationTxv = (TextView) view.findViewById(R.id.textView_duration);
            }

            public void showRideInfo(RideInfo rideInfo) {
                timeTxv.setText(formatter.format(new Date(rideInfo.getStartTime())));
                startPointTxv.setText(rideInfo.getStartLocDescription());
                endPointTxv.setText(rideInfo.getEndLocDescription());
                durationTxv.setText(getShowDuration(rideInfo.getRideDuration()));

            }

            private String getShowDuration(int seconds) {
                if (seconds < 60) {
                    return seconds + "s";
                } else if (seconds < 60 * 60) {
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    return minutes + "m" + seconds + "s";
                } else {
                    int hours = seconds / 3600;
                    seconds = seconds % 3600;
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    return hours + "h" + minutes + "m" + seconds + "s";
                }
            }

        }
    }


}
