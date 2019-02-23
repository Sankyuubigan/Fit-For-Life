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

class SpeedNotifier(private val mListener: Listener, internal var mSettings: PedometerSettings, internal var mUtils: Utils) : PaceNotifier.Listener, SpeakingTimer.Listener {

    internal var mCounter = 0
    internal var mSpeed = 0f

    internal var mIsMetric: Boolean = false
    internal var mStepLength: Float = 0.toFloat()

    /** Desired speed, adjusted by the user  */
    internal var mDesiredSpeed: Float = 0.toFloat()

    /** Should we speak?  */
    internal var mShouldTellFasterslower: Boolean = false
    internal var mShouldTellSpeed: Boolean = false

    /** When did the TTS speak last time  */
    private var mSpokenAt: Long = 0

    interface Listener {
        fun valueChanged(value: Float)
        fun passValue()
    }

    init {
        mDesiredSpeed = mSettings.desiredSpeed
        reloadSettings()
    }

    fun setSpeed(speed: Float) {
        mSpeed = speed
        notifyListener()
    }

    fun reloadSettings() {
        mIsMetric = mSettings.isMetric
        mStepLength = mSettings.stepLength
        mShouldTellSpeed = mSettings.shouldTellSpeed()
        mShouldTellFasterslower = mSettings.shouldTellFasterslower() && mSettings.maintainOption == PedometerSettings.M_SPEED
        notifyListener()
    }

    fun setDesiredSpeed(desiredSpeed: Float) {
        mDesiredSpeed = desiredSpeed
    }

    private fun notifyListener() {
        mListener.valueChanged(mSpeed)
    }

    override fun paceChanged(value: Int) {
        if (mIsMetric) {
            mSpeed = // kilometers / hour
                    (value * mStepLength // centimeters / minute
                            / 100000f) * 60f // centimeters/kilometer
        } else {
            mSpeed = // miles / hour
                    (value * mStepLength // inches / minute
                            / 63360f) * 60f // inches/mile
        }
        tellFasterSlower()
        notifyListener()
    }

    /**
     * Say slower/faster, if needed.
     */
    private fun tellFasterSlower() {
        if (mShouldTellFasterslower && mUtils.isSpeakingEnabled) {
            val now = System.currentTimeMillis()
            if (now - mSpokenAt > 3000 && !mUtils.isSpeakingNow) {
                val little = 0.10f
                val normal = 0.30f
                val much = 0.50f

                var spoken = true
                if (mSpeed < mDesiredSpeed * (1 - much)) {
                    mUtils.say("much faster!")
                } else if (mSpeed > mDesiredSpeed * (1 + much)) {
                    mUtils.say("much slower!")
                } else if (mSpeed < mDesiredSpeed * (1 - normal)) {
                    mUtils.say("faster!")
                } else if (mSpeed > mDesiredSpeed * (1 + normal)) {
                    mUtils.say("slower!")
                } else if (mSpeed < mDesiredSpeed * (1 - little)) {
                    mUtils.say("a little faster!")
                } else if (mSpeed > mDesiredSpeed * (1 + little)) {
                    mUtils.say("a little slower!")
                } else {
                    spoken = false
                }
                if (spoken) {
                    mSpokenAt = now
                }
            }
        }
    }

    override fun passValue() {
        // Not used
    }

    override fun speak() {
        if (mSettings.shouldTellSpeed()) {
            if (mSpeed >= .01f) {
                mUtils.say(("" + (mSpeed + 0.000001f)).substring(0, 4) + if (mIsMetric) " kilometers per hour" else " miles per hour")
            }
        }

    }

}

