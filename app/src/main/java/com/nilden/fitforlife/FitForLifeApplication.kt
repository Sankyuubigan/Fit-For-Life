package com.nilden.fitforlife

import android.app.Application
import com.nilden.fitforlife.utils.SharedPreferencesHelper

class FitForLifeApplication : Application() {

    lateinit var preferencesHelper: SharedPreferencesHelper
        private set


    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesHelper = SharedPreferencesHelper(this)
    }

    companion object {

        lateinit var instance: FitForLifeApplication
            private set
    }
}
