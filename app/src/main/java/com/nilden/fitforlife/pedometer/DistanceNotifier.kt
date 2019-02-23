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

class DistanceNotifier(private val mListener: Listener, internal var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

    internal var mDistance = 0f

    internal var mIsMetric: Boolean = false
    internal var mStepLength: Float = 0.toFloat()

    interface Listener {
        fun valueChanged(value: Float)
        fun passValue()
    }

    init {
        reloadSettings()
    }

    fun setDistance(distance: Float) {
        mDistance = distance
        notifyListener()
    }

    fun reloadSettings() {
        mIsMetric = mSettings.isMetric
        mStepLength = mSettings.stepLength
        notifyListener()
    }

    override fun onStep() {

        if (mIsMetric) {
            mDistance += (// kilometers
                    mStepLength // centimeters
                            / 100000.0).toFloat() // centimeters/kilometer
        } else {
            mDistance += (// miles
                    mStepLength // inches
                            / 63360.0).toFloat() // inches/mile
        }

        notifyListener()
    }

    private fun notifyListener() {
        mListener.valueChanged(mDistance)
    }

    override fun passValue() {
        // Callback of StepListener - Not implemented
    }

    override fun speak() {
        if (mSettings.shouldTellDistance()) {
            if (mDistance >= .001f) {
                mUtils.say(("" + (mDistance + 0.000001f)).substring(0, 4) + if (mIsMetric) " kilometers" else " miles")
                // TODO: format numbers (no "." at the end)
            }
        }
    }


}

