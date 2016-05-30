package com.qixingbang.qxb.fragment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zqj on 2016/5/30 10:48.
 */
public class RideInfoFragment extends BaseFragment {

    public interface RideInfoListener {
        void startRide();

        void stopRide();

        float getSpeed();
    }

    private SimpleDateFormat dateFormat_ms = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    private SimpleDateFormat dateFormat_hms = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);

    public RideInfoFragment() {
    }

    private RideInfoListener mListener;

    Button startRideBtn;
    TextView mileageTxv;
    TextView timeTxv;
    TextView aveSpeedTxv;
    TextView currSpeedTxv;

    private boolean isStarted = false;
    private double mileage;

    private long startTime;
    private int rideTimeLength;

    private final static int MSG_TIME_REFRESH = 0;
    private final static int MSG_MILEAGE_REFRESH = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handleMsg(msg);
        }
    };
    private Runnable timeRefreshTask = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_TIME_REFRESH);
            if (isStarted) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    private LocationManager mLocationManager;
    private boolean GPS_STATE;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        startRideBtn = (Button) view.findViewById(R.id.button_start_ride);
        startRideBtn.setOnClickListener(this);

        mileageTxv = (TextView) view.findViewById(R.id.textView_mileage);
        timeTxv = (TextView) view.findViewById(R.id.textView_time);
        aveSpeedTxv = (TextView) view.findViewById(R.id.textView_ave_speed);
        currSpeedTxv = (TextView) view.findViewById(R.id.textView_speed);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float speed = 3.6f * location.getSpeed();
                currSpeedTxv.setText(String.format("%.2f", speed));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    /**
     * may call from other thread,refresh UI carefully
     * @param distance
     */
    public void refreshMileage(double distance) {
        mileage = distance;
        mHandler.sendEmptyMessage(MSG_MILEAGE_REFRESH);
    }


    public void setRideInfoListener(RideInfoListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == startRideBtn) {
            if (isStarted) {
                if (mListener != null) {
                    mListener.stopRide();
                }
                startRideBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                startRideBtn.setText(R.string.ride_begin);
                isStarted = false;
            } else {
                resetInfo();
                if (mListener != null) {
                    mListener.startRide();
                    startTime = System.currentTimeMillis();
                    mHandler.post(timeRefreshTask);
                }
                startRideBtn.setBackgroundColor(getResources().getColor(R.color.red_ff));
                startRideBtn.setText(R.string.ride_close);
                isStarted = true;
            }
        }
    }

    private void handleMsg(Message msg) {
        long time = System.currentTimeMillis() - startTime;
        switch (msg.what) {
            case MSG_TIME_REFRESH:
                /**
                 * time
                 */
                long fixedDateTime = time + 4 * 3600 * 1000;
                Date date = new Date(fixedDateTime);
                if (time >= 4 * 3600 * 1000) {
                    timeTxv.setText(dateFormat_hms.format(date));
                    timeTxv.setTextScaleX(getResources().getDimension(R.dimen.text_size_24px));
                } else {
                    timeTxv.setText(dateFormat_ms.format(date));
                }

                break;
            case MSG_MILEAGE_REFRESH:
                mileageTxv.setText(String.format("%.2f", mileage));
                /**
                 * average speed
                 */
                rideTimeLength = (int) (time / 1000);
                double timeLength = rideTimeLength / 3600d;//unit in hour
                double aveSpeed = 0;
                aveSpeed = mileage / timeLength;
                aveSpeedTxv.setText(String.format("%.2f", aveSpeed));
                break;
            default:
                break;
        }
    }

    private void resetInfo() {
        aveSpeedTxv.setText("0.00");
        mileageTxv.setText("0.00");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rideinfo, container, false);
    }

    @Override
    public void onDestroy() {
        //TODO seems do not work
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent gpsIntent = new Intent();
            gpsIntent.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
            gpsIntent.setData(Uri.parse("custom:3"));
            try {
                PendingIntent.getBroadcast(getActivity(), 0, gpsIntent, 0).send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

}
