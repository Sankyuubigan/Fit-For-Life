/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nilden.fitforlife.pedometer

import java.util.*

class SpeakingTimer(internal var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener {
    internal var mShouldSpeak: Boolean = false
    internal var mInterval: Float = 0.toFloat()
    internal var mLastSpeakTime: Long = 0
    private val mListeners = ArrayList<Listener>()

    //-----------------------------------------------------
    // Speaking

    val isSpeaking: Boolean
        get() = mUtils.isSpeakingNow

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

    fun notifyListeners() {
        mUtils.ding()
        for (listener in mListeners) {
            listener.speak()
        }
    }
}

