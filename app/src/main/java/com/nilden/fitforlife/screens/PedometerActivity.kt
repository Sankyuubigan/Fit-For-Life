package com.nilden.fitforlife.screens

import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.nilden.fitforlife.R
import com.nilden.fitforlife.pedometer.PedometerSettings
import com.nilden.fitforlife.pedometer.Settings
import com.nilden.fitforlife.pedometer.Utils
import com.nilden.fitforlife.services.StepService
import kotlinx.android.synthetic.main.fragment_pedometer.*

class PedometerActivity : Activity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mPedometerSettings: PedometerSettings
    private lateinit var mUtils: Utils
    private var mStepValue: Int = 0
    private var mPaceValue: Int = 0
    private var mDistanceValue: Float = 0.toFloat()
    private var mSpeedValue: Float = 0.toFloat()
    private var mCaloriesValue: Int = 0
    private var mDesiredPaceOrSpeed: Float = 0.toFloat()
    private var mMaintain: Int = 0
    private var mIsMetric: Boolean = false
    private var mMaintainInc: Float = 0.toFloat()
    private var mQuitting = false // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy


    /**
     * True, when service is running.
     */
    private var mIsRunning: Boolean = false

    private var mService: StepService? = null

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = (service as StepService.StepBinder).service

            mService?.registerCallback(mCallback)

            mService?.reloadSettings()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
        }
    }

    // TODO: unite all into 1 type of message
    private val mCallback = object : StepService.ICallback {
        override fun stepsChanged(value: Int) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0))
        }

        override fun paceChanged(value: Int) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0))
        }

        override fun distanceChanged(value: Float) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (value * 1000).toInt(), 0))
        }

        override fun speedChanged(value: Float) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (value * 1000).toInt(), 0))
        }

        override fun caloriesChanged(value: Float) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, value.toInt(), 0))
        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                STEPS_MSG -> {
                    mStepValue = msg.arg1
                    step_value.text = "" + mStepValue
                }
                PACE_MSG -> {
                    mPaceValue = msg.arg1
                    if (mPaceValue <= 0) {
                        pace_value.text = "0"
                    } else {
                        pace_value.text = "" + mPaceValue
                    }
                }
                DISTANCE_MSG -> {
                    mDistanceValue = msg.arg1 / 1000f
                    if (mDistanceValue <= 0) {
                        distance_value.text = "0"
                    } else {
                        distance_value.text = ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                    }
                }
                SPEED_MSG -> {
                    mSpeedValue = msg.arg1 / 1000f
                    if (mSpeedValue <= 0) {
                        speed_value.text = "0"
                    } else {
                        speed_value.text = ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                    }
                }
                CALORIES_MSG -> {
                    mCaloriesValue = msg.arg1
                    if (mCaloriesValue <= 0) {
                        calories_value.text = "0"
                    } else {
                        calories_value.text = "" + mCaloriesValue
                    }
                }
                else -> super.handleMessage(msg)
            }
        }

    }

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "[ACTIVITY] onCreate")
        super.onCreate(savedInstanceState)

        mStepValue = 0
        mPaceValue = 0

        setContentView(R.layout.fragment_pedometer)
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        mUtils = Utils.getInstance()
    }

    override fun onStart() {
        Log.i(TAG, "[ACTIVITY] onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.i(TAG, "[ACTIVITY] onResume")
        super.onResume()

        mSettings = PreferenceManager.getDefaultSharedPreferences(this)
        mSettings.edit().putString("units", "metric").apply()
        mPedometerSettings = PedometerSettings(mSettings)

        mUtils.setSpeak(mSettings.getBoolean("speak", false))

        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning

        // Start the service if this is considered to be an application start (last onPause was long ago)
        //        if (!mIsRunning)
        startStepService()
        bindStepService()


        mPedometerSettings.clearServiceRunning()

        mIsMetric = mPedometerSettings.isMetric
        (findViewById<View>(R.id.distance_units) as TextView).text = getString(
                if (mIsMetric)
                    R.string.kilometers
                else
                    R.string.miles
        )
        (findViewById<View>(R.id.speed_units) as TextView).text = getString(
                if (mIsMetric)
                    R.string.kilometers_per_hour
                else
                    R.string.miles_per_hour
        )

        mMaintain = mPedometerSettings.maintainOption
        (this.findViewById<View>(R.id.desired_pace_control) as LinearLayout).visibility = if (mMaintain != PedometerSettings.M_NONE)
            View.VISIBLE
        else
            View.GONE
        if (mMaintain == PedometerSettings.M_PACE) {
            mMaintainInc = 5f
            mDesiredPaceOrSpeed = mPedometerSettings.desiredPace.toFloat()
        } else if (mMaintain == PedometerSettings.M_SPEED) {
            mDesiredPaceOrSpeed = mPedometerSettings.desiredSpeed
            mMaintainInc = 0.1f
        }
        val button1 = findViewById<View>(R.id.button_desired_pace_lower) as Button
        button1.setOnClickListener {
            mDesiredPaceOrSpeed -= mMaintainInc
            mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f
            displayDesiredPaceOrSpeed()
            setDesiredPaceOrSpeed(mDesiredPaceOrSpeed)
        }
        val button2 = findViewById<View>(R.id.button_desired_pace_raise) as Button
        button2.setOnClickListener {
            mDesiredPaceOrSpeed += mMaintainInc
            mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f
            displayDesiredPaceOrSpeed()
            setDesiredPaceOrSpeed(mDesiredPaceOrSpeed)
        }
        if (mMaintain != PedometerSettings.M_NONE) {
            (findViewById<View>(R.id.desired_pace_label) as TextView).setText(
                    if (mMaintain == PedometerSettings.M_PACE)
                        R.string.desired_pace
                    else
                        R.string.desired_speed
            )
        }


        displayDesiredPaceOrSpeed()
    }

    private fun displayDesiredPaceOrSpeed() {
        if (mMaintain == PedometerSettings.M_PACE) {
            desired_pace_value.text = "" + mDesiredPaceOrSpeed.toInt()
        } else {
            desired_pace_value.text = "" + mDesiredPaceOrSpeed
        }
    }

    override fun onPause() {
        Log.i(TAG, "[ACTIVITY] onPause")
        if (mIsRunning) {
            unbindStepService()
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning)
        } else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning)
        }

        super.onPause()
        savePaceSetting()
    }

    override fun onStop() {
        Log.i(TAG, "[ACTIVITY] onStop")
        stopStepService()
        super.onStop()
    }

    override fun onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "[ACTIVITY] onRestart")
        super.onDestroy()
    }

    private fun setDesiredPaceOrSpeed(desiredPaceOrSpeed: Float) {
        if (mService != null) {
            if (mMaintain == PedometerSettings.M_PACE) {
                mService!!.setDesiredPace(desiredPaceOrSpeed.toInt())
            } else if (mMaintain == PedometerSettings.M_SPEED) {
                mService!!.setDesiredSpeed(desiredPaceOrSpeed)
            }
        }
    }

    private fun savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed)
    }


    private fun startStepService() {
        mIsRunning = true
        if (!isMyServiceRunning(StepService::class.java)) {
            startService(Intent(this@PedometerActivity, StepService::class.java))
            Log.i(TAG, "[SERVICE] Start")
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.d(TAG, "isMyServiceRunning = " + true + "")
                return true
            }
        }
        Log.d(TAG, "isMyServiceRunning = " + false + "")
        return false
    }

    private fun bindStepService() {
        Log.i(TAG, "[SERVICE] Bind")
        bindService(Intent(this@PedometerActivity,
                StepService::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind")
        unbindService(mConnection)
    }

    private fun stopStepService() {
        Log.i(TAG, "[SERVICE] Stop")
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService")
            stopService(Intent(this@PedometerActivity, StepService::class.java))
        }
        mIsRunning = false
    }

    private fun resetValues(updateDisplay: Boolean) {
        if (mService != null && mIsRunning) {
            mService!!.resetValues()
        } else {
            step_value.text = "0"
            pace_value.text = "0"
            distance_value.text = "0"
            speed_value.text = "0"
            calories_value.text = "0"
            val state = getSharedPreferences("state", 0)
            val stateEditor = state.edit()
            if (updateDisplay) {
                stateEditor.putInt("steps", 0)
                stateEditor.putInt("pace", 0)
                stateEditor.putFloat("distance", 0f)
                stateEditor.putFloat("speed", 0f)
                stateEditor.putFloat("calories", 0f)
                stateEditor.apply()
            }
        }
    }

    /* Creates the menu items */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
                    .setIcon(android.R.drawable.ic_media_pause)
                    .setShortcut('1', 'p')
        } else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
                    .setIcon(android.R.drawable.ic_media_play)
                    .setShortcut('1', 'p')
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setShortcut('2', 'r')
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setShortcut('8', 's').intent = Intent(this, Settings::class.java)
        menu.add(0, MENU_QUIT, 0, R.string.quit)
                .setIcon(android.R.drawable.ic_lock_power_off)
                .setShortcut('9', 'q')
        return true
    }

    /* Handles item selections */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_PAUSE -> {
                unbindStepService()
                stopStepService()
                return true
            }
            MENU_RESUME -> {
                startStepService()
                bindStepService()
                return true
            }
            MENU_RESET -> {
                resetValues(true)
                return true
            }
            MENU_QUIT -> {
                resetValues(false)
                unbindStepService()
                stopStepService()
                mQuitting = true
                finish()
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "myQ"

        private const val MENU_SETTINGS = 8
        private const val MENU_QUIT = 9

        private const val MENU_PAUSE = 1
        private const val MENU_RESUME = 2
        private const val MENU_RESET = 3

        private const val STEPS_MSG = 1
        private const val PACE_MSG = 2
        private const val DISTANCE_MSG = 3
        private const val SPEED_MSG = 4
        private const val CALORIES_MSG = 5
    }
}