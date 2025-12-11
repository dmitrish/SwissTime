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

private val ClockFaceColor = Color(0xFFF5F5F0) // Off-white dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold border
private val HourHandColor = Color(0xFF000080) // Blue hour hand (blued steel)
private val MinuteHandColor = Color(0xFF000080) // Blue minute hand
private val SecondHandColor = Color(0xFF000080) // Blue second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black roman numerals
private val AccentColor = Color(0xFFD4AF37) // Gold accent

@Composable
fun AventinusClassiqueWatchface(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault()
) {
  WatchfaceScaffold(
    modifier = modifier,
    timeZone = timeZone,
    staticContent = { center, radius, _ ->
      drawAventinusClockFace(center, radius)
      drawAventinusHourMarkersAndNumbers(center, radius)
      drawAventinusGuillochePattern(center, radius)
      drawAventinusLogo(center, radius)
    },
    animatedContent = { center, radius, currentTime, _ ->
      val time = currentTime.toWatchTime()
      drawAventinusClockHands(center, radius, time)
      drawAventinusCenterDot(center, radius)
    }
  )
}

private fun DrawScope.drawAventinusClockFace(center: Offset, radius: Float) {
  // Draw the outer border (fluted bezel)
  drawCircle(
    color = ClockBorderColor,
    radius = radius,
    center = center,
    style = Stroke(width = radius * 0.08f)
  )

  // Draw the inner fluted pattern
  val flutesCount = 60
  for (i in 0 until flutesCount) {
    val angle = (i * 360f / flutesCount) * (Math.PI / 180f)
    val outerRadius = radius * 0.96f
    val innerRadius = radius * 0.92f

    val startX = center.x + cos(angle).toFloat() * innerRadius
    val startY = center.y + sin(angle).toFloat() * innerRadius
    val endX = center.x + cos(angle).toFloat() * outerRadius
    val endY = center.y + sin(angle).toFloat() * outerRadius

    drawLine(
      color = ClockBorderColor,
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 2f
    )
  }

  // Draw the main face
  drawCircle(color = ClockFaceColor, radius = radius * 0.9f, center = center)
}

private fun DrawScope.drawAventinusHourMarkersAndNumbers(center: Offset, radius: Float) {
  // AVENTINUS is known for its elegant roman numerals
  val textPaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.15f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      typeface =
        android.graphics.Typeface.create(
          android.graphics.Typeface.SERIF,
          android.graphics.Typeface.NORMAL
        )
    }

  // Roman numerals
  val romanNumerals =
    listOf("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")

  for (i in 0 until 12) {
    val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
    val numberRadius = radius * 0.75f

    val numberX = center.x + cos(angle).toFloat() * numberRadius
    val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

    // Draw roman numerals
    drawContext.canvas.nativeCanvas.drawText(romanNumerals[i], numberX, numberY, textPaint)

    // Draw minute markers
    for (j in 0 until 5) {
      val minuteAngle = Math.PI / 30 * (i * 5 + j) - Math.PI / 2
      val innerRadius = radius * 0.85f
      val outerRadius = radius * 0.88f

      val startX = center.x + cos(minuteAngle).toFloat() * innerRadius
      val startY = center.y + sin(minuteAngle).toFloat() * innerRadius
      val endX = center.x + cos(minuteAngle).toFloat() * outerRadius
      val endY = center.y + sin(minuteAngle).toFloat() * outerRadius

      drawLine(
        color = MarkersColor,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 1f
      )
    }
  }
}

