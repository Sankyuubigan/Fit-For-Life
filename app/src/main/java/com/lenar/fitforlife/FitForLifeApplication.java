package com.lenar.fitforlife;

import android.app.Application;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.lenar.fitforlife.utils.SharedPreferencesHelper;

public class FitForLifeApplication extends Application {

    private static FitForLifeApplication sInstance;

    private SharedPreferencesHelper mPreferencesHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mPreferencesHelper = new SharedPreferencesHelper(this);
    }

    public static void initAnimation(TextView textView) {
        ScaleAnimation animationScale = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, ScaleAnimation.RELATIVE_TO_SELF, 0.51f, ScaleAnimation.RELATIVE_TO_SELF, 0.52f);
        animationScale.setDuration(5000);
        animationScale.setRepeatCount(Animation.INFINITE);
        animationScale.setInterpolator(new CycleInterpolator(1));
        RotateAnimation animationRotate = new RotateAnimation(0, 3, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animationRotate.setDuration(800);
        animationRotate.setInterpolator(new CycleInterpolator(1));
        animationRotate.setRepeatCount(Animation.INFINITE);
        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(animationRotate);
        animationSet.addAnimation(animationScale);
        animationSet.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(animationSet);
    }

    public static FitForLifeApplication getInstance() {
        return sInstance;
    }

    public SharedPreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }
}
