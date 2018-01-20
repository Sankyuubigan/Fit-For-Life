package com.lenar.healthyfit.entities;

import com.google.gson.annotations.SerializedName;

class Wind {
    @SerializedName("speed")
    private double mSpeed;

    @SerializedName("deg")
    private double mDegree;

    public double getSpeed() {
        return mSpeed;
    }

    public double getDegree() {
        return mDegree;
    }
}
