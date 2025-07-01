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
import androidx.compose.ui.graphics.Path
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

// Colors inspired by IWC Portugieser
private val ClockFaceColor = Color(0xFFF5F5F5) // Silver-white dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFF000080) // Navy blue hour hand
private val MinuteHandColor = Color(0xFF000080) // Navy blue minute hand
private val SecondHandColor = Color(0xFFB22222) // Red second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val SubdialColor = Color(0xFFE0E0E0) // Light gray subdial
private val SubdialHandColor = Color(0xFF000080) // Navy blue subdial hand
private val CenterDotColor = Color(0xFF000080) // Navy blue center dot

@Composable
fun IWCPortugieser(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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
    
    // Draw IWC logo text
    val logoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "IWC",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "SCHAFFHAUSEN" text
    val originPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "SCHAFFHAUSEN",
        center.x,
        center.y - radius * 0.2f,
        originPaint
    )
    
    // Draw small seconds subdial at 6 o'clock
    val subdialCenter = Offset(center.x, center.y + radius * 0.4f)
    val subdialRadius = radius * 0.2f
    
    // Subdial background
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = subdialCenter
    )
    
    // Subdial border
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = subdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Subdial markers
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60
        val markerLength = if (i % 15 == 0) subdialRadius * 0.2f else if (i % 5 == 0) subdialRadius * 0.15f else subdialRadius * 0.05f
        val startX = subdialCenter.x + cos(angle).toFloat() * (subdialRadius - markerLength)
        val startY = subdialCenter.y + sin(angle).toFloat() * (subdialRadius - markerLength)
        val endX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.9f
        val endY = subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.9f
        
        drawLine(
            color = Color.Black,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 15 == 0) 1.5f else 1f
        )
    }
    
    // Draw seconds numbers in subdial (15, 30, 45, 60)
    val secondsNumberPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = subdialRadius * 0.3f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    val secondsNumbers = listOf("60", "15", "30", "45")
    for (i in 0..3) {
        val angle = Math.PI / 2 * i
        val numberX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.6f
        val numberY = subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.6f + secondsNumberPaint.textSize / 3
        
        drawContext.canvas.nativeCanvas.drawText(
            secondsNumbers[i],
            numberX,
            numberY,
            secondsNumberPaint
        )
    }
    
    // Draw seconds hand in subdial
    val second = Calendar.getInstance().get(Calendar.SECOND)
    val secondAngle = second * 6f
    
    rotate(secondAngle, pivot = subdialCenter) {
        drawLine(
            color = SubdialHandColor,
            start = subdialCenter,
            end = Offset(subdialCenter.x, subdialCenter.y - subdialRadius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Portugieser uses Arabic numerals and simple markers
    
    // Draw hour markers
    for (i in 1..60) {
        val angle = Math.PI / 30 * (i - 15)
        val markerLength = if (i % 5 == 0) radius * 0.05f else radius * 0.02f
        val strokeWidth = if (i % 5 == 0) 2f else 1f
        
        // Skip markers where the subdial is (around 6 o'clock)
        if (i >= 25 && i <= 35) continue
        
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
    
    // Draw Arabic numerals for hours
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.15f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    // Skip 6 because of the subdial
    val hours = listOf(12, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11)
    val positions = listOf(0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11)
    
    for (i in hours.indices) {
        val angle = Math.PI / 6 * positions[i]
        val numberRadius = radius * 0.75f
        val numberX = center.x + cos(angle).toFloat() * numberRadius
        val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

        drawContext.canvas.nativeCanvas.drawText(
            hours[i].toString(),
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
    // Hour hand - leaf-shaped (characteristic of Portugieser)
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.04f, center.y - radius * 0.25f, // Control point
                center.x + radius * 0.02f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f, // Control point
                center.x - radius * 0.02f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.04f, center.y - radius * 0.25f, // Control point
                center.x, center.y - radius * 0.5f // End point (back to start)
            )
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand - longer leaf-shaped
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.03f, center.y - radius * 0.35f, // Control point
                center.x + radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f, // Control point
                center.x - radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.03f, center.y - radius * 0.35f, // Control point
                center.x, center.y - radius * 0.7f // End point (back to start)
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }
    
    // Note: The second hand is drawn in the subdial in the drawClockFace function
}

@Preview(showBackground = true)
@Composable
fun IWCPortugieserPreview() {
    SwissTimeTheme {
        IWCPortugieser()
    }
}