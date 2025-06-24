package com.coroutines.swisstime.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Omega Seamaster 300m (blue dial with stainless steel case)
private val ClockFaceColor = Color(0xFF0A4D8C) // Deep blue dial
private val ClockBorderColor = Color(0xFFE0E0E0) // Stainless steel border
private val HourHandColor = Color(0xFFE0E0E0) // Steel hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Steel minute hand
private val SecondHandColor = Color(0xFFFF5252) // Red second hand
private val MarkersColor = Color(0xFFE0E0E0) // Steel markers
private val NumbersColor = Color(0xFFE0E0E0) // Steel numbers
private val CenterDotColor = Color(0xFFE0E0E0) // Steel center dot
private val DigitalTimeColor = Color(0xFFE0E0E0) // Steel digital time
private val WavePatternColor = Color(0xFF0D5DA6) // Slightly lighter blue for wave pattern

@Composable
fun OmegaSeamasterClock(modifier: Modifier = Modifier) {
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

    // Draw wave pattern (characteristic of Seamaster)
    val waveCount = 12
    val waveAmplitude = radius * 0.05f
    val waveRadius = radius * 0.7f

    for (i in 0 until 360 step 10) {
        val angle = Math.toRadians(i.toDouble())
        val waveOffset = (sin(angle * waveCount) * waveAmplitude).toFloat()
        val x1 = center.x + cos(angle).toFloat() * (waveRadius - waveOffset)
        val y1 = center.y + sin(angle).toFloat() * (waveRadius - waveOffset)
        val x2 = center.x + cos(angle).toFloat() * (waveRadius + waveOffset)
        val y2 = center.y + sin(angle).toFloat() * (waveRadius + waveOffset)

        drawLine(
            color = WavePatternColor,
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 1.5f
        )
    }

    // Draw rotating bezel with minute markers
    drawCircle(
        color = ClockBorderColor,
        radius = radius * 0.95f,
        center = center,
        style = Stroke(width = 5f)
    )

    // Draw bezel markers
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60
        val markerLength = if (i % 5 == 0) radius * 0.05f else radius * 0.02f
        val startX = center.x + cos(angle).toFloat() * (radius * 0.95f - markerLength)
        val startY = center.y + sin(angle).toFloat() * (radius * 0.95f - markerLength)
        val endX = center.x + cos(angle).toFloat() * radius * 0.95f
        val endY = center.y + sin(angle).toFloat() * radius * 0.95f

        drawLine(
            color = ClockBorderColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 5 == 0) 2f else 1f,
            cap = StrokeCap.Round
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

    // Seamaster typically uses rectangular hour markers
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerOuterRadius = radius * 0.85f
        val markerInnerRadius = radius * 0.75f
        val markerWidth = radius * 0.04f

        val outerX = center.x + cos(angle).toFloat() * markerOuterRadius
        val outerY = center.y + sin(angle).toFloat() * markerOuterRadius
        val innerX = center.x + cos(angle).toFloat() * markerInnerRadius
        val innerY = center.y + sin(angle).toFloat() * markerInnerRadius

        // Calculate perpendicular direction for rectangle width
        val perpAngle = angle + Math.PI / 2
        val perpX = cos(perpAngle).toFloat() * markerWidth / 2
        val perpY = sin(perpAngle).toFloat() * markerWidth / 2

        // Draw rectangular hour markers
        drawLine(
            color = MarkersColor,
            start = Offset(outerX + perpX, outerY + perpY),
            end = Offset(innerX + perpX, innerY + perpY),
            strokeWidth = markerWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = MarkersColor,
            start = Offset(outerX - perpX, outerY - perpY),
            end = Offset(innerX - perpX, innerY - perpY),
            strokeWidth = markerWidth,
            cap = StrokeCap.Round
        )

        // Draw small dots at 5-minute intervals between hour markers
        if (i < 12) {
            for (j in 1..4) {
                val minuteAngle = Math.PI / 6 * (i - 3) + Math.PI / 30 * j
                val minuteX = center.x + cos(minuteAngle).toFloat() * radius * 0.85f
                val minuteY = center.y + sin(minuteAngle).toFloat() * radius * 0.85f

                drawCircle(
                    color = MarkersColor,
                    radius = radius * 0.01f,
                    center = Offset(minuteX, minuteY)
                )
            }
        }
    }
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand (skeleton style with luminous tip)
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Broad arrow-shaped hour hand (Seamaster style)
        val handLength = radius * 0.5f
        val handWidth = radius * 0.07f

        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - handLength),
            strokeWidth = handWidth,
            cap = StrokeCap.Round
        )
    }

    // Minute hand (skeleton style with luminous tip)
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Broad arrow-shaped minute hand (Seamaster style)
        val handLength = radius * 0.7f
        val handWidth = radius * 0.05f

        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - handLength),
            strokeWidth = handWidth,
            cap = StrokeCap.Round
        )
    }

    // Second hand with lollipop counterbalance (Seamaster style)
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Lollipop counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OmegaSeamasterClockPreview() {
    SwissTimeTheme {
        OmegaSeamasterClock()
    }
}
