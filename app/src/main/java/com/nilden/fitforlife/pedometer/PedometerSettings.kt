
package com.nilden.fitforlife.pedometer

import android.content.SharedPreferences

class PedometerSettings(private var mSettings: SharedPreferences) {

    val isMetric: Boolean
        get() = mSettings.getString("units", "imperial") == "metric"

    // TODO: reset value, & notify user somehow
    val stepLength: Float
        get() {
            return try {
                java.lang.Float.valueOf(mSettings.getString("step_length", "20")!!.trim { it <= ' ' })!!
            } catch (e: NumberFormatException) {
                0f
            }

        }

    // TODO: reset value, & notify user somehow
    val bodyWeight: Float
        get() {
            return try {
                java.lang.Float.valueOf(mSettings.getString("body_weight", "50")!!.trim { it <= ' ' })!!
            } catch (e: NumberFormatException) {
                0f
            }

        }

    val isRunning: Boolean
        get() = mSettings.getString("exercise_type", "running") == "running"

    val maintainOption: Int
        get() {
            val p = mSettings.getString("maintain", "none")
            return when (p) {
                "none" -> M_NONE
                "pace" -> M_PACE
                "speed" -> M_SPEED
                else -> 0
            }
        }

    //-------------------------------------------------------------------
    // Desired pace & speed:
    // these can not be set in the preference activity, only on the main
    // screen if "maintain" is set to "pace" or "speed"

    // steps/minute
    val desiredPace: Int
        get() = mSettings.getInt("desired_pace", 180)
    // km/h or mph
    val desiredSpeed: Float
        get() = mSettings.getFloat("desired_speed", 4f)
    // This could not happen as the value is selected from a list.
    val speakingInterval: Float
        get() {
            return try {
                java.lang.Float.valueOf(mSettings.getString("speaking_interval", "1"))!!
            } catch (e: NumberFormatException) {
                1f
            }

        }

    val isServiceRunning: Boolean
        get() = mSettings.getBoolean("service_running", false)

    fun savePaceOrSpeedSetting(maintain: Int, desiredPaceOrSpeed: Float) {
        val editor = mSettings.edit()
        if (maintain == M_PACE) {
            editor.putInt("desired_pace", desiredPaceOrSpeed.toInt())
        } else if (maintain == M_SPEED) {
            editor.putFloat("desired_speed", desiredPaceOrSpeed)
        }
        editor.apply()
    }

    //-------------------------------------------------------------------
    // Speaking:

    fun shouldSpeak(): Boolean {
        return mSettings.getBoolean("speak", false)
    }

    fun shouldTellSteps(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_steps", false)
    }

    fun shouldTellPace(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_pace", false)
    }

    fun shouldTellDistance(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_distance", false)
    }

    fun shouldTellSpeed(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_speed", false)
    }

    fun shouldTellCalories(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_calories", false)
    }

    fun shouldTellFasterslower(): Boolean {
        return mSettings.getBoolean("speak", false) && mSettings.getBoolean("tell_fasterslower", false)
    }

    fun wakeAggressively(): Boolean {
        return mSettings.getString("operation_level", "run_in_background") == "wake_up"
    }

    fun keepScreenOn(): Boolean {
        return mSettings.getString("operation_level", "run_in_background") == "keep_screen_on"
    }

    //
    // Internal

    fun saveServiceRunningWithTimestamp(running: Boolean) {
        val editor = mSettings.edit()
        editor.putBoolean("service_running", running)
        editor.putLong("last_seen", Utils.currentTimeInMillis())
        editor.apply()
    }

    fun saveServiceRunningWithNullTimestamp(running: Boolean) {
        val editor = mSettings.edit()
        editor.putBoolean("service_running", running)
        editor.putLong("last_seen", 0)
        editor.apply()
    }

    fun clearServiceRunning() {
        val editor = mSettings.edit()
        editor.putBoolean("service_running", false)
        editor.putLong("last_seen", 0)
        editor.apply()
    }

    companion object {

        var M_NONE = 1
        var M_PACE = 2
        var M_SPEED = 3
    }

}
