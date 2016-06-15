package com.qixingbang.qxb.beans.mine.map;

import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zqj on 2016/6/7 14:18.
 */
public class RideInfo implements Comparable {

    private int id;

    /**
     * ms
     */
    private long startTime;

    /**
     * km
     */
    private double mileage;

    /**
     * s
     */
    private int rideDuration;

    private String startLocDescription;
    private String endLocDescription;

    private ArrayList<Double> pointsLat = new ArrayList<>();
    private ArrayList<Double> pointsLng = new ArrayList<>();

    private static final String ID = "ridingId";
    private static final String START_TIME = "startTime";
    private static final String MILEAGE = "mileage";
    private static final String RIDE_DURATION = "rideDuration";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String START_LOC_DESCRIPTION = "startLocDescription";
    private static final String END_LOC_DESCRIPTION = "endLocDescription";

    private static final String CONTENT = "content";
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public JSONObject toJson() {
        JSONObject js = new JSONObject();
        try {
            js.put(ID, id);
            js.put(START_TIME, startTime);
            js.put(MILEAGE, mileage);
            js.put(RIDE_DURATION, rideDuration);
            js.put(START_LOC_DESCRIPTION, startLocDescription);
            js.put(END_LOC_DESCRIPTION, endLocDescription);
            JSONArray latJsonArray = new JSONArray();
            JSONArray lngJsonArray = new JSONArray();
            int length = pointsLat.size();
            for (int i = 0; i < length; i++) {
                latJsonArray.put(pointsLat.get(i));
                lngJsonArray.put(pointsLng.get(i));
            }
            js.put(LATITUDE, latJsonArray);
            js.put(LONGITUDE, lngJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();
        try {
            result.put(START_TIME, startTime);
            result.put(CONTENT, js.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static RideInfo fromJson(JSONObject jsonObject) {
        String content = jsonObject.optString(CONTENT);
        if (!TextUtils.isEmpty(content)) {
            try {
                jsonObject = new JSONObject(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RideInfo result = new RideInfo();
        result.id = jsonObject.optInt(ID);
        result.startTime = jsonObject.optLong(START_TIME);
        result.rideDuration = jsonObject.optInt(RIDE_DURATION);
        result.mileage = jsonObject.optDouble(MILEAGE);
        result.startLocDescription = jsonObject.optString(START_LOC_DESCRIPTION);
        result.endLocDescription = jsonObject.optString(END_LOC_DESCRIPTION);
        JSONArray latJsonArray = jsonObject.optJSONArray(LATITUDE);
        JSONArray lngJsonArray = jsonObject.optJSONArray(LONGITUDE);
        int length = latJsonArray.length();
        for (int i = 0; i < length; i++) {
            result.pointsLat.add((Double) latJsonArray.opt(i));
            result.pointsLng.add((Double) lngJsonArray.opt(i));
        }
        return result;
    }

    public static RideInfo fromString(String rideInfo) {
        RideInfo result = null;
        try {
            JSONObject jsonObject = new JSONObject(rideInfo);
            result = fromJson(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized void setPoints(ArrayList<LatLng> points) {
        pointsLat.clear();
        pointsLng.clear();
        for (LatLng point : points) {
            pointsLat.add(point.latitude);
            pointsLng.add(point.longitude);
        }
    }

    public synchronized ArrayList<LatLng> getPoints() {
        ArrayList<LatLng> result = new ArrayList<>();
        if (pointsLat.size() == 0 || pointsLng.size() == 0
                || pointsLat.size() != pointsLng.size()) {
            return result;
        }
        for (int i = 0; i < pointsLat.size(); i++) {
            result.add(new LatLng(pointsLat.get(i), pointsLng.get(i)));
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMileage() {
        return mileage;
    }

    public int getRideDuration() {
        return rideDuration;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        return formatter.format(new Date(startTime));
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setRideDuration(int rideDuration) {
        this.rideDuration = rideDuration;
    }

    public String getStartLocDescription() {
        return startLocDescription;
    }

    public void setStartLocDescription(String startLocDescription) {
        this.startLocDescription = startLocDescription;
    }

    public String getEndLocDescription() {
        return endLocDescription;
    }

    public void setEndLocDescription(String endLocDescription) {
        this.endLocDescription = endLocDescription;
    }

    @Override
    public int compareTo(Object another) {
        RideInfo o = (RideInfo) another;
        if (o.startTime > startTime) {
            return 1;
        } else if (o.startTime < startTime) {
            return -1;
        } else {
            return 0;
        }
    }
}
