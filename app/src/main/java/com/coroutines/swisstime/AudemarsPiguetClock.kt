package com.coroutines.swisstime

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Audemars Piguet Royal Oak (blue dial with steel case)
private val ClockFaceColor = Color(0xFF0F3460) // Deep blue dial
private val ClockBorderColor = Color(0xFFE0E0E0) // Stainless steel border
private val HourHandColor = Color(0xFFE0E0E0) // Steel hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Steel minute hand
private val SecondHandColor = Color(0xFFFF5252) // Red second hand
private val MarkersColor = Color(0xFFE0E0E0) // Steel markers
private val NumbersColor = Color(0xFFE0E0E0) // Steel numbers
private val CenterDotColor = Color(0xFFE0E0E0) // Steel center dot
private val DigitalTimeColor = Color(0xFFE0E0E0) // Steel digital time

@Composable
fun AudemarsPiguetClock(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    // Update time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = Calendar.getInstance()
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
                radius = radius * 0.05f,
                center = center
            )
        }

        // Display digital time in the center
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTime.time),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DigitalTimeColor
            )
        )
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw outer circle (border)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 4f,
        center = center
    )

    // Draw octagonal bezel (characteristic of Royal Oak)
    val octagonPoints = mutableListOf<Offset>()
    for (i in 0 until 8) {
        val angle = Math.PI / 4 * i
        val x = center.x + cos(angle).toFloat() * (radius * 0.95f)
        val y = center.y + sin(angle).toFloat() * (radius * 0.95f)
        octagonPoints.add(Offset(x, y))
    }

    for (i in 0 until 8) {
        drawLine(
            color = ClockBorderColor,
            start = octagonPoints[i],
            end = octagonPoints[(i + 1) % 8],
            strokeWidth = 5f
        )
    }
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.15f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerLength = if (i % 3 == 0) radius * 0.1f else radius * 0.05f
        val startX = center.x + cos(angle).toFloat() * (radius - markerLength)
        val startY = center.y + sin(angle).toFloat() * (radius - markerLength)
        val endX = center.x + cos(angle).toFloat() * radius * 0.9f
        val endY = center.y + sin(angle).toFloat() * radius * 0.9f

        // Draw hour markers
        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 3f else 1.5f,
            cap = StrokeCap.Round
        )

        // Draw hour numbers
        val numberRadius = radius * 0.75f
        val numberX = center.x + cos(angle).toFloat() * numberRadius
        val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

        drawContext.canvas.nativeCanvas.drawText(
            i.toString(),
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
    // Hour hand
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.5f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
    }

    // Minute hand
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }

    // Second hand
    val secondAngle = second * 6f
    rotate(secondAngle) {
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AudemarsPiguetClockPreview() {
    SwissTimeTheme {
        AudemarsPiguetClock()
    }
}
