package com.qixingbang.qxb.beans.ridecycle.drycargo;

/**
 * Created by zqj on 2015/11/3 11:11.
 */
public class DryCargoItemBean {

    private int dryCargoId;
    private String logo;
    private String title;
    private String content;
    private String firstWord;


    public void setDryCargoId(int dryCargoId) {
        this.dryCargoId = dryCargoId;
    }

    public int getDryCargoId() {
        return dryCargoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setFirstWord(String firstWord) {
        this.firstWord = firstWord;
    }

    public String getFirstWord() {
        return firstWord;
    }

}
