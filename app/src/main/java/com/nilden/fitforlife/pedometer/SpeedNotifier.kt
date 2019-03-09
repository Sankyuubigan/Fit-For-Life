
package com.nilden.fitforlife.pedometer

class SpeedNotifier(private val mListener: Listener, private var mSettings: PedometerSettings, private var mUtils: Utils) : PaceNotifier.Listener, SpeakingTimer.Listener {

    private var mSpeed = 0f

    private var mIsMetric: Boolean = false
    private var mStepLength: Float = 0.toFloat()

    /** Desired speed, adjusted by the user  */
    private var mDesiredSpeed: Float = 0.toFloat()

    /** Should we speak?  */
    private var mShouldTellFasterslower: Boolean = false
    private var mShouldTellSpeed: Boolean = false

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
        mSpeed = if (mIsMetric) {
            (value * mStepLength // centimeters / minute
                    / 100000f) * 60f // centimeters/kilometer
        } else {
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
                when {
                    mSpeed < mDesiredSpeed * (1 - much) -> mUtils.say("much faster!")
                    mSpeed > mDesiredSpeed * (1 + much) -> mUtils.say("much slower!")
                    mSpeed < mDesiredSpeed * (1 - normal) -> mUtils.say("faster!")
                    mSpeed > mDesiredSpeed * (1 + normal) -> mUtils.say("slower!")
                    mSpeed < mDesiredSpeed * (1 - little) -> mUtils.say("a little faster!")
                    mSpeed > mDesiredSpeed * (1 + little) -> mUtils.say("a little slower!")
                    else -> spoken = false
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

