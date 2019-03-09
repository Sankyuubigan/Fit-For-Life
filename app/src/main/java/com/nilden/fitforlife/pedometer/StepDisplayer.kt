
package com.nilden.fitforlife.pedometer

import java.util.*

class StepDisplayer(private var mSettings: PedometerSettings, internal var mUtils: Utils) : StepListener, SpeakingTimer.Listener {

    private var mCount = 0
    private val mListeners = ArrayList<Listener>()

    init {
        notifyListener()
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
