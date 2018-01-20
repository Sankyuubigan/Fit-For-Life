package com.lenar.healthyfit.callbacks;

import android.support.v4.content.Loader;

import com.lenar.healthyfit.entities.WeatherData;

import java.util.ArrayList;

public interface OnDataLoadedListener {
    void onDataLoaded(Loader loader, ArrayList<WeatherData> weatherData);
}
