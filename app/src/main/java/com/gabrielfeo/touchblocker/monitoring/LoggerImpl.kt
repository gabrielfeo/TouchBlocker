package com.gabrielfeo.touchblocker.monitoring

import android.util.Log
import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag
import com.gabrielfeo.touchblocker.BuildConfig
import com.gabrielfeo.touchblocker.monitoring.Logger.Level

class LoggerImpl : Logger {

    override fun log(message: String, level: Level, data: Map<String, Any?>?) {
        when {
            BuildConfig.DEBUG -> logInLogcat(message, level, data)
            else -> logInBugsnag(message, level, data)
        }
    }

    private fun logInLogcat(
        message: String,
        level: Level,
        data: Map<String, Any?>?,
    ) {
        val fullMessage = buildSingleMessage(message, data)
        when (level) {
            Level.DEBUG -> Log.d("TouchBlocker", fullMessage)
            Level.WARN -> Log.w("TouchBlocker", fullMessage)
        }

    }

    private fun buildSingleMessage(
        message: String,
        data: Map<String, Any?>?
    ): String {
        return if (data == null) message
        else buildString {
            append(message)
            data.entries.joinTo(this, prefix = ". Data: ") { (k, v) -> "$k=$v" }
        }
    }

    private fun logInBugsnag(
        message: String,
        level: Level,
        data: Map<String, Any?>?,
    ) {
        when {
            level == Level.WARN -> {
                data?.let { Bugsnag.leaveBreadcrumb(message, data, BreadcrumbType.MANUAL) }
                Bugsnag.notify(Throwable(message))
            }
            data != null -> Bugsnag.leaveBreadcrumb(message, data, BreadcrumbType.MANUAL)
            else -> Bugsnag.leaveBreadcrumb(message)
        }
    }
}