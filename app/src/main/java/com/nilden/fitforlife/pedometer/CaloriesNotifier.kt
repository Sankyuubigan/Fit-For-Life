
package com.nilden.fitforlife.pedometer

class CaloriesNotifier(private val mListener: Listener, private var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

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

    override fun onStep() {

        mCalories += if (mIsMetric) {
            ((mBodyWeight * if (mIsRunning) METRIC_RUNNING_FACTOR else METRIC_WALKING_FACTOR
                    // Distance:
                    * mStepLength) // centimeters
                    / 100000.0) // centimeters/kilometer
        } else {
            ((mBodyWeight * if (mIsRunning) IMPERIAL_RUNNING_FACTOR else IMPERIAL_WALKING_FACTOR
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

        private const val METRIC_RUNNING_FACTOR = 1.02784823
        private const val IMPERIAL_RUNNING_FACTOR = 0.75031498

        private const val METRIC_WALKING_FACTOR = 0.708
        private const val IMPERIAL_WALKING_FACTOR = 0.517
    }


}

