package com.lenar.healthyfit.entities;

import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("country")
    private String mCountry;
    @SerializedName("coord")
    private Coordinates mCoordinates;
    private String mRequestName;

    private int image;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getCountry() {
        return mCountry;
    }

    public Coordinates getCoordinates() {
        return mCoordinates;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public int getImage() {
        return image;
    }

    public void setImage(@DrawableRes int image) {
        this.image = image;
    }

    public String getRequestName() {
        return mRequestName;
    }

    public void setRequestName(String requestName) {
        mRequestName = requestName;
    }

    public void setCoordinates(Coordinates coordinates) {
        mCoordinates = coordinates;
    }
}

