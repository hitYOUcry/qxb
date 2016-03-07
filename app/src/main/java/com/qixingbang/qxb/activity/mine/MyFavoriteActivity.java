package com.qixingbang.qxb.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.L;
import com.common.utils.T;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.equipment.BicycleDetailsActivity;
import com.qixingbang.qxb.activity.equipment.BicycleEqpDetailsActivity;
import com.qixingbang.qxb.activity.equipment.BicyclePartsDetailsActivity;
import com.qixingbang.qxb.activity.equipment.PersonalEqpDetailsActivity;
import com.qixingbang.qxb.adapter.mine.MyFavoritePagerAdapter;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.base.activity.CommonAdapter;
import com.qixingbang.qxb.base.activity.ViewHolder;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.equipment.bicycleEqp.BicycleEqp;
import com.qixingbang.qxb.beans.equipment.userEqp.PersonalEqp;
import com.qixingbang.qxb.beans.mine.myFav.MyFavoriteEqpBean;
import com.qixingbang.qxb.beans.mine.myFav.MyFavoriteEqpList;
import com.qixingbang.qxb.beans.mine.myFav.MyFavoriteRCycleBean;
import com.qixingbang.qxb.beans.mine.myFav.MyFavoriteRCycleList;
import com.qixingbang.qxb.server.UrlUtil;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class MyFavoriteActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private String token;
    /**
     * 界面标题
     */
    @Bind(R.id.textView_tabTip)
    TextView tvTitle;
    /**
     * 装备选项卡的父布局
     */
    @Bind(R.id.ll_tab_equipment)
    LinearLayout llTabEquipment;
    /**
     * 骑行圈选项卡的父布局
     */
    @Bind(R.id.ll_tab_ride_cycle)
    LinearLayout llTabRideCycle;
    /**
     * 装备选项卡选中时标识
     */
    @Bind(R.id.tv_tab_equipment_bottom_image)
    ImageView ivTabEquipment;
    /**
     * 骑行圈选项卡选中时标识
     */
    @Bind(R.id.tv_tab_ride_cycle_bottom_image)
    ImageView ivTabRideCycle;

    @Bind(R.id.vp_content)
    ViewPager vpContent;
    private MyFavoritePagerAdapter mAdapter;
    private ArrayList<View> mPagerViews;

    //装备选项卡列表
    private PullToRefreshListView ptrlvEquipment;
    private MyFavoriteEqpList mFavoriteEqpList;
    private LinkedList<MyFavoriteEqpBean> mMyFavoriteEqpBeans;
    private CommonAdapter<MyFavoriteEqpBean> mEqpListAdapter;

    //骑行圈选项卡列表
    private PullToRefreshListView ptrlvRideCycle;
    private MyFavoriteRCycleList mFavoriteRCycleList;
    private LinkedList<MyFavoriteRCycleBean> mMyFavoriteRCycleBeans;
    private CommonAdapter<MyFavoriteRCycleBean> mRCycleListAdapter;

    private BitmapUtils mBitmapUtils;

    @Bind(R.id.imageView_back)
    ImageView mImageViewBack;
    @Bind(R.id.imageView_share)
    ImageView mImageViewShare;
    @Bind(R.id.tv_tab_equipment)
    TextView mTvTabEquipment;
    @Bind(R.id.tv_tab_ride_cycle)
    TextView mTvTabRideCycle;

    private static final int REQUEST_EQP_OK = 0x01;
    private static final int REQUEST_RCYCLE_OK = 0x02;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_EQP_OK){
                if (mEqpListAdapter == null){
                    mEqpListAdapter = new CommonAdapter<MyFavoriteEqpBean>(getApplicationContext(),
                            mMyFavoriteEqpBeans ,R.layout.item_listview_my_favorite_eqp) {
                        @Override
                        protected void convert(ViewHolder helper, MyFavoriteEqpBean item) {
                            mBitmapUtils.display(helper.getView(R.id.iv_item), UrlUtil.getBaseAddress() + item.logo.substring(20));
                            helper.setText(R.id.tv_title, item.name);
                            if(helper.getPosition() % 2 == 0){
                                helper.getConvertView().setBackgroundColor(getResources().getColor(R.color.theme_black));
                            }else {
                                helper.getConvertView().setBackgroundColor(getResources().getColor(R.color.black_242424));
                            }
                            helper.setText(R.id.tv_price, "参考价位: " + item.price + "元");

                        }
                    };
                    ptrlvEquipment.setAdapter(mEqpListAdapter);
                }else {
                    ptrlvEquipment.onRefreshComplete();
                    mEqpListAdapter.notifyDataSetChanged();
                }

            }else if (msg.what == REQUEST_RCYCLE_OK){
                mRCycleListAdapter = new CommonAdapter<MyFavoriteRCycleBean>(getApplicationContext(),
                        mMyFavoriteRCycleBeans ,R.layout.item_listview_my_favorite_cycle) {
                    @Override
                    protected void convert(ViewHolder helper, MyFavoriteRCycleBean item) {
                        mBitmapUtils.display(helper.getView(R.id.iv_item), item.logo);
                        helper.setText(R.id.tv_title, item.name);
                        if(helper.getPosition() % 2 == 0){
                            helper.getConvertView().setBackgroundColor(getResources().getColor(R.color.theme_black));
                        }else {
                            helper.getConvertView().setBackgroundColor(getResources().getColor(R.color.black_242424));
                        }
                    }
                };
                ptrlvRideCycle.setAdapter(mRCycleListAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //// TODO: 2015/10/12 骑行圈列表需要数据调试
        setContentView(R.layout.activity_my_favorite);
        ButterKnife.bind(this);
        //获取token
        token = QAccount.getToken();

        initView();
        initData();
        initListener();
    }


    @Override
    public void initView() {
        mBitmapUtils = new BitmapUtils(this);
        vpContent.addOnPageChangeListener(this);
        mPagerViews = new ArrayList<View>();

        LayoutInflater inflater = getLayoutInflater();
        View eqp = inflater.inflate(R.layout.my_favorite_viewpager_item_eqp, null);
        View ride = inflater.inflate(R.layout.my_favorite_viewpager_item_ride, null);

        ptrlvEquipment = (PullToRefreshListView) eqp.findViewById(R.id.lv_equipment);
        ptrlvEquipment.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        ptrlvRideCycle = (PullToRefreshListView) ride.findViewById(R.id.lv_ridecycle);
        ptrlvRideCycle.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        mPagerViews.add(eqp);
        mPagerViews.add(ride);

        mFavoriteEqpList = new MyFavoriteEqpList();
        mMyFavoriteEqpBeans = new LinkedList<MyFavoriteEqpBean>();
        mMyFavoriteRCycleBeans = new LinkedList<MyFavoriteRCycleBean>();
    }

    @Override
    public void initData() {
        tvTitle.setText(R.string.my_favorite);
        initTabState(ivTabEquipment, ivTabRideCycle, llTabEquipment, llTabRideCycle);
        mAdapter = new MyFavoritePagerAdapter(this, mPagerViews);
        vpContent.setAdapter(mAdapter);
        //初始化listview
        initListView();
    }

    private void initListener() {
        ptrlvEquipment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyFavoriteEqpBean bean = mMyFavoriteEqpBeans.get(position - 1);
                String type = bean.type;
                int beanId = bean.parentId;
                jumpToDetailsActivity(type, beanId, position);
            }
        });

        ptrlvEquipment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String myFavEqpUrl = UrlUtil.getMyFavEqpList() + "/" + mMyFavoriteEqpBeans.getLast().favId;
                L.d(myFavEqpUrl);
                getFavEqpFromServer(myFavEqpUrl);
            }
        });

        ptrlvRideCycle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.d(String.valueOf(position));
            }
        });

        ptrlvRideCycle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    private void jumpToDetailsActivity(String type, int beanId, int position) {
        switch (type) {
            case "bike":
                //整车
                L.d(String.valueOf(position) + "整车");
//                Bicycle bicycle = new Bicycle();
//                bicycle.setBikeId(beanId);
                BicycleDetailsActivity.start(MyFavoriteActivity.this, beanId);
                break;
            case "bodyEqp":
                //人身装备
                L.d(String.valueOf(position) + "人身装备");
                PersonalEqp personalEqp = new PersonalEqp();
                personalEqp.setId(beanId);
                PersonalEqpDetailsActivity.start(MyFavoriteActivity.this, personalEqp);
                break;
            case "bikeEqp":
                //车身装备
                L.d(String.valueOf(position) + "车身装备");
                BicycleEqp bicycleEqp = new BicycleEqp();
                bicycleEqp.setId(beanId);
                BicycleEqpDetailsActivity.start(MyFavoriteActivity.this, bicycleEqp);
                break;
            case "accessory":
                //零部件
//                L.d(String.valueOf(position) + "零部件");
//                Accessory bicycleParts = new Accessory();
//                bicycleParts.setId(beanId);
                BicyclePartsDetailsActivity.start(MyFavoriteActivity.this, beanId);
                break;
            default:
                break;
        }
    }

    /**
     * 设置Tab被选中的UI
     *
     * @param checkedIv
     * @param unCheckedIv
     * @param checkedLl
     * @param unCheckedLl
     */
    private void initTabState(ImageView checkedIv, ImageView unCheckedIv, LinearLayout checkedLl, LinearLayout unCheckedLl) {
        checkedIv.setVisibility(View.VISIBLE);
        unCheckedIv.setVisibility(View.INVISIBLE);
        checkedLl.setBackgroundResource(R.color.black_242424);
        unCheckedLl.setBackgroundResource(R.color.theme_black);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            initTabState(ivTabEquipment, ivTabRideCycle, llTabEquipment, llTabRideCycle);
        } else {
            initTabState(ivTabRideCycle, ivTabEquipment, llTabRideCycle, llTabEquipment);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initListView() {
        //TODO: 登陆调试通后，去掉模拟的数据，调用服务器数据
        String myFavEqpUrl = UrlUtil.getMyFavEqpList() + "/0";
        getFavEqpFromServer(myFavEqpUrl);

        String myFavArticleUrl = UrlUtil.getMyFavArticleList() + "/0";
        getFavArticleFromServer(myFavArticleUrl);
    }

    //从服务器获取装备收藏列表
    private void getFavEqpFromServer(String url) {
        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                L.d(result);
                Gson gson = new Gson();
                mFavoriteEqpList = gson.fromJson(result, MyFavoriteEqpList.class);
                int resultCode = mFavoriteEqpList.result;
                if (resultCode == 200){
                    mMyFavoriteEqpBeans.addAll(mFavoriteEqpList.favorites);
                    Message msg = Message.obtain();
                    msg.what = REQUEST_EQP_OK;
                    mHandler.sendMessage(msg);

                }else if (resultCode == 300){
                    T.show(MyFavoriteActivity.this, "获取失败", Toast.LENGTH_SHORT);
                }else if (resultCode == 250){
                    T.show(MyFavoriteActivity.this, "未登录", Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MyFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //从服务器获取文章收藏列表
    private void getFavArticleFromServer(String url) {
        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                L.d("文章列表:" + result);
                Gson gson = new Gson();
                mFavoriteRCycleList = gson.fromJson(result, MyFavoriteRCycleList.class);
                int resultCode = mFavoriteRCycleList.result;
                if (resultCode == 200){
                    mMyFavoriteRCycleBeans.addAll(mFavoriteRCycleList.favorites);
                    Message msg = Message.obtain();
                    msg.what = REQUEST_RCYCLE_OK;
                    mHandler.sendMessage(msg);

                }else if (resultCode == 300){
                    T.show(MyFavoriteActivity.this, "获取失败", Toast.LENGTH_SHORT);
                }else if (resultCode == 250){
                    T.show(MyFavoriteActivity.this, "未登录", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(MyFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //点击返回
    @OnClick(R.id.imageView_back)
    public void back(){
        finish();
    }

    //点击分享
    @OnClick(R.id.imageView_share)
    public void share(){
        T.show(this, "clicked share part", Toast.LENGTH_SHORT);
    }

    //点击装备Tab
    @OnClick(R.id.ll_tab_equipment)
    public void showEqpList(){
        initTabState(ivTabEquipment, ivTabRideCycle, llTabEquipment, llTabRideCycle);
        vpContent.setCurrentItem(0);
    }
    //点击骑行圈Tab
    @OnClick(R.id.ll_tab_ride_cycle)
    public void showRCycleList(){
        initTabState(ivTabRideCycle, ivTabEquipment, llTabRideCycle, llTabEquipment);
        vpContent.setCurrentItem(1);
    }
}
