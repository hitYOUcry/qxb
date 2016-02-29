package com.qixingbang.qxb.activity.mine.chooseHeadPortrait;

/**
 * Created by Z.H. on 2015/9/5 16:43.
 */
public class ImageModel {
    public String path;
    public long date;
    public long id;

    public ImageModel() {
    }

    public ImageModel(String path, long id, long date) {
        this.path = path;
        this.id = id;
        this.date = date;
    }
}
