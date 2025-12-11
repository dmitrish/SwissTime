package com.coroutines.swisstime.watches

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.min
import kotlinx.coroutines.delay

/** Common scaffold for analog watch faces with resume animation support. */
@Composable
fun WatchFaceScaffold(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault(),
  enableResumeAnimation: Boolean = true,
  resumeAnimationDurationMs: Int = 200,
  resumeAnimationTargetAlpha: Float = 0.5f,
  updateIntervalMs: Long = 1000L,
  staticContent: DrawScope.(Offset, Float, Calendar) -> Unit,
  animatedContent: DrawScope.(Offset, Float, Calendar, Float) -> Unit
) {
  var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }
  val timeZoneState by rememberUpdatedState(timeZone)

  // Resume detection and animation
  var justResumed by remember { mutableStateOf(false) }
  // val lifecycleOwner = LocalLifecycleOwner.current

  // Get the ACTIVITY lifecycle, not the NavBackStackEntry lifecycle
  val context = LocalContext.current
  val lifecycleOwner = remember(context) { (context as? ComponentActivity) }

  if (enableResumeAnimation) {
    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
          justResumed = true
        }
      }
      lifecycleOwner?.lifecycle?.addObserver(observer)
      onDispose { lifecycleOwner?.lifecycle?.removeObserver(observer) }
    }
  }

  val alpha by
    animateFloatAsState(
      targetValue = if (enableResumeAnimation && justResumed) resumeAnimationTargetAlpha else 1f,
      animationSpec = tween(durationMillis = resumeAnimationDurationMs),
      finishedListener = { justResumed = false },
      label = "resumeFade"
    )

  // Time updates
  LaunchedEffect(Unit) {
    while (true) {
      currentTime = Calendar.getInstance(timeZoneState)
      delay(updateIntervalMs)
    }
  }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Static elements (don't animate)
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = min(size.width, size.height) / 2 * 0.8f

      // Call as extension function
      this.staticContent(center, radius, currentTime)
    }

    // Animated elements (fade on resume)
    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { this.alpha = alpha }) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = min(size.width, size.height) / 2 * 0.8f

      // Call as extension function
      this.animatedContent(center, radius, currentTime, alpha)
    }
  }
}

/** Data class representing the current time values for watch hands. */
data class WatchTime(
  val hour: Int,
  val minute: Int,
  val second: Int,
  val hourAngle: Float,
  val minuteAngle: Float,
  val secondAngle: Float
) {
  companion object {
    fun from(calendar: Calendar): WatchTime {
      val hour = calendar.get(Calendar.HOUR)
      val minute = calendar.get(Calendar.MINUTE)
      val second = calendar.get(Calendar.SECOND)

      return WatchTime(
        hour = hour,
        minute = minute,
        second = second,
        hourAngle = hour * 30f + minute * 0.5f,
        minuteAngle = minute * 6f,
        secondAngle = second * 6f
      )
    }
  }
}

/** Helper extension to extract WatchTime from Calendar. */
fun Calendar.toWatchTime(): WatchTime = WatchTime.from(this)
