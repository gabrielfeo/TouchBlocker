package com.gabrielfeo.touchblocker.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.gabrielfeo.touchblocker.R
import com.gabrielfeo.touchblocker.ui.OverlayFactory
import com.gabrielfeo.touchblocker.ui.OverlayFactoryImpl

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

class TouchBlockerService : Service() {

    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private val overlay by lazy(LazyThreadSafetyMode.NONE) {
        OverlayFactoryImpl().createOverlay(this, checkNotNull(getSystemService()))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val shouldBlock = intent.getBooleanExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, true)
        toggleOverlay(active = shouldBlock)
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

    private fun toggleOverlay(active: Boolean) {
        val windowManager = checkNotNull(getSystemService<WindowManager>())
        if (active) {
            windowManager.addView(overlay.view, overlay.layoutParams)
        } else {
            windowManager.removeView(overlay.view)
        }
    }

}