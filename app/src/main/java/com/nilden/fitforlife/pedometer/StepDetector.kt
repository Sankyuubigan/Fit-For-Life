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

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.*

class StepDetector : SensorEventListener {
    private var mLimit = 10f
    private val mLastValues = FloatArray(3 * 2)
    private val mScale = FloatArray(2)
    private val mYOffset: Float

    private val mLastDirections = FloatArray(3 * 2)
    private val mLastExtremes = arrayOf(FloatArray(3 * 2), FloatArray(3 * 2))
    private val mLastDiff = FloatArray(3 * 2)
    private var mLastMatch = -1

    private val mStepListeners = ArrayList<StepListener>()

    init {
        val h = 480 // TODO: remove this constant
        mYOffset = h * 0.5f
        mScale[0] = -(h.toFloat() * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)))
        mScale[1] = -(h.toFloat() * 0.5f * (1.0f / SensorManager.MAGNETIC_FIELD_EARTH_MAX))
    }

    fun setSensitivity(sensitivity: Float) {
        mLimit = sensitivity // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    }

    fun addStepListener(sl: StepListener) {
        mStepListeners.add(sl)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensor = event.sensor
        synchronized(this) {
            if (sensor.type != Sensor.TYPE_ORIENTATION) {
                val j = if (sensor.type == Sensor.TYPE_ACCELEROMETER) 1 else 0
                if (j == 1) {
                    var vSum = 0f
                    for (i in 0..2) {
                        val v = mYOffset + event.values[i] * mScale[j]
                        vSum += v
                    }
                    val k = 0
                    val v = vSum / 3

                    val direction = (if (v > mLastValues[k]) 1 else if (v < mLastValues[k]) -1 else 0).toFloat()
                    if (direction == -mLastDirections[k]) {
                        // Direction changed
                        val extType = if (direction > 0) 0 else 1 // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k]
                        val diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k])

                        if (diff > mLimit) {

                            val isAlmostAsLargeAsPrevious = diff > mLastDiff[k] * 2 / 3
                            val isPreviousLargeEnough = mLastDiff[k] > diff / 3
                            val isNotContra = mLastMatch != 1 - extType

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                Log.i(TAG, "step")
                                for (stepListener in mStepListeners) {
                                    stepListener.onStep()
                                }
                                mLastMatch = extType
                            } else {
                                mLastMatch = -1
                            }
                        }
                        mLastDiff[k] = diff
                    }
                    mLastDirections[k] = direction
                    mLastValues[k] = v
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    companion object {
        private val TAG = "StepDetector"
    }

}