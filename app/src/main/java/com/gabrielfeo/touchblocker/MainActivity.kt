package com.gabrielfeo.touchblocker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData

class MainActivity : AppCompatActivity() {

    private val canDrawOverlays = MutableLiveData<Boolean>()
    private val activated = MutableLiveData<Boolean>().apply { value = false }
    private lateinit var overlayView: View

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = createContentView()
        setContentView(container)
    }

    private fun createContentView(): LinearLayout {
        val canDrawStatusView = createCanDrawStatusView()
        overlayView = createActivateButton().apply {
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
        val activateButton = createActivateButton()
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
    private fun createActivateButton(): Button {
        val activateButton = Button(this).apply {
            text = "Activate"
            setOnClickListener {
                toggleOverlayView()
            }
        }
        return activateButton
    }

    private fun toggleOverlayView() {
        if (activated.value != true) {
            val windowManager = checkNotNull(getSystemService<WindowManager>())
            val layoutParams = WindowManager.LayoutParams(TYPE_APPLICATION_OVERLAY).apply {
                gravity = Gravity.CENTER
            }
            windowManager.addView(overlayView, layoutParams)
            activated.value = true
        } else {
            windowManager.removeView(overlayView)
            activated.value = false
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