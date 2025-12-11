package com.coroutines.swisstime.wearos.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.TimeZone

/** Data class to hold watch face information */
data class WatchFace(
  val id: String,
  val name: String,
  val description: String,
  val composable: @Composable (Modifier, TimeZone, () -> Unit) -> Unit
)

/** Function to get the watch face color based on the watch name */
fun getWatchFaceColor(watchName: String): androidx.compose.ui.graphics.Color {
  return when {
    watchName.contains("Valentinianus") ->
      androidx.compose.ui.graphics.Color(0xFFF5F5F5) // Silver-white dial
    watchName.contains("Centurio Luminor") ->
      androidx.compose.ui.graphics.Color(0xFF1E5631) // Green dial
    watchName.contains("Chronomagus") ->
      androidx.compose.ui.graphics.Color(0xFF000080) // Deep blue dial
    watchName.contains("Zenith El Primero") ->
      androidx.compose.ui.graphics.Color(0xFFF0F0F0) // Silver-white dial
    watchName.contains("Tokinoha") -> androidx.compose.ui.graphics.Color(0xFF000000) // Black dial
    watchName.contains("Concordia") -> androidx.compose.ui.graphics.Color(0xFFF5F5DC) // Cream dial
    else -> androidx.compose.ui.graphics.Color(0xFF2F4F4F) // Default to dark slate gray
  }
}
