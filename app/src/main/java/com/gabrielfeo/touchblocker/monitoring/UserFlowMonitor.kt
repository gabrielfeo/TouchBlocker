package com.gabrielfeo.touchblocker.monitoring

interface UserFlowMonitor {
    fun registerEvent(description: String, data: Map<String, Any?>? = null)
}
