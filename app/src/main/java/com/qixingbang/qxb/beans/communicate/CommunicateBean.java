package com.qixingbang.qxb.beans.communicate;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cr30 on 2015/9/6.
 */
public class CommunicateBean implements Comparable {
    private int questionId;
    private Asker asker = new Asker();
    private String title;
    private String content;
    private String likeCount;
    private String answerCount;
    private String createTime;
    private String pic1;
    private String pic2;
    private String pic3;
    private List<String> picList = new ArrayList<>();

    public int getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public String getAnswerCount() {
        return answerCount;
    }

    public Asker getAsker() {
        return asker;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAnswerCount(String answerCount) {
        this.answerCount = answerCount;
    }

    public void setAsker(Asker asker) {
        this.asker = asker;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getAskerName() {
        return asker.getNickname();
    }

    public int getAskerId() {
        return asker.getUserId();
    }

    public String getAskerIcon() {
        return asker.getIcon();
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
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


    @Override
    public int compareTo(Object another) {
        CommunicateBean anotherBean = (CommunicateBean) another;
        return questionId > anotherBean.questionId ? 1 : (questionId == anotherBean.questionId ? 0 : -1);
    }

    public class Asker {
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
