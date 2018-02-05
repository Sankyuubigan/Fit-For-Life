package com.lenar.fitforlife.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Nilden on 03.02.2018.
 */


public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("myQ", "Service Stops! Oooooooooooooppppssssss!!!!");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        },1000);
        context.startService(new Intent(context, StepService.class));
    }
}