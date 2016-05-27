package com.qixingbang.qxb.activity.mine.map;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by zqj on 2016/5/27 18:58.
 */
public class MapService {
    public static final int SERVICE_ID = 117158;//百度鹰眼服务Id

    private static final String TAG = "MapService";

    private Context mContext;

    /**
     * trace service
     */
    private Trace mTrace;
    private String mEntityName;
    private LBSTraceClient mTraceClient;

    /**
     * location service
     */
    private LocationService mLocationService;

    /**
     * listeners
     */
    private OnEntityListener mEntityListener;
    private OnTrackListener mTrackListener;
    private OnStartTraceListener mStartTraceListener;
    private OnStopTraceListener mStopTraceListener;
    private BDLocationListener mLocationListener;
    /**
     * map obj
     */
    private MapView mMapView;
    private BaiduMap mMap;

    /**
     * draw real time map
     */
    private MapStatusUpdate msUpdate;
    private OverlayOptions mOverlay;
    private PolylineOptions mPolyline;
    private BitmapDescriptor mBitmapDescriptor;

    /**
     * trace points
     */
    private ArrayList<LatLng> mTracePoints = new ArrayList<>();


    /**
     * refresh task
     */
    private RefreshMapTask mRefreshTask;
    private int mRefreshPeriod = 10;// default 30s;

    private int mPackageInterval = 2 * mRefreshPeriod;

    /**
     * start trace
     */
    public void startRecordTrace() {
        mTraceClient.startTrace(mTrace, mStartTraceListener);
    }

    public MapService(MapView mapView, String entityName, Context context) {
        mMapView = mapView;
        mEntityName = entityName;
        mContext = context;
        init();
    }

    /**
     * stop trace
     */
    public void stopRecordTrace() {
        mTraceClient.stopTrace(mTrace, mStopTraceListener);
    }

    public void stop() {
        stopRecordTrace();
        mLocationService.stop();
        mLocationService.unregisterListener(mLocationListener);
    }

    private void init() {
        initListener();

        mMap = mMapView.getMap();

        mTraceClient = new LBSTraceClient(mContext.getApplicationContext());
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        mTraceClient.setInterval(mRefreshPeriod, mPackageInterval);
        mTraceClient.setProtocolType(0);

        mTrace = new Trace(mContext.getApplicationContext(), SERVICE_ID, mEntityName, 2);

        addEntity();

        /**
         * location one time;
         */
        mLocationService = new LocationService(mContext);
        mLocationService.setLocationOption(mLocationService.getDefaultLocationClientOption());
        mLocationService.registerListener(mLocationListener);
        mLocationService.start();

    }

    private void addEntity() {
        String columnKey = "";
        mTraceClient.addEntity(SERVICE_ID, mEntityName, columnKey, mEntityListener);
    }

    private void initListener() {
        mEntityListener = new OnEntityListener() {
            @Override
            public void onRequestFailedCallback(String s) {
                Looper.prepare();
                ToastUtil.toast("添加entity失败！");
                Looper.loop();
            }

            @Override
            public void onReceiveLocation(TraceLocation traceLocation) {
                super.onReceiveLocation(traceLocation);
                showRealTimeTrack(traceLocation);
            }
        };
        mTrackListener = new OnTrackListener() {
            @Override
            public void onRequestFailedCallback(String s) {
                Looper.prepare();
                ToastUtil.toast("轨迹查询失败！");
                Looper.loop();
            }

            @Override
            public void onQueryHistoryTrackCallback(String s) {
                super.onQueryHistoryTrackCallback(s);
                //TODO
                //                showHistoryTrack(s);
            }
        };

        mStartTraceListener = new OnStartTraceListener() {
            @Override
            public void onTraceCallback(int i, String s) {
                if (0 == i || 10006 == i || 10008 == i) {
                    setRefreshTaskState(true);
                }
            }

            @Override
            public void onTracePushCallback(byte b, String s) {
            }
        };

        mStopTraceListener = new OnStopTraceListener() {
            @Override
            public void onStopTraceSuccess() {
                setRefreshTaskState(false);
            }

            @Override
            public void onStopTraceFailed(int i, String s) {

            }
        };

        mLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double latitude = bdLocation.getLatitude();
                double longitude = bdLocation.getLongitude();
                if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
                    LogUtil.i(TAG, "轨迹点为（0,0），无效");
                } else {
                    LatLng latLng = new LatLng(latitude, longitude);
                    mTracePoints.add(latLng);
                    drawRealTimePoint(latLng);
                }
            }
        };
    }

    private void queryRealTimeLocation() {
        mTraceClient.queryRealtimeLoc(SERVICE_ID, mEntityListener);
    }

    private void showRealTimeTrack(TraceLocation location) {
        if (null == mRefreshTask || !mRefreshTask.refresh) {
            return;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            LogUtil.i(TAG, "轨迹点为（0,0），无效");
        } else {

            LatLng latLng = new LatLng(latitude, longitude);

            mTracePoints.add(latLng);
            drawRealTimePoint(latLng);
        }
    }

    private void drawRealTimePoint(LatLng point) {
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBitmapDescriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        mOverlay = new MarkerOptions().position(point)
                .icon(mBitmapDescriptor).zIndex(9).draggable(true);
        if (mTracePoints.size() >= 2 && mTracePoints.size() <= 10000) {
            // 添加路线（轨迹）
            mPolyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(mTracePoints);
        }
        /**
         * plot info on map
         */
        mMap.clear();
        mMap.setMapStatus(msUpdate);
        mMap.addOverlay(mOverlay);
        if (null != mPolyline) {
            mMap.addOverlay(mPolyline);
        }

    }

    private class RefreshMapTask extends Thread {

        protected boolean refresh = true;

        @Override
        public void run() {
            while (refresh) {
                queryRealTimeLocation();
                try {
                    Thread.sleep(mRefreshPeriod * 1000);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    protected void setRefreshTaskState(boolean isStart) {
        if (null == mRefreshTask) {
            mRefreshTask = new RefreshMapTask();
        }
        mRefreshTask.refresh = isStart;
        if (isStart) {
            if (!mRefreshTask.isAlive()) {
                mRefreshTask.start();
            }
        } else {
            mRefreshTask = null;
        }
    }
}
