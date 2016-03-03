package com.qixingbang.qxb.beans.equipment.bicycle;

import android.text.TextUtils;

import com.qixingbang.qxb.beans.equipment.Comment;
import com.qixingbang.qxb.beans.equipment.accesory.Accessory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/8/26 16:03.
 * 自行车
 */
public class Bicycle implements Serializable, Comparable {

    private int bikeId;         //后台生成的自行车ID

    private String brand;      //品牌
    private int price;   //参考价
    private String bikeType;       //种类
    private String model;      //型号
    private String color;      //颜色
    private String size;       //尺寸
    private int likeCount;     //点赞数
    private int favCount;      //收藏数
    private int year;          //年份
    private String pic1;
    private String pic2;
    private String pic3;
    private String roadType;   //路况
    private String series;     //系列
    private String level;      //级别
    private String speed;
    private String frame;      //车架
    private Accessory frontFork;  //前叉
    private Accessory kit;
    private Accessory frontDerailleur;//前变速器
    private Accessory backDerailleur;//后变速器
    private Accessory lever;     //变速
    private Accessory brake;     //刹车
    private Accessory cassettes;  //齿轮
    private String cassettesParam;//齿轮参数
    private String outerTire; //外胎
    private String outerTireParam;//外胎参数
    private Accessory wheelSystem;//轮组
    private String wheelSystemParam;//轮组参数
    private int version;
    private List<Comment> comments;
    private List<String> picList = new ArrayList<>(); //图片URL ，三张


    public Bicycle() {
    }

    public Bicycle(int bikeId) {
        this.bikeId = bikeId;
    }

    public int getBikeId() {
        return bikeId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getPicUrl() {
        picList.clear();
        if (!TextUtils.isEmpty(pic1))
            picList.add(pic1);
        if (!TextUtils.isEmpty(pic2))
            picList.add(pic2);
        if (!TextUtils.isEmpty(pic3))
            picList.add(pic3);
        return picList;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public Accessory getFrontFork() {
        return frontFork;
    }

    public void setFrontFork(Accessory frontFork) {
        this.frontFork = frontFork;
    }

    public Accessory getFrontDerailleur() {
        return frontDerailleur;
    }

    public void setFrontDerailleur(Accessory frontDerailleur) {
        this.frontDerailleur = frontDerailleur;
    }

    public Accessory getBackDerailleur() {
        return backDerailleur;
    }

    public void setBackDerailleur(Accessory backDerailleur) {
        this.backDerailleur = backDerailleur;
    }

    public Accessory getLever() {
        return lever;
    }

    public void setLever(Accessory lever) {
        this.lever = lever;
    }

    public Accessory getBrake() {
        return brake;
    }

    public void setBrake(Accessory brake) {
        this.brake = brake;
    }

    public Accessory getCassettes() {
        return cassettes;
    }

    public void setCassettes(Accessory cassettes) {
        this.cassettes = cassettes;
    }

    public String getCassettesParam() {
        return cassettesParam;
    }

    public void setCassettesParam(String cassettesParam) {
        this.cassettesParam = cassettesParam;
    }

    public String getOuterTire() {
        return outerTire;
    }

    public void setOuterTire(String outerTire) {
        this.outerTire = outerTire;
    }

    public String getOuterTireParam() {
        return outerTireParam;
    }

    public void setOuterTireParam(String outerTireParam) {
        this.outerTireParam = outerTireParam;
    }

    public Accessory getWheelSystem() {
        return wheelSystem;
    }

    public void setWheelSystem(Accessory wheelSystem) {
        this.wheelSystem = wheelSystem;
    }

    public String getWheelSystemParam() {
        return wheelSystemParam;
    }

    public void setWheelSystemParam(String wheelSystemParam) {
        this.wheelSystemParam = wheelSystemParam;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int compareTo(Object another) {
        int anotherId = ((Bicycle) another).getBikeId();
        return bikeId > anotherId ? 1 : (bikeId == anotherId ? 0 : -1);
    }
}
