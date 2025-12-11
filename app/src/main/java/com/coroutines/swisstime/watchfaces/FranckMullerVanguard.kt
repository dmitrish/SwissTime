package com.coroutines.swisstime.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlinx.coroutines.delay

private val ClockFaceColor = Color(0xFF000000) // Black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val AccentColor = Color(0xFF0000FF) // Blue accent color

@Composable
fun LucernaRoma(
  modifier: Modifier = Modifier,
  // watchViewModel: WatchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  timeZone: TimeZone = TimeZone.getDefault()
) {

  // val watchName = watchViewModel.selectedWatch.value?.name ?: "Watch 1"

  //  val currentTimeZone = watchViewModel.getWatchTimeZone(watchName).collectAsState()

  var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

  val timeZoneX by rememberUpdatedState(timeZone)
  // Update time every second
  LaunchedEffect(key1 = timeZone) {
    while (true) {
      currentTime = Calendar.getInstance(timeZoneX)
      delay(1000) // Update every second
    }
  }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Draw the clock
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = min(size.width, size.height) / 2 * 0.8f

      // Draw clock face (tonneau shape)
      drawTonneauCase(center, radius)

      // Get current time values
      val hour = currentTime.get(Calendar.HOUR)
      val minute = currentTime.get(Calendar.MINUTE)
      val second = currentTime.get(Calendar.SECOND)

      // Draw hour markers and numbers
      drawHourMarkersAndNumbers(center, radius)

      // Draw clock hands
      drawClockHands(center, radius, hour, minute, second)

      // Draw center dot
      drawCircle(color = AccentColor, radius = radius * 0.03f, center = center)

      // Draw Franck Muller logo
      drawLogo(center, radius)
    }
  }
}

private fun DrawScope.drawTonneauCase(center: Offset, radius: Float) {
  // Create a tonneau (barrel) shape path
  val path =
    Path().apply {
      // Define the tonneau shape
      val width = radius * 1.8f
      val height = radius * 2.0f
      val curveRadius = width * 0.2f

      // Top curve
      moveTo(center.x - width / 2 + curveRadius, center.y - height / 2)
      lineTo(center.x + width / 2 - curveRadius, center.y - height / 2)

      // Right curve
      cubicTo(
        center.x + width / 2,
        center.y - height / 2,
        center.x + width / 2,
        center.y - height / 2 + curveRadius,
        center.x + width / 2,
        center.y - height / 4
      )
      lineTo(center.x + width / 2, center.y + height / 4)

      cubicTo(
        center.x + width / 2,
        center.y + height / 2 - curveRadius,
        center.x + width / 2,
        center.y + height / 2,
        center.x + width / 2 - curveRadius,
        center.y + height / 2
      )

      // Bottom curve
      lineTo(center.x - width / 2 + curveRadius, center.y + height / 2)

      // Left curve
      cubicTo(
        center.x - width / 2,
        center.y + height / 2,
        center.x - width / 2,
        center.y + height / 2 - curveRadius,
        center.x - width / 2,
        center.y + height / 4
      )
      lineTo(center.x - width / 2, center.y - height / 4)

      cubicTo(
        center.x - width / 2,
        center.y - height / 2 + curveRadius,
        center.x - width / 2,
        center.y - height / 2,
        center.x - width / 2 + curveRadius,
        center.y - height / 2
      )

      close()
    }

  // Draw the case border
  drawPath(path = path, color = ClockBorderColor, style = Stroke(width = 8f))

  // Draw the case face
  drawPath(path = path, color = ClockFaceColor)
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
  // Franck Muller is known for its distinctive large, colorful numerals
  val textPaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.2f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
      typeface =
        android.graphics.Typeface.create(
          android.graphics.Typeface.DEFAULT,
          android.graphics.Typeface.BOLD
        )
    }

  // Draw the distinctive Franck Muller numerals
  for (i in 1..12) {
    val angle = Math.PI / 6 * (i - 3) // Start at 12 o'clock
    val numberRadius = radius * 0.7f

    // Adjust positions for tonneau shape
    val adjustedRadius =
      if (i == 12 || i == 6) {
        numberRadius * 0.9f
      } else if (i == 3 || i == 9) {
        numberRadius * 1.1f
      } else {
        numberRadius
      }

    val numberX = center.x + cos(angle).toFloat() * adjustedRadius
    val numberY = center.y + sin(angle).toFloat() * adjustedRadius + textPaint.textSize / 3

    // Draw colorful numbers (a signature of Franck Muller)
    val numberColor =
      when (i) {
        12 -> Color.Red
        3 -> Color.Blue
        6 -> Color.Green
        9 -> Color.Yellow
        else -> NumbersColor
      }

    textPaint.color = numberColor.hashCode()

    drawContext.canvas.nativeCanvas.drawText(i.toString(), numberX, numberY, textPaint)
  }
}

private fun DrawScope.drawClockHands(
  center: Offset,
  radius: Float,
  hour: Int,
  minute: Int,
  second: Int
) {
  // Hour hand
  val hourAngle = (hour * 30 + minute * 0.5f)
  rotate(hourAngle, pivot = center) {
    // Distinctive Franck Muller hand shape
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.5f) // Tip
        lineTo(center.x + radius * 0.04f, center.y - radius * 0.4f)
        lineTo(center.x + radius * 0.02f, center.y)
        lineTo(center.x - radius * 0.02f, center.y)
        lineTo(center.x - radius * 0.04f, center.y - radius * 0.4f)
        close()
      }
    drawPath(path, HourHandColor)
  }

  // Minute hand
  val minuteAngle = minute * 6f
  rotate(minuteAngle, pivot = center) {
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.7f) // Tip
        lineTo(center.x + radius * 0.03f, center.y - radius * 0.6f)
        lineTo(center.x + radius * 0.015f, center.y)
        lineTo(center.x - radius * 0.015f, center.y)
        lineTo(center.x - radius * 0.03f, center.y - radius * 0.6f)
        close()
      }
    drawPath(path, MinuteHandColor)
  }

  // Second hand
  val secondAngle = second * 6f
  rotate(secondAngle, pivot = center) {
    drawLine(
      color = SecondHandColor,
      start = center,
      end = Offset(center.x, center.y - radius * 0.8f),
      strokeWidth = 2f,
      cap = StrokeCap.Round
    )
  }
}

private fun DrawScope.drawLogo(center: Offset, radius: Float) {
  val logoPaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = radius * 0.12f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  // Draw "FRANCK MULLER" text
  drawContext.canvas.nativeCanvas.drawText("LUCERNA", center.x, center.y - radius * 0.3f, logoPaint)

  // Draw "GENEVE" text
  val genevePaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText("ROMA", center.x, center.y - radius * 0.15f, genevePaint)

  // Draw "MASTER OF COMPLICATIONS" text
  val sloganPaint =
    Paint().apply {
      color = AccentColor.hashCode()
      textSize = radius * 0.06f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  /*  drawContext.canvas.nativeCanvas.drawText(
      "Ars Romanorum",
      center.x,
      center.y + radius * 0.6f,
      sloganPaint
  )*/
}

@Preview(showBackground = true)
@Composable
fun FranckMullerVanguardPreview() {
  SwissTimeTheme { LucernaRoma() }
}
