package com.gabrielfeo.touchblocker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.gabrielfeo.touchblocker.R
import com.gabrielfeo.touchblocker.monitoring.LoggerImpl
import com.gabrielfeo.touchblocker.monitoring.Logger
import com.gabrielfeo.touchblocker.state.TransientState
import com.gabrielfeo.touchblocker.ui.OverlayFactoryImpl

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

class TouchBlockerService : Service() {

    private val logger: Logger = LoggerImpl()
    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private val overlay by lazy(LazyThreadSafetyMode.NONE) {
        OverlayFactoryImpl().createOverlay(this, checkNotNull(getSystemService()))
            .also { it.view.registerAttachedStateChangeLogger() }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        logOnStartCommand(intent)
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
        val overlayAdded = overlay.view.isAttachedToWindow
        if (active && !overlayAdded) {
            windowManager.addView(overlay.view, overlay.layoutParams)
        } else if (!active && overlayAdded) {
            windowManager.removeView(overlay.view)
        } else {
            // Do nothing, as everything seems to be in target state
            logInconsistentState(targetToggleValue = active, attachedToWindow = overlayAdded)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logOnDestroy()
    }

    private fun View.registerAttachedStateChangeLogger() {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                logger.log("View attached to window")
            }

            override fun onViewDetachedFromWindow(v: View) {
                logger.log("View detached from window")
            }
        })
    }

    private fun logOnStartCommand(intent: Intent?) {
        logger.log(
            "TouchBlockerService#onStartCommand()",
            data = mapOf(
                "intent" to intent,
                "toggleBlockValue" to intent?.getToggleBlockValue(),
            )
        )
    }

    private fun logInconsistentState(targetToggleValue: Boolean, attachedToWindow: Boolean) {
        logger.log(
            "Inconsistent state",
            level = Logger.Level.WARN,
            data = mapOf(
                "toggleActive" to targetToggleValue,
                "attachedToWindow" to attachedToWindow,
            ),
        )
    }

    private fun logOnDestroy() {
        logger.log("TouchBlockerService#onDestroy()")
    }

}