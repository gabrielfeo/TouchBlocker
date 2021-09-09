package com.gabrielfeo.touchblocker.monitoring

interface Logger {

    fun log(
        message: String,
        level: Level = Level.DEBUG,
        data: Map<String, Any?>? = null
    )

    enum class Level {
        DEBUG,
        WARN,
    }
}
