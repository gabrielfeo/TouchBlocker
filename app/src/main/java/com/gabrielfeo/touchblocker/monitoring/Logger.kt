package com.gabrielfeo.touchblocker.monitoring

interface Logger {
    fun log(message: String, data: Map<String, Any?>? = null)
}
