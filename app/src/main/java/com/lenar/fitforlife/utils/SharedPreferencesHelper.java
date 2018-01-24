package com.lenar.fitforlife.utils;

import android.app.Application;
import android.content.SharedPreferences;

import com.lenar.fitforlife.FitForLifeApplication;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {

    private static final String SETTING_PREFERENCES_FILE = "com.alekzunder.weatherapp.settings";

    public static final String KEY_KEEP_SCREEN_ON = "com.alekzunder.weatherapp.keep_screen_on";
    public static final String KEY_UPDATING_TIME = "com.alekzunder.weatherapp.updating_time";
    public static final String KEY_UPDATE_AUTOMATICALLY = "com.alekzunder.weatherapp.update_automatically";

    private static final String DEFAULT_STRING = "";
    private static final int DEFAULT_INT = 30;
    private static final boolean DEFAULT_BOOLEAN = false;

    private Application mApplication;

    public SharedPreferencesHelper(FitForLifeApplication application) {
        mApplication = application;
    }

    private SharedPreferences getSPrefs() {
        return mApplication.getSharedPreferences(SETTING_PREFERENCES_FILE, MODE_PRIVATE);
    }

    public void saveBoolean(String key, boolean value) {
        getSPrefs().edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return getSPrefs().getBoolean(key, DEFAULT_BOOLEAN);
    }

    public void saveString(String key, String value) {
        getSPrefs().edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return getSPrefs().getString(key, DEFAULT_STRING);
    }

    public void saveInt(String key, int value) {
        getSPrefs().edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return getSPrefs().getInt(key, DEFAULT_INT);
    }
}
