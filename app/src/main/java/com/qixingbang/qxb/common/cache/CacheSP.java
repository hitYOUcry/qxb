package com.qixingbang.qxb.common.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.qixingbang.qxb.beans.ridecycle.Type;
import com.qixingbang.qxb.common.application.QApplication;
import com.qixingbang.qxb.common.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zqj on 2015/10/28 17:18.
 * 用SharedPreference缓存 从服务器获得的字符Json数据，图片缓存用xUtils框架。
 */
public class CacheSP {

    private final static String TAG = "com.qixingbang.qxb.common.cache.CacheSP";

    private final static String CACHE_SDP_PATH = "com.qxb.sharedpreference.cache";
    private final static String CACHE_NEW_INFO = "com.qxb.sharedpreference.new_info";

    private final static String KEY_EQP_NEWS = "key_eqp_news";
    private final static String KEY_EQP_BICYCLE = "key_eqp_bicycle";
    private final static String KEY_EQP_BICYCLE_EQP = "key_eqp_bicycle_eqp";
    private final static String KEY_EQP_BICYCLE_PARTS = "key_eqp_bicycle_parts";
    private final static String KEY_EQP_PERSONAL_EQP = "key_eqp_personal_eqp";

    private final static String KEY_COMMUNICATE_QUESTIONS = "key_communicate_questions";

    private final static String KEY_RIDE_CYCLE_NEWS = "key_ride_cycle_news";
    private final static String KEY_RIDE_CYCLE_CARE = "key_ride_cycle_care";
    private final static String KEY_RIDE_CYCLE_STRATEGY = "key_ride_cycle_strategy";
    private final static String KEY_RIDE_CYCLE_DRY_CARGO = "key_ride_cycle_dry_cargo";

    private final static String KEY_QUESTION_NEW_INFO = "key_question_new_info";
    private final static String KEY_REPLY_NEW_INFO = "key_reply_new_info";
    private final static String KEY_SYSTEM_NEW_INFO = "key_system_new_info";


