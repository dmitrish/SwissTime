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

// Function to get the watch face color based on the watch name
fun getWatchFaceColor(watchName: String): Color {
  return when {
    watchName.contains("Autobahn Neomatic 41") -> Color(0xFF4A4A4A) // Sports gray dial
    watchName.contains("Zenith El Primero") -> Color(0xFFF0F0F0) // Silver-white dial
    watchName.contains("Omega Seamaster") -> Color(0xFF0A4D8C) // Deep blue dial
    watchName.contains("Rolex Submariner") -> Color(0xFF000000) // Black dial
    watchName.contains("Patek Philippe") -> Color(0xFFF5F5DC) // Cream dial
    watchName.contains("Audemars Piguet") -> Color(0xFF00008B) // Dark blue dial
    watchName.contains("Vacheron Constantin") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Jaeger-LeCoultre") -> Color(0xFFF5F5F5) // Silver dial
    watchName.contains("Blancpain") -> Color(0xFF000000) // Black dial
    watchName.contains("IWC") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Breitling") -> Color(0xFF000080) // Navy blue dial
    watchName.contains("Tokinoha") -> Color(0xFF000000) // Black dial
    watchName.contains("Longines") -> Color(0xFFF5F5F5) // Silver dial
    watchName.contains("Chopard") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Constantinus") -> Color(0xFF00008B) // Dark blue dial
    watchName.contains("Girard-Perregaux") -> Color(0xFFF5F5F5) // Silver dial
    watchName.contains("Oris") -> Color(0xFF000000) // Black dial
    watchName.contains("Tudor") -> Color(0xFF000000) // Black dial
    watchName.contains("Baume & Mercier") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Rado") -> Color(0xFF000000) // Black dial
    watchName.contains("Tissot") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Raymond Weil") -> Color(0xFFF5F5F5) // Silver dial
    watchName.contains("Frederique Constant") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Alpina") -> Color(0xFF000000) // Black dial
    watchName.contains("Mondaine") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Swatch") -> Color(0xFFFFFFFF) // White dial
    watchName.contains("Ahoi Neomatic") -> Color(0xFF1A3A5A) // Deep Atlantic blue dial
    else -> Color(0xFF2F4F4F) // Default to the current background color
  }
}
