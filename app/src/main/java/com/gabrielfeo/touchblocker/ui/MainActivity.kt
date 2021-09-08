package com.gabrielfeo.touchblocker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gabrielfeo.touchblocker.monitoring.BugsnagLogger
import com.gabrielfeo.touchblocker.monitoring.Logger
import com.gabrielfeo.touchblocker.service.TouchBlockerService
import com.gabrielfeo.touchblocker.state.TransientState

class MainActivity : AppCompatActivity() {


    private val logger: Logger = BugsnagLogger()
    private var canBlock: Boolean by mutableStateOf(false)

    private fun checkCanBlock() = Settings.canDrawOverlays(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManageScreen(
                canBlock = canBlock,
                serviceRunning = TransientState.isTouchBlockActive,
                onGrantPermissionClick = {
                    logger.log("Grant permission click")
                    goToPermissionSettings()
                },
                onStartBlockingClick = {
                    startTouchBlockerService()
                },
            )
        }
    }

    override fun onStart() {
        super.onStart()
        canBlock = checkCanBlock()
        logger.log(
            "MainActivity#onStart()",
            data = mapOf("canBlock" to canBlock)
        )
    }

    private fun startTouchBlockerService() {
        val startTouchBlocker = Intent(this@MainActivity, TouchBlockerService::class.java)
        startService(startTouchBlocker)
    }

    private fun goToPermissionSettings() {
        val packageUri = Uri.parse("package:$packageName")
        val managePermission = Intent(ACTION_MANAGE_OVERLAY_PERMISSION, packageUri)
        startActivity(managePermission)
    }
}
