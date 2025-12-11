package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.watchfaces.getWatches
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * SwissTimePagerCircle
 *
 * A circular arrangement of watches: all items are placed evenly around a circle. This is a
 * standalone version derived from SwissTimePager but not horizontal; instead it lays out all
 * watches in a ring. Each watch has a base size of 200.dp.
 */
@Composable
fun SwissTimePagerCircle() {
  val watches = getWatches()
  val itemSizeDp = 200.dp
  val marginDp = 8.dp
  val density = LocalDensity.current

  BoxWithConstraints(
    modifier = Modifier.fillMaxWidth().aspectRatio(1f) // square canvas for the circle
  ) {
    // Convert sizes to px for precise placement
    val widthPx = with(density) { maxWidth.toPx() }
    val heightPx = with(density) { maxHeight.toPx() }
    val itemSizePx = with(density) { itemSizeDp.toPx() }
    val marginPx = with(density) { marginDp.toPx() }

    val sizePx = min(widthPx, heightPx)
    val centerX = widthPx / 2f
    val centerY = heightPx / 2f
    // Radius so that items stay within bounds (leave a small margin)
    val radius = sizePx / 2f - itemSizePx / 2f - marginPx

    Box(modifier = Modifier.fillMaxSize()) {
      val n = watches.size.coerceAtLeast(1)
      watches.forEachIndexed { index, watch ->
        // Even angular distribution; start at top (-90 degrees)
        val angle = (2.0 * PI * index / n) - (PI / 2.0)
        val x = centerX + radius * cos(angle).toFloat() - itemSizePx / 2f
        val y = centerY + radius * sin(angle).toFloat() - itemSizePx / 2f

        Box(
          modifier =
            Modifier.size(itemSizeDp).graphicsLayer {
              // Position this item at the computed coordinates
              translationX = x
              translationY = y
            },
          contentAlignment = Alignment.Center
        ) {
          watch.composable(Modifier.fillMaxSize(), TimeZone.getDefault())
        }
      }
    }
  }
}

@Preview
@Composable
fun PreviewSwissTimePagerCircle() {
  SwissTimePagerCircle()
}
