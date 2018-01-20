package com.lenar.healthyfit.entities;

import com.google.gson.annotations.SerializedName;

public class Coordinates {
    @SerializedName("lat")
    private double mLatitude;

    @SerializedName("lon")
    private double mLongitude;

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public Coordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }
}
