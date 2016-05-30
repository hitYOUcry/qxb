package com.qixingbang.qxb.activity.mine.map.mapservice;

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
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.map.trackutils.GsonService;
import com.qixingbang.qxb.activity.mine.map.trackutils.HistoryTrackData;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2016/5/27 18:58.
 */
public class MapService {

    public interface StateListener {
        void distanceChanged(double distance);
    }

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

    private double mCurrentDistance = 0d;
    private LatLng prev = null;
    private LatLng curr = null;
    private float speed = 0f;

    private StateListener mStateListener;

    public MapService(MapView mapView, String entityName, Context context) {
        mMapView = mapView;
        mEntityName = entityName;
        mContext = context;
        init();
    }

    /**
     * start trace
     */
    public void startRecordTrace() {
        mCurrentDistance = 0;
        mMap.clear();
        mTracePoints.clear();
        resetMarker();
        mTraceClient.startTrace(mTrace, mStartTraceListener);
    }

    /**
     * stop trace
     */
    public void stopRecordTrace() {
        mTraceClient.stopTrace(mTrace, mStopTraceListener);
        setRefreshTaskState(false);
        drawHistoryTrack(mTracePoints);
    }

    public void setStateListener(StateListener listener) {
        mStateListener = listener;
    }

    /**
     * stop service
     */
    public void stop() {
        mLocationService.stop();
        mLocationService.unregisterListener(mLocationListener);
    }

    public void queryhistoryTrack(int startTime, int endTime) {
        int simpleReturn = 0;
        int pageSize = 1000;
        int pageIndex = 1;
        mTraceClient.queryHistoryTrack(SERVICE_ID, mEntityName, simpleReturn, startTime, endTime,
                pageSize, pageIndex, mTrackListener);
    }

    public boolean isTracing() {
        return !(mRefreshTask == null);
    }

    public double getCurrentDistance() {
        return mCurrentDistance;
    }

    public float getSpeed() {
        return speed;
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
                showHistoryTrack(s);
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
            if (mTracePoints.size() >= 2) {
                curr = mTracePoints.get(mTracePoints.size() - 1);
                prev = mTracePoints.get(mTracePoints.size() - 2);
                double distance = DistanceUtil.getDistance(prev, curr);
                mCurrentDistance += distance;
                if (Math.abs(distance) > 1 && mStateListener != null) {
                    mStateListener.distanceChanged(mCurrentDistance);
                }
            }
            speed = location.getSpeed();
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

        if (mCurrentDistance > 5) {
            mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(mTracePoints.get(0)).icon(mBitmapDescriptor)
                    .zIndex(9).draggable(true);
        }
        /**
         * plot info on map
         */
        mMap.clear();
        mMap.setMapStatus(msUpdate);
        mMap.addOverlay(mOverlay);
        if(startMarker != null){
            mMap.addOverlay(startMarker);
        }
        if (null != mPolyline) {
            mMap.addOverlay(mPolyline);
        }

    }

    private void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);

        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }
            drawHistoryTrack(latLngList);
        }

    }

    /**
     * 绘制历史轨迹
     */
    // 起点图标覆盖物
    private MarkerOptions startMarker = null;
    // 终点图标覆盖物
    private MarkerOptions endMarker = null;

    private void drawHistoryTrack(final List<LatLng> points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mMap.clear();
        if (points == null || points.size() == 0) {
            resetMarker();
        } else if (points.size() > 1) {
            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(0)).icon(mBitmapDescriptor)
                    .zIndex(9).draggable(true);
            mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);


            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(points.size() - 1))
                    .icon(mBitmapDescriptor).zIndex(9).draggable(true);

            // 添加路线（轨迹）
            mPolyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);
            mMap.setMapStatus(msUpdate);
            mMap.addOverlay(startMarker);
            mMap.addOverlay(endMarker);
            mMap.addOverlay(mPolyline);
        }

    }

    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        mPolyline = null;
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
