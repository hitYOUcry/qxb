package com.qixingbang.qxb.beans;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qixingbang.qxb.activity.mine.chooseHeadPortrait.ImageFolder;
import com.qixingbang.qxb.common.application.QApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by zqj on 2015/9/23 12:48.
 * 用sharePreference 缓存账号
 */
public class QAccount {

    /**
     * sharedPreference key
     */
    private static final String ACCOUNT_SDF_PATH = "com.qxb.sharedpreference.account";

    private static final String KEY_OPENTIME = "key_openTimes";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_USER_CODE = "userCode";
    private static final String KEY_PASSWD = "passwd";
    //用户信息
    private static final String KEY_USER_INFO = "userInfo";
    //极光推送
    private static final String KEY_IS_PUSH = "isPush";
    private static final String KEY_IS_REGISTER = "isRegister";

    public static ArrayList<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
    private String mNickname;
    private int mUserId;
    private Date mBirthday;
    private int mSex;
    private int mAge;
    private String mIcon;

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public Date getBirthday() {
        return mBirthday;
    }

    public void setBirthday(Date birthday) {
        mBirthday = birthday;
    }

    public int getSex() {
        return mSex;
    }

    public void setSex(int sex) {
        mSex = sex;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    /**
     * 各种静态方法
     */

    public synchronized static boolean hasAccount() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        return !TextUtils.isEmpty(sPreference.getString(KEY_TOKEN, ""));
    }

    public synchronized static void saveToken(String token) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public synchronized static void savePassword(String encryptPassword) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putString(KEY_PASSWD, encryptPassword);
        editor.apply();
    }

    public synchronized static String getToken() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        return sPreference.getString(KEY_TOKEN, "");
    }

    public synchronized static void saveLoginInfo(String id, String passwd) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putString(KEY_USER_CODE, id);
        editor.putString(KEY_PASSWD, passwd);
        editor.apply();
    }

    public synchronized static JSONObject getLoginInfo() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        String userCode = sPreference.getString(KEY_USER_CODE, "");
        String passwd = sPreference.getString(KEY_PASSWD, "");
        JSONObject jsonObject = new JSONObject();
        if (!passwd.isEmpty() && !userCode.isEmpty()) {
            try {
                jsonObject.put(KEY_USER_CODE, userCode);
                jsonObject.put(KEY_PASSWD, passwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    public synchronized static void setUserInfo(String userInfo) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putString(KEY_USER_INFO, userInfo);
        editor.apply();
    }

    public synchronized static String getUserInfo() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        return sPreference.getString(KEY_USER_INFO, "");
    }

    public QAccount fromJsonObject(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), QAccount.class);
    }

    /**
     * 登出的时候清理 SharePreferences中的信息
     */
    public static void clear() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 记录app启动次数
     */
    public synchronized static void appStart() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putInt(KEY_OPENTIME, sPreference.getInt(KEY_OPENTIME, Context.MODE_PRIVATE) + 1);
        editor.apply();
    }

    /**
     * 判断是否是第一次打开app
     *
     * @return
     */
    public synchronized static boolean isFirstOpen() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        return sPreference.getInt(KEY_OPENTIME, Context.MODE_PRIVATE) == 0;
    }

    /**
     * 记录是否启动极光推送
     *
     * @param isPush
     */
    public synchronized static void saveIsPush(boolean isPush) {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPreference.edit();
        editor.putBoolean(KEY_IS_PUSH, isPush);
        editor.commit();
    }

    public synchronized static boolean getIsPush() {
        SharedPreferences sPreference = QApplication.getInstance()
                .getSharedPreferences(ACCOUNT_SDF_PATH, Context.MODE_PRIVATE);
        return sPreference.getBoolean(KEY_IS_PUSH, true);
    }


    public synchronized static void registerJPush() {
        //TODO:注册极光 暂时放在这 后期调整注册里面 TAG ALIAS设置
        //TODO: isRegister  暂时随便放置一个值
        boolean isRegister = true;
        //没有注册  就注册
        if (!isRegister) {
            JSONObject jsonObject = QAccount.getLoginInfo();
            try {
                long phone = jsonObject.getLong("phone");
                String phoneAlias = String.valueOf(phone);
                JPushInterface.setAliasAndTags(QApplication.getInstance(), phoneAlias, null, new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        if (i == 0) {
                            //注册成功

                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
