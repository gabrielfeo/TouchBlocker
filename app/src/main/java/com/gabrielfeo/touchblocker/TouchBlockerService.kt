package com.gabrielfeo.touchblocker

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData

internal const val ACTION_TOGGLE_BLOCK = "com.gabrielfeo.touchblocker.ACTION_TOGGLE_BLOCK"
internal const val EXTRA_TOGGLE_BLOCK_TARGET_VALUE = "ACTION_TOGGLE_BLOCK.value"
internal const val REQUEST_TOGGLE_BLOCK = 1029

class TouchBlockerService : Service() {

    private val notificationFactory: NotificationFactory = NotificationFactoryImpl()
    private var currentlyBlocking = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == ACTION_TOGGLE_BLOCK) {
            val value = intent.getBooleanExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, true)
            toggleBlock(value)
        }
        showNotification()
    }

    private fun showNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        val notification = notificationFactory.createNotification(
            this,
            notificationManager,
            currentlyBlocking,
        )
        notificationManager.notify(R.id.blocker_notification_id, notification)
    }

    private fun toggleBlock(value: Boolean) {
        currentlyBlocking = value
        Toast.makeText(this, "Toggled $value", Toast.LENGTH_LONG).show()
    }

}