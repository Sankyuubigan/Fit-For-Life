package com.lenar.healthyfit.entities;

import android.support.annotation.DrawableRes;

public class WeatherData {
    private int mIcon;
    private String mText;

    public WeatherData(@DrawableRes int icon, String text) {
        mIcon = icon;
        mText = text;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getText() {
        return mText;
    }
}
