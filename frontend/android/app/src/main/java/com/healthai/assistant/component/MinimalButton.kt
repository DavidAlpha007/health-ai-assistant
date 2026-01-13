package com.healthai.assistant.component

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

/**
 * Minimal button component with design spec styling
 * Usage: Primary action buttons throughout the app
 */
class MinimalButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        // Set button style according to design spec
        setBackgroundColor(android.graphics.Color.parseColor("#87CEEB")) // Light sky blue
        setTextColor(android.graphics.Color.WHITE)
        textSize = 16f // 16sp
        setPadding(0, 0, 0, 0) // No padding, full width
        height = 48 // 48dp
        // Remove default shadows and background
        setBackgroundResource(0)
        // Set custom background drawable for rounded corners and click effect
        setBackgroundDrawable(createButtonBackground())
    }

    /**
     * Create button background with rounded corners and click effect
     */
    private fun createButtonBackground(): android.graphics.drawable.Drawable {
        // Create a shape drawable for the button background
        val shapeDrawable = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            cornerRadius = 4f // 4dp rounded corners
            setColor(android.graphics.Color.parseColor("#87CEEB")) // Light sky blue
        }

        // Create a state list drawable for different button states
        return android.graphics.drawable.StateListDrawable().apply {
            // Default state
            addState(intArrayOf(), shapeDrawable)
            // Pressed state
            val pressedDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 4f // 4dp rounded corners
                setColor(android.graphics.Color.parseColor("#70B3D1")) // Darker sky blue for pressed state
            }
            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            // Disabled state
            val disabledDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 4f // 4dp rounded corners
                setColor(android.graphics.Color.parseColor("#CCCCCC")) // Gray for disabled state
            }
            addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable)
        }
    }
}
