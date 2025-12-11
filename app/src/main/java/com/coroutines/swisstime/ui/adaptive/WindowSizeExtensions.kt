package com.coroutines.swisstime.ui.adaptive

import android.app.Activity
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.WindowMetricsCalculator

// Ergonomic extension properties to make width/height classes easier to use.
// Usage example:
// val isWide = windowSizeClass.widthSizeClass.isExpanded
// val isTall = windowSizeClass.heightSizeClass.isExpanded

val WindowWidthSizeClass.isCompact: Boolean
  get() = this == WindowWidthSizeClass.Compact

val WindowWidthSizeClass.isMedium: Boolean
  get() = this == WindowWidthSizeClass.Medium

val WindowWidthSizeClass.isExpanded: Boolean
  get() = this == WindowWidthSizeClass.Expanded

val WindowHeightSizeClass.isCompact: Boolean
  get() = this == WindowHeightSizeClass.Compact

val WindowHeightSizeClass.isMedium: Boolean
  get() = this == WindowHeightSizeClass.Medium

val WindowHeightSizeClass.isExpanded: Boolean
  get() = this == WindowHeightSizeClass.Expanded

@Composable
fun isLandscape(): Boolean {
  val context = LocalContext.current
  val windowMetrics =
    WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context as Activity)
  val bounds = windowMetrics.bounds
  return bounds.width() > bounds.height()
}
