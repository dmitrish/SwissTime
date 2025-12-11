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

// Urushi lacquer inspired colors
private val UrushiRed = Color(0xFF8C0000) // Deep red lacquer
private val UrushiBlack = Color(0xFF1A1A1A) // Black lacquer
private val UrushiGold = Color(0xFFD9A441) // Gold accent
private val UrushiCream = Color(0xFFF2F0E6) // Cream/ivory

@Composable
fun KnotUrushiRed(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
  WatchfaceScaffold(
    modifier = modifier,
    timeZone = timeZone,
    staticContent = { center, radius, _ ->
      drawKnotClockFace(center, radius)
      drawKnotPattern(center, radius)
      drawKnotHourMarkers(center, radius)
      drawKnotLogo(center, radius)
      drawKnotDateWindow(center, radius, timeZone)
    },
    animatedContent = { center, radius, currentTime, _ ->
      val time = currentTime.toWatchTime()
      drawKnotClockHands(center, radius, time)
      drawKnotCenterDot(center, radius)
    }
  )
}

private fun DrawScope.drawKnotClockFace(center: Offset, radius: Float) {
  // Outer border (gold bezel)
  drawCircle(color = UrushiGold, radius = radius, center = center, style = Stroke(width = 8f))

  // Main dial (urushi lacquer red)
  drawCircle(color = UrushiRed, radius = radius * 0.95f, center = center)

  // Inner decorative circle
  drawCircle(
    color = UrushiGold,
    radius = radius * 0.7f,
    center = center,
    style = Stroke(width = 1f)
  )
}

private fun DrawScope.drawKnotPattern(center: Offset, radius: Float) {
  // Draw Celtic/Japanese inspired knot pattern
  // This creates an interwoven circular pattern characteristic of traditional Japanese design

  val knotRadius = radius * 0.4f
  val knotCount = 8

  for (i in 0 until knotCount) {
    val angle1 = Math.PI * 2 * i / knotCount
    val angle2 = Math.PI * 2 * (i + 1) / knotCount

    val x1 = center.x + cos(angle1).toFloat() * knotRadius
    val y1 = center.y + sin(angle1).toFloat() * knotRadius
    val x2 = center.x + cos(angle2).toFloat() * knotRadius
    val y2 = center.y + sin(angle2).toFloat() * knotRadius

    // Create curved segments
    val path =
      Path().apply {
        moveTo(x1, y1)

        val controlAngle = (angle1 + angle2) / 2
        val controlRadius = knotRadius * 0.7f
        val controlX = center.x + cos(controlAngle).toFloat() * controlRadius
        val controlY = center.y + sin(controlAngle).toFloat() * controlRadius

        quadraticTo(controlX, controlY, x2, y2)
      }

    drawPath(
      path = path,
      color = UrushiGold.copy(alpha = 0.5f),
      style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
  }

  // Draw interlocking circles
  for (i in 0 until 4) {
    val angle = Math.PI / 2 * i
    val circleX = center.x + cos(angle).toFloat() * radius * 0.3f
    val circleY = center.y + sin(angle).toFloat() * radius * 0.3f

    drawCircle(
      color = UrushiGold.copy(alpha = 0.6f),
      radius = radius * 0.1f,
      center = Offset(circleX, circleY),
      style = Stroke(width = 2f)
    )
  }
}

private fun DrawScope.drawKnotHourMarkers(center: Offset, radius: Float) {
  // Draw elegant hour markers with Japanese aesthetic
  for (i in 0 until 12) {
    val angle = Math.PI / 6 * i - Math.PI / 2

    // Outer markers (larger for 12, 3, 6, 9)
    val isCardinal = i % 3 == 0
    val markerLength = if (isCardinal) radius * 0.12f else radius * 0.08f
    val markerWidth = if (isCardinal) 3f else 2f

    val outerX = center.x + cos(angle).toFloat() * radius * 0.85f
    val outerY = center.y + sin(angle).toFloat() * radius * 0.85f
    val innerX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
    val innerY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)

    drawLine(
      color = UrushiCream,
      start = Offset(outerX, outerY),
      end = Offset(innerX, innerY),
      strokeWidth = markerWidth,
      cap = StrokeCap.Round
    )

    // Add gold accent to cardinal markers
    if (isCardinal) {
      val dotX = center.x + cos(angle).toFloat() * radius * 0.9f
      val dotY = center.y + sin(angle).toFloat() * radius * 0.9f

      drawCircle(color = UrushiGold, radius = 3f, center = Offset(dotX, dotY))
    }
  }
}

