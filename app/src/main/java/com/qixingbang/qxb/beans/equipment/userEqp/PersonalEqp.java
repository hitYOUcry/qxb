package com.qixingbang.qxb.beans.equipment.userEqp;

import com.qixingbang.qxb.beans.equipment.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zqj on 2015/8/31 20:05.
 */
public class PersonalEqp implements Comparable {
    private int mId;
    private String mName;
    private String mBrand;
    private int mPrice;
    private String mSize;
    private String mType;
    private int mLikeCount;     //点赞数
    private int mFavCount;      //收藏数
    private String mIntroduce;
    private List<String> mPicUrl = Arrays.asList("url1", "url2", "url3");
    private long mCreateTime;
    private List<Comment> mCommentLists;

    /**
     * Simple version
     */
    private final static String KEY_ID = "bodyEqpId";
    private final static String KEY_BRAND = "brand";
    private final static String KEY_PRICE = "price";
    private final static String KEY_NAME = "name";
    private final static String KEY_SIZE = "size";
    private final static String KEY_TYPE = "type";
    private final static String KEY_LIKECOUNT = "likeCount";
    private final static String KEY_FAVCOUNT = "favCount";
    private final static String KEY_PIC1 = "pic1";
    private final static String KEY_PIC2 = "pic2";
    private final static String KEY_PIC3 = "pic3";

    /**
     * Details
     */
    private final static String KEY_INTRODUCE = "introduce";
    private final static String KEY_COMMENTS = "comments";

    /**
     * build personeqp from a jsonobject
     *
     * @param jsonObject
     * @return
     */
    public static PersonalEqp fromJsonObject(JSONObject jsonObject) {
        PersonalEqp personalEqp = new PersonalEqp();
        personalEqp.mId = jsonObject.optInt(KEY_ID);
        personalEqp.mBrand = jsonObject.optString(KEY_BRAND);
        personalEqp.mPrice = jsonObject.optInt(KEY_PRICE);
        personalEqp.mName = jsonObject.optString(KEY_NAME);
        personalEqp.mSize = jsonObject.optString(KEY_SIZE);
        personalEqp.mType = jsonObject.optString(KEY_TYPE);
        personalEqp.mLikeCount = jsonObject.optInt(KEY_LIKECOUNT);
        personalEqp.mFavCount = jsonObject.optInt(KEY_FAVCOUNT);
        personalEqp.mPicUrl.set(0, jsonObject.optString(KEY_PIC1));
        return personalEqp;
    }

    /**
     * return a personaleqp list with a input jsonarray
     *
     * @param jsonArray
     * @return
     */
    public static List<PersonalEqp> fromJsonArray(JSONArray jsonArray) {
        List<PersonalEqp> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(fromJsonObject(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static void addDetailsInfo(JSONObject response, PersonalEqp personalEqp) {
        personalEqp.mBrand = response.optString(KEY_BRAND);
        personalEqp.mPrice = response.optInt(KEY_PRICE);
        personalEqp.mName = response.optString(KEY_NAME);
        personalEqp.mSize = response.optString(KEY_SIZE);
        personalEqp.mType = response.optString(KEY_TYPE);
        personalEqp.mLikeCount = response.optInt(KEY_LIKECOUNT);
        personalEqp.mFavCount = response.optInt(KEY_FAVCOUNT);
        personalEqp.mPicUrl.set(0, response.optString(KEY_PIC1));
        personalEqp.mPicUrl.set(1, response.optString(KEY_PIC2));
        personalEqp.mPicUrl.set(2, response.optString(KEY_PIC3));
        personalEqp.mIntroduce = response.optString(KEY_INTRODUCE);
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
        mIntroduce = introduce;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public List<String> getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.mPicUrl = picUrl;
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

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        this.mCreateTime = createTime;
    }

    @Override
    public int compareTo(Object another) {
        int anotherId = ((PersonalEqp) another).getId();
        return mId > anotherId ? 1 : (mId == anotherId ? 0 : -1);
    }
}
