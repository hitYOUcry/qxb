package com.qixingbang.qxb.beans.equipment.accesory;

import android.text.TextUtils;

import com.qixingbang.qxb.beans.equipment.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/8/31 20:28.
 */
public class Accessory implements Comparable {
    private int accessoryId;
    private String name;
    private int price;
    private String brand;
    private String type;
    private String introduce;
    private int version;
    private int likeCount;
    private int favCount;
    private String pic1;
    private String pic2;
    private String pic3;
    private List<Comment> comments;
    private boolean hasDetail = false;
    private List<String> picList = new ArrayList<>(); //图片URL ，三张

    public Accessory() {
    }

    public Accessory(int accessoryId) {
        this.accessoryId = accessoryId;
    }

    public Accessory(String name) {
        this.name = name;
    }

    public int getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(int accessoryId) {
        this.accessoryId = accessoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public boolean getHasDetail() {
        return hasDetail;
    }


    @Override
    public int compareTo(Object another) {
        int anotherId = ((Accessory) another).getAccessoryId();
        return accessoryId > anotherId ? 1 : (accessoryId == anotherId ? 0 : -1);
    }
}
