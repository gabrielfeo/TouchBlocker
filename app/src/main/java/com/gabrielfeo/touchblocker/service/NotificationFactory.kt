package com.gabrielfeo.touchblocker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gabrielfeo.touchblocker.R

interface NotificationFactory {
    fun createNotification(
        context: Context,
        notificationManager: NotificationManagerCompat,
        currentlyBlocking: Boolean,
    ): Notification
}

class NotificationFactoryImpl : NotificationFactory {

    private val channelId = R.id.blocker_notification_channel_id.toString()

    @Suppress("SameParameterValue", "SameParameterValue")
    override fun createNotification(
        context: Context,
        notificationManager: NotificationManagerCompat,
        currentlyBlocking: Boolean,
    ): Notification {
        createNotificationChannel(context, notificationManager)
        return createNotification(context, currentlyBlocking)
    }

    @Suppress("SameParameterValue")
    private fun createNotification(
        context: Context,
        currentlyBlocking: Boolean,
    ): Notification {
        val contentText = when (currentlyBlocking) {
            true -> "Blocking touches"
            else -> "Idle"
        }
        val toggleBlockAction = createAction(currentlyBlocking, context)
        return NotificationCompat.Builder(context, channelId)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("TouchBlocker")
            .setContentText(contentText)
            .addAction(toggleBlockAction)
            .setSmallIcon(R.drawable.ic_block_24)
            .apply {
                if (currentlyBlocking)
                    setOngoing(true)
            }
            .build()
    }

    private fun createAction(currentlyBlocking: Boolean, context: Context): NotificationCompat.Action {
        val actionText = when (currentlyBlocking) {
            true -> "Stop"
            else -> "Start"
        }
        val toggleBlockIntent = Intent(ACTION_TOGGLE_BLOCK).apply {
            putExtra(EXTRA_TOGGLE_BLOCK_TARGET_VALUE, !currentlyBlocking)
        }
        return NotificationCompat.Action(
            null,
            actionText,
            PendingIntent.getService(
                context,
                REQUEST_TOGGLE_BLOCK,
                toggleBlockIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
            )
        )
    }

    @Suppress("SameParameterValue")
    private fun createNotificationChannel(
        context: Context,
        notificationManager: NotificationManagerCompat,
    ) {
        val notificationChannel = NotificationChannel(
            channelId,
            context.getString(R.string.blocker_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}