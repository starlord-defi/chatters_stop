package com.example.chatwhatsapp.Models;

import com.example.chatwhatsapp.Models.StaTus;

import java.util.ArrayList;

public class UserStatus {
    private String name, proImg;
    private long lstUpd;
    private ArrayList<StaTus> staTuses;

    public UserStatus(){

    }

    public UserStatus(String name, String proImg, long lstUpd, ArrayList<StaTus> staTuses) {
        this.name = name;
        this.proImg = proImg;
        this.lstUpd = lstUpd;
        this.staTuses = staTuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProImg() {
        return proImg;
    }

    public void setProImg(String proImg) {
        this.proImg = proImg;
    }

    public long getLstUpd() {
        return lstUpd;
    }

    public void setLstUpd(long lstUpd) {
        this.lstUpd = lstUpd;
    }

    public ArrayList<StaTus> getStaTuses() {
        return staTuses;
    }

    public void setStaTuses(ArrayList<StaTus> staTuses) {
        this.staTuses = staTuses;
    }
}
