package com.gabrielfeo.touchblocker.ui.overlay

import android.content.Context
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.view.postDelayed
import com.gabrielfeo.touchblocker.R

private const val CONFIRM_STOP_BLOCKING_TIMEOUT = 2000L

class TouchBlockingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    val onStopBlockingRequested: () -> Unit = {},
) : AppCompatTextView(context, attrs) {

    private var doubleTapCount = 0
    private var isStopBlockingConfirmed = false
    private val onConfirmStopBlockingTimeout = {
        if (!isStopBlockingConfirmed) {
            setCurrentlyBlockingState()
            doubleTapCount = 0
        }
    }

    init {
        setVisuals(context)
        setCurrentlyBlockingState()
        setOnDoubleTapListener {
            doubleTapCount++
            when (doubleTapCount) {
                1 -> {
                    setConfirmStopBlockingState()
                    postDelayed(CONFIRM_STOP_BLOCKING_TIMEOUT, onConfirmStopBlockingTimeout)
                }
                2 -> {
                    removeCallbacks(onConfirmStopBlockingTimeout)
                    isStopBlockingConfirmed = true // In case removeCallbacks fails
                    onStopBlockingRequested()
                }
            }
            true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resetState()
    }

    private fun resetState() {
        setCurrentlyBlockingState()
        doubleTapCount = 0
        isStopBlockingConfirmed = false
    }

    private fun setVisuals(context: Context) {
        gravity = Gravity.CENTER
        textSize = 28f
        val textColor = ContextCompat.getColor(context, R.color.overlay_text_color)
        setTextColor(textColor)
    }

    private fun setCurrentlyBlockingState() {
        text = buildSpannedString {
            bold {
                append(context.getString(R.string.overlay_text))
            }
        }
    }

    private fun setConfirmStopBlockingState() {
        text = buildSpannedString {
            bold {
                appendLine(context.getString(R.string.overlay_text))
            }
            inSpans(RelativeSizeSpan(0.6f)) {
                appendLine(context.getString(R.string.overlay_double_tap_again_instructions))
            }
        }
    }

}