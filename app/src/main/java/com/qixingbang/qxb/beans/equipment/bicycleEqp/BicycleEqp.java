package com.qixingbang.qxb.beans.equipment.bicycleEqp;

import com.qixingbang.qxb.beans.equipment.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zqj on 2015/8/31 20:15.
 */
public class BicycleEqp implements Comparable {
    private int mId;
    private String mName;
    private String mBrand;
    private int mPrice;
    private String mType;
    private String mIntroduce;
    private List<String> mPicUrl = Arrays.asList("url1", "url2", "url3");
    private int mLikeCount;
    private int mFavCount;
    private long mCreateTime;
    private List<Comment> mCommentLists;

    /**
     * Simple info
     */
    private final static String KEY_ID = "bikeEqpId";
    private final static String KEY_TYPE = "type";
    private final static String KEY_PRICE = "price";
    private final static String KEY_BRAND = "brand";
    private final static String KEY_PIC1 = "pic1";
    private final static String KEY_PIC2 = "pic2";
    private final static String KEY_PIC3 = "pic3";
    private final static String KEY_NAME = "name";
    private final static String KEY_LIKECOUNT = "likeCount";
    private final static String KEY_FAVCOUNT = "favCount";

    /**
     * Details
     */
    private final static String KEY_INTRODUCE = "introduce";
    private final static String KEY_COMMENTS = "comments";

    public static BicycleEqp fromInternetJsonObject(JSONObject jsonObject) {
        BicycleEqp bicycleEqp = new BicycleEqp();
        bicycleEqp.mId = jsonObject.optInt(KEY_ID);
        bicycleEqp.mType = jsonObject.optString(KEY_TYPE);
        bicycleEqp.mPrice = jsonObject.optInt(KEY_PRICE);
        bicycleEqp.mBrand = jsonObject.optString(KEY_BRAND);
        bicycleEqp.mPicUrl.set(0, jsonObject.optString(KEY_PIC1));
        bicycleEqp.mName = jsonObject.optString(KEY_NAME);
        bicycleEqp.mLikeCount = jsonObject.optInt(KEY_LIKECOUNT);
        bicycleEqp.mFavCount = jsonObject.optInt(KEY_FAVCOUNT);
        return bicycleEqp;
    }

    public static List<BicycleEqp> fromInternetJsonArray(JSONArray jsonArray) {
        List<BicycleEqp> list = new ArrayList<>();
        if (null != jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(fromInternetJsonObject(jsonArray.optJSONObject(i)));
            }
        }
        return list;
    }

    public static void addDetailsInfo(JSONObject response, BicycleEqp bicycleEqp) {
        bicycleEqp.mType = response.optString(KEY_TYPE);
        bicycleEqp.mPrice = response.optInt(KEY_PRICE);
        bicycleEqp.mBrand = response.optString(KEY_BRAND);
        bicycleEqp.mPicUrl.set(0, response.optString(KEY_PIC1));
        bicycleEqp.mPicUrl.set(1, response.optString(KEY_PIC2));
        bicycleEqp.mPicUrl.set(2, response.optString(KEY_PIC3));
        bicycleEqp.mName = response.optString(KEY_NAME);
        bicycleEqp.mLikeCount = response.optInt(KEY_LIKECOUNT);
        bicycleEqp.mFavCount = response.optInt(KEY_FAVCOUNT);
        bicycleEqp.mIntroduce = response.optString(KEY_INTRODUCE);
    }

    public List<Comment> getCommentLists() {
        return mCommentLists;
    }

    public void setCommentLists(List<Comment> commentLists) {
        mCommentLists = commentLists;
    }

    public String getIntroduce() {
        return mIntroduce;
    }

    public void setIntroduce(String introduce) {
        this.mIntroduce = introduce;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public List<String> getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.mPicUrl = picUrl;
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }


    public int getFavCount() {
        return mFavCount;
    }

    public void setFavCount(int favCount) {
        mFavCount = favCount;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        this.mBrand = brand;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        this.mCreateTime = createTime;
    }

    @Override
    public int compareTo(Object another) {
        int anotherId = ((BicycleEqp) another).getId();
        return mId > anotherId ? 1 : (mId == anotherId ? 0 : -1);
    }
}
