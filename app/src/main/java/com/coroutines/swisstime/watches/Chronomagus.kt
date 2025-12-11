package com.coroutines.swisstime.watches

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Colors inspired by Chronomagus Regum
private val ClockFaceColor = Color(0xFF000080) // Deep blue dial
private val ClockBorderColor = Color(0xFFFFFFFF) // White gold case
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFFFFFF) // White second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val LogoColor = Color(0xFFFFFFFF) // White logo

@Composable
fun Chronomagus(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
  WatchFaceScaffold(
    modifier = modifier,
    timeZone = timeZone,
    staticContent = { center, radius, _ ->
      drawChronomagusClockFace(center, radius)
      drawChronomagusHourMarkers(center, radius)
      drawChronomagusLogo(center, radius)
    },
    animatedContent = { center, radius, currentTime, _ ->
      val time = currentTime.toWatchTime()
      drawChronomagusClockHands(center, radius, time)
      drawChronomagusCenterDot(center, radius)
    }
  )
}

private fun DrawScope.drawChronomagusClockFace(center: Offset, radius: Float) {
  // Draw outer circle (case) - very thin to represent the ultra-thin profile
  drawCircle(color = ClockBorderColor, radius = radius, center = center, style = Stroke(width = 4f))

  // Draw inner circle (face)
  drawCircle(color = ClockFaceColor, radius = radius - 2f, center = center)
}

private fun DrawScope.drawChronomagusHourMarkers(center: Offset, radius: Float) {
  // Chronomagus Regum typically has very minimalist hour markers
  // Often just simple thin lines or small dots

  for (i in 0 until 12) {
    val angle = PI / 6 * i

    // For 3, 6, 9, and 12 o'clock, use slightly longer markers
    val markerLength = if (i % 3 == 0) radius * 0.05f else radius * 0.03f
    val markerWidth = if (i % 3 == 0) 1.5f else 1f

    val startX = center.x + cos(angle).toFloat() * (radius * 0.85f)
    val startY = center.y + sin(angle).toFloat() * (radius * 0.85f)
    val endX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
    val endY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)

    // Draw minimalist markers
    drawLine(
      color = MarkersColor,
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = markerWidth,
      cap = StrokeCap.Round
    )
  }

  // Add small dots at each hour position for a more refined look
  for (i in 0 until 12) {
    val angle = PI / 6 * i
    val dotRadius = if (i % 3 == 0) 1.5f else 1f

    val dotX = center.x + cos(angle).toFloat() * (radius * 0.9f)
    val dotY = center.y + sin(angle).toFloat() * (radius * 0.9f)

    drawCircle(color = MarkersColor, radius = dotRadius, center = Offset(dotX, dotY))
  }
}

private fun DrawScope.drawChronomagusLogo(center: Offset, radius: Float) {
  val logoPaint =
    Paint().apply {
      color = LogoColor.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false // Chronomagus Regum logo is typically thin and elegant
      isAntiAlias = true
    }

  // Draw "CHRONOMAGUS" text
  drawContext.canvas.nativeCanvas.drawText(
    "CHRONOMAGUS",
    center.x,
    center.y - radius * 0.3f,
    logoPaint
  )

  // Draw "REGIUM" text
  val modelPaint =
    Paint().apply {
      color = LogoColor.hashCode()
      textSize = radius * 0.06f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText("REGIUM", center.x, center.y - radius * 0.2f, modelPaint)

  // Draw "Fabricatum Romae" text
  val swissMadePaint =
    Paint().apply {
      color = LogoColor.hashCode()
      textSize = radius * 0.04f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "Fabricatum Romae",
    center.x,
    center.y + radius * 0.5f,
    swissMadePaint
  )

  // Draw "ULTRA-THIN" text - a key feature
  val ultraThinPaint =
    Paint().apply {
      color = LogoColor.hashCode()
      textSize = radius * 0.05f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "ULTRA-THIN",
    center.x,
    center.y + radius * 0.2f,
    ultraThinPaint
  )
}

private fun DrawScope.drawChronomagusClockHands(center: Offset, radius: Float, time: WatchTime) {
  // Hour hand - very thin and elegant
  rotate(time.hourAngle, pivot = center) {
    drawLine(
      color = HourHandColor,
      start = center,
      end = Offset(center.x, center.y - radius * 0.5f),
      strokeWidth = 2f,
      cap = StrokeCap.Round
    )
  }

  // Minute hand - longer and equally thin
  rotate(time.minuteAngle, pivot = center) {
    drawLine(
      color = MinuteHandColor,
      start = center,
      end = Offset(center.x, center.y - radius * 0.7f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
  }

  // Second hand - extremely thin
  rotate(time.secondAngle, pivot = center) {
    drawLine(
      color = SecondHandColor,
      start = center,
      end = Offset(center.x, center.y - radius * 0.8f),
      strokeWidth = 0.5f,
      cap = StrokeCap.Round
    )
  }
}

private fun DrawScope.drawChronomagusCenterDot(center: Offset, radius: Float) {
  drawCircle(color = HourHandColor, radius = radius * 0.01f, center = center)
}

@Preview(showBackground = true)
@Composable
fun ChronomagusPreview() {
  SwissTimeTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Chronomagus() }
  }
}