private fun DrawScope.drawAventinusGuillochePattern(center: Offset, radius: Float) {
  // AVENTINUS is famous for its guilloche patterns
  val guillocheRadius = radius * 0.6f
  val circleCount = 15
  val circleSpacing = guillocheRadius / circleCount

  for (i in 1..circleCount) {
    drawCircle(
      color = Color(0xFFEEEEE0),
      radius = guillocheRadius - (i * circleSpacing),
      center = center,
      style = Stroke(width = 0.5f)
    )
  }

  // Add cross-hatching
  for (angle in 0 until 360 step 10) {
    val radians = angle * Math.PI / 180
    val startX = center.x + cos(radians).toFloat() * (radius * 0.1f)
    val startY = center.y + sin(radians).toFloat() * (radius * 0.1f)
    val endX = center.x + cos(radians).toFloat() * guillocheRadius
    val endY = center.y + sin(radians).toFloat() * guillocheRadius

    drawLine(
      color = Color(0xFFEEEEE0),
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 0.5f
    )
  }
}

private fun DrawScope.drawAventinusLogo(center: Offset, radius: Float) {
  val logoPaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.1f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      typeface =
        android.graphics.Typeface.create(
          android.graphics.Typeface.SERIF,
          android.graphics.Typeface.ITALIC
        )
    }

  // Draw "AVENTINUS" text
  drawContext.canvas.nativeCanvas.drawText(
    "AVENTINUS",
    center.x,
    center.y - radius * 0.3f,
    logoPaint
  )

  // Draw signature (Aventinus watches often have a secret signature)
  val signaturePaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.06f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      typeface =
        android.graphics.Typeface.create(
          android.graphics.Typeface.SERIF,
          android.graphics.Typeface.ITALIC
        )
    }

  drawContext.canvas.nativeCanvas.drawText(
    "No. 1947",
    center.x,
    center.y + radius * 0.4f,
    signaturePaint
  )
}

private fun DrawScope.drawAventinusClockHands(center: Offset, radius: Float, time: WatchTime) {
  // Hour hand - AVENTINUS-style with hollow moon tip
  rotate(time.hourAngle, pivot = center) {
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.5f) // Tip
        quadraticTo(
          center.x + radius * 0.03f,
          center.y - radius * 0.48f,
          center.x + radius * 0.02f,
          center.y - radius * 0.45f
        )
        lineTo(center.x + radius * 0.02f, center.y)
        lineTo(center.x - radius * 0.02f, center.y)
        lineTo(center.x - radius * 0.02f, center.y - radius * 0.45f)
        quadraticTo(
          center.x - radius * 0.03f,
          center.y - radius * 0.48f,
          center.x,
          center.y - radius * 0.5f
        )
        close()
      }
    drawPath(path, HourHandColor)
  }

  // Minute hand - AVENTINUS-style with hollow moon tip
  rotate(time.minuteAngle, pivot = center) {
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.7f) // Tip
        quadraticTo(
          center.x + radius * 0.025f,
          center.y - radius * 0.68f,
          center.x + radius * 0.015f,
          center.y - radius * 0.65f
        )
        lineTo(center.x + radius * 0.015f, center.y)
        lineTo(center.x - radius * 0.015f, center.y)
        lineTo(center.x - radius * 0.015f, center.y - radius * 0.65f)
        quadraticTo(
          center.x - radius * 0.025f,
          center.y - radius * 0.68f,
          center.x,
          center.y - radius * 0.7f
        )
        close()
      }
    drawPath(path, MinuteHandColor)
  }

  // Second hand - thin with counterbalance
  rotate(time.secondAngle, pivot = center) {
    // Main hand
    drawLine(
      color = SecondHandColor,
      start = Offset(center.x, center.y + radius * 0.2f),
      end = Offset(center.x, center.y - radius * 0.8f),
      strokeWidth = 1f,
      cap = StrokeCap.Round
    )

    // Counterbalance
    drawCircle(
      color = SecondHandColor,
      radius = radius * 0.03f,
      center = Offset(center.x, center.y + radius * 0.15f)
    )
  }
}

private fun DrawScope.drawAventinusCenterDot(center: Offset, radius: Float) {
  drawCircle(color = AccentColor, radius = radius * 0.02f, center = center)
}

@Preview(showBackground = true)
@Composable
fun AventinusClassiquePreview() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    AventinusClassiqueWatchface()
  }
}
