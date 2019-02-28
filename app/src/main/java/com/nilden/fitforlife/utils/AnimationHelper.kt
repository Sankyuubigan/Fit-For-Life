package com.nilden.fitforlife.utils

import android.view.animation.*
import android.widget.TextView

class AnimationHelper {
    companion object {

        fun initAnimation(textView: TextView) {
            val animationScale = ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, ScaleAnimation.RELATIVE_TO_SELF,
                    0.51f, ScaleAnimation.RELATIVE_TO_SELF, 0.52f)
            animationScale.duration = 5000
            animationScale.repeatCount = Animation.INFINITE
            animationScale.interpolator = CycleInterpolator(1f)
            val animationRotate = RotateAnimation(0f, 3f, ScaleAnimation.RELATIVE_TO_SELF,
                    0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
            animationRotate.duration = 800
            animationRotate.interpolator = CycleInterpolator(1f)
            animationRotate.repeatCount = Animation.INFINITE
            val animationSet = AnimationSet(false)
            animationSet.addAnimation(animationRotate)
            animationSet.addAnimation(animationScale)
            animationSet.repeatCount = Animation.INFINITE
            textView.startAnimation(animationSet)
        }
    }
}