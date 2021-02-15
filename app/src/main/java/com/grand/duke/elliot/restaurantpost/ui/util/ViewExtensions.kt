package com.grand.duke.elliot.restaurantpost.ui.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.fadeIn(duration: Number) {
    this.apply {
        alpha = 0F
        visibility = View.VISIBLE

        animate()
            .alpha(1F)
            .setDuration(duration.toLong())
            .setListener(null)
    }
}

fun View.fadeOut(duration: Number) {
    this.apply {
        alpha = 1F
        visibility = View.VISIBLE

        animate()
            .alpha(0F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@fadeOut.visibility = View.GONE
                    super.onAnimationEnd(animation)
                }
            })
    }
}

fun View.hideDown(duration: Number, height: Float) {
    this.apply {
        translationY = 0F

        animate().translationY(height)
            .setDuration(duration.toLong())
            .setListener(null)
    }
}

fun View.showUp(duration: Number, height: Float) {
    this.apply {
        alpha = 1F
        translationY = height
        visibility = View.VISIBLE

        animate().translationY(0F)
            .setDuration(duration.toLong())
            .setListener(null)
    }
}

fun View.rotate(
    degrees: Float, duration: Number,
    animationListenerAdapter: AnimatorListenerAdapter? = null
) {
    this.animate().rotation(degrees)
        .setDuration(duration.toLong())
        .setListener(animationListenerAdapter)
        .start()
}
