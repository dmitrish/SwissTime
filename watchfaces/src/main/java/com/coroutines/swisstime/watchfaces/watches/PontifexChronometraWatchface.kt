package com.coroutines.swisstime.watchfaces.watches

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

private val ClockFaceColor = Color(0xFF1E2C4A) // Deep blue dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold border
private val HourHandColor = Color(0xFFE0E0E0) // Silver hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Silver minute hand
private val SecondHandColor = Color(0xFFD4AF37) // Gold second hand
private val MarkersColor = Color(0xFFE0E0E0) // Silver markers
private val NumbersColor = Color(0xFFE0E0E0) // Silver numbers
private val AccentColor = Color(0xFFD4AF37) // Gold accent

@Composable
fun PontifexChronometraWatchface(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault()
) {
  WatchfaceScaffold(
    modifier = modifier,
    timeZone = timeZone,
    staticContent = { center, radius, _ ->
      drawPontifexClockFace(center, radius)
      drawPontifexHourMarkers(center, radius)
      drawPontifexGuilloche(center, radius)
      drawPontifexDateWindow(center, radius, timeZone)
      drawPontifexLogo(center, radius)
    },
    animatedContent = { center, radius, currentTime, _ ->
      val time = currentTime.toWatchTime()
      drawPontifexClockHands(center, radius, time)
      drawPontifexCenterDot(center, radius)
    }
  )
}

private fun DrawScope.drawPontifexClockFace(center: Offset, radius: Float) {
  // Draw the outer border
  drawCircle(
    color = ClockBorderColor,
    radius = radius,
    center = center,
    style = Stroke(width = radius * 0.03f)
  )

  // Draw the main face
  drawCircle(color = ClockFaceColor, radius = radius * 0.97f, center = center)

  // Draw a subtle inner ring
  drawCircle(
    color = ClockBorderColor,
    radius = radius * 0.9f,
    center = center,
    style = Stroke(width = radius * 0.005f)
  )
}

private fun DrawScope.drawPontifexHourMarkers(center: Offset, radius: Float) {
  for (i in 0 until 12) {
    val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
    val markerRadius = radius * 0.85f

    val markerX = center.x + cos(angle).toFloat() * markerRadius
    val markerY = center.y + sin(angle).toFloat() * markerRadius

    // Draw applied markers
    if (i % 3 == 0) {
      // Double marker for 12, 3, 6, 9
      drawLine(
        color = MarkersColor,
        start =
          Offset(
            markerX - cos(angle + Math.PI / 2).toFloat() * radius * 0.04f,
            markerY - sin(angle + Math.PI / 2).toFloat() * radius * 0.04f
          ),
        end =
          Offset(
            markerX + cos(angle + Math.PI / 2).toFloat() * radius * 0.04f,
            markerY + sin(angle + Math.PI / 2).toFloat() * radius * 0.04f
          ),
        strokeWidth = radius * 0.02f,
        cap = StrokeCap.Round
      )
    } else {
      // Teardrop markers for other hours (distinctive Parmigiani style)
      val teardropPath =
        Path().apply {
          moveTo(
            markerX + cos(angle).toFloat() * radius * 0.03f,
            markerY + sin(angle).toFloat() * radius * 0.03f
          )

          // Create teardrop shape pointing toward center
          val angleToCenter =
            Math.atan2((center.y - markerY).toDouble(), (center.x - markerX).toDouble()).toFloat()

          // Control points for the teardrop curve
          val controlX1 = markerX + cos(angleToCenter + 0.5f).toFloat() * radius * 0.02f
          val controlY1 = markerY + sin(angleToCenter + 0.5f).toFloat() * radius * 0.02f
          val controlX2 = markerX + cos(angleToCenter - 0.5f).toFloat() * radius * 0.02f
          val controlY2 = markerY + sin(angleToCenter - 0.5f).toFloat() * radius * 0.02f

          // Draw the teardrop
          quadraticTo(controlX1, controlY1, markerX, markerY)

          quadraticTo(
            controlX2,
            controlY2,
            markerX + cos(angle).toFloat() * radius * 0.03f,
            markerY + sin(angle).toFloat() * radius * 0.03f
          )

          close()
        }

      drawPath(path = teardropPath, color = MarkersColor)
    }
  }
}

private fun DrawScope.drawPontifexGuilloche(center: Offset, radius: Float) {
  val patternRadius = radius * 0.7f

  // Draw a wave pattern (simplified guilloche)
  for (angle in 0 until 360 step 5) {
    val radians = angle * Math.PI / 180

    // Create a wave effect
    val waveAmplitude = radius * 0.02f
    val waveFrequency = 8f

    val path =
      Path().apply {
        val startX = center.x + cos(radians).toFloat() * (radius * 0.2f)
        val startY = center.y + sin(radians).toFloat() * (radius * 0.2f)

        moveTo(startX, startY)

        for (i in 0..100) {
          val t = i / 100f
          val distance = radius * 0.2f + t * (patternRadius - radius * 0.2f)
          val waveOffset = sin(t * waveFrequency * Math.PI).toFloat() * waveAmplitude

          val x =
            center.x +
              cos(radians).toFloat() * distance +
              cos(radians + Math.PI / 2).toFloat() * waveOffset
          val y =
            center.y +
              sin(radians).toFloat() * distance +
              sin(radians + Math.PI / 2).toFloat() * waveOffset

          lineTo(x, y)
        }
      }

    drawPath(path = path, color = Color(0xFF2A3C5A), style = Stroke(width = 0.5f))
  }
}

