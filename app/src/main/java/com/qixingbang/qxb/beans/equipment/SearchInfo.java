package com.qixingbang.qxb.beans.equipment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zqj on 2015/9/24 10:48.
 */
public class SearchInfo {

    private String mAction = "";
    private String mBrand = "";
    private String mType = "";
    private int mPrice;
    private String mSearch = "";
    private int mId;

    private final String NO_CONDITION = "不限";
    public final static String ACTION_NEW = "new";
    public final static String ACTION_MORE = "more";

    private final String KEY_ACTION = "action";
    private final String KEY_BRAND = "brand";
    private final String KEY_TYPE = "type";
    private final String KEY_SEARCH = "search";
    private final String KEY_PRICE = "price";
    private final String KEY_ID = "id";

    public SearchInfo() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ACTION, mAction);
            if (!mBrand.isEmpty()) {
                jsonObject.put(KEY_BRAND, mBrand);
            }
            if (!mType.isEmpty()) {
                jsonObject.put(KEY_TYPE, mType);
            }
            if (!mSearch.isEmpty()) {
                jsonObject.put(KEY_SEARCH, mSearch);
            }
            if (mPrice != 0) {
                jsonObject.put(KEY_PRICE, mPrice);
            }
            jsonObject.put(KEY_ID, mId);
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public int getId() {
        return mId;
    }

    public int getPrice() {
        return mPrice;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getSearch() {
        return mSearch;
    }

    public String getType() {
        return mType;
    }

    public void setBrand(String brand) {
        if (NO_CONDITION.equals(brand)) {
            brand = "";
        }
        mBrand = brand;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public void setSearch(String search) {
        mSearch = search;
    }

    public void setType(String type) {
        if (NO_CONDITION.equals(type)) {
            type = "";
        }
        mType = type;
    }
}
