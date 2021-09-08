package com.gabrielfeo.touchblocker.monitoring

import android.util.Log
import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag
import com.gabrielfeo.touchblocker.BuildConfig

class LoggerImpl : Logger {

    override fun log(message: String, data: Map<String, Any?>?) {
        when {
            BuildConfig.DEBUG -> logInLogcat(message, data)
            else -> logInBugsnag(message, data)
        }
    }

    private fun logInLogcat(
        message: String,
        data: Map<String, Any?>?,
    ) {
        val fullMessage = buildString {
            append(message)
            data?.entries?.joinTo(this, prefix = ". Data: ") { (k, v) -> "$k=$v" }
        }
        Log.d("TouchBlocker", fullMessage)
    }

    private fun logInBugsnag(
        message: String,
        data: Map<String, Any?>?,
    ) {
        when {
            data != null -> Bugsnag.leaveBreadcrumb(message, data, BreadcrumbType.MANUAL)
            else -> Bugsnag.leaveBreadcrumb(message)
        }
    }
}