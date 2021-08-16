package com.gabrielfeo.touchblocker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

class MainActivity : AppCompatActivity() {

    private val canDrawOverlays = MutableLiveData<Boolean>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = createContentView()
        setContentView(container)
    }

    private fun createContentView(): LinearLayout {
        val canDrawStatusView = createCanDrawStatusView()
        val activateButton = createStartButton()
        val manageButton = createManageButton()
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            addView(canDrawStatusView)
            addView(manageButton)
            addView(activateButton)
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("LiftReturnOrAssignment")
    private fun createStartButton(): Button {
        return Button(this).apply {
            text = "Start service"
            setOnClickListener {
                val startTouchBlocker = Intent(this@MainActivity, TouchBlockerService::class.java)
                startService(startTouchBlocker)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createManageButton(): Button {
        return Button(this).apply {
            text = "Manage"
            setOnClickListener {
                val packageUri = Uri.parse("package:$packageName")
                val managePermission = Intent(ACTION_MANAGE_OVERLAY_PERMISSION, packageUri)
                startActivity(managePermission)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createCanDrawStatusView(): TextView {
        return TextView(this).apply {
            canDrawOverlays.observe(this@MainActivity, {
                text = "Can draw overlays: $it"
            })
        }
    }

    override fun onResume() {
        super.onResume()
        canDrawOverlays.value = Settings.canDrawOverlays(this)
    }
}