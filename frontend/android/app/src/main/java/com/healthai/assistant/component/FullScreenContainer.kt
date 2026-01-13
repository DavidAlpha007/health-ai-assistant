package com.healthai.assistant.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Full screen centered container component
 * Usage: Wrap content that needs to be centered on the screen
 */
class FullScreenContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        // Set background color to white as per design spec
        setBackgroundColor(android.graphics.Color.WHITE)
    }
}
