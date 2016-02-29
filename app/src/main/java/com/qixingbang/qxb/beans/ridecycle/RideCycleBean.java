package com.qixingbang.qxb.beans.ridecycle;

import com.qixingbang.qxb.beans.equipment.Comment;

import java.util.List;

/**
 * Created by zqj on 2015/11/5 10:15.
 */
public class RideCycleBean implements Comparable {
    private int articleId;
    private Type type;
    private String title;
    private String logo;
    private String createTime;
    private int likeCount;
    private int favCount;
    private List<Comment> mCommentLists;

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int id) {
        this.articleId = id;
    }

    public Type getType() {
        //default --> news
        if(null == type){
            return Type.NEWS;
        }
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        if (null == title || title.isEmpty()) {
            return " ";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        if (null == logo || logo.isEmpty()) {
            return " ";
        }
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCreateTime() {
        if (null == createTime || createTime.isEmpty()) {
            return "";
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public List<Comment> getCommentLists() {
        return mCommentLists;
    }

    public void setCommentLists(List<Comment> commentLists) {
        mCommentLists = commentLists;
    }

    @Override
    public int compareTo(Object another) {
        int anotherId = ((RideCycleBean) another).articleId;
        return (articleId > anotherId) ? 1 : (articleId == anotherId ? 0 : -1);
    }
}
