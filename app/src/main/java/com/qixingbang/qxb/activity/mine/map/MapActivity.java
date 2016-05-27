package com.qixingbang.qxb.activity.mine.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.common.utils.ToastUtil;

/**
 * Created by zqj on 2016/5/27 18:53.
 */
public class MapActivity extends BaseActivity {

    private MapService mMapService;

    private boolean isTracing = false;
    Button btnTraceControl;
    Button btnTraceHistory;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.showZoomControls(false);
        btnTraceControl = (Button) findViewById(R.id.btn_trace_control);
        btnTraceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracing) {
                    mMapService.stopRecordTrace();
                    isTracing = false;
                    btnTraceControl.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnTraceControl.setText(R.string.ride_begin);
                } else {
                    mMapService.startRecordTrace();
                    isTracing = true;
                    btnTraceControl.setBackgroundColor(getResources().getColor(R.color.red_ff));
                    btnTraceControl.setText(R.string.ride_close);
                }
            }
        });
        btnTraceHistory = (Button) findViewById(R.id.btn_trace_history);
        btnTraceHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toast("building");
            }
        });

    }

    @Override
    protected void initData() {
        //TODO entityName
        mMapService = new MapService(mapView, "testName", this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onStop() {
        mMapService.stop();
        super.onStop();

    }
}
