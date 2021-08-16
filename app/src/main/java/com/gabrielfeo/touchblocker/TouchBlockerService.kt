package com.gabrielfeo.touchblocker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

class TouchBlockerService : Service() {

    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private var currentlyBlocking = true
    private val overlayView by lazy(LazyThreadSafetyMode.NONE) { View(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val shouldBlock = intent.getBooleanExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, true)
        println("KKK: $shouldBlock")
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
        currentlyBlocking = active
        toggleOverlayView(active)
        Toast.makeText(this, "Toggled $active", Toast.LENGTH_LONG).show()
    }

    private fun toggleOverlayView(active: Boolean) {
        val windowManager = checkNotNull(getSystemService<WindowManager>())
        if (active) {
            val layoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY).apply {
                gravity = Gravity.CENTER
                width = 0
                height = 0
            }
            windowManager.addView(overlayView, layoutParams)
        } else {
            windowManager.removeView(overlayView)
        }
    }

}