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

package com.nilden.fitforlife.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log

import com.google.firebase.database.FirebaseDatabase
import com.nilden.fitforlife.R
import com.nilden.fitforlife.pedometer.CaloriesNotifier
import com.nilden.fitforlife.pedometer.DistanceNotifier
import com.nilden.fitforlife.pedometer.PaceNotifier
import com.nilden.fitforlife.pedometer.PedometerSettings
import com.nilden.fitforlife.pedometer.SpeakingTimer
import com.nilden.fitforlife.pedometer.SpeedNotifier
import com.nilden.fitforlife.pedometer.StepDetector
import com.nilden.fitforlife.pedometer.StepDisplayer
import com.nilden.fitforlife.pedometer.Utils
import com.nilden.fitforlife.screens.PedometerActivity

import java.util.Calendar

class StepService : Service() {
    private var mSettings: SharedPreferences? = null
    private var mPedometerSettings: PedometerSettings? = null
    private var mState: SharedPreferences? = null
    private var mStateEditor: SharedPreferences.Editor? = null
    private var mUtils: Utils? = null
    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var mStepDetector: StepDetector? = null
    // private StepBuzzer mStepBuzzer; // used for debugging
    private var mStepDisplayer: StepDisplayer? = null
    private var mPaceNotifier: PaceNotifier? = null
    private var mDistanceNotifier: DistanceNotifier? = null
    private var mSpeedNotifier: SpeedNotifier? = null
    private var mCaloriesNotifier: CaloriesNotifier? = null
    private var mSpeakingTimer: SpeakingTimer? = null

    private var wakeLock: PowerManager.WakeLock? = null
    private var mNM: NotificationManager? = null

    internal val date = DateFormat.format("dd MMMM", Calendar.getInstance()).toString()
    private var mSteps: Int = 0
    private var mPace: Int = 0
    private var mDistance: Float = 0.toFloat()
    private var mSpeed: Float = 0.toFloat()
    private var mCalories: Float = 0.toFloat()

    /**
     * Receives messages from activity.
     */
    private val mBinder = StepBinder()

    private var mCallback: ICallback? = null

