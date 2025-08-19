package com.coroutines.swisstime.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// Function to darken a color by a factor
fun Color.darken(factor: Float = 0.2f): Color {
    val alpha = this.alpha
    val red = this.red * (1 - factor)
    val green = this.green * (1 - factor)
    val blue = this.blue * (1 - factor)
    return Color(red, green, blue, alpha)
}

// Function to determine if a color is dark
fun Color.isDark(): Boolean {
    return this.luminance() < 0.5f
}