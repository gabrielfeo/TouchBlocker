package com.gabrielfeo.touchblocker.ui.overlay

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

inline fun View.setOnDoubleTapListener(
    crossinline onDoubleTap: (MotionEvent) -> Boolean
) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            return onDoubleTap(e)
        }
    }
    val gestureDetector = GestureDetectorCompat(context, gestureListener)
    setOnTouchListener { view, event ->
        gestureDetector.onTouchEvent(event) || view.performClick()
    }
}