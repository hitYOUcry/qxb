package com.qixingbang.qxb.activity.mine.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseFragmentActivity;
import com.qixingbang.qxb.dialog.DialogUtil;
import com.qixingbang.qxb.fragment.MapFragment;
import com.qixingbang.qxb.fragment.RideInfoFragment;

/**
 * Created by zqj on 2016/5/30 09:38.
 */
public class RideActivity extends BaseFragmentActivity implements RideInfoFragment.RideInfoListener
        , MapFragment.DistanceListener {

    private final static int INDEX_MAP = 0;
    private final static int INDEX_RIDE_INFO = 1;

    private MapFragment mapFragment;
    private RideInfoFragment rideInfoFragment;
    private FragmentManager fm;
    private int mCurrentIndex = INDEX_RIDE_INFO;

    ImageView backImageView;
    TextView tipTxv;

    Button rideInfoBtn;
    Button mapBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_ride);
        initView();
        initData();
    }

    @Override
    public void initView() {
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeFinish();
            }
        });

        tipTxv = (TextView) findViewById(R.id.textView_tabTip);
        tipTxv.setText(R.string.ride_map);

        rideInfoBtn = (Button) findViewById(R.id.btn_ride_info);
        rideInfoBtn.setOnClickListener(this);
        mapBtn = (Button) findViewById(R.id.btn_map);
        mapBtn.setOnClickListener(this);

    }

    @Override
    public void initData() {
        fm = getSupportFragmentManager();
        mapFragment = new MapFragment();
        mapFragment.setDistanceListener(this);

        rideInfoFragment = new RideInfoFragment();
        rideInfoFragment.setRideInfoListener(this);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fragment_content, mapFragment);
        transaction.add(R.id.fragment_content, rideInfoFragment);
        transaction.hide(mapFragment);
        transaction.commit();
        refreshHint();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ride_info:
                mCurrentIndex = INDEX_RIDE_INFO;
                break;
            case R.id.btn_map:
                mCurrentIndex = INDEX_MAP;
                break;
            default:
                break;
        }
        showFragment();
        refreshHint();
    }

    private void refreshHint() {
        Resources res = getResources();
        rideInfoBtn.setTextColor(res.getColor(R.color.black_242424_fade50));
        mapBtn.setTextColor(res.getColor(R.color.black_242424_fade50));
        if (mCurrentIndex == INDEX_RIDE_INFO) {
            tipTxv.setText(R.string.ride_info);
            rideInfoBtn.setTextColor(res.getColor(R.color.black_242424));
        } else {
            tipTxv.setText(R.string.ride_map);
            mapBtn.setTextColor(res.getColor(R.color.black_242424));
        }
    }

    private void showFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(mapFragment);
        transaction.hide(rideInfoFragment);
        switch (mCurrentIndex) {
            case INDEX_MAP:
                transaction.show(mapFragment);
                break;
            case INDEX_RIDE_INFO:
                transaction.show(rideInfoFragment);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            beforeFinish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void beforeFinish() {
        DialogUtil.showTextDialog(this, R.string.leave, "是否放弃此次骑行记录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void startRide() {
        mapFragment.startTrace();
    }

    @Override
    public void stopRide() {
        mapFragment.stopTrace();
    }

    @Override
    public float getSpeed() {
        return mapFragment.getSpeed();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RideActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onDistanceChanged(double distance) {
        distance = distance / 1000;
        rideInfoFragment.refreshMileage(distance);
    }
}
