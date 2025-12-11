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

private val ClockFaceColor = Color(0xFFFAF0E6) // Ivory/cream dial
private val ClockBorderColor = Color(0xFFB8860B) // Dark gold border
private val HourHandColor = Color(0xFF000000) // Black hour hand
private val MinuteHandColor = Color(0xFF000000) // Black minute hand
private val SecondHandColor = Color(0xFFB22222) // Red second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val PowerReserveColor = Color(0xFFB8860B) // Dark gold power reserve indicator
private val CenterDotColor = Color(0xFF000000) // Black center dot

@Composable
fun YamaNoToki(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {

  var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

  val timeZoneX by rememberUpdatedState(timeZone)
  // Update time every second
  LaunchedEffect(key1 = true) {
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

      // Draw clock face
      drawClockFace(center, radius)

      // Get current time values
      val hour = currentTime.get(Calendar.HOUR)
      val minute = currentTime.get(Calendar.MINUTE)
      val second = currentTime.get(Calendar.SECOND)

      // Draw hour markers and numbers
      drawHourMarkersAndNumbers(center, radius)

      // Draw clock hands
      drawClockHands(center, radius, hour, minute, second)

      // Draw center dot
      drawCircle(color = CenterDotColor, radius = radius * 0.03f, center = center)
    }
  }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
  // Draw outer circle (border)
  drawCircle(color = ClockBorderColor, radius = radius, center = center, style = Stroke(width = 6f))

  // Draw inner circle (face)
  drawCircle(color = ClockFaceColor, radius = radius - 3f, center = center)

  // Draw elaborate guilloche pattern (characteristic of Chopard L.U.C)
  // Central sunburst pattern
  for (i in 0 until 72) {
    val angle = Math.PI * 2 * i / 72
    val startX = center.x + cos(angle).toFloat() * radius * 0.1f
    val startY = center.y + sin(angle).toFloat() * radius * 0.1f
    val endX = center.x + cos(angle).toFloat() * radius * 0.5f
    val endY = center.y + sin(angle).toFloat() * radius * 0.5f

    drawLine(
      color = Color.Black.copy(alpha = 0.05f),
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = 1f
    )
  }

  // Outer circular pattern
  for (i in 0 until 180) {
    val angle = Math.PI * 2 * i / 180
    val radius1 = radius * 0.5f
    val radius2 = radius * 0.85f
    val x1 = center.x + cos(angle).toFloat() * radius1
    val y1 = center.y + sin(angle).toFloat() * radius1
    val x2 = center.x + cos(angle).toFloat() * radius2
    val y2 = center.y + sin(angle).toFloat() * radius2

    drawLine(
      color = Color.Black.copy(alpha = 0.03f),
      start = Offset(x1, y1),
      end = Offset(x2, y2),
      strokeWidth = 0.5f
    )
  }

  // Draw Chopard logo
  val logoPaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = radius * 0.12f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText("山の時", center.x, center.y - radius * 0.3f, logoPaint)

  // Draw "L.U.C" text
  val lucPaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "Yama-no-Toki",
    center.x,
    center.y - radius * 0.15f,
    lucPaint
  )

  // Draw power reserve indicator at 6 o'clock
  val powerReserveY = center.y + radius * 0.4f
  val powerReserveWidth = radius * 0.4f
  val powerReserveHeight = radius * 0.1f

  // Power reserve background
  drawRect(
    color = Color.White,
    topLeft = Offset(center.x - powerReserveWidth / 2, powerReserveY - powerReserveHeight / 2),
    size = androidx.compose.ui.geometry.Size(powerReserveWidth, powerReserveHeight)
  )

  // Power reserve border
  drawRect(
    color = Color.Black,
    topLeft = Offset(center.x - powerReserveWidth / 2, powerReserveY - powerReserveHeight / 2),
    size = androidx.compose.ui.geometry.Size(powerReserveWidth, powerReserveHeight),
    style = Stroke(width = 1f)
  )

  // Power reserve markings
  for (i in 0..8) {
    val x = center.x - powerReserveWidth / 2 + powerReserveWidth * i / 8

    drawLine(
      color = Color.Black,
      start = Offset(x, powerReserveY - powerReserveHeight / 2),
      end = Offset(x, powerReserveY - powerReserveHeight / 4),
      strokeWidth = if (i % 2 == 0) 1.5f else 0.5f
    )
  }

  // Power reserve text
  val powerReservePaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = powerReserveHeight * 0.6f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "POWER RESERVE",
    center.x,
    powerReserveY + powerReserveHeight / 4,
    powerReservePaint
  )

  // Power reserve indicator (simulated at 75% full)
  val indicatorWidth = powerReserveWidth * 0.75f
  drawRect(
    color = PowerReserveColor,
    topLeft = Offset(center.x - powerReserveWidth / 2, powerReserveY - powerReserveHeight / 2),
    size = androidx.compose.ui.geometry.Size(indicatorWidth, powerReserveHeight)
  )

  // Small seconds subdial at 6 o'clock (above power reserve)
  val secondsSubdialY = center.y + radius * 0.25f
  val secondsSubdialRadius = radius * 0.15f

  // Subdial background
  drawCircle(
    color = Color.White,
    radius = secondsSubdialRadius,
    center = Offset(center.x, secondsSubdialY)
  )

  // Subdial border
  drawCircle(
    color = Color.Black,
    radius = secondsSubdialRadius,
    center = Offset(center.x, secondsSubdialY),
    style = Stroke(width = 1f)
  )

  // Subdial markers
  for (i in 0 until 60) {
    val angle = Math.PI * 2 * i / 60
    val markerLength = if (i % 5 == 0) secondsSubdialRadius * 0.2f else secondsSubdialRadius * 0.1f
    val startX = center.x + cos(angle).toFloat() * (secondsSubdialRadius - markerLength)
    val startY = secondsSubdialY + sin(angle).toFloat() * (secondsSubdialRadius - markerLength)
    val endX = center.x + cos(angle).toFloat() * secondsSubdialRadius * 0.9f
    val endY = secondsSubdialY + sin(angle).toFloat() * secondsSubdialRadius * 0.9f

    drawLine(
      color = Color.Black,
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = if (i % 15 == 0) 1.5f else 0.5f
    )
  }

  // Subdial numbers (15, 30, 45, 60)
  val subdialNumberPaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = secondsSubdialRadius * 0.3f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  val secondsNumbers = listOf("60", "15", "30", "45")
  for (i in 0..3) {
    val angle = Math.PI / 2 * i
    val numberX = center.x + cos(angle).toFloat() * secondsSubdialRadius * 0.7f
    val numberY =
      secondsSubdialY +
        sin(angle).toFloat() * secondsSubdialRadius * 0.7f +
        subdialNumberPaint.textSize / 3

    drawContext.canvas.nativeCanvas.drawText(
      secondsNumbers[i],
      numberX,
      numberY,
      subdialNumberPaint
    )
  }

  // Draw seconds hand in subdial
  val second = Calendar.getInstance().get(Calendar.SECOND)
  val secondAngle = second * 6f

  rotate(secondAngle, pivot = Offset(center.x, secondsSubdialY)) {
    drawLine(
      color = SecondHandColor,
      start = Offset(center.x, secondsSubdialY),
      end = Offset(center.x, secondsSubdialY - secondsSubdialRadius * 0.8f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
  }
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
  // Chopard L.U.C typically uses applied hour markers with Roman numerals
  val romanNumerals =
    listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")

  // Draw hour markers (applied baton style with gold accents)
  for (i in 0 until 12) {
    val angle = Math.PI / 6 * i - Math.PI / 2
    val markerLength = radius * 0.08f
    val markerWidth = radius * 0.02f

    // Skip VI (6 o'clock) where the power reserve and small seconds are
    if (i == 6) continue

    val markerX = center.x + cos(angle).toFloat() * radius * 0.75f
    val markerY = center.y + sin(angle).toFloat() * radius * 0.75f

    // Draw rectangular marker with 3D effect
    rotate(degrees = i * 30f, pivot = Offset(markerX, markerY)) {
      // Main marker
      drawRect(
        color = MarkersColor,
        topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
        size = androidx.compose.ui.geometry.Size(markerWidth, markerLength)
      )

      // Gold accent
      drawRect(
        color = ClockBorderColor,
        topLeft = Offset(markerX - markerWidth / 2 + 1f, markerY - markerLength / 2 + 1f),
        size = androidx.compose.ui.geometry.Size(markerWidth - 2f, markerLength - 2f)
      )
    }
  }

  // Draw Roman numerals at 12, 3, and 9 o'clock
  val positions = listOf(0, 3, 9)
  val numerals = listOf("XII", "III", "IX")
  val textPaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.1f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  for (i in positions.indices) {
    val angle = Math.PI / 6 * positions[i] - Math.PI / 2
    val numberRadius = radius * 0.6f
    val numberX = center.x + cos(angle).toFloat() * numberRadius
    val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

    drawContext.canvas.nativeCanvas.drawText(numerals[i], numberX, numberY, textPaint)
  }

  // Draw date window at 4:30 position
  val dateAngle = Math.PI / 6 * 4.5 - Math.PI / 2 // Between 4 and 5, adjusted for consistency
  val dateX = center.x + cos(dateAngle).toFloat() * radius * 0.55f
  val dateY = center.y + sin(dateAngle).toFloat() * radius * 0.55f

  // Date window
  drawRect(
    color = Color.White,
    topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
    size = androidx.compose.ui.geometry.Size(radius * 0.16f, radius * 0.12f)
  )
  drawRect(
    color = ClockBorderColor,
    topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
    size = androidx.compose.ui.geometry.Size(radius * 0.16f, radius * 0.12f),
    style = Stroke(width = 1f)
  )

  // Date text
  val datePaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
  drawContext.canvas.nativeCanvas.drawText(day, dateX, dateY + radius * 0.03f, datePaint)
}

private fun DrawScope.drawClockHands(
  center: Offset,
  radius: Float,
  hour: Int,
  minute: Int,
  second: Int
) {
  // Hour hand - elegant dauphine-style with black polished finish
  val hourAngle = (hour * 30 + minute * 0.5f)
  rotate(hourAngle) {
    val hourHandPath =
      androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - radius * 0.4f) // Tip
        lineTo(center.x + radius * 0.04f, center.y) // Right corner
        lineTo(center.x, center.y + radius * 0.1f) // Bottom
        lineTo(center.x - radius * 0.04f, center.y) // Left corner
        close()
      }
    drawPath(hourHandPath, HourHandColor)
  }

  // Minute hand - longer dauphine-style
  val minuteAngle = minute * 6f
  rotate(minuteAngle) {
    val minuteHandPath =
      androidx.compose.ui.graphics.Path().apply {
        moveTo(center.x, center.y - radius * 0.6f) // Tip
        lineTo(center.x + radius * 0.03f, center.y) // Right corner
        lineTo(center.x, center.y + radius * 0.1f) // Bottom
        lineTo(center.x - radius * 0.03f, center.y) // Left corner
        close()
      }
    drawPath(minuteHandPath, MinuteHandColor)
  }

  // Note: The second hand is drawn in the small seconds subdial in the drawClockFace function
}

@Preview(showBackground = true)
@Composable
fun ChopardLUCPreview() {
  SwissTimeTheme { YamaNoToki() }
}
