package com.qixingbang.qxb.beans.mine.myFav;

/**
 * Created by Z.H. on 2015/10/8 14:53.
 */
public class MyFavoriteEqpBean {
    /**
     * 创建时间
     */
    public String createTime;
    /**
     * ID
     */
    public int favId;
    /**
     * ic_mine_logo
     */
    public String logo;
    /**
     * name
     */
    public String name;
    /**
     * parentId
     */
    public int parentId;
    /**
     * price
     */
    public int price;
    /**
     * type
     */
    public String type;

    @Override
    public String toString() {
        return "Favorite{" +
                "favId=" + favId +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                ", createTime='" + createTime + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", price=" + price +
                '}';
    }
}