    private var mDesiredPace: Int = 0
    private var mDesiredSpeed: Float = 0.toFloat()

    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private val mStepListener = object : StepDisplayer.Listener {
        override fun stepsChanged(value: Int) {
            mSteps = value
            passValue()
        }

        override fun passValue() {
            if (mCallback != null) {
                mCallback!!.stepsChanged(mSteps)
                Log.d(TAG, "stepsChanged=$mSteps")
            }
        }
    }
    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private val mPaceListener = object : PaceNotifier.Listener {
        override fun paceChanged(value: Int) {
            mPace = value
            passValue()
        }

        override fun passValue() {
            if (mCallback != null) {
                mCallback!!.paceChanged(mPace)
            }
        }
    }
    /**
     * Forwards distance values from DistanceNotifier to the activity.
     */
    private val mDistanceListener = object : DistanceNotifier.Listener {
        override fun valueChanged(value: Float) {
            mDistance = value
            passValue()
        }

        override fun passValue() {
            if (mCallback != null) {
                mCallback!!.distanceChanged(mDistance)
            }
        }
    }
    /**
     * Forwards speed values from SpeedNotifier to the activity.
     */
    private val mSpeedListener = object : SpeedNotifier.Listener {
        override fun valueChanged(value: Float) {
            mSpeed = value
            passValue()
        }

        override fun passValue() {
            if (mCallback != null) {
                mCallback!!.speedChanged(mSpeed)
            }
        }
    }
    /**
     * Forwards calories values from CaloriesNotifier to the activity.
     */
    private val mCaloriesListener = object : CaloriesNotifier.Listener {
        override fun valueChanged(value: Float) {
            mCalories = value
            passValue()
        }

        override fun passValue() {
            if (mCallback != null) {
                mCallback!!.caloriesChanged(mCalories)
            }
        }
    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Check action just to be on the safe side.
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                // Unregisters the listener and registers it again.
                this@StepService.unregisterDetector()
                this@StepService.registerDetector()
                if (mPedometerSettings!!.wakeAggressively()) {
                    wakeLock!!.release()
                    acquireWakeLock()
                }
            }
        }
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    inner class StepBinder : Binder() {
        val service: StepService
            get() = this@StepService
    }

    override fun onCreate() {
        Log.i(TAG, "[SERVICE] onCreate")
        super.onCreate()

        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showNotification()

        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this)
        mPedometerSettings = PedometerSettings(mSettings as SharedPreferences)
        mState = getSharedPreferences("state", 0)

        mUtils = Utils.getInstance()
        mUtils!!.setService(this)
        mUtils!!.initTTS()

        acquireWakeLock()

        // Start detecting
        mStepDetector = StepDetector()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        registerDetector()

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)

        mStepDisplayer = StepDisplayer(mPedometerSettings!!, mUtils!!)
        mSteps = mState!!.getInt("steps", 0)
        mStepDisplayer!!.setSteps(mSteps)
        mStepDisplayer!!.addListener(mStepListener)
        mStepDetector!!.addStepListener(mStepDisplayer!!)

        mPaceNotifier = PaceNotifier(mPedometerSettings!!, mUtils!!)
        mPace = mState!!.getInt("pace", 0)
        mPaceNotifier!!.setPace(mPace)
        mPaceNotifier!!.addListener(mPaceListener)
        mStepDetector!!.addStepListener(mPaceNotifier!!)

        mDistanceNotifier = DistanceNotifier(mDistanceListener, mPedometerSettings!!, mUtils!!)
        mDistance = mState!!.getFloat("distance", 0f)
        mDistanceNotifier!!.setDistance(mDistance)
        mStepDetector!!.addStepListener(mDistanceNotifier!!)

        mSpeedNotifier = SpeedNotifier(mSpeedListener, mPedometerSettings!!, mUtils!!)
        mSpeed = mState!!.getFloat("speed", 0f)
        mSpeedNotifier!!.setSpeed(mSpeed)
        mPaceNotifier!!.addListener(mSpeedNotifier!!)

        mCaloriesNotifier = CaloriesNotifier(mCaloriesListener, mPedometerSettings!!, mUtils!!)
        mCalories = mState!!.getFloat("calories", 0f)
        mCaloriesNotifier!!.setCalories(mCalories)
        mStepDetector!!.addStepListener(mCaloriesNotifier!!)

        mSpeakingTimer = SpeakingTimer(mPedometerSettings!!, mUtils!!)
        mSpeakingTimer!!.addListener(mStepDisplayer!!)
        mSpeakingTimer!!.addListener(mPaceNotifier!!)
        mSpeakingTimer!!.addListener(mDistanceNotifier!!)
        mSpeakingTimer!!.addListener(mSpeedNotifier!!)
        mSpeakingTimer!!.addListener(mCaloriesNotifier!!)
        mStepDetector!!.addStepListener(mSpeakingTimer!!)

        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings()

        // Tell the user we started.
        //        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
        val last_date = mSettings!!.getString("last_date", "")
        if (!last_date!!.isEmpty() && date != last_date)
            sendToFireBase(last_date)
    }

    override fun onStart(intent: Intent, startId: Int) {
        Log.i(TAG, "[SERVICE] onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy")
        mUtils!!.shutdownTTS()

        // Unregister our receiver.
        unregisterReceiver(mReceiver)
        unregisterDetector()

        mStateEditor = mState!!.edit()
        mStateEditor!!.putInt("steps", mSteps)
        mStateEditor!!.putInt("pace", mPace)
        mStateEditor!!.putFloat("distance", mDistance)
        mStateEditor!!.putFloat("speed", mSpeed)
        mStateEditor!!.putFloat("calories", mCalories)
        mStateEditor!!.apply()

        mSettings!!.edit().putString("last_date", date).apply()

        mNM!!.cancel(R.string.app_name)

        wakeLock!!.release()

        super.onDestroy()
        // Stop detecting
        mSensorManager!!.unregisterListener(mStepDetector)

        // Tell the user we stopped.
        //        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_LONG).show();
        val broadcastIntent = Intent("com.android.ServiceStopped")
        sendBroadcast(broadcastIntent)
    }

    fun sendToFireBase(last_date: String) {
        val androidID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        FirebaseDatabase.getInstance().getReference("users_history").child(androidID).child(last_date).child("calories_burned").child(mSteps.toString()).setValue(mCalories.toString())
        resetValues()
    }

    private fun registerDetector() {
        mSensor = mSensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/)
        mSensorManager!!.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun unregisterDetector() {
        mSensorManager!!.unregisterListener(mStepDetector)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "[SERVICE] onBind")
        return mBinder
    }

    interface ICallback {
        fun stepsChanged(value: Int)

        fun paceChanged(value: Int)

        fun distanceChanged(value: Float)

        fun speedChanged(value: Float)

        fun caloriesChanged(value: Float)
    }

    fun registerCallback(cb: ICallback) {
        mCallback = cb
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }

    /**
     * Called by activity to pass the desired pace value,
     * whenever it is modified by the user.
     *
     * @param desiredPace
     */
    fun setDesiredPace(desiredPace: Int) {
        mDesiredPace = desiredPace
        if (mPaceNotifier != null) {
            mPaceNotifier!!.setDesiredPace(mDesiredPace)
        }
    }

    /**
     * Called by activity to pass the desired speed value,
     * whenever it is modified by the user.
     *
     * @param desiredSpeed
     */
    fun setDesiredSpeed(desiredSpeed: Float) {
        mDesiredSpeed = desiredSpeed
        if (mSpeedNotifier != null) {
            mSpeedNotifier!!.setDesiredSpeed(mDesiredSpeed)
        }
    }

    fun reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this)

        if (mStepDetector != null) {
            mStepDetector!!.setSensitivity(java.lang.Float.valueOf(mSettings!!.getString("sensitivity", "13"))!!)
        }

        if (mStepDisplayer != null) mStepDisplayer!!.reloadSettings()
        if (mPaceNotifier != null) mPaceNotifier!!.reloadSettings()
        if (mDistanceNotifier != null) mDistanceNotifier!!.reloadSettings()
        if (mSpeedNotifier != null) mSpeedNotifier!!.reloadSettings()
        if (mCaloriesNotifier != null) mCaloriesNotifier!!.reloadSettings()
        if (mSpeakingTimer != null) mSpeakingTimer!!.reloadSettings()
    }

    fun resetValues() {
        mStepDisplayer!!.setSteps(0)
        mPaceNotifier!!.setPace(0)
        mDistanceNotifier!!.setDistance(0f)
        mSpeedNotifier!!.setSpeed(0f)
        mCaloriesNotifier!!.setCalories(0f)
    }

    /**
     * Show a notification while this service is running.
     */
    private fun showNotification() {
        val text = getText(R.string.app_name)
        //        Notification notification = new Notification(R.drawable.ic_notification, null,
        //                System.currentTimeMillis());
        //        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        val pedometerIntent = Intent()
        pedometerIntent.component = ComponentName(this, PedometerActivity::class.java)
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0)
        //        notification.contentIntent=contentIntent;
        //        notification.tickerText=text;


        val builder = Notification.Builder(this)

        builder.setAutoCancel(false)
        builder.setTicker(getString(R.string.started))
        builder.setContentTitle(getText(R.string.notification_subtitle))
        builder.setContentText(text)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentIntent(contentIntent)
        builder.setOngoing(true)
        builder.setWhen(System.currentTimeMillis())
        builder.setSubText("Нажмите, чтобы открыть подробную информацию")   //API level 16
        //        builder.setNumber(100);
        builder.build()

        val myNotication = builder.notification
        //        manager.notify(11, myNotication);
        myNotication.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT

        //        notification.setLatestEventInfo(this, text, getText(R.string.notification_subtitle), contentIntent);
        //        startForeground(R.string.app_name, myNotication);
        mNM!!.notify(R.string.app_name, myNotication)
        //        mNM.notify();
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeFlags: Int
        if (mPedometerSettings!!.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP
        } else if (mPedometerSettings!!.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK
        } else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG)
        wakeLock!!.acquire()
    }

    companion object {
        private val TAG = "myapp:myQ"
    }

}

