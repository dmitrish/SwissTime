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
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
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
fun Valentinianus(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone )) }

    val timeZoneX by rememberUpdatedState(timeZone)
    // Update time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = Calendar.getInstance(timeZoneX)
            delay(1000) // Update every second
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
            drawCircle(
                color = CenterDotColor,
                radius = radius * 0.03f,
                center = center
            )
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
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

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {

    
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

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - slim and elegant
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.5f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }

    // Minute hand - slim and elegant
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    // Second hand - very thin with a distinctive counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
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

@Preview(showBackground = true)
@Composable
fun VacheronConstantinClockPreview() {
    SwissTimeTheme {
        Valentinianus()
    }
}