

package com.nilden.fitforlife.pedometer

class DistanceNotifier(private val mListener: Listener, private var mSettings: PedometerSettings, private var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

    private var mDistance = 0f

    private var mIsMetric: Boolean = false
    private var mStepLength: Float = 0.toFloat()

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

        mDistance += if (mIsMetric) {
            (// kilometers
                    mStepLength // centimeters
                            / 100000.0).toFloat() // centimeters/kilometer
        } else {
            (// miles
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

