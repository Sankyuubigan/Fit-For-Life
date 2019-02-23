package com.nilden.fitforlife.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SensorRestarterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startService(Intent(context, StepService::class.java))
    }
}