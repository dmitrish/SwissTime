package com.coroutines.worldclock.common.watchface

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import java.util.*
import kotlin.math.min
import kotlinx.coroutines.delay

abstract class WorldClockWatchTheme {
  abstract val staticElementsDrawer: List<(Offset, Float) -> DrawScope.() -> Unit>
  // Changed function signatures to match the extension function pattern
  abstract val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit
  abstract val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit
  abstract val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit
  abstract val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit
}

@Composable
fun BaseWatch(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault(),
  theme: WorldClockWatchTheme
) {
  var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }
  val timeZoneState by rememberUpdatedState(timeZone)

  LaunchedEffect(Unit) {
    while (true) {
      currentTime = Calendar.getInstance(timeZoneState)
      delay(1000)
    }
  }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    StaticWatchElements(theme)
    DynamicWatchHands(currentTime, theme)
    CenterDot(theme)
  }
}

@Composable
private fun StaticWatchElements(theme: WorldClockWatchTheme) {
  Canvas(
    modifier =
      Modifier.fillMaxSize().drawWithCache {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f

        val cache = theme.staticElementsDrawer.map { drawer -> drawer(center, radius) }

        onDrawBehind { cache.forEach { it.invoke(this) } }
      }
  ) {}
}

@Composable
private fun DynamicWatchHands(currentTime: Calendar, theme: WorldClockWatchTheme) {
  // Hour Hand
  Canvas(
    modifier =
      Modifier.fillMaxSize().graphicsLayer {
        val hour = currentTime.get(Calendar.HOUR)
        val minute = currentTime.get(Calendar.MINUTE)
        rotationZ = (hour * 30 + minute * 0.5f)
      }
  ) {
    theme.hourHandDrawer(center, size.minDimension / 2 * 0.8f)(this)
  }

  // Minute Hand
  Canvas(
    modifier =
      Modifier.fillMaxSize().graphicsLayer { rotationZ = currentTime.get(Calendar.MINUTE) * 6f }
  ) {
    theme.minuteHandDrawer(center, size.minDimension / 2 * 0.8f)(this)
  }

  // Second Hand
  Canvas(
    modifier =
      Modifier.fillMaxSize().graphicsLayer { rotationZ = currentTime.get(Calendar.SECOND) * 6f }
  ) {
    theme.secondHandDrawer(center, size.minDimension / 2 * 0.8f)(this)
  }
}

@Composable
private fun CenterDot(theme: WorldClockWatchTheme) {
  Canvas(
    modifier =
      Modifier.fillMaxSize().drawWithCache {
        onDrawBehind { theme.centerDotDrawer(center, size.minDimension / 2 * 0.8f)(this) }
      }
  ) {}
}
