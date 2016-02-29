package com.qixingbang.qxb.beans.mine;

/**
 * Created by Z.H. on 2015/11/20 10:56.
 */
public class UserInfoBean {
    public int result;

    public User user;

    public class User{
        public int age;
        public String birthday;
        public String icon;
        public String nickname;
        public String phone;
        public int userId;
        public boolean sex;
    }
}
