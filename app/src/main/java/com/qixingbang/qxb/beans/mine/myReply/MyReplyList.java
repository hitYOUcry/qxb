package com.qixingbang.qxb.beans.mine.myReply;

import java.util.ArrayList;

/**
 * Created by Z.H. on 2015/10/12 20:46.
 */
public class MyReplyList {
    public ArrayList<MyReplyBean> answer;
    public int result;

    @Override
    public String toString() {
        return "MyReplyList{" +
                "answer=" + answer +
                ", result=" + result +
                '}';
    }
}
