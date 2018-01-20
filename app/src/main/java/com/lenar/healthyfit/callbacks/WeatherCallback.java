package com.lenar.healthyfit.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.lenar.healthyfit.entities.WeatherData;
import com.lenar.healthyfit.loaders.WeatherLoader;

import java.util.ArrayList;

public class WeatherCallback implements LoaderManager.LoaderCallbacks<ArrayList<WeatherData>> {

    private OnDataLoadedListener loadingCallbacksInterface;
    private String city;
    private String country;
    private Context context;

    public WeatherCallback(Context context, String city, String country) {
        this.context = context;
        this.city = city;
        this.country = country;
    }

    public void setLoadingCallbacksInterface(OnDataLoadedListener loadingCallbacksInterface) {
        this.loadingCallbacksInterface = loadingCallbacksInterface;
    }

    @Override
    public Loader<ArrayList<WeatherData>> onCreateLoader(int id, Bundle args) {
        return new WeatherLoader(context, city + "," + country.toLowerCase());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<WeatherData>> loader, ArrayList<WeatherData> data) {
        if (loadingCallbacksInterface != null) {
            loadingCallbacksInterface.onDataLoaded(loader, data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<WeatherData>> loader) {

    }
}
