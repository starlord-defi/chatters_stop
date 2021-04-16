package com.example.chatwhatsapp.Models;

public class StaTus {
    private String imgUri;
    private long TimeStamp;

    public StaTus(){

    }

    public StaTus(String imgUri, long timeStamp) {
        this.imgUri = imgUri;
        TimeStamp = timeStamp;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}
