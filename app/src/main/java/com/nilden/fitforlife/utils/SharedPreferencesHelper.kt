package com.nilden.fitforlife.utils

import android.app.Application
import android.content.SharedPreferences

import com.nilden.fitforlife.FitForLifeApplication

import android.content.Context.MODE_PRIVATE

class SharedPreferencesHelper(application: FitForLifeApplication) {

    private val mApplication: Application

    private val sPrefs: SharedPreferences
        get() = mApplication.getSharedPreferences(SETTING_PREFERENCES_FILE, MODE_PRIVATE)

    init {
        mApplication = application
    }

    fun saveBoolean(key: String, value: Boolean) {
        sPrefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sPrefs.getBoolean(key, DEFAULT_BOOLEAN)
    }

    fun saveString(key: String, value: String) {
        sPrefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return sPrefs.getString(key, DEFAULT_STRING)
    }

    fun saveInt(key: String, value: Int) {
        sPrefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sPrefs.getInt(key, DEFAULT_INT)
    }

    companion object {

        private val SETTING_PREFERENCES_FILE = "com.alekzunder.weatherapp.settings"

        val KEY_KEEP_SCREEN_ON = "com.alekzunder.weatherapp.keep_screen_on"
        val KEY_UPDATING_TIME = "com.alekzunder.weatherapp.updating_time"
        val KEY_UPDATE_AUTOMATICALLY = "com.alekzunder.weatherapp.update_automatically"

        private val DEFAULT_STRING = ""
        private val DEFAULT_INT = 30
        private val DEFAULT_BOOLEAN = false
    }
}
