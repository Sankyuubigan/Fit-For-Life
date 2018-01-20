package com.lenar.healthyfit.entities;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("temp")
    private double mTemperature;

    @SerializedName("pressure")
    private double mPressure;

    @SerializedName("humidity")
    private double mHumidity;

    @SerializedName("temp_min")
    private double mMinimalTemperature;

    @SerializedName("temp_max")
    private double mMaximalTemperature;

    public double getTemperature() {
        return mTemperature;
    }

    public double getPressure() {
        return mPressure;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public double getMinimalTemperature() {
        return mMinimalTemperature;
    }

    public double getMaximalTemperature() {
        return mMaximalTemperature;
    }
}
