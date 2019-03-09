
package com.nilden.fitforlife.pedometer

import java.util.*

class SpeakingTimer(private var mSettings: PedometerSettings, private var mUtils: Utils) : StepListener {
    private var mShouldSpeak: Boolean = false
    private var mInterval: Float = 0.toFloat()
    private var mLastSpeakTime: Long = 0
    private val mListeners = ArrayList<Listener>()

    //-----------------------------------------------------
    // Speaking

    init {
        mLastSpeakTime = System.currentTimeMillis()
        reloadSettings()
    }

    fun reloadSettings() {
        mShouldSpeak = mSettings.shouldSpeak()
        mInterval = mSettings.speakingInterval
    }

    override fun onStep() {
        val now = System.currentTimeMillis()
        val delta = now - mLastSpeakTime

        if (delta / 60000.0 >= mInterval) {
            mLastSpeakTime = now
            notifyListeners()
        }
    }

    override fun passValue() {
        // not used
    }


    //-----------------------------------------------------
    // Listener

    interface Listener {
        fun speak()
    }

    fun addListener(l: Listener) {
        mListeners.add(l)
    }

    private fun notifyListeners() {
        mUtils.ding()
        for (listener in mListeners) {
            listener.speak()
        }
    }
}

