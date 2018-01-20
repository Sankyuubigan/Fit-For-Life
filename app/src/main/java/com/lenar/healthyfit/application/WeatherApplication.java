package com.lenar.healthyfit.application;

import android.app.Application;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.application.api.OpenWeatherApi;
import com.lenar.healthyfit.application.api.RetrofitUtil;
import com.lenar.healthyfit.entities.City;
import com.lenar.healthyfit.entities.Coordinates;
import com.lenar.healthyfit.utils.SharedPreferencesHelper;

import java.util.ArrayList;

public class WeatherApplication extends Application {

    private static WeatherApplication sInstance;

    private OpenWeatherApi mOpenWeatherApi;
    private SharedPreferencesHelper mPreferencesHelper;

    private ArrayList<City> mCities;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        RetrofitUtil retrofitUtil = new RetrofitUtil();
        retrofitUtil.initialize();
        mOpenWeatherApi = retrofitUtil.getInstance();
        mPreferencesHelper = new SharedPreferencesHelper(this);

        mCities = new ArrayList<>();

        City nChelnyCity = new City();
        nChelnyCity.setId(523750);
        nChelnyCity.setRequestName("Naberezhnyye Chelny");
        nChelnyCity.setName("Набережные Челны");
        nChelnyCity.setCountry("RU");
        nChelnyCity.setCoordinates(new Coordinates(55.756111, 52.42889));
        nChelnyCity.setImage(R.drawable.chelny);
        mCities.add(nChelnyCity);

        City moskowCity = new City();
        moskowCity.setId(524894);
        moskowCity.setRequestName("Moskva");
        moskowCity.setName("Москва");
        moskowCity.setCountry("RU");
        moskowCity.setCoordinates(new Coordinates(55.761665, 37.606667));
        moskowCity.setImage(R.drawable.moskow);
        mCities.add(moskowCity);

        City kazanCity = new City();
        kazanCity.setId(551487);
        kazanCity.setRequestName("Kazan");
        kazanCity.setName("Казань");
        kazanCity.setCountry("RU");
        kazanCity.setCoordinates(new Coordinates(55.788738, 49.122139));
        kazanCity.setImage(R.drawable.kazan);
        mCities.add(kazanCity);
    }

    public ArrayList<City> getCities() {
        return mCities;
    }

    public static WeatherApplication getInstance() {
        return sInstance;
    }

    public OpenWeatherApi getApi() {
        return mOpenWeatherApi;
    }

    public SharedPreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }
}
