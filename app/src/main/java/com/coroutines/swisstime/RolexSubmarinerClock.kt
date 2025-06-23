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

// Colors inspired by Rolex Submariner (black dial with gold accents)
private val ClockFaceColor = Color(0xFF000000) // Black dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold border
private val HourHandColor = Color(0xFFD4AF37) // Gold hour hand
private val MinuteHandColor = Color(0xFFD4AF37) // Gold minute hand
private val SecondHandColor = Color(0xFFD4AF37) // Gold second hand
private val MarkersColor = Color(0xFFD4AF37) // Gold markers
private val NumbersColor = Color(0xFFD4AF37) // Gold numbers
private val CenterDotColor = Color(0xFFD4AF37) // Gold center dot
private val DigitalTimeColor = Color(0xFFD4AF37) // Gold digital time
private val BezelColor = Color(0xFF00008B) // Dark blue bezel

@Composable
fun RolexSubmarinerClock(modifier: Modifier = Modifier) {
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

    // Draw rotating bezel (characteristic of Submariner)
    drawCircle(
        color = BezelColor,
        radius = radius * 0.95f,
        center = center
    )

    // Draw bezel markers (triangles and dots)
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60

        if (i % 5 == 0) {
            // Draw triangle at 12 o'clock position
            if (i == 0) {
                val triangleSize = radius * 0.06f
                val x1 = center.x + cos(angle).toFloat() * (radius * 0.95f - triangleSize)
                val y1 = center.y + sin(angle).toFloat() * (radius * 0.95f - triangleSize)
                val x2 = center.x + cos(angle - 0.1).toFloat() * (radius * 0.95f)
                val y2 = center.y + sin(angle - 0.1).toFloat() * (radius * 0.95f)
                val x3 = center.x + cos(angle + 0.1).toFloat() * (radius * 0.95f)
                val y3 = center.y + sin(angle + 0.1).toFloat() * (radius * 0.95f)

                drawLine(color = MarkersColor, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 3f)
                drawLine(color = MarkersColor, start = Offset(x2, y2), end = Offset(x3, y3), strokeWidth = 3f)
                drawLine(color = MarkersColor, start = Offset(x3, y3), end = Offset(x1, y1), strokeWidth = 3f)
            } else {
                // Draw larger dots at 5-minute intervals
                val dotX = center.x + cos(angle).toFloat() * radius * 0.95f
                val dotY = center.y + sin(angle).toFloat() * radius * 0.95f

                drawCircle(
                    color = MarkersColor,
                    radius = radius * 0.03f,
                    center = Offset(dotX, dotY)
                )
            }
        } else {
            // Draw smaller dots for minutes
            val dotX = center.x + cos(angle).toFloat() * radius * 0.95f
            val dotY = center.y + sin(angle).toFloat() * radius * 0.95f

            drawCircle(
                color = MarkersColor,
                radius = radius * 0.01f,
                center = Offset(dotX, dotY)
            )
        }
    }

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.85f,
        center = center
    )

    // Draw date window (characteristic of Submariner)
    val dateWindowSize = radius * 0.15f
    val dateWindowX = center.x + radius * 0.6f
    val dateWindowY = center.y

    drawRect(
        color = Color.White,
        topLeft = Offset(dateWindowX - dateWindowSize / 2, dateWindowY - dateWindowSize / 2),
        size = androidx.compose.ui.geometry.Size(dateWindowSize, dateWindowSize)
    )

    // Draw date text
    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = dateWindowSize * 0.7f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateWindowX,
        dateWindowY + dateWindowSize * 0.25f,
        datePaint
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Submariner typically uses circular hour markers
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)

        // Draw circular hour markers
        val markerRadius = if (i % 3 == 0) radius * 0.06f else radius * 0.04f
        val markerX = center.x + cos(angle).toFloat() * radius * 0.7f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.7f

        drawCircle(
            color = MarkersColor,
            radius = markerRadius,
            center = Offset(markerX, markerY)
        )

        // Draw "Rolex" text at 12 o'clock
        if (i == 12) {
            val textPaint = Paint().apply {
                color = MarkersColor.hashCode()
                textSize = radius * 0.12f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
            }

            val textY = center.y - radius * 0.4f
            drawContext.canvas.nativeCanvas.drawText(
                "ROLEX",
                center.x,
                textY,
                textPaint
            )
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
    // Hour hand (Mercedes-style)
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Mercedes-style hour hand (Submariner style)
        val handLength = radius * 0.5f
        val handWidth = radius * 0.07f

        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - handLength),
            strokeWidth = handWidth,
            cap = StrokeCap.Round
        )

        // Mercedes logo at the end of hour hand
        drawCircle(
            color = HourHandColor,
            radius = handWidth * 1.2f,
            center = Offset(center.x, center.y - handLength + handWidth * 1.2f)
        )

        // Draw the three spokes of the Mercedes logo
        val spokeLength = handWidth * 0.8f
        val spokeWidth = handWidth * 0.4f

        // Top spoke
        drawLine(
            color = ClockFaceColor,
            start = Offset(center.x, center.y - handLength + handWidth * 0.6f),
            end = Offset(center.x, center.y - handLength + handWidth * 1.8f),
            strokeWidth = spokeWidth,
            cap = StrokeCap.Round
        )

        // Left spoke
        rotate(-60f) {
            drawLine(
                color = ClockFaceColor,
                start = Offset(center.x, center.y - handLength + handWidth * 0.6f),
                end = Offset(center.x, center.y - handLength + handWidth * 1.8f),
                strokeWidth = spokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Right spoke
        rotate(60f) {
            drawLine(
                color = ClockFaceColor,
                start = Offset(center.x, center.y - handLength + handWidth * 0.6f),
                end = Offset(center.x, center.y - handLength + handWidth * 1.8f),
                strokeWidth = spokeWidth,
                cap = StrokeCap.Round
            )
        }
    }

    // Minute hand
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
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

    // Second hand with lollipop counterbalance (Submariner style)
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

        // Lollipop at the end of second hand
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y - radius * 0.75f)
        )

        // Counterbalance
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y + radius * 0.2f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RolexSubmarinerClockPreview() {
    SwissTimeTheme {
        RolexSubmarinerClock()
    }
}
