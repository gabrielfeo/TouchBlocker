package com.gabrielfeo.touchblocker.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.TypefaceCompat
import com.gabrielfeo.touchblocker.R

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    init {
        gravity = Gravity.CENTER
        rotation = -45f
        text = context.getString(R.string.overlay_text)
        textSize = 28f
        typeface = TypefaceCompat.create(context, null, Typeface.BOLD)
        val textColor = ContextCompat.getColor(context, R.color.overlay_text_color)
        setTextColor(textColor)
    }

}