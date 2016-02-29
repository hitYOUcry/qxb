package com.qixingbang.qxb.beans;

import android.text.TextUtils;

import com.qixingbang.qxb.beans.communicate.CommunicateBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/12/2 20:55.
 */
public class ReplyBean {
    private int answerId;
    private Answerer answerer = new Answerer();
    private String content;
    private int likeCount;
    private int isLike;
    private String answerTime;
    private String pic1;
    private String pic2;
    private String pic3;
    private List<String> picList = new ArrayList<>();

    public ReplyBean() {

    }

    public ReplyBean(CommunicateBean communicateBean){
        // ReplyListView title
        answerer.nickname = communicateBean.getAskerName();
        answerer.icon = communicateBean.getAskerIcon();
        content = communicateBean.getContent();
        answerTime = communicateBean.getCreateTime();
        pic1 = communicateBean.getPic1();
        pic2 = communicateBean.getPic2();
        pic3 = communicateBean.getPic3();
    }

    public int getAnswerId() {
        return answerId;
    }


    public String getContent() {
        return content;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getAnswererName() {
        return answerer.getNickname();
    }

    public int getAnswererId() {
        return answerer.getUserId();
    }

    public String getAnsererIcon() {
        return answerer.getIcon();
    }

    public String getPic1() {
        return pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public String getAnswerTime() {
        if(TextUtils.isEmpty(answerTime)){
            return "11-11";
        }
        return answerTime;
    }

    public List<String> getPicList() {
        picList.clear();
        if (!TextUtils.isEmpty(pic1))
            picList.add(pic1);
        if (!TextUtils.isEmpty(pic2))
            picList.add(pic2);
        if (!TextUtils.isEmpty(pic3))
            picList.add(pic3);
        return picList;

    }

    private class Answerer {
        private String icon;
        private int userId;
        private String nickname;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
