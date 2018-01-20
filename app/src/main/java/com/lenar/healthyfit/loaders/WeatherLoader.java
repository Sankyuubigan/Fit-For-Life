package com.lenar.healthyfit.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import com.lenar.healthyfit.R;
import com.lenar.healthyfit.application.WeatherApplication;
import com.lenar.healthyfit.entities.Weather;
import com.lenar.healthyfit.entities.WeatherData;
import com.lenar.healthyfit.entities.WeatherResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherLoader extends Loader<ArrayList<WeatherData>> {

    DecimalFormat df = new DecimalFormat("#.##");
    private String mCityName;

    @Nullable
    private ArrayList<WeatherData> mWeatherData;

    private Call<WeatherResponse> mCall;

    public WeatherLoader(@NonNull Context context, String cityRequestName) {
        super(context);
        mCityName = cityRequestName;
        mCall = WeatherApplication.getInstance().getApi().getWeather(mCityName, getContext().getString(R.string.api_key));
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mWeatherData == null) {
            forceLoad();
        } else {
            deliverResult(mWeatherData);
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        if (!mCall.isExecuted()) {
            mCall.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mWeatherData = new ArrayList<>();
                        Weather weather = response.body().getWeather();
                        mWeatherData.add(new WeatherData(R.drawable.ic_tem, "Темп. " + (df.format(weather.getTemperature() - 273.15))
                                + " \u2103"));
                        mWeatherData.add(new WeatherData(R.drawable.ic_pressure, "Давл. " + df.format(weather.getPressure() * 100 / 133.322) + " мм. рт. ст"));
                        mWeatherData.add(new WeatherData(R.drawable.ic_humidity, "Влажн. " + weather.getHumidity() + "%"));
                        mWeatherData.add(new WeatherData(R.drawable.ic_min_temp, "Мин. темп. " + (df.format(weather.getMinimalTemperature() - 273.15))
                                + " \u2103"));
                        mWeatherData.add(new WeatherData(R.drawable.ic_max_temp, "Макс. темп. " + (df.format(weather.getMaximalTemperature() - 273.15))
                                + " \u2103"));
                        deliverResult(mWeatherData);
                    } else {
                        deliverResult(null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                    deliverResult(null);
                }
            });
        } else {
            deliverResult(mWeatherData);
        }
    }
}
