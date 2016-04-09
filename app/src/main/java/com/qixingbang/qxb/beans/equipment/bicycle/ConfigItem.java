package com.qixingbang.qxb.beans.equipment.bicycle;

import android.text.TextUtils;

import com.qixingbang.qxb.beans.equipment.accesory.Accessory;

import java.util.List;

/**
 * Created by zqj on 2015/9/6 09:45.
 */
public class ConfigItem {
    private String mName;
    private Accessory mContent;

    public ConfigItem() {

    }

    public ConfigItem(String name, Accessory content) {
        mName = name;
        mContent = content;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Accessory getContent() {
        return mContent;
    }

    public void setContent(Accessory content) {
        mContent = content;
    }

    public static void buildConfigListFromBicycle(Bicycle mBicycle, List<ConfigItem> listTotal) {
        if (null == mBicycle || null == listTotal)
            return;
        ConfigItem configItem;
        if (!TextUtils.isEmpty(mBicycle.getFrame())) {
            configItem = new ConfigItem("车架", new Accessory(mBicycle.getFrame()));
            listTotal.add(configItem);
        }
        if (null != mBicycle.getFrontFork()) {
            configItem = new ConfigItem("前叉", mBicycle.getFrontFork());
            listTotal.add(configItem);
        }
        if (null != mBicycle.getBrake()) {
            configItem = new ConfigItem("刹车", mBicycle.getBrake());
            listTotal.add(configItem);
        }
        if (null != mBicycle.getCassettes()) {
            configItem = new ConfigItem("齿轮", mBicycle.getCassettes());
            listTotal.add(configItem);
        } else {
            if (!TextUtils.isEmpty(mBicycle.getCassettesParam())) {
                configItem = new ConfigItem("齿轮参数", new Accessory(mBicycle.getCassettesParam()));
                listTotal.add(configItem);
            }
        }
        if (!TextUtils.isEmpty(mBicycle.getOuterTire())) {
            configItem = new ConfigItem("外胎", new Accessory(mBicycle.getOuterTire()));
            listTotal.add(configItem);
        }

        if (null != mBicycle.getWheelSystem()) {
            configItem = new ConfigItem("轮组", mBicycle.getWheelSystem());
            listTotal.add(configItem);
        } else {
            if (!TextUtils.isEmpty(mBicycle.getWheelSystemParam())) {
                configItem = new ConfigItem("轮组参数", new Accessory(mBicycle.getWheelSystemParam()));
                listTotal.add(configItem);
            }
        }

        if (null != mBicycle.getKit()) {
            configItem = new ConfigItem("套件", mBicycle.getKit());
            listTotal.add(configItem);
        }

        if (null != mBicycle.getFrontDerailleur()) {
            configItem = new ConfigItem("前变速器", mBicycle.getFrontDerailleur());
            listTotal.add(configItem);
        }

        if (null != mBicycle.getBackDerailleur()) {
            configItem = new ConfigItem("后变速器", mBicycle.getBackDerailleur());
            listTotal.add(configItem);
        }
    }
}
