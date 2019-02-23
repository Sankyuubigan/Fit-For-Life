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

class CaloriesNotifier(private val mListener: Listener, internal var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

    private var mCalories = 0.0

    internal var mIsMetric: Boolean = false
    internal var mIsRunning: Boolean = false
    internal var mStepLength: Float = 0.toFloat()
    internal var mBodyWeight: Float = 0.toFloat()

    interface Listener {
        fun valueChanged(value: Float)
        fun passValue()
    }

    init {
        reloadSettings()
    }

    fun setCalories(calories: Float) {
        mCalories = calories.toDouble()
        notifyListener()
    }

    fun reloadSettings() {
        mIsMetric = mSettings.isMetric
        mIsRunning = mSettings.isRunning
        mStepLength = mSettings.stepLength
        mBodyWeight = mSettings.bodyWeight
        notifyListener()
    }

    fun resetValues() {
        mCalories = 0.0
    }

    fun isMetric(isMetric: Boolean) {
        mIsMetric = isMetric
    }

    fun setStepLength(stepLength: Float) {
        mStepLength = stepLength
    }

    override fun onStep() {

        if (mIsMetric) {
            mCalories += ((mBodyWeight * if (mIsRunning) METRIC_RUNNING_FACTOR else METRIC_WALKING_FACTOR
                    // Distance:
                    * mStepLength) // centimeters
                    / 100000.0) // centimeters/kilometer
        } else {
            mCalories += ((mBodyWeight * if (mIsRunning) IMPERIAL_RUNNING_FACTOR else IMPERIAL_WALKING_FACTOR
                    // Distance:
                    * mStepLength) // inches
                    / 63360.0) // inches/mile
        }

        notifyListener()
    }

    private fun notifyListener() {
        mListener.valueChanged(mCalories.toFloat())
    }

    override fun passValue() {

    }

    override fun speak() {
        if (mSettings.shouldTellCalories()) {
            if (mCalories > 0) {
                mUtils.say("" + mCalories.toInt() + " calories burned")
            }
        }

    }

    companion object {

        private val METRIC_RUNNING_FACTOR = 1.02784823
        private val IMPERIAL_RUNNING_FACTOR = 0.75031498

        private val METRIC_WALKING_FACTOR = 0.708
        private val IMPERIAL_WALKING_FACTOR = 0.517
    }


}

