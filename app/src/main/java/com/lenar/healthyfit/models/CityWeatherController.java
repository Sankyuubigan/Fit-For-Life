package com.lenar.healthyfit.models;

import android.support.v7.app.AppCompatActivity;

import com.lenar.healthyfit.view.CityWeatherView;

public class CityWeatherController {

    private CityWeatherModel cityWeatherModel;


    public CityWeatherController(AppCompatActivity activity, CityWeatherView view, int index) {
        cityWeatherModel = new CityWeatherModel(activity, view, index);
    }

    public void loadWeather(boolean reload) {
        cityWeatherModel.loadWeather(reload);
    }

    public void onDestroy() {
        cityWeatherModel.onDestroy();
    }

    public void onStart() {
        cityWeatherModel.onStart();
    }

    public void onStop() {
        cityWeatherModel.onStop();
    }
}
