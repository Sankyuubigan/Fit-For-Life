package com.lenar.healthyfit.application.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static final String BASE_URL = "https://api.openweathermap.org";
    private OpenWeatherApi mOpenWeatherApi;

    private GsonConverterFactory createGsonFactory() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return GsonConverterFactory.create(gson);
    }

    private HttpLoggingInterceptor createHttpInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(createHttpInterceptor())
                .build();
    }

    private OpenWeatherApi createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(createOkHttpClient())
                .addConverterFactory(createGsonFactory())
                .build();
        return retrofit.create(OpenWeatherApi.class);
    }


    public void initialize() {
        mOpenWeatherApi = createApiService();
    }

    public OpenWeatherApi getInstance() {
        return mOpenWeatherApi;
    }
}
