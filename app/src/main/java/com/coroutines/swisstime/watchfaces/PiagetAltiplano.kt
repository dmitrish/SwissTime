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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Piaget Altiplano
private val ClockFaceColor = Color(0xFF000080) // Deep blue dial
private val ClockBorderColor = Color(0xFFFFFFFF) // White gold case
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFFFFFF) // White second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val LogoColor = Color(0xFFFFFFFF) // White logo

@Composable
fun PiagetAltiplano(
    modifier: Modifier = Modifier,
    currentTime: Calendar = Calendar.getInstance(),
    timeZone: TimeZone = TimeZone.getDefault()
) {
    // Use the provided time zone to get the current time
    var internalTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

    // Update time every second using the provided time zone
    LaunchedEffect(key1 = timeZone) {
        while (true) {
            internalTime = Calendar.getInstance(timeZone)
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
            val hour = internalTime.get(Calendar.HOUR)
            val minute = internalTime.get(Calendar.MINUTE)
            val second = internalTime.get(Calendar.SECOND)

            // Draw hour markers
            drawHourMarkers(center, radius)

            // Draw clock hands
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = HourHandColor,
                radius = radius * 0.01f,
                center = center
            )

            // Draw Piaget logo
            drawLogo(center, radius)
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw outer circle (case) - very thin to represent the ultra-thin profile
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 4f)
    )

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 2f,
        center = center
    )
}

private fun DrawScope.drawHourMarkers(center: Offset, radius: Float) {
    // Piaget Altiplano typically has very minimalist hour markers
    // Often just simple thin lines or small dots

    for (i in 0 until 12) {
        val angle = PI / 6 * i

        // For 3, 6, 9, and 12 o'clock, use slightly longer markers
        val markerLength = if (i % 3 == 0) radius * 0.05f else radius * 0.03f
        val markerWidth = if (i % 3 == 0) 1.5f else 1f

        val startX = center.x + cos(angle).toFloat() * (radius * 0.85f)
        val startY = center.y + sin(angle).toFloat() * (radius * 0.85f)
        val endX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
        val endY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)

        // Draw minimalist markers
        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = markerWidth,
            cap = StrokeCap.Round
        )
    }

    // Add small dots at each hour position for a more refined look
    for (i in 0 until 12) {
        val angle = PI / 6 * i
        val dotRadius = if (i % 3 == 0) 1.5f else 1f

        val dotX = center.x + cos(angle).toFloat() * (radius * 0.9f)
        val dotY = center.y + sin(angle).toFloat() * (radius * 0.9f)

        drawCircle(
            color = MarkersColor,
            radius = dotRadius,
            center = Offset(dotX, dotY)
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
    // Hour hand - very thin and elegant
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle, pivot = center) {
        // Simple thin line for hour hand
        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.5f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }

    // Minute hand - longer and equally thin
    val minuteAngle = minute * 6f
    rotate(minuteAngle, pivot = center) {
        // Simple thin line for minute hand
        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }

    // Second hand - extremely thin
    val secondAngle = second * 6f
    rotate(secondAngle, pivot = center) {
        // Ultra-thin line for second hand
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 0.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false // Piaget logo is typically thin and elegant
        isAntiAlias = true
    }

    // Draw "PIAGET" text
    drawContext.canvas.nativeCanvas.drawText(
        "PIAGET",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    // Draw "ALTIPLANO" text
    val modelPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "ALTIPLANO",
        center.x,
        center.y - radius * 0.2f,
        modelPaint
    )

    // Draw "SWISS MADE" text
    val swissMadePaint = Paint().apply {
        color = LogoColor.hashCode()
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

    // Draw "ULTRA-THIN" text - a key feature of the Altiplano
    val ultraThinPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "ULTRA-THIN",
        center.x,
        center.y + radius * 0.2f,
        ultraThinPaint
    )
}

@Preview(showBackground = true)
@Composable
fun PiagetAltiplanoPreviews() {
    SwissTimeTheme {
        PiagetAltiplano()
    }
}
