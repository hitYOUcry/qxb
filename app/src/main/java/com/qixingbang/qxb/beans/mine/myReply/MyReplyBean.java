package com.qixingbang.qxb.beans.mine.myReply;

/**
 * Created by Z.H. on 2015/9/18 15:07.
 */
public class MyReplyBean {
    public int answerId;
    public Question question;
    public class Question{
        public int answerCount;
        public Asker asker;
        public class Asker{
            public String icon;
            public String nickname;
            public int userId;
        }
        public String content;
        public String createTime;
        public int likeCount;
        public int questionId;
        public String pic1;
        public String pic2;
        public String pic3;
    }
}
