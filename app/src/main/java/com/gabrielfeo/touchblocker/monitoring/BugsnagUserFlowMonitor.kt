package com.gabrielfeo.touchblocker.monitoring

import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag

class BugsnagUserFlowMonitor : UserFlowMonitor {

    override fun registerEvent(description: String, data: Map<String, Any?>?) {
        if (data != null) Bugsnag.leaveBreadcrumb(description, data, BreadcrumbType.MANUAL)
        else Bugsnag.leaveBreadcrumb(description)
    }
}