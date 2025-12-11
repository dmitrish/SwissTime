package com.coroutines.swisstime.watchfaces.watches

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.coroutines.swisstime.watchfaces.scaffold.WatchTime
import com.coroutines.swisstime.watchfaces.scaffold.WatchfaceScaffold
import com.coroutines.swisstime.watchfaces.scaffold.toWatchTime
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

private val ClockFaceColor = Color(0xFF000000) // Black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val AccentColor = Color(0xFF0000FF) // Blue accent color

@Composable
fun LucernaRomaWatchface(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault()
) {
  WatchfaceScaffold(
    modifier = modifier,
    timeZone = timeZone,
    staticContent = { center, radius, _ ->
      drawLucernaTonneauCase(center, radius)
      drawLucernaHourMarkersAndNumbers(center, radius)
      drawLucernaLogo(center, radius)
    },
    animatedContent = { center, radius, currentTime, _ ->
      val time = currentTime.toWatchTime()
      drawLucernaClockHands(center, radius, time)
      drawLucernaCenterDot(center, radius)
    }
  )
}

private fun DrawScope.drawLucernaTonneauCase(center: Offset, radius: Float) {
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

private fun DrawScope.drawLucernaHourMarkersAndNumbers(center: Offset, radius: Float) {
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

private fun DrawScope.drawLucernaClockHands(center: Offset, radius: Float, time: WatchTime) {
  // Hour hand
  rotate(time.hourAngle, pivot = center) {
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
  rotate(time.minuteAngle, pivot = center) {
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
  rotate(time.secondAngle, pivot = center) {
    drawLine(
      color = SecondHandColor,
      start = center,
      end = Offset(center.x, center.y - radius * 0.8f),
      strokeWidth = 2f,
      cap = StrokeCap.Round
    )
  }
}

private fun DrawScope.drawLucernaLogo(center: Offset, radius: Float) {
  val logoPaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = radius * 0.12f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  // Draw "LUCERNA" text
  drawContext.canvas.nativeCanvas.drawText("LUCERNA", center.x, center.y - radius * 0.3f, logoPaint)

  // Draw "ROMA" text
  val genevePaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText("ROMA", center.x, center.y - radius * 0.15f, genevePaint)
}

private fun DrawScope.drawLucernaCenterDot(center: Offset, radius: Float) {
  drawCircle(color = AccentColor, radius = radius * 0.03f, center = center)
}

@Preview(showBackground = true)
@Composable
fun LucernaRomaPreview() {

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    LucernaRomaWatchface()
  }
}
