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

class PaceNotifier(internal var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {
    private val mListeners = ArrayList<Listener>()

    internal var mCounter = 0

    private var mLastStepTime: Long = 0
    private val mLastStepDeltas = longArrayOf(-1, -1, -1, -1)
    private var mLastStepDeltasIndex = 0
    private var mPace: Long = 0

    /** Desired pace, adjusted by the user  */
    internal var mDesiredPace: Int = 0

    /** Should we speak?  */
    internal var mShouldTellFasterslower: Boolean = false

    /** When did the TTS speak last time  */
    private var mSpokenAt: Long = 0

    interface Listener {
        fun paceChanged(value: Int)
        fun passValue()
    }

    init {
        mDesiredPace = mSettings.desiredPace
        reloadSettings()
    }

    fun setPace(pace: Int) {
        mPace = pace.toLong()
        val avg = (60 * 1000.0 / mPace).toInt()
        for (i in mLastStepDeltas.indices) {
            mLastStepDeltas[i] = avg.toLong()
        }
        notifyListener()
    }

    fun reloadSettings() {
        mShouldTellFasterslower = mSettings.shouldTellFasterslower() && mSettings.maintainOption == PedometerSettings.M_PACE
        notifyListener()
    }

    fun addListener(l: Listener) {
        mListeners.add(l)
    }

    fun setDesiredPace(desiredPace: Int) {
        mDesiredPace = desiredPace
    }

    override fun onStep() {
        val thisStepTime = System.currentTimeMillis()
        mCounter++

        // Calculate pace based on last x steps
        if (mLastStepTime > 0) {
            val delta = thisStepTime - mLastStepTime

            mLastStepDeltas[mLastStepDeltasIndex] = delta
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.size

            var sum: Long = 0
            var isMeaningfull = true
            for (i in mLastStepDeltas.indices) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false
                    break
                }
                sum += mLastStepDeltas[i]
            }
            if (isMeaningfull && sum > 0) {
                val avg = sum / mLastStepDeltas.size
                mPace = 60 * 1000 / avg

                // TODO: remove duplication. This also exists in SpeedNotifier
                if (mShouldTellFasterslower && !mUtils.isSpeakingEnabled) {
                    if (thisStepTime - mSpokenAt > 3000 && !mUtils.isSpeakingNow) {
                        val little = 0.10f
                        val normal = 0.30f
                        val much = 0.50f

                        var spoken = true
                        if (mPace < mDesiredPace * (1 - much)) {
                            mUtils.say("much faster!")
                        } else if (mPace > mDesiredPace * (1 + much)) {
                            mUtils.say("much slower!")
                        } else if (mPace < mDesiredPace * (1 - normal)) {
                            mUtils.say("faster!")
                        } else if (mPace > mDesiredPace * (1 + normal)) {
                            mUtils.say("slower!")
                        } else if (mPace < mDesiredPace * (1 - little)) {
                            mUtils.say("a little faster!")
                        } else if (mPace > mDesiredPace * (1 + little)) {
                            mUtils.say("a little slower!")
                        } else {
                            spoken = false
                        }
                        if (spoken) {
                            mSpokenAt = thisStepTime
                        }
                    }
                }
            } else {
                mPace = -1
            }
        }
        mLastStepTime = thisStepTime
        notifyListener()
    }

    private fun notifyListener() {
        for (listener in mListeners) {
            listener.paceChanged(mPace.toInt())
        }
    }

    override fun passValue() {
        // Not used
    }

    //-----------------------------------------------------
    // Speaking

    override fun speak() {
        if (mSettings.shouldTellPace()) {
            if (mPace > 0) {
                mUtils.say("$mPace steps per minute")
            }
        }
    }


}

