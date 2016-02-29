package com.qixingbang.qxb.adapter;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * Created by zqj on 2016/1/4 18:25.
 */
public class ShareSDKAdapter extends AuthorizeAdapter {
    @Override
    public void onCreate() {
        hideShareSDKLogo();
        super.onCreate();
    }
}
