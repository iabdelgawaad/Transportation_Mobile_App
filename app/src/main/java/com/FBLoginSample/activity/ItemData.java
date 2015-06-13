package com.FBLoginSample.activity;

import android.graphics.drawable.Drawable;

/**
 * Created by sarah on 5/18/2015.
 */
public class ItemData {
    private String title;
    private String subTitle;
    private String itemposotion;
    private String itemstatus;
    private String counter;
    private String buslineNum;
    Drawable img;
    String numb;


    public ItemData(String title){
        this.title = title;
    }
    public ItemData(String title,String subTitle,String itemposotion){
        this.title = title;
        this.subTitle=subTitle;
        this.itemposotion=itemposotion;
    }

    public ItemData(String title,String counter,String buslineNum,String itemstatus,String s,Drawable d,String num){
        this.title = title;
        this.counter=counter;
        this.buslineNum=buslineNum;
        this.itemstatus=itemstatus;
        this.img = d;
        this.numb=num;

    }

    public ItemData(String title,String counter,String buslineNum,String itemposotion){
        this.title = title;
        this.counter=counter;
        this.buslineNum=buslineNum;
        this.itemposotion=itemposotion;
    }

    public String getCounter(){
        return counter;
    }
    public String getBuslineNum(){
        return buslineNum;
    }
    public String getTitle(){
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }
    public String getItemposotion() {
        return itemposotion;
    }

    public String getitemstatus() {
        return itemstatus;
    }
    public String getid() {
        return numb;
    }
    public Drawable getimgdrwable() {
        return img;
    }
}
