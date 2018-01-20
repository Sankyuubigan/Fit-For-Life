package com.lenar.healthyfit.entities;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("coord")
    private Coordinates mCoordinates;

    @SerializedName("main")
    private Weather mWeather;

    @SerializedName("base")
    private String mBase;

    @SerializedName("wind")
    private Wind mWind;

    @SerializedName("name")
    private String mCity;

    public Coordinates getCoordinates() {
        return mCoordinates;
    }

    public Weather getWeather() {
        return mWeather;
    }

    public String getBase() {
        return mBase;
    }

    public Wind getWind() {
        return mWind;
    }

    public String getCity() {
        return mCity;
    }
}
