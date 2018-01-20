package com.lenar.healthyfit.models;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.application.WeatherApplication;
import com.lenar.healthyfit.callbacks.OnDataLoadedListener;
import com.lenar.healthyfit.callbacks.WeatherCallback;
import com.lenar.healthyfit.entities.City;
import com.lenar.healthyfit.entities.WeatherData;
import com.lenar.healthyfit.utils.SharedPreferencesHelper;
import com.lenar.healthyfit.view.CityWeatherView;

import java.util.ArrayList;

public class CityWeatherModel implements OnDataLoadedListener {

    private AppCompatActivity activity;
    private CityWeatherView mView;
    private City mCity;

    private Handler updateHandler;
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mView.isRefreshing())
                getWeather(true);
        }
    };


    CityWeatherModel(AppCompatActivity activity, CityWeatherView view, int index) {
        mView = view;
        updateHandler = new Handler();
        this.activity = activity;
        mCity = WeatherApplication.getInstance().getCities().get(index);
    }

    void loadWeather(boolean reload) {
        updateHandler.removeCallbacksAndMessages(null);
        getWeather(reload);
    }

    void onDestroy() {
        activity = null;
        updateHandler.removeCallbacksAndMessages(null);
        updateHandler = null;
    }

    void onStart() {
        updateHandler = new Handler();
    }

    void onStop() {
        updateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDataLoaded(Loader loader, ArrayList<WeatherData> weatherData) {
        if (loader.getId() == R.id.weather_loader_id) {
            mView.hideProgress();
            mView.showWeather(weatherData);
            if (WeatherApplication.getInstance().getPreferencesHelper().getBoolean(SharedPreferencesHelper.KEY_UPDATE_AUTOMATICALLY)) {
                updateHandler.postDelayed(updateRunnable,
                        WeatherApplication.getInstance().getPreferencesHelper().getInt(SharedPreferencesHelper.KEY_UPDATING_TIME) * 60000);
            }
        }
    }

    private void getWeather(boolean restart) {
        WeatherCallback callbacks = new WeatherCallback(activity, mCity.getRequestName(), mCity.getCountry());
        callbacks.setLoadingCallbacksInterface(this);
        if (restart) {
            activity.getSupportLoaderManager().restartLoader(R.id.weather_loader_id, Bundle.EMPTY, callbacks);
        } else {
            activity.getSupportLoaderManager().initLoader(R.id.weather_loader_id, Bundle.EMPTY, callbacks);
        }
    }
}
