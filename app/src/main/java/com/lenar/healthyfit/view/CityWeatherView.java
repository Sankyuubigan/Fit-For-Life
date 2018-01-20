package com.lenar.healthyfit.view;

import com.lenar.healthyfit.entities.WeatherData;

import java.util.ArrayList;

public interface CityWeatherView {
    void showWeather(ArrayList<WeatherData> data);

    void hideProgress();

    boolean isRefreshing();
}
