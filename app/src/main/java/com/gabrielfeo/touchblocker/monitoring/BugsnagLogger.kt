package com.gabrielfeo.touchblocker.monitoring

import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag

class BugsnagLogger : Logger {

    override fun log(message: String, data: Map<String, Any?>?) {
        if (data != null) Bugsnag.leaveBreadcrumb(message, data, BreadcrumbType.MANUAL)
        else Bugsnag.leaveBreadcrumb(message)
    }
}