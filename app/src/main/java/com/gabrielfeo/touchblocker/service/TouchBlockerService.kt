package com.gabrielfeo.touchblocker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.gabrielfeo.touchblocker.R
import com.gabrielfeo.touchblocker.monitoring.BugsnagUserFlowMonitor
import com.gabrielfeo.touchblocker.monitoring.UserFlowMonitor
import com.gabrielfeo.touchblocker.state.TransientState
import com.gabrielfeo.touchblocker.ui.OverlayFactoryImpl

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

class TouchBlockerService : Service() {

    private val userFlowMonitor: UserFlowMonitor = BugsnagUserFlowMonitor()
    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private val overlay by lazy(LazyThreadSafetyMode.NONE) {
        OverlayFactoryImpl().createOverlay(this, checkNotNull(getSystemService()))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        registerOnStartCommand(intent)
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val shouldBlock = intent.getToggleBlockValue()
        TransientState.isTouchBlockActive = shouldBlock
        toggleOverlay(active = shouldBlock)
        showNotification(currentlyBlocking = shouldBlock)
    }

    private fun Intent.getToggleBlockValue() =
        getBooleanExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, true)

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

    override fun onDestroy() {
        super.onDestroy()
        registerOnDestroy()
    }

    private fun registerOnStartCommand(intent: Intent?) {
        userFlowMonitor.registerEvent(
            "TouchBlockerService#onStartCommand()",
            data = mapOf(
                "intent" to intent,
                "toggleBlockValue" to intent?.getToggleBlockValue(),
            )
        )
    }

    private fun registerOnDestroy() {
        userFlowMonitor.registerEvent("TouchBlockerService#onDestroy()")
    }

}