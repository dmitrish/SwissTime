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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RadialGradientShader
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

// Colors inspired by H. Moser & Cie Endeavour with fumé dial
private val ClockFaceStartColor = Color(0xFF1E5631) // Deep green center
private val ClockFaceEndColor = Color(0xFF0A2714) // Almost black edges
private val ClockBorderColor = Color(0xFFE0E0E0) // Silver case
private val HourHandColor = Color(0xFFE0E0E0) // Silver hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Silver minute hand
private val SecondHandColor = Color(0xFFE0E0E0) // Silver second hand
private val MarkersColor = Color(0xFFE0E0E0) // Silver markers
private val LogoColor = Color(0xFFE0E0E0) // Silver logo

@Composable
fun HMoserEndeavour(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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

            // Draw clock face with fumé effect
            drawClockFace(center, radius)

            // Get current time values
            val hour = currentTime.get(Calendar.HOUR)
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw hour markers (minimalist style)
            drawHourMarkers(center, radius)

            // Draw clock hands (leaf-shaped)
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = HourHandColor,
                radius = radius * 0.02f,
                center = center
            )
            
            // Draw H. Moser & Cie logo
            drawLogo(center, radius)
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw outer circle (case)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )
    
    // Draw inner circle with fumé effect (gradient from center to edge)
    val shader = RadialGradientShader(
        center = center,
        radius = radius * 0.95f,
        colors = listOf(ClockFaceStartColor, ClockFaceEndColor),
        colorStops = listOf(0f, 1f)
    )
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ClockFaceStartColor, ClockFaceEndColor),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center
    )
}

private fun DrawScope.drawHourMarkers(center: Offset, radius: Float) {
    // H. Moser & Cie watches are known for their minimalist design
    // Often with no numerals, just simple markers
    
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i
        val markerLength = radius * 0.1f
        
        val startX = center.x + cos(angle).toFloat() * (radius * 0.8f)
        val startY = center.y + sin(angle).toFloat() * (radius * 0.8f)
        val endX = center.x + cos(angle).toFloat() * (radius * 0.8f - markerLength)
        val endY = center.y + sin(angle).toFloat() * (radius * 0.8f - markerLength)
        
        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 3f else 1.5f,
            cap = StrokeCap.Round
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
    // Hour hand - leaf-shaped
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.03f, center.y - radius * 0.25f, // Control point
                center.x + radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.05f, // Control point
                center.x - radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.03f, center.y - radius * 0.25f, // Control point
                center.x, center.y - radius * 0.5f // End point (back to start)
            )
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand - longer leaf-shaped
    val minuteAngle = minute * 6f
    rotate(minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.025f, center.y - radius * 0.35f, // Control point
                center.x + radius * 0.01f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.05f, // Control point
                center.x - radius * 0.01f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.025f, center.y - radius * 0.35f, // Control point
                center.x, center.y - radius * 0.7f // End point (back to start)
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }

    // Second hand - simple and thin
    val secondAngle = second * 6f
    rotate(secondAngle, pivot = center) {
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.2f), // Counterbalance
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    // Draw "H. MOSER & CIE" text
    drawContext.canvas.nativeCanvas.drawText(
        "H. MOSER & CIE",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "1828" text (founding year)
    val yearPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "1828",
        center.x,
        center.y + radius * 0.4f,
        yearPaint
    )
    
    // Draw "SWISS MADE" text
    val swissMadePaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.05f
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

@Preview(showBackground = true)
@Composable
fun HMoserEndeavourPreview() {
    SwissTimeTheme {
        HMoserEndeavour()
    }
}