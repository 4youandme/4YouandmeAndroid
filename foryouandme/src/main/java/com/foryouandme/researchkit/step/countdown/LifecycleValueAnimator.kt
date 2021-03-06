package com.foryouandme.researchkit.step.countdown

import android.animation.ValueAnimator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LifecycleValueAnimator(private val animator: ValueAnimator) : LifecycleObserver {

    init {

        animator.start()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop() {

        animator.removeAllUpdateListeners()
        animator.end()

    }

}