private fun DrawScope.drawKnotLogo(center: Offset, radius: Float) {
  // Draw Japanese-style branding
  val logoPaint =
    Paint().apply {
      color = UrushiCream.hashCode()
      textSize = radius * 0.15f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  // Japanese character "結" (Knot)
  drawContext.canvas.nativeCanvas.drawText(
    "結",
    center.x,
    center.y - radius * 0.5f + logoPaint.textSize / 3,
    logoPaint
  )

  // Subtitle "URUSHI"
  val subtitlePaint =
    Paint().apply {
      color = UrushiGold.hashCode()
      textSize = radius * 0.08f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "URUSHI",
    center.x,
    center.y - radius * 0.35f + subtitlePaint.textSize / 3,
    subtitlePaint
  )
}

private fun DrawScope.drawKnotDateWindow(center: Offset, radius: Float, timeZone: TimeZone) {
  // Date window at 6 o'clock position
  val dateX = center.x
  val dateY = center.y + radius * 0.5f

  // Date window background
  drawRoundRect(
    color = UrushiCream,
    topLeft = Offset(dateX - radius * 0.12f, dateY - radius * 0.08f),
    size = androidx.compose.ui.geometry.Size(radius * 0.24f, radius * 0.16f),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
  )

  // Date window border
  drawRoundRect(
    color = UrushiGold,
    topLeft = Offset(dateX - radius * 0.12f, dateY - radius * 0.08f),
    size = androidx.compose.ui.geometry.Size(radius * 0.24f, radius * 0.16f),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
    style = Stroke(width = 1f)
  )

  // Date text
  val datePaint =
    Paint().apply {
      color = UrushiBlack.hashCode()
      textSize = radius * 0.1f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  val calendar = java.util.Calendar.getInstance(timeZone)
  val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

  drawContext.canvas.nativeCanvas.drawText(
    day.toString(),
    dateX,
    dateY + datePaint.textSize / 3,
    datePaint
  )
}

private fun DrawScope.drawKnotClockHands(center: Offset, radius: Float, time: WatchTime) {
  // Hour hand (katana sword style)
  rotate(time.hourAngle, pivot = center) {
    val hourPath =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.45f) // Tip
        lineTo(center.x + radius * 0.03f, center.y - radius * 0.35f)
        lineTo(center.x + radius * 0.02f, center.y + radius * 0.1f)
        lineTo(center.x - radius * 0.02f, center.y + radius * 0.1f)
        lineTo(center.x - radius * 0.03f, center.y - radius * 0.35f)
        close()
      }

    // Fill with cream
    drawPath(hourPath, UrushiCream)

    // Gold outline
    drawPath(path = hourPath, color = UrushiGold, style = Stroke(width = 1f))
  }

  // Minute hand (katana sword style, longer)
  rotate(time.minuteAngle, pivot = center) {
    val minutePath =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.65f) // Tip
        lineTo(center.x + radius * 0.025f, center.y - radius * 0.5f)
        lineTo(center.x + radius * 0.015f, center.y + radius * 0.1f)
        lineTo(center.x - radius * 0.015f, center.y + radius * 0.1f)
        lineTo(center.x - radius * 0.025f, center.y - radius * 0.5f)
        close()
      }

    // Fill with cream
    drawPath(minutePath, UrushiCream)

    // Gold outline
    drawPath(path = minutePath, color = UrushiGold, style = Stroke(width = 1f))
  }

  // Second hand (thin, elegant)
  rotate(time.secondAngle, pivot = center) {
    drawLine(
      color = UrushiGold,
      start = Offset(center.x, center.y + radius * 0.15f),
      end = Offset(center.x, center.y - radius * 0.75f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )

    // Second hand counterweight
    drawOval(
      color = UrushiGold,
      topLeft = Offset(center.x - radius * 0.02f, center.y + radius * 0.1f),
      size = androidx.compose.ui.geometry.Size(radius * 0.04f, radius * 0.08f)
    )
  }
}

private fun DrawScope.drawKnotCenterDot(center: Offset, radius: Float) {
  // Outer ring (gold)
  drawCircle(color = UrushiGold, radius = radius * 0.04f, center = center)

  // Inner dot (red)
  drawCircle(color = UrushiRed, radius = radius * 0.02f, center = center)
}

@Preview(showBackground = true)
@Composable
fun KnotUrushiRedPreview() {

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { KnotUrushiRed() }
}
