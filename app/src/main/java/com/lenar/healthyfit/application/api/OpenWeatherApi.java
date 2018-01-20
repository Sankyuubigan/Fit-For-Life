package com.lenar.healthyfit.application.api;

import com.lenar.healthyfit.entities.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherApi {
    String apiKey = "GUFr3H7n6sVRuS100FdIpyA80NFk3HkOUuWFtHh3";
    String apiAddress="http://api.nal.usda.gov/ndb/list";
    @GET("")
    Call<WeatherResponse> getWeatlher(@Query("q") String city, @Query("appid") String appId);

    @GET("/data/2.5/weather")
    Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String appId);
}
