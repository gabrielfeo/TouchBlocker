package com.gabrielfeo.touchblocker.ui

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.gabrielfeo.touchblocker.R

data class Overlay(
    val view: View,
    val layoutParams: WindowManager.LayoutParams,
)

interface OverlayFactory {
    fun createOverlay(context: Context, windowManager: WindowManager): Overlay
}
    
class OverlayFactoryImpl : OverlayFactory {

    override fun createOverlay(context: Context, windowManager: WindowManager): Overlay {
        val screenSize = windowManager.getScreenSizeCompat()
        return Overlay(
            view = OverlayView(context),
            layoutParams = createOverlayLayoutParams(screenSize),
        )
    }

    private fun createOverlayLayoutParams(screenSize: Pair<Int, Int>): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT,
        ).apply {
            val (screenWidth, screenHeight) = screenSize
            width = screenWidth
            height = screenHeight
            gravity = Gravity.CENTER
            dimAmount = 0.6f
        }
    }

    @Suppress("Deprecation")
    private fun WindowManager.getScreenSizeCompat(): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = currentWindowMetrics
            windowMetrics.bounds.let {
                it.width() to it.height()
            }
        } else {
            val displayMetrics = DisplayMetrics()
            defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.let {
                it.widthPixels to it.heightPixels
            }
        }
    }
    
}