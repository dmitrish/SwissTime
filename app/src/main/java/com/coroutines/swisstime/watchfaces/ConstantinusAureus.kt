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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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

// Colors inspired by Ulysse Nardin Marine Chronometer
private val ClockFaceColor = Color(0xFFFFFFFF) // White dial
private val ClockBorderColor = Color(0xFF00008B) // Dark blue border
private val HourHandColor = Color(0xFF00008B) // Dark blue hour hand
private val MinuteHandColor = Color(0xFF00008B) // Dark blue minute hand
private val SecondHandColor = Color(0xFFB22222) // Red second hand
private val MarkersColor = Color(0xFF00008B) // Dark blue markers
private val NumbersColor = Color(0xFF00008B) // Dark blue numbers
private val PowerReserveColor = Color(0xFFB22222) // Red power reserve indicator
private val CenterDotColor = Color(0xFF00008B) // Dark blue center dot

@Composable
fun ConstantinusAureusChronometer(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault()
) {
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

      // Draw clock face with current time
      drawClockFaceWithTime(center, radius, currentTime, timeZoneX)

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
  drawClockFaceWithTime(center, radius, Calendar.getInstance())
}

private fun DrawScope.drawClockFaceWithTime(
  center: Offset,
  radius: Float,
  currentTime: Calendar,
  timeZone: TimeZone = TimeZone.getDefault()
) {
  // Draw outer circle (border)
  drawCircle(color = ClockBorderColor, radius = radius, center = center, style = Stroke(width = 8f))

  // Draw inner circle (face)
  drawCircle(color = ClockFaceColor, radius = radius - 4f, center = center)

  // Draw Ulysse Nardin logo (anchor symbol)
  val anchorSize = radius * 0.15f
  val anchorY = center.y - radius * 0.3f

  // Draw anchor stock (top horizontal bar)
  drawLine(
    color = ClockBorderColor,
    start = Offset(center.x - anchorSize / 2, anchorY - anchorSize / 4),
    end = Offset(center.x + anchorSize / 2, anchorY - anchorSize / 4),
    strokeWidth = anchorSize / 8
  )

  // Draw anchor stem (vertical bar)
  drawLine(
    color = ClockBorderColor,
    start = Offset(center.x, anchorY - anchorSize / 4),
    end = Offset(center.x, anchorY + anchorSize / 2),
    strokeWidth = anchorSize / 10
  )

  // Draw anchor arms (curved parts)
  val armWidth = anchorSize / 2
  val armHeight = anchorSize / 3

  // Left arm
  val leftArmPath =
    Path().apply {
      moveTo(center.x, anchorY + anchorSize / 4)
      quadraticBezierTo(
        center.x - armWidth / 2,
        anchorY + anchorSize / 4, // Control point
        center.x - armWidth,
        anchorY + armHeight // End point
      )
    }
  drawPath(leftArmPath, ClockBorderColor, style = Stroke(width = anchorSize / 10))

  // Right arm
  val rightArmPath =
    Path().apply {
      moveTo(center.x, anchorY + anchorSize / 4)
      quadraticBezierTo(
        center.x + armWidth / 2,
        anchorY + anchorSize / 4, // Control point
        center.x + armWidth,
        anchorY + armHeight // End point
      )
    }
  drawPath(rightArmPath, ClockBorderColor, style = Stroke(width = anchorSize / 10))

  // Draw "ULYSSE NARDIN" text
  val logoPaint =
    Paint().apply {
      color = ClockBorderColor.hashCode()
      textSize = radius * 0.1f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "CONSTANTINUS",
    center.x,
    center.y - radius * 0.1f,
    logoPaint
  )

  // Draw "MARINE CHRONOMETER" text
  val modelPaint =
    Paint().apply {
      color = ClockBorderColor.hashCode()
      textSize = radius * 0.06f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText("AUREUS CHRONOMETER", center.x, center.y, modelPaint)

  // Draw power reserve indicator at 12 o'clock
  val powerReserveY = center.y - radius * 0.5f
  val powerReserveWidth = radius * 0.4f
  val powerReserveHeight = radius * 0.1f

  // Power reserve background (semi-circular)
  val powerReservePath =
    Path().apply {
      // Draw semi-circle
      arcTo(
        Rect(
          left = center.x - powerReserveWidth / 2,
          top = powerReserveY - powerReserveHeight,
          right = center.x + powerReserveWidth / 2,
          bottom = powerReserveY + powerReserveHeight
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
      )
    }

  drawPath(path = powerReservePath, color = Color.White, style = Stroke(width = powerReserveHeight))

  // Power reserve border
  drawPath(path = powerReservePath, color = ClockBorderColor, style = Stroke(width = 2f))

  // Power reserve markings
  for (i in 0..4) {
    val angle = Math.PI * i / 4
    val markerX = center.x + cos(angle + Math.PI).toFloat() * (powerReserveWidth / 2)
    val markerY = powerReserveY

    drawCircle(color = ClockBorderColor, radius = 2f, center = Offset(markerX, markerY))
  }

  // Power reserve text
  val powerReservePaint =
    Paint().apply {
      color = ClockBorderColor.hashCode()
      textSize = powerReserveHeight * 0.5f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "NAUTILUS CENTURION",
    center.x,
    powerReserveY - powerReserveHeight * 1.2f,
    powerReservePaint
  )

  // Power reserve indicator (simulated at 60% full)
  val indicatorAngle = Math.PI * 0.6
  val indicatorPath =
    Path().apply {
      // Draw partial semi-circle
      arcTo(
        Rect(
          left = center.x - powerReserveWidth / 2,
          top = powerReserveY - powerReserveHeight,
          right = center.x + powerReserveWidth / 2,
          bottom = powerReserveY + powerReserveHeight
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 180f * 0.6f,
        forceMoveTo = false
      )
    }

  drawPath(
    path = indicatorPath,
    color = PowerReserveColor,
    style = Stroke(width = powerReserveHeight * 0.8f)
  )

  // Draw date window at 6 o'clock
  val dateY = center.y + radius * 0.5f

  // Date window (rectangular with rounded corners)
  drawRoundRect(
    color = Color.White,
    topLeft = Offset(center.x - radius * 0.15f, dateY - radius * 0.08f),
    size = Size(radius * 0.3f, radius * 0.16f),
    cornerRadius = CornerRadius(radius * 0.02f)
  )
  drawRoundRect(
    color = ClockBorderColor,
    topLeft = Offset(center.x - radius * 0.15f, dateY - radius * 0.08f),
    size = Size(radius * 0.3f, radius * 0.16f),
    cornerRadius = CornerRadius(radius * 0.02f),
    style = Stroke(width = 2f)
  )

  // Date text
  val datePaint =
    Paint().apply {
      color = ClockBorderColor.hashCode()
      textSize = radius * 0.1f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  val day = currentTime.get(Calendar.DAY_OF_MONTH).toString()
  drawContext.canvas.nativeCanvas.drawText(day, center.x, dateY + radius * 0.04f, datePaint)
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
  // Ulysse Nardin Marine Chronometer typically uses Roman numerals
  val romanNumerals =
    listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")

  val textPaint =
    Paint().apply {
      color = NumbersColor.hashCode()
      textSize = radius * 0.12f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  // Draw Roman numerals
  for (i in 0 until 12) {
    // Skip VI (6 o'clock) and XII (12 o'clock) where the date window and power reserve are
    if (i == 5 || i == 0) continue

    val angle = Math.PI / 6 * i - Math.PI / 3
    val numberRadius = radius * 0.7f
    val numberX = center.x + cos(angle).toFloat() * numberRadius
    val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

    drawContext.canvas.nativeCanvas.drawText(romanNumerals[i], numberX, numberY, textPaint)
  }

  // Draw minute markers (small dots)
  for (i in 0 until 60) {
    if (i % 5 == 0) continue // Skip where hour markers are

    val angle = Math.PI * 2 * i / 60
    val markerRadius = radius * 0.01f
    val markerX = center.x + cos(angle).toFloat() * radius * 0.85f
    val markerY = center.y + sin(angle).toFloat() * radius * 0.85f

    drawCircle(color = MarkersColor, radius = markerRadius, center = Offset(markerX, markerY))
  }

  // Draw railroad-style minute track (characteristic of marine chronometers)
  val trackRadius = radius * 0.85f
  val trackWidth = radius * 0.03f

  drawCircle(
    color = Color.Transparent,
    radius = trackRadius,
    center = center,
    style = Stroke(width = trackWidth)
  )

  // Draw minute track divisions
  for (i in 0 until 60) {
    val angle = Math.PI * 2 * i / 60
    val innerRadius = trackRadius - trackWidth / 2
    val outerRadius = trackRadius + trackWidth / 2

    val innerX = center.x + cos(angle).toFloat() * innerRadius
    val innerY = center.y + sin(angle).toFloat() * innerRadius
    val outerX = center.x + cos(angle).toFloat() * outerRadius
    val outerY = center.y + sin(angle).toFloat() * outerRadius

    drawLine(
      color = MarkersColor.copy(alpha = 0.3f),
      start = Offset(innerX, innerY),
      end = Offset(outerX, outerY),
      strokeWidth = 1f
    )
  }
}

private fun DrawScope.drawClockHands(
  center: Offset,
  radius: Float,
  hour: Int,
  minute: Int,
  second: Int
) {
  // Hour hand - pear-shaped (characteristic of marine chronometers)
  val hourAngle = (hour * 30 + minute * 0.5f)
  rotate(hourAngle) {
    val hourHandPath =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.5f) // Tip
        quadraticBezierTo(
          center.x + radius * 0.04f,
          center.y - radius * 0.25f, // Control point
          center.x + radius * 0.02f,
          center.y // End point
        )
        quadraticBezierTo(
          center.x,
          center.y + radius * 0.1f, // Control point
          center.x - radius * 0.02f,
          center.y // End point
        )
        quadraticBezierTo(
          center.x - radius * 0.04f,
          center.y - radius * 0.25f, // Control point
          center.x,
          center.y - radius * 0.5f // End point (back to start)
        )
        close()
      }
    drawPath(hourHandPath, HourHandColor)
  }

  // Minute hand - longer pear-shaped
  val minuteAngle = minute * 6f
  rotate(minuteAngle) {
    val minuteHandPath =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.7f) // Tip
        quadraticBezierTo(
          center.x + radius * 0.03f,
          center.y - radius * 0.35f, // Control point
          center.x + radius * 0.015f,
          center.y // End point
        )
        quadraticBezierTo(
          center.x,
          center.y + radius * 0.1f, // Control point
          center.x - radius * 0.015f,
          center.y // End point
        )
        quadraticBezierTo(
          center.x - radius * 0.03f,
          center.y - radius * 0.35f, // Control point
          center.x,
          center.y - radius * 0.7f // End point (back to start)
        )
        close()
      }
    drawPath(minuteHandPath, MinuteHandColor)
  }

  // Second hand - thin with distinctive arrow tip
  val secondAngle = second * 6f
  rotate(secondAngle) {
    // Main second hand
    drawLine(
      color = SecondHandColor,
      start = Offset(center.x, center.y + radius * 0.15f),
      end = Offset(center.x, center.y - radius * 0.75f),
      strokeWidth = 2f,
      cap = StrokeCap.Round
    )

    // Arrow tip
    val arrowSize = radius * 0.05f
    val arrowPath =
      Path().apply {
        moveTo(center.x, center.y - radius * 0.75f - arrowSize) // Tip
        lineTo(center.x + arrowSize / 2, center.y - radius * 0.75f) // Right corner
        lineTo(center.x - arrowSize / 2, center.y - radius * 0.75f) // Left corner
        close()
      }
    drawPath(arrowPath, SecondHandColor)

    // Counterbalance
    drawCircle(
      color = SecondHandColor,
      radius = radius * 0.03f,
      center = Offset(center.x, center.y + radius * 0.1f)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun UlysseNardinMarineChrometerPreview() {
  SwissTimeTheme { ConstantinusAureusChronometer() }
}
