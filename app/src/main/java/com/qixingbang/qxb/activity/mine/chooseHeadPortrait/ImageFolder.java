package com.qixingbang.qxb.activity.mine.chooseHeadPortrait;

/**
 * 图片文件夹实体
 * Created by Z.H. on 2015/9/15 15:20.
 */
public class ImageFolder {
    /**
     * 该文件夹所含有的图片的数量
     */
    private int count;
    /**
     * 该文件夹的路径
     */
    private String folderDir;
    /**
     * 第一张图片的路径
     * 用于listview的专辑封面显示
     */
    private String firstImagePath;
    /**
     * 该文件夹的名字
     */
    private String folderName;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getFolderDir() {
        return folderDir;
    }

    public void setFolderDir(String folderDir) {
        this.folderDir = folderDir;
        //取最后一个斜杠 后面的字符串为该文件夹的名字
        //1.找到最后一个斜杠的位置
        //2.向后截取
        int lastIndexOf = this.folderDir.lastIndexOf("/");
        this.folderName = folderDir.substring(lastIndexOf);
    }

    public String getFolderName() {
        return folderName;
    }
}
