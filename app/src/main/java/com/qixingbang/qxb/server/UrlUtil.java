package com.qixingbang.qxb.server;

/**
 * Created by zqj on 2015/9/22 18:28.
 */
public class UrlUtil {

    public final static String BASE_ADDRESS = "http://139.129.117.90";

    private final static String ADDRESS = "http://139.129.117.90/qxb_back";

    public static String getBaseAddress() {
        return BASE_ADDRESS;
    }

    public static String getAddress() {
        return ADDRESS;
    }

    /**
     * 公共部分：登录、注册
     */
    //登录
    private final static String USER_LOGIN = ADDRESS + "/user/login";

    public static String getUserLoginUrl() {
        return USER_LOGIN;
    }

    //注册
    private final static String USER_REGISTER = ADDRESS + "/user/register";

    public static String getUserRegisterUrl() {
        return USER_REGISTER;
    }

    //注销
    private final static String USER_LOGOUT = ADDRESS + "/user/logout";

    public static String getUserLogoutUrl() {
        return USER_LOGOUT;
    }

    /**
     * 首页
     */
    public static String getHomePageUrl() {
        return ADDRESS + "/json/homepage.json";
    }


    /**
     * 装备板块
     */
    //整车品牌
    private final static String BICYCLE_BRAND = ADDRESS + "/bike/brand";

    public static String getBicycleBrandUrl() {
        return BICYCLE_BRAND;
    }

    //整车列表
    private final static String BICYCLE_LIST = ADDRESS + "/bike/list";

    public static String getBicycleURL() {
        return BICYCLE_LIST;
    }

    //整车详情
    public static String getBicycleDetails(int bikeId) {
        return ADDRESS + "/bike/detail/" + bikeId;
    }

    //人身装备品牌
    private final static String BODY_EQP_BRAND = ADDRESS + "/bodyEqp/brand";

    public static String getBodyEqpBrandUrl() {
        return BODY_EQP_BRAND;
    }

    //人身装备列表
    private final static String BODY_EQP_LIST = ADDRESS + "/bodyEqp/list";

    //人身装备详情
    public static String getBodyEqpDetails(int bodyEqpId) {
        return ADDRESS + "/bodyEqp/detail/" + bodyEqpId;
    }

    public static String getBodyEqpListUrl() {
        return BODY_EQP_LIST;
    }

    //车身装备品牌
    private final static String BIKE_EQP_BRAND = ADDRESS + "/bikeEqp/brand";

    public static String getBikeEqpBrand() {
        return BIKE_EQP_BRAND;
    }

    //车身装备列表
    private final static String BIKE_EQP_LIST = ADDRESS + "/bikeEqp/list";

    public static String getBikeEqpList() {
        return BIKE_EQP_LIST;
    }

    //车身装备详情
    public static String getBikeEqpDetails(int bikeEqpId) {
        return ADDRESS + "/bikeEqp/detail/" + bikeEqpId;
    }

    //零部件品牌
    private final static String BIKE_PARTS_BRAND = ADDRESS + "/accessory/brand";

    public static String getBikePartsBrand() {
        return BIKE_PARTS_BRAND;
    }

    //零部件类型
    private final static String BIKE_ACCESSORY_TYPE = ADDRESS + "/accessory/type";

    public static String getBikeAccessoryType() {
        return BIKE_ACCESSORY_TYPE;
    }

    //零部件列表
    private final static String BIKE_PARTS_LIST = ADDRESS + "/accessory/list";

    public static String getBikePartsList() {
        return BIKE_PARTS_LIST;
    }

    //零部件详情
    public static String getBikePartsDetails(int bikePartsId) {
        return ADDRESS + "/accessory/detail/" + bikePartsId;
    }

    //收藏
    public static String getFavUrl(String itemType, int itemId) {
        return ADDRESS + "/user/favorite/" + itemType + "/" + itemId;
    }

    //点赞
    public static String getLikeUrl(String itemType, int itemId) {
        return ADDRESS + "/user/like/" + itemType + "/" + itemId;
    }

    //发送评论
    public static String getSendCommentUrl() {
        return ADDRESS + "/user/postComment";
    }

    //获取评论
    public static String getObtainCommentsUrl(String type, int itemId, int commentId) {
        return ADDRESS + "/comment/" + type + "/" + itemId + "/" + commentId;
    }

    /**
     * 骑行圈板块
     */
    public static String getRideCycleListUrl(String type, String action, int articleId) {
        return ADDRESS + "/article/list/" + type + "/" + action + "/" + articleId;
    }

    public static String getDryCargoDetailsUrl(int cargoSubId) {
        return ADDRESS + "/article/list/dryCargoSub/" + cargoSubId;
    }

    public static String getNewsDetailsUrl(int id) {
        return ADDRESS + "/article/detail/news/" + id;
    }

    public static String getfavlikeDetails(String type, int articleId) {
        return ADDRESS + "/article/likeFav/" + type + "/" + articleId;
    }

    /**
     * 交流板块
     */
    //获取问题列表
    public static String getObtainQuestionUrl(String action, int questionId) {
        return ADDRESS + "/question/list/" + action + "/" + questionId;
    }

    //发送问题
    public static String getSendQuestionUrl() {
        return ADDRESS + "/user/ask";
    }

    //    获取详细问题
    public static String getDetailQuestionUrl(int questionId) {
        return ADDRESS + "/question/detail/" + questionId;
    }

    //获取回答列表
    public static String getReplyUrl(int questionId, int count) {
        return ADDRESS + "/answer/" + questionId + "/" + count;
    }

    //发送回答
    public static String getSendAnswerUrl() {
        return ADDRESS + "/user/answer";
    }

    //收藏
    public static String getFavQuestionUrl(String itemType, int itemId) {
        return ADDRESS + "/user/favorite/" + itemType + "/" + itemId;
    }

    //点赞
    public static String getLikeAnswerUrl(String itemType, int itemId) {
        return ADDRESS + "/user/like/" + itemType + "/" + itemId;
    }

    /**
     * 我的板块
     */
    private final static String MY_DETAIL_INFO = ADDRESS + "/user/getDetail";

    public static String getUserDetails() {
        return MY_DETAIL_INFO;
    }

    //我的收藏列表
    private final static String MY_FAV_EQP_LIST = ADDRESS + "/user/favList/equip";
    private final static String MY_FAV_ARTICLE_LIST = ADDRESS + "/user/favList/article";
    private final static String MY_HEAD_PORTRAIT = ADDRESS + "/user/icon";


    public static String getMyFavEqpList() {
        return MY_FAV_EQP_LIST;
    }

    public static String getMyFavArticleList() {
        return MY_FAV_ARTICLE_LIST;
    }

    public static String getMyHeadPortrait() {
        return MY_HEAD_PORTRAIT;
    }

    //我的提问列表
    private final static String MY_QUESTION_LIST = ADDRESS + "/user/question";

    public static String getMyQuestionList() {
        return MY_QUESTION_LIST;
    }

    //我的回复列表
    private final static String MY_REPLY_LIST = ADDRESS + "/user/answer";

    public static String getMyReplyList() {
        return MY_REPLY_LIST;
    }

    private final static String UPDATE_USER_INFO = ADDRESS + "/user/update";

    public static String getUpdateUserInfo() {
        return UPDATE_USER_INFO;
    }
    //更新
    private final static String VERSION_INFO = ADDRESS + "/json/version.json";

    public static String getVersionInfo(){
        return VERSION_INFO;
    }
}
