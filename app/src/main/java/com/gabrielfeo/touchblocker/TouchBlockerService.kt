package com.gabrielfeo.touchblocker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.TypefaceCompat

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

@SuppressLint("SetTextI18n", "InflateParams")
class TouchBlockerService : Service() {

    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private val overlayView by lazy(LazyThreadSafetyMode.NONE) { createOverlayView() }

    private fun createOverlayView(): View {
        val textColor = ContextCompat.getColor(this, R.color.overlay_text_color)
        val textTypeface = TypefaceCompat.create(this, null, Typeface.BOLD)
        return TextView(this).apply {
            gravity = Gravity.CENTER
            rotation = -45f
            text = "Blocking touches"
            textSize = 28f
            setTextColor(textColor)
            typeface = textTypeface
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val shouldBlock = intent.getBooleanExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, true)
        toggleBlock(active = shouldBlock)
        showNotification(currentlyBlocking = shouldBlock)
    }

    private fun showNotification(currentlyBlocking: Boolean) {
        val notificationManager = NotificationManagerCompat.from(this)
        val notification = notificationFactory.createNotification(
            this,
            notificationManager,
            currentlyBlocking,
        )
        notificationManager.notify(R.id.blocker_notification_id, notification)
    }

    private fun toggleBlock(active: Boolean) {
        toggleOverlayView(active)
        Toast.makeText(this, "Toggled $active", Toast.LENGTH_LONG).show()
    }

    private fun toggleOverlayView(active: Boolean) {
        val windowManager = checkNotNull(getSystemService<WindowManager>())
        if (active) {
            val screenSize = windowManager.getScreenSizeCompat()
            val layoutParams = createOverlayLayoutParams(screenSize)
            windowManager.addView(overlayView, layoutParams)
        } else {
            windowManager.removeView(overlayView)
        }
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

    private fun WindowManager.getScreenSizeCompat(): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = currentWindowMetrics
            // val insets: Insets = windowMetrics.windowInsets
            //     .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            // windowMetrics.bounds.width() - insets.left - insets.right
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