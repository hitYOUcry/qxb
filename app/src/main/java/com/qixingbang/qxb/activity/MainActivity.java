package com.qixingbang.qxb.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.communicate.ReplyActivity;
import com.qixingbang.qxb.activity.login.LoginActivity;
import com.qixingbang.qxb.base.activity.BaseFragmentActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.common.application.GlobalConstant;
import com.qixingbang.qxb.common.utils.BitmapUtil;
import com.qixingbang.qxb.fragment.CommunicateFragment;
import com.qixingbang.qxb.fragment.EquipmentFragment;
import com.qixingbang.qxb.fragment.MineFragment;
import com.qixingbang.qxb.fragment.RideCycleFragment;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;


/**
 * Created by zqj on 2015/8/17.
 */
public class MainActivity extends BaseFragmentActivity {

    private final String TAG = MainActivity.class.getName();

    TextView tabTipTextView;
    ImageView shareImageView;

    ImageView equipmentImageView;
    TextView equipmentTextView;

    ImageView rideCircleImageView;
    TextView rideCircleTextView;

    ImageView communicateImageView;
    TextView communicateTextView;

    ImageView mineImageView;
    TextView mineTextView;
    ImageView mineInfoHint;

    RelativeLayout equipment;
    RelativeLayout rideCircle;
    RelativeLayout communicate;
    RelativeLayout mine;

    FragmentManager mFragmentManager;
    EquipmentFragment equipmentFragment;
    RideCycleFragment ridecycleFragment;
    CommunicateFragment communicateFragment;
    MineFragment mineFragment;

    private int mCurrentIndex = 0;
    private final static int INDEX_HOMEPAGE = 0;
    private final static int INDEX_RIDE_CIRCLE = 1;
    private final static int INDEX_COMMUNICATE = 2;
    private final static int INDEX_MINE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        onClick(equipment);
    }

    @Override
    public void initView() {

        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        shareImageView = (ImageView) findViewById(R.id.imageView_share);
        shareImageView.setOnClickListener(this);
        equipmentImageView = (ImageView) findViewById(R.id.imageView_equipment);
        equipmentTextView = (TextView) findViewById(R.id.textView_equipment);

        rideCircleImageView = (ImageView) findViewById(R.id.imageView_ridingCircle);
        rideCircleTextView = (TextView) findViewById(R.id.textView_ridingCircle);

        communicateImageView = (ImageView) findViewById(R.id.imageView_communicate);
        communicateTextView = (TextView) findViewById(R.id.textView_communicate);

        mineImageView = (ImageView) findViewById(R.id.imageView_mine);
        mineTextView = (TextView) findViewById(R.id.textView_mine);
        mineInfoHint = (ImageView) findViewById(R.id.imageView_mine_infoHint);

        equipment = (RelativeLayout) findViewById(R.id.relativeLayout_equipment);
        equipment.setOnClickListener(this);
        rideCircle = (RelativeLayout) findViewById(R.id.relativeLayout_riding_circle);
        rideCircle.setOnClickListener(this);
        communicate = (RelativeLayout) findViewById(R.id.relativeLayout_communicate);
        communicate.setOnClickListener(this);
        mine = (RelativeLayout) findViewById(R.id.relativeLayout_mine);
        mine.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayout_equipment:
                mCurrentIndex = INDEX_HOMEPAGE;
                break;
            case R.id.relativeLayout_riding_circle:
                mCurrentIndex = INDEX_RIDE_CIRCLE;
                break;
            case R.id.relativeLayout_communicate:
                mCurrentIndex = INDEX_COMMUNICATE;
                break;
            case R.id.relativeLayout_mine:
                //TODO
                if (!QAccount.hasAccount()) {
                    LoginActivity.start(this,REQUEST_LOGIN);
                    //                    finish();
                } else {
                    mCurrentIndex = INDEX_MINE;
                }
                break;
            case R.id.imageView_share:
                showShare();
                return;
        }
        showFragment(mCurrentIndex);
    }

    private void showFragment(int index) {
        setBtmBar(index);
        setTabTip(index);
        //先隐藏所有fragment,然后根据index显示相应fragment。
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        //TODO 其他三个fragment有待添加
        if (null != equipmentFragment) {
            transaction.hide(equipmentFragment);
        }
        if (null != ridecycleFragment) {
            transaction.hide(ridecycleFragment);
        }
        if (null != communicateFragment) {
            transaction.hide(communicateFragment);
        }
        if (null != mineFragment) {
            transaction.hide(mineFragment);
        }

        switch (index) {
            case INDEX_HOMEPAGE:
                if (null == equipmentFragment) {
                    equipmentFragment = new EquipmentFragment();
                    transaction.add(R.id.fragment_content, equipmentFragment);
                }
                transaction.show(equipmentFragment);
                break;
            case INDEX_RIDE_CIRCLE:
                if (null == ridecycleFragment) {
                    ridecycleFragment = new RideCycleFragment();
                    transaction.add(R.id.fragment_content, ridecycleFragment);
                }
                transaction.show(ridecycleFragment);
                break;
            case INDEX_COMMUNICATE:
                if (null == communicateFragment) {
                    communicateFragment = new CommunicateFragment();
                    transaction.add(R.id.fragment_content, communicateFragment);
                }
                transaction.show(communicateFragment);
                break;
            case INDEX_MINE:
                if (null == mineFragment) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.fragment_content, mineFragment);
                }
                transaction.show(mineFragment);
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }


    private void setBtmBar(int index) {
        Resources res = getResources();
        equipmentImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_eqp_not_selected));
        equipmentTextView.setTextColor(res.getColor(R.color.white));
        rideCircleImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_ride_circle_not_selected));
        rideCircleTextView.setTextColor(res.getColor(R.color.white));
        communicateImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_communicate_not_selected));
        communicateTextView.setTextColor(res.getColor(R.color.white));
        mineImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_mine_not_selected));
        mineTextView.setTextColor(res.getColor(R.color.white));
        switch (index) {
            case INDEX_HOMEPAGE:
                equipmentImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_eqp_selected));
                equipmentTextView.setTextColor(res.getColor(R.color.yellow_light));
                break;
            case INDEX_RIDE_CIRCLE:
                rideCircleImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_ride_circle_selected));
                rideCircleTextView.setTextColor(res.getColor(R.color.yellow_light));
                break;
            case INDEX_COMMUNICATE:
                communicateImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_communicate_selected));
                communicateTextView.setTextColor(res.getColor(R.color.yellow_light));
                break;
            case INDEX_MINE:
                mineImageView.setImageBitmap(BitmapUtil.compressBitmap(R.drawable.ic_mine_selected));
                mineTextView.setTextColor(res.getColor(R.color.yellow_light));
                break;
            default:
                break;
        }
    }

    private void setTabTip(int index) {
        Resources res = getResources();
        switch (index) {
            case INDEX_HOMEPAGE:
                tabTipTextView.setText(res.getString(R.string.equipment));
                break;
            case INDEX_RIDE_CIRCLE:
                tabTipTextView.setText(res.getString(R.string.riding_circle));
                break;
            case INDEX_COMMUNICATE:
                tabTipTextView.setText(res.getString(R.string.communicate));
                break;
            case INDEX_MINE:
                tabTipTextView.setText(res.getString(R.string.mine));
                break;
            default:
                break;
        }
    }


    /**
     * shareSDK分享设置
     * 后期需要根据骑行邦具体内容修订
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTheme(OnekeyShareTheme.CLASSIC);

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(GlobalConstant.QXB_WEBSITE);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("http://g.hiphotos.baidu.com/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=52a0efa0c9bf6c81e33a24badd57da50/dbb44aed2e738bd47d3eb9f9a48b87d6267ff9df.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(GlobalConstant.QXB_WEBSITE);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(GlobalConstant.QXB_WEBSITE);

        // 启动分享GUI
        oks.show(this);
    }

    //监测返回信息，更新相应的fragment
    public final static int REQUEST_LOGIN = 100;
    public final static int REQUEST_COMMUNICATE_ASK = 200;
    public final static int REQUEST_COMMUNICATE_REPLAY = 300;
    public final static int REQUEST_MINE_SETTING = 400;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != communicateFragment) {
            if (requestCode == REQUEST_COMMUNICATE_ASK) {
                communicateFragment.onPullDownToRefresh(null);
            } else if (requestCode == REQUEST_COMMUNICATE_REPLAY) {
                if (resultCode == ReplyActivity.COMMENTED) {
                    communicateFragment.refreshQuestionsList();
                }
            }
        }
        if (null != mineFragment) {
            if (requestCode == REQUEST_MINE_SETTING) {
                if (resultCode == RESULT_OK) {
                    mineFragment.refreshUserInfo();
                }
            }
        }
        if(requestCode == REQUEST_LOGIN){
            if(resultCode == RESULT_OK){
                onClick(mine);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * start
     *
     * @param context
     */

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        QAccount.appStart();
    }

}