    //------------------- equipment part start -------------------//
    //eqp/news(save)
    public static void addEqpNewsCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_EQP_NEWS);
    }

    // eqp/news(get)
    public static JSONObject getEqpNewsCache() {
        return getCache(KEY_EQP_NEWS);
    }

    // eqp/bicycle(save)
    public static void addEqpBicycleCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_EQP_BICYCLE);
    }

    // eqp/bicycle(get)
    public static JSONObject getEqpBicycleCache() {
        return getCache(KEY_EQP_BICYCLE);
    }

    // eqp/bicycleEqp(save)
    public static void addEqpBicycleEqpCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_EQP_BICYCLE_EQP);
    }

    // eqp/bicycleEqp(get)
    public static JSONObject getEqpBicycleEqpCache() {
        return getCache(KEY_EQP_BICYCLE_EQP);
    }

    // eqp/bicycleParts(save)
    public static void addEqpBicyclePartsCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_EQP_BICYCLE_PARTS);
    }

    // eqp/bicycleParts(get)
    public static JSONObject getEqpBicyclePartsCache() {
        return getCache(KEY_EQP_BICYCLE_PARTS);
    }

    // eqp/personalEqp(save)
    public static void addEqpPersonalEqpCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_EQP_PERSONAL_EQP);
    }

    // eqp/personalEqp(get)
    public static JSONObject getEqpPersonalEqpCache() {
        return getCache(KEY_EQP_PERSONAL_EQP);
    }

    //------------------- equipment part end -------------------//

    //------------------- communicate part start -------------------//

    public static void addCommunicateQuestionsCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_COMMUNICATE_QUESTIONS);
    }

    public static JSONObject getCommunicateQuestionsCache() {
        return getCache(KEY_COMMUNICATE_QUESTIONS);
    }


    //------------------- communicate part end -------------------//


    //------------------- ride cycle part start -------------------//
    //rideCycle/news(save)
    public static void addRideCycleNewsCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_RIDE_CYCLE_NEWS);
    }

    //rideCycle/news(get)
    public static JSONObject getRideCycleNewsCache() {
        return getCache(KEY_RIDE_CYCLE_NEWS);
    }

    //rideCycle/care(save)
    public static void addRideCycleCareCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_RIDE_CYCLE_CARE);
    }

    //rideCycle/care(get)
    public static JSONObject getRideCycleCareCache() {
        return getCache(KEY_RIDE_CYCLE_CARE);
    }

    //rideCycle/strategy(save)
    public static void addRideCycleStrategyCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_RIDE_CYCLE_STRATEGY);
    }

    //rideCycle/strategy(get)
    public static JSONObject getRideCycleStrategyCache() {
        return getCache(KEY_RIDE_CYCLE_STRATEGY);
    }

    //rideCycle/dryCargo(save)
    public static void addRideCycleDryCargoCache(JSONObject jsonObject) {
        addCache(jsonObject, KEY_RIDE_CYCLE_DRY_CARGO);
    }

    //rideCycle/dryCargo(get)
    public static JSONObject getRideCycleDryCargoCache() {
        return getCache(KEY_RIDE_CYCLE_DRY_CARGO);
    }

    //another choice of add/get cache of ride cycle
    public static void addRideCycleCache(JSONObject jsonObject, Type type) {
        switch (type) {
            case NEWS:
                addRideCycleNewsCache(jsonObject);
                break;
            case CARE:
                addRideCycleCareCache(jsonObject);
                break;
            case STRATEGY:
                addRideCycleStrategyCache(jsonObject);
                break;
            case DRY_CARGO:
                addRideCycleDryCargoCache(jsonObject);
                break;
            default:
                break;
        }
    }

    public static JSONObject getRideCycleCache(Type type) {
        JSONObject jsonObject = null;
        switch (type) {
            case NEWS:
                jsonObject = getRideCycleNewsCache();
                break;
            case CARE:
                jsonObject = getRideCycleCareCache();
                break;
            case STRATEGY:
                jsonObject = getRideCycleStrategyCache();
                break;
            case DRY_CARGO:
                jsonObject = getRideCycleDryCargoCache();
                break;
            default:
                break;
        }
        return jsonObject;
    }

    //------------------- ride cycle part end -------------------//


    private static void addCache(JSONObject jsonObject, String key) {
        if (null == jsonObject)
            return;
        SharedPreferences sharedPreferences = QApplication.getInstance()
                .getSharedPreferences(CACHE_SDP_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, jsonObject.toString());
        editor.commit();
    }

    private static JSONObject getCache(String key) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(CACHE_SDP_PATH, Context.MODE_PRIVATE);
        JSONObject jsonObject = null;
        String content = sPreference.getString(key, "");
        if (!content.isEmpty()) {
            try {
                jsonObject = new JSONObject(content);
            } catch (JSONException e) {
                LogUtil.i(TAG, "build jsonObject failed");
            }
        }
        return jsonObject;
    }

    public static void clear() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(CACHE_SDP_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.clear();
        editor.commit();
    }


    private static void setNewMessageCache(Boolean b, String key) {
        SharedPreferences sharedPreferences = QApplication.getInstance()
                .getSharedPreferences(CACHE_NEW_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }

    private static Boolean getNewMessageCache(String key) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(CACHE_NEW_INFO, Context.MODE_PRIVATE);

        return sPreference.getBoolean(key, false);
    }

    public static void setMyQuestionHint(Boolean b) {
        setNewMessageCache(b, KEY_QUESTION_NEW_INFO);
    }

    public static void setMyReplyHint(Boolean b) {
        setNewMessageCache(b, KEY_REPLY_NEW_INFO);
    }

    public static void setSystemMessageHint(Boolean b) {
        setNewMessageCache(b, KEY_SYSTEM_NEW_INFO);
    }

    public static Boolean getMyQuestionHint(){
        return getNewMessageCache(KEY_QUESTION_NEW_INFO);
    }

    public static Boolean getMyReplyHint(){
        return getNewMessageCache(KEY_REPLY_NEW_INFO);
    }

    public static Boolean getSystemMessageHint(){
        return getNewMessageCache(KEY_SYSTEM_NEW_INFO);
    }
}
