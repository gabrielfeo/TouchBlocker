package com.gabrielfeo.touchblocker

import android.app.Application
import com.bugsnag.android.Bugsnag

class TouchBlockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugsnag.start(this)
    }
}