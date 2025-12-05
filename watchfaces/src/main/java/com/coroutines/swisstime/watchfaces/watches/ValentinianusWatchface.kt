package com.coroutines.swisstime.watchfaces.watches


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
import com.coroutines.swisstime.watchfaces.scaffold.WatchTime
import com.coroutines.swisstime.watchfaces.scaffold.WatchfaceScaffold
import com.coroutines.swisstime.watchfaces.scaffold.toWatchTime
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

private val ClockFaceColor = Color(0xFFF8F5E6) // Ivory/cream dial
private val ClockBorderColor = Color(0xFFB27D4B) // Rose gold border
private val HourHandColor = Color(0xFFB27D4B) // Rose gold hour hand
private val MinuteHandColor = Color(0xFFB27D4B) // Rose gold minute hand
private val SecondHandColor = Color(0xFF8B4513) // Brown second hand
private val MarkersColor = Color(0xFFB27D4B) // Rose gold markers
private val NumbersColor = Color(0xFFB27D4B) // Rose gold numbers
private val CenterDotColor = Color(0xFFB27D4B) // Rose gold center dot

@Composable
fun ValentinianusWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawValentinianusClockFace(center, radius)
            drawValentinianusHourMarkersAndNumbers(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawValentinianusClockHands(center, radius, time)
            drawValentinianusCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawValentinianusClockFace(center: Offset, radius: Float) {
    // Draw outer circle (border)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 6f)
    )

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 3f,
        center = center
    )

    // Draw subtle concentric circles for texture (guilloche pattern)
    for (i in 1..5) {
        drawCircle(
            color = ClockBorderColor.copy(alpha = 0.05f),
            radius = radius * (0.9f - i * 0.1f),
            center = center,
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawValentinianusHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Draw hour markers (slim lines)
    for (i in 1..60) {
        val angle = Math.PI / 30 * (i - 15)
        val markerLength = if (i % 5 == 0) radius * 0.08f else radius * 0.03f
        val strokeWidth = if (i % 5 == 0) 2f else 1f

        val startX = center.x + cos(angle).toFloat() * (radius - markerLength)
        val startY = center.y + sin(angle).toFloat() * (radius - markerLength)
        val endX = center.x + cos(angle).toFloat() * radius * 0.9f
        val endY = center.y + sin(angle).toFloat() * radius * 0.9f

        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }

    // Draw Roman numerals at 12, 3, 6, 9
    val romanNumerals = listOf("XII", "III", "VI", "IX")
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false // Slim font for elegance
        isAntiAlias = true
    }

    for (i in 0..3) {
        val angle = Math.PI / 2 * i
        val numberRadius = radius * 0.75f
        val numberX = center.x + cos(angle).toFloat() * numberRadius
        val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

        drawContext.canvas.nativeCanvas.drawText(
            romanNumerals[i],
            numberX,
            numberY,
            textPaint
        )
    }
}

private fun DrawScope.drawValentinianusClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand - slim and elegant
    rotate(time.hourAngle, pivot = center) {
        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.5f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }

    // Minute hand - slim and elegant
    rotate(time.minuteAngle, pivot = center) {
        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    // Second hand - very thin with a distinctive counterbalance
    rotate(time.secondAngle, pivot = center) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        // Counterbalance
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y + radius * 0.2f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawValentinianusCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = CenterDotColor,
        radius = radius * 0.03f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun ValentinianusPreview() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ValentinianusWatchface()
      }
}