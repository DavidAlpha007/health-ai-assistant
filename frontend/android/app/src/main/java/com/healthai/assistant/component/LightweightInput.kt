package com.healthai.assistant.component

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

/**
 * Lightweight input component with design spec styling
 * Usage: Input fields for phone number and verification code
 */
class LightweightInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr) {

    init {
        // Set input style according to design spec
        setBackgroundColor(android.graphics.Color.TRANSPARENT) // Transparent background
        setTextColor(android.graphics.Color.parseColor("#333333")) // Dark gray text
        hintTextColor = android.graphics.Color.parseColor("#999999") // Light gray hint text
        textSize = 16f // 16sp
        height = 44 // 44dp
        // Remove default background and underline
        setBackgroundResource(0)
        // Set custom underline
        setBackgroundDrawable(createUnderline())
        // Set cursor color to light sky blue
        try {
            val field = android.widget.TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(this, 0) // Use default cursor with custom color
            // Set cursor color
            val editorField = android.widget.TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(this)
            val cursorField = editor.javaClass.getDeclaredField("mCursorDrawable")
            cursorField.isAccessible = true
            val cursorDrawable = arrayOf(
                createCursorDrawable(),
                createCursorDrawable()
            )
            cursorField.set(editor, cursorDrawable)
        } catch (e: Exception) {
            // Fallback if reflection fails
        }
    }

    /**
     * Create underline for input field
     */
    private fun createUnderline(): android.graphics.drawable.Drawable {
        // Create a shape drawable for the underline
        val shapeDrawable = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            setSize(0, 1) // 1dp height
            setColor(android.graphics.Color.parseColor("#F5F5F5")) // Super light gray
        }

        // Create a state list drawable for different input states
        return android.graphics.drawable.StateListDrawable().apply {
            // Default state
            addState(intArrayOf(), shapeDrawable)
            // Focused state
            val focusedDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                setSize(0, 1) // 1dp height
                setColor(android.graphics.Color.parseColor("#87CEEB")) // Light sky blue for focused state
            }
            addState(intArrayOf(android.R.attr.state_focused), focusedDrawable)
        }
    }

    /**
     * Create cursor drawable with light sky blue color
     */
    private fun createCursorDrawable(): android.graphics.drawable.Drawable {
        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            setSize(2, 20) // 2dp width, 20dp height
            setColor(android.graphics.Color.parseColor("#87CEEB")) // Light sky blue cursor
        }
    }
}
