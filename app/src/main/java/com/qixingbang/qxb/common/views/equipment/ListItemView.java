package com.qixingbang.qxb.common.views.equipment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.equipment.FilterInfoAdapter;
import com.qixingbang.qxb.beans.equipment.SearchInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/9/30 10:16.
 */
public class ListItemView extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    public ListItemView(Context context) {
        super(context);
        init(context);
    }

    public ListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * content views
     */
    private final static int INDEX_BRAND = 0;
    private final static int INDEX_TYPE = 1;
    private final static int INDEX_PRICE = 2;

    private List<String> mPriceList;

    private List<String> mBrandList;

    private List<String> mTypeList;

    ImageView backImageView;
    ImageView shareImageView;
    TextView tabTipTextView;

    EditText searchEdittext;
    TextView searchConfirmTextView;

    RelativeLayout brandRelativeLayout;
    TextView brandTextView;
    View brandHintView;

    RelativeLayout typeRelativeLayout;
    TextView typeTextView;
    View typeHintView;

    RelativeLayout priceRelativeLayout;
    TextView priceTextView;
    View priceHintView;

    PullToRefreshListView mListView;
    private BaseAdapter mAdapter;

    PopupWindow filterPopupWindow;

    private SearchInfo mSearchInfo;
    private int mMaxId;
    private ItemListListener mItemListListener;
    private Context mContext;

    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.common_itemlist, this);
        initView();
        initData();
    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);
        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);

        searchEdittext = (EditText) findViewById(R.id.editText_search);
        searchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    onClick(searchConfirmTextView);
                    return true;
                }
                return false;
            }
        });
        searchConfirmTextView = (TextView) findViewById(R.id.textView_search_confirm);
        searchConfirmTextView.setOnClickListener(this);

        brandRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_brand);
        brandRelativeLayout.setOnClickListener(this);
        typeRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_type);
        typeRelativeLayout.setOnClickListener(this);
        priceRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_price);
        priceRelativeLayout.setOnClickListener(this);

        brandHintView = findViewById(R.id.view_filterHint_brand);
        typeHintView = findViewById(R.id.view_filterHint_type);
        priceHintView = findViewById(R.id.view_filterHint_price);

        brandTextView = (TextView) findViewById(R.id.textView_filter_brand);
        typeTextView = (TextView) findViewById(R.id.textView_filter_type);
        priceTextView = (TextView) findViewById(R.id.textView_filter_price);

        mListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(this);
        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(mContext.getString(R.string.load_more));// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel(mContext.getString(R.string.loading));// 刷新时
        endLabels.setReleaseLabel(mContext.getString(R.string.begin_load));// 下来达到一定距离时，显示的提示
    }

    private void initData() {
        mSearchInfo = new SearchInfo();
        mPriceList = new ArrayList<>();
        mBrandList = new ArrayList<>();
        mTypeList = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_back:
                ((Activity) mContext).finish();
                break;
            case R.id.imageView_share:
                //TODO
                break;
            case R.id.relativeLayout_brand:
                brandClicked();
                break;
            case R.id.relativeLayout_type:
                typeClicked();
                break;
            case R.id.relativeLayout_price:
                priceClicked();
                break;
            case R.id.textView_search_confirm:
                mSearchInfo.setAction(SearchInfo.ACTION_NEW);
                mSearchInfo.setId(0);
                mSearchInfo.setSearch(searchEdittext.getText().toString());
                mItemListListener.sentSearchInfoToServer();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        mSearchInfo.setAction(SearchInfo.ACTION_MORE);
        mSearchInfo.setId(mMaxId);
        mItemListListener.pullToRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO position-1 because of the pullToRefresh listView.
        mItemListListener.jumpNextActivity(position - 1);
    }

    public void setTabTip(String title) {
        tabTipTextView.setText(title);
    }

    public void setMaxId(int maxId) {
        mMaxId = maxId;
    }

    public void setLists(List<String> brandList, List<String> typeList, List<String> priceList) {
        mBrandList = brandList;
        mTypeList = typeList;
        mPriceList = priceList;
    }

    public void setItemListener(ItemListListener listener) {
        mItemListListener = listener;
        onClick(searchConfirmTextView);
    }

    public void setListViewAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    public JSONObject getSearchJsonObject() {
        return mSearchInfo.toJsonObject();
    }

    public void pullRefreshDone() {
        if (null != mListView) {
            mListView.onRefreshComplete();
        }
    }

    private void priceClicked() {
        setFilterHintColor(INDEX_PRICE);
        setPopWindow(INDEX_PRICE, mPriceList);
        filterPopupWindow.showAsDropDown(priceHintView);
    }


    private void typeClicked() {
        setFilterHintColor(INDEX_TYPE);
        setPopWindow(INDEX_TYPE, mTypeList);
        filterPopupWindow.showAsDropDown(typeHintView);
    }

    private void brandClicked() {
        setFilterHintColor(INDEX_BRAND);
        setPopWindow(INDEX_BRAND, mBrandList);
        filterPopupWindow.showAsDropDown(brandHintView);
    }

    /**
     * 设置选择栏底边提示条颜色
     *
     * @param index
     */
    private void setFilterHintColor(int index) {
        Resources res = getResources();
        brandHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        typeHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        priceHintView.setBackgroundColor(res.getColor(R.color.theme_black));
        switch (index) {
            case INDEX_BRAND:
                brandHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                break;
            case INDEX_TYPE:
                typeHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                break;
            case INDEX_PRICE:
                priceHintView.setBackgroundColor(res.getColor(R.color.yellow_light));
                break;
            default:
                break;
        }
    }

    /**
     * 选择栏 文字显示
     */
    private void showFilterText() {
        Resources res = getResources();
        if (mSearchInfo.getBrand().isEmpty()) {
            brandTextView.setText(res.getString(R.string.brand));
        } else {
            brandTextView.setText(mSearchInfo.getBrand());
        }

        if (mSearchInfo.getType().isEmpty()) {
            typeTextView.setText(res.getString(R.string.type));
        } else {
            typeTextView.setText(mSearchInfo.getType());
        }

        if (mSearchInfo.getPrice() == 0) {
            priceTextView.setText(res.getString(R.string.price));
        } else {
            priceTextView.setText(mPriceList.get(mSearchInfo.getPrice()));
        }
    }

    /**
     * PopupWindow显示
     *
     * @param index：点击      索引
     * @param list:显示的字符串列表
     */
    private void setPopWindow(final int index, final List<String> list) {
        View view = View.inflate(mContext, R.layout.popwindow_filter, null);
        ListView popWindowListView = (ListView) view.findViewById(R.id.listView_filter);
        filterPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != filterPopupWindow && filterPopupWindow.isShowing()) {
                    filterPopupWindow.dismiss();
                    filterPopupWindow = null;
                    setFilterHintColor(-1);//全暗
                }
                return false;
            }
        });
        FilterInfoAdapter adapter = new FilterInfoAdapter(mContext, list);
        popWindowListView.setAdapter(adapter);
        popWindowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (index) {
                    case INDEX_BRAND:
                        if (position == list.size() - 1) {
                            //TODO
                        }
                        mSearchInfo.setBrand(list.get(position));
                        break;
                    case INDEX_TYPE:
                        mSearchInfo.setType(list.get(position));
                        break;
                    case INDEX_PRICE:
                        mSearchInfo.setPrice(position);
                        break;
                    default:
                        break;
                }
                mSearchInfo.setAction(SearchInfo.ACTION_NEW);
                showFilterText();
                mItemListListener.sentSearchInfoToServer();
                if (null != filterPopupWindow && filterPopupWindow.isShowing()) {
                    filterPopupWindow.dismiss();
                    filterPopupWindow = null;
                    setFilterHintColor(-1);//全暗
                }
            }
        });
    }

    /**
     * itemlist interface
     */
    public interface ItemListListener {
        //搜索信息
        void sentSearchInfoToServer();

        //跳转AC
        void jumpNextActivity(int position);

        //上拉刷新
        void pullToRefresh();
    }
}
