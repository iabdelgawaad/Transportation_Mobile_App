package com.FBLoginSample.model;


public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    String imgResID;

    public NavDrawerItem() {

    }
    public NavDrawerItem(String itemName, String imgResID) {
        super();
        this.title = itemName;
        this.imgResID = imgResID;
    }


   /* public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }*/



    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return imgResID;
    }

    public void setImage(String imgResID) {
        this.imgResID = imgResID;
    }

}
