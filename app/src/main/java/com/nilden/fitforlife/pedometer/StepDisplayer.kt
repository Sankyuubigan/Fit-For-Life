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

import java.util.ArrayList

class StepDisplayer(internal var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

    private var mCount = 0
    private val mListeners = ArrayList<Listener>()

    init {
        notifyListener()
    }

    fun setUtils(utils: Utils) {
        mUtils = utils
    }

    fun setSteps(steps: Int) {
        mCount = steps
        notifyListener()
    }

    override fun onStep() {
        mCount++
        notifyListener()
    }

    fun reloadSettings() {
        notifyListener()
    }

    override fun passValue() {}


    //-----------------------------------------------------
    // Listener

    interface Listener {
        fun stepsChanged(value: Int)
        fun passValue()
    }

    fun addListener(l: Listener) {
        mListeners.add(l)
    }

    fun notifyListener() {
        for (listener in mListeners) {
            listener.stepsChanged(mCount)
        }
    }

    //-----------------------------------------------------
    // Speaking

    override fun speak() {
        if (mSettings.shouldTellSteps()) {
            if (mCount > 0) {
                mUtils.say("$mCount steps")
            }
        }
    }


}
