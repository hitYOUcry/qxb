package com.qixingbang.qxb.activity.mine.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.mapapi.SDKInitializer;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseFragmentActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.map.RideInfo;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.common.views.RideHistoryDrawLayout;
import com.qixingbang.qxb.database.ride.RideDao;
import com.qixingbang.qxb.dialog.DialogUtil;
import com.qixingbang.qxb.fragment.MapFragment;
import com.qixingbang.qxb.fragment.RideInfoFragment;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqj on 2016/5/30 09:38.
 */
public class RideActivity extends BaseFragmentActivity implements RideInfoFragment.RideInfoListener
        , MapFragment.DistanceListener, RideHistoryDrawLayout.ItemClickListener {

    private final static int INDEX_MAP = 0;
    private final static int INDEX_RIDE_INFO = 1;

    private MapFragment mapFragment;
    private RideInfoFragment rideInfoFragment;
    private FragmentManager fm;
    private int mCurrentIndex = INDEX_RIDE_INFO;

    ImageView backImageView;
    TextView tipTxv;
    TextView saveTxv;

    TextView historyHintTxv;

    Button rideInfoBtn;
    Button mapBtn;
    RideHistoryDrawLayout rideHistoryLayout;

    DrawerLayout leftDrawerLayout;

    private boolean isSaved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_ride);
        initView();
        initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getRideInfoRecordsFromServer();
            }
        }).start();
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

        saveTxv = (TextView) findViewById(R.id.textView_commit);
        saveTxv.setText(R.string.save);
        saveTxv.setOnClickListener(this);

        tipTxv = (TextView) findViewById(R.id.textView_tabTip);
        tipTxv.setText(R.string.ride_map);

        rideInfoBtn = (Button) findViewById(R.id.btn_ride_info);
        rideInfoBtn.setOnClickListener(this);
        mapBtn = (Button) findViewById(R.id.btn_map);
        mapBtn.setOnClickListener(this);

        rideHistoryLayout = (RideHistoryDrawLayout) findViewById(R.id.ride_history_layout);
        rideHistoryLayout.setItemClickListener(this);

        leftDrawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        leftDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                RideDao rideDao = new RideDao(getApplicationContext());
                rideHistoryLayout.setRideInfo(rideDao.getAll());
                rideDao.close();
            }
        });

        historyHintTxv = (TextView) findViewById(R.id.textView_timeHint);

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
                showFragment();
                refreshHint();
                break;
            case R.id.btn_map:
                mCurrentIndex = INDEX_MAP;
                showFragment();
                refreshHint();
                break;
            case R.id.textView_commit:
                saveRideInfo();
                break;
            default:
                break;
        }
    }

    private void getRideInfoRecordsFromServer() {
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.POST, UrlUtil.getRideInfoGet(),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.optInt("result") == 200) {
                                    RideDao rideDao = new RideDao(RideActivity.this);
                                    JSONArray jsonArray = response.optJSONArray("riding");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                                        rideDao.add(RideInfo.fromJson(jsonObject));
                                    }
                                    rideDao.close();

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", QAccount.getToken());
                        return headers;
                    }
                };
        RequestUtil.getInstance().addToRequestQueue(request);
    }

    private void saveRideInfo() {
        int duration = rideInfoFragment.getRideTimeLength();
        double mileage = rideInfoFragment.getMileage();

        if (Math.abs(mileage) < 0.01 || duration < 60) {
            ToastUtil.toast("运动时间或里程太小，不存了~");
            return;
        }
        final RideInfo rideInfo = new RideInfo();
        rideInfo.setMileage(mileage);
        rideInfo.setRideDuration(duration);
        rideInfo.setStartTime(rideInfoFragment.getStartTime());
        rideInfo.setPoints(mapFragment.getTracePoints());
        rideInfo.setStartLocDescription(mapFragment.getStartLocDescription());
        rideInfo.setEndLocDescription(mapFragment.getEndLocDescription());

        DialogUtil.showWaitingDialog(this, "正在保存...");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, UrlUtil.getRideInfoAdd(),
                rideInfo.toJson(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int responseCode = response.optInt("result");
                if (responseCode == 200) {
                    isSaved = true;
                    saveTxv.setVisibility(View.INVISIBLE);

                    //存入本地数据库
                    RideDao rideDao = new RideDao(RideActivity.this);
                    rideDao.add(rideInfo);
                    rideDao.close();

                    ToastUtil.toast("运动信息存好了~");
                } else if (responseCode == 250) {
                    ToastUtil.toast(response.optString("message"));
                } else {
                    ToastUtil.toast("服务器出错了，稍后重试吧！");
                }
                DialogUtil.dismissWaitingDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.dismissWaitingDialog();
                ResponseUtil.toastError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", QAccount.getToken());
                return headers;
            }
        };
        RequestUtil.getInstance().addToRequestQueue(request);
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
        if (!isSaved) {
            DialogUtil.showTextDialog(this, R.string.leave, "是否放弃此次骑行记录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void startRide() {
        mapFragment.startTrace();
        onRidingState();
    }

    private void onRidingState() {
        saveTxv.setVisibility(View.INVISIBLE);
        isSaved = false;
        historyHintTxv.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(RideInfo rideInfo) {
        rideInfoFragment.showRideInfo(rideInfo);
        mapFragment.showHistoryTrace(rideInfo.getPoints());
        historyHintTxv.setText(rideInfo.getStartTimeString() + "(历史记录)");
        historyHintTxv.setVisibility(View.VISIBLE);

        mCurrentIndex = INDEX_MAP;
        showFragment();
        refreshHint();
        leftDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void stopRide() {
        mapFragment.stopTrace();
        onSaveState();
    }

    private void onSaveState() {
        saveTxv.setVisibility(View.VISIBLE);
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