private fun DrawScope.drawPontifexDateWindow(center: Offset, radius: Float, timeZone: TimeZone) {
  val dateWindowX = center.x + radius * 0.6f
  val dateWindowY = center.y
  val dateWindowWidth = radius * 0.15f
  val dateWindowHeight = radius * 0.1f

  // Draw date window background
  drawRect(
    color = Color(0xFF0A1525),
    topLeft = Offset(dateWindowX - dateWindowWidth / 2, dateWindowY - dateWindowHeight / 2),
    size = Size(dateWindowWidth, dateWindowHeight)
  )

  // Draw date window border
  drawRect(
    color = AccentColor,
    topLeft = Offset(dateWindowX - dateWindowWidth / 2, dateWindowY - dateWindowHeight / 2),
    size = Size(dateWindowWidth, dateWindowHeight),
    style = Stroke(width = 1f)
  )

  // Draw date text
  val datePaint =
    Paint().apply {
      color = MarkersColor.hashCode()
      textSize = radius * 0.06f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  // Get current date
  val calendar = Calendar.getInstance(timeZone)
  val date = calendar.get(Calendar.DAY_OF_MONTH)

  drawContext.canvas.nativeCanvas.drawText(
    date.toString(),
    dateWindowX,
    dateWindowY + radius * 0.02f,
    datePaint
  )
}

private fun DrawScope.drawPontifexLogo(center: Offset, radius: Float) {
  val brandPaint =
    Paint().apply {
      color = MarkersColor.hashCode()
      textSize = radius * 0.07f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "PONTIFEX",
    center.x,
    center.y - radius * 0.25f,
    brandPaint
  )

  val fleurierPaint =
    Paint().apply {
      color = MarkersColor.hashCode()
      textSize = radius * 0.05f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "CHRONOMETRA",
    center.x,
    center.y - radius * 0.15f,
    fleurierPaint
  )

  // Draw "SWISS MADE" text
  val swissMadePaint =
    Paint().apply {
      color = MarkersColor.hashCode()
      textSize = radius * 0.04f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "SWISS MADE",
    center.x,
    center.y + radius * 0.5f,
    swissMadePaint
  )
}

private fun DrawScope.drawPontifexClockHands(center: Offset, radius: Float, time: WatchTime) {
  // Hour hand - Delta-shaped (Parmigiani style)
  rotate(time.hourAngle, pivot = center) {
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.45f) // Tip
        lineTo(center.x + radius * 0.04f, center.y - radius * 0.2f) // Right shoulder
        lineTo(center.x + radius * 0.015f, center.y) // Right base
        lineTo(center.x - radius * 0.015f, center.y) // Left base
        lineTo(center.x - radius * 0.04f, center.y - radius * 0.2f) // Left shoulder
        close()
      }
    drawPath(path, HourHandColor)
  }

  // Minute hand - Delta-shaped
  rotate(time.minuteAngle, pivot = center) {
    val path =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.65f) // Tip
        lineTo(center.x + radius * 0.03f, center.y - radius * 0.3f) // Right shoulder
        lineTo(center.x + radius * 0.01f, center.y) // Right base
        lineTo(center.x - radius * 0.01f, center.y) // Left base
        lineTo(center.x - radius * 0.03f, center.y - radius * 0.3f) // Left shoulder
        close()
      }
    drawPath(path, MinuteHandColor)
  }

  // Second hand with oval counterbalance
  rotate(time.secondAngle, pivot = center) {
    // Main hand
    drawLine(
      color = SecondHandColor,
      start = Offset(center.x, center.y + radius * 0.2f),
      end = Offset(center.x, center.y - radius * 0.75f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )

    // Oval counterbalance
    val ovalPath =
      Path().apply {
        val ovalWidth = radius * 0.05f
        val ovalHeight = radius * 0.1f

        // Draw oval
        moveTo(center.x, center.y + ovalHeight)
        quadraticTo(
          center.x + ovalWidth,
          center.y + ovalHeight,
          center.x + ovalWidth,
          center.y + ovalHeight / 2
        )
        quadraticTo(center.x + ovalWidth, center.y, center.x, center.y)
        quadraticTo(center.x - ovalWidth, center.y, center.x - ovalWidth, center.y + ovalHeight / 2)
        quadraticTo(center.x - ovalWidth, center.y + ovalHeight, center.x, center.y + ovalHeight)
        close()
      }

    drawPath(path = ovalPath, color = SecondHandColor)
  }
}

private fun DrawScope.drawPontifexCenterDot(center: Offset, radius: Float) {
  drawCircle(color = AccentColor, radius = radius * 0.02f, center = center)
}

@Preview(showBackground = true)
@Composable
fun PontifexChronometraPreview() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    PontifexChronometraWatchface()
  }
}
