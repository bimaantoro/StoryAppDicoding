package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomNameEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.bg_custom_view)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}