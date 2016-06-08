package com.qixingbang.qxb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.map.mapservice.MapService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2016/5/30 09:45.
 */
public class MapFragment extends Fragment implements MapService.StateListener {

    public interface DistanceListener {

        void onDistanceChanged(double distance);
    }

    private MapService mMapService;

    MapView mapView;

    private DistanceListener mDistanceListener;

    public void setDistanceListener(DistanceListener listener) {
        mDistanceListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.showZoomControls(false);

        //TODO
        mMapService = new MapService(mapView, "testName", getActivity());
        mMapService.setStateListener(this);
    }

    @Override
    public void distanceChanged(double distance) {
        if (mDistanceListener != null) {
            mDistanceListener.onDistanceChanged(distance);
        }
    }

    @Override
    public void onDestroy() {
        if (mMapService != null) {
            mMapService.stop();
        }
        super.onDestroy();
    }

    public void startTrace() {
        mMapService.startRecordTrace();
    }

    public void stopTrace() {
        mMapService.stopRecordTrace();
    }

    public boolean isTracing() {
        return mMapService.isTracing();
    }

    public double getCurrentDistance() {
        return mMapService.getCurrentDistance();
    }

    public float getSpeed() {
        return mMapService.getSpeed();
    }

    public ArrayList<LatLng> getTracePoints() {
        return mMapService.getTracePoints();
    }

    public String getStartLocDescription() {
        return mMapService.getStartLocDescription();
    }

    public String getEndLocDescription() {
        return mMapService.getEndLocDescription();
    }

    public void showHistoryTrace(List<LatLng> points) {
        mMapService.showHistoryTrace(points);
    }
}
