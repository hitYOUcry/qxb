package com.qixingbang.qxb.common.share;

import android.content.Context;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * Created by zqj on 2016/1/8 18:15.
 */
public class ShareUtil {
    public static final String SHARE_TITLE_DEFAULT = "骑行邦";
    public static final String SHARE_URL_DEFAULT = "www.njhwt.com";
    public static final String SHARE_TEXT_DEFAULT = "骑行邦，骑行的乌托邦";
    public static final String SHARE_IMG_URL_DEFAULT = "http://www.njhwt.com/upload/share/share.png";

    private static String mTitle = SHARE_TITLE_DEFAULT;
    private static String mUrl = SHARE_URL_DEFAULT;
    private static String mText = SHARE_TEXT_DEFAULT;
    private static String mImgUrl = SHARE_IMG_URL_DEFAULT;

    public static void share(Context context) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        //        oks.setSilent(true);
        oks.setSilent(false);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用(QQ系必填)
        oks.setTitle(mTitle);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(mImgUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(SHARE_TITLE_DEFAULT);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(SHARE_URL_DEFAULT);
        //        oks.setViewToShare(rootView);
        // 启动分享GUI
        oks.show(context);
    }

    public static void share(Context context, String title) {
        mTitle = title;
        share(context);
    }
}
