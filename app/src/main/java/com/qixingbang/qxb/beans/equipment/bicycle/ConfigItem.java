package com.qixingbang.qxb.beans.equipment.bicycle;

/**
 * Created by zqj on 2015/9/6 09:45.
 */
public class ConfigItem {
    private String mName;
    private String mContent;

    public ConfigItem() {

    }

    public ConfigItem(String name, String content) {
        mName = name;
        mContent = content;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
