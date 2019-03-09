
package com.nilden.fitforlife.pedometer

import java.util.*

class PaceNotifier(private var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {
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
                        when {
                            mPace < mDesiredPace * (1 - much) -> mUtils.say("much faster!")
                            mPace > mDesiredPace * (1 + much) -> mUtils.say("much slower!")
                            mPace < mDesiredPace * (1 - normal) -> mUtils.say("faster!")
                            mPace > mDesiredPace * (1 + normal) -> mUtils.say("slower!")
                            mPace < mDesiredPace * (1 - little) -> mUtils.say("a little faster!")
                            mPace > mDesiredPace * (1 + little) -> mUtils.say("a little slower!")
                            else -> spoken = false
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

