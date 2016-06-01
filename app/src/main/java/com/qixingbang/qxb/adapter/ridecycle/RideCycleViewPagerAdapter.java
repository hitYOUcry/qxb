package com.qixingbang.qxb.adapter.ridecycle;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qixingbang.qxblibrary.pulltorefresh.ILoadingLayout;
import com.qixingbang.qxblibrary.pulltorefresh.PullToRefreshBase;
import com.qixingbang.qxblibrary.pulltorefresh.PullToRefreshListView;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.ridecycle.RideCycleBean;
import com.qixingbang.qxb.beans.ridecycle.Type;

import java.util.List;

/**
 * Created by zqj on 2015/11/5 17:57.
 */
public class RideCycleViewPagerAdapter extends PagerAdapter {
    public final static int NUM = 4;
    private List<List<RideCycleBean>> mList;
    private View[] mViews = new View[NUM];
    private PullToRefreshListView[] mListViews = new PullToRefreshListView[NUM];
    private RideCycleInfoAdapter[] mAdapters = new RideCycleInfoAdapter[NUM];
    private Context mContext;

    private ListViewListener mListener;

    public RideCycleViewPagerAdapter(Context context, List<List<RideCycleBean>> list) {
        mContext = context;
        mList = list;
        initViews();
    }

    public void setListViewListener(ListViewListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return NUM;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews[position]);
        return mViews[position];
    }

    public void dataSetChanged(int index) {
        mAdapters[index].notifyDataSetChanged();
    }

    public void refreshComplete(int index) {
        if (mListViews[index].isRefreshing()) {
            mListViews[index].onRefreshComplete();
        }
    }

    private void initViews() {
        for (int i = 0; i < NUM; i++) {
            View view = View.inflate(mContext, R.layout.item_ridecaycle_viewpager, null);
            mListViews[i] = (PullToRefreshListView) view.findViewById(R.id.pullToRefreshListView);

            PullToRefreshListView listView = mListViews[i];

            mAdapters[i] = new RideCycleInfoAdapter(mContext, mList.get(i));
            listView.setAdapter(mAdapters[i]);
            listView.setMode(PullToRefreshBase.Mode.BOTH);

            ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
            endLabels.setPullLabel(mContext.getString(R.string.load_more));// 刚下拉时，显示的提示
            endLabels.setRefreshingLabel(mContext.getString(R.string.loading));// 刷新时
            endLabels.setReleaseLabel(mContext.getString(R.string.begin_load));// 下来达到一定距离时，显示的提示

            final Type type = Type.get(i);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mListener.onItemClick(view,position, type);
                }
            });
            listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    mListener.onPullDownRefresh(type);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    mListener.onPullUpToRefresh(type);
                }
            });
            mViews[i] = view;
        }
    }

    public interface ListViewListener {
        void onItemClick(View view,int position, Type type);

        void onPullDownRefresh(Type type);

        void onPullUpToRefresh(Type type);
    }
}
