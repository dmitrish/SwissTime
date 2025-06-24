package com.coroutines.swisstime.watchfaces

import android.graphics.Paint
import android.graphics.Typeface
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
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Vacheron Constantin Patrimony
private val ClockFaceColor = Color(0xFFF8F5E6) // Cream dial
private val ClockBorderColor = Color(0xFFB8860B) // Dark gold border
private val HourHandColor = Color(0xFF4A4A4A) // Dark gray hour hand
private val MinuteHandColor = Color(0xFF4A4A4A) // Dark gray minute hand
private val SecondHandColor = Color(0xFF8B0000) // Dark red second hand
private val MarkersColor = Color(0xFFB8860B) // Gold markers
private val NumbersColor = Color(0xFF4A4A4A) // Dark gray numbers
private val AccentColor = Color(0xFFB8860B) // Gold accent

@Composable
fun VacheronConstantinPatrimony(modifier: Modifier = Modifier) {
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

            // Draw clock face (elegant minimalist design)
            drawClockFace(center, radius)

            // Get current time values
            val hour = currentTime.get(Calendar.HOUR)
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw hour markers (applied gold indices)
            drawHourMarkers(center, radius)

            // Draw subtle pattern
            drawSubtlePattern(center, radius)

            // Draw clock hands (dauphine-style hands)
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = AccentColor,
                radius = radius * 0.02f,
                center = center
            )

            // Draw Vacheron Constantin logo and Maltese cross
            drawLogo(center, radius)
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw the outer border
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.03f)
    )

    // Draw the main face
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.97f,
        center = center
    )

    // Draw a subtle inner ring
    drawCircle(
        color = ClockBorderColor,
        radius = radius * 0.92f,
        center = center,
        style = Stroke(width = radius * 0.005f)
    )
}

private fun DrawScope.drawHourMarkers(center: Offset, radius: Float) {
    // Vacheron Constantin Patrimony typically has applied gold hour markers
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
        val markerRadius = radius * 0.85f

        val markerX = center.x + cos(angle).toFloat() * markerRadius
        val markerY = center.y + sin(angle).toFloat() * markerRadius

        // Draw applied gold markers
        if (i % 3 == 0) {
            // Double marker for 12, 3, 6, 9
            drawLine(
                color = MarkersColor,
                start = Offset(
                    markerX - cos(angle + Math.PI/2).toFloat() * radius * 0.03f,
                    markerY - sin(angle + Math.PI/2).toFloat() * radius * 0.03f
                ),
                end = Offset(
                    markerX + cos(angle + Math.PI/2).toFloat() * radius * 0.03f,
                    markerY + sin(angle + Math.PI/2).toFloat() * radius * 0.03f
                ),
                strokeWidth = radius * 0.02f,
                cap = StrokeCap.Round
            )
        } else {
            // Single marker for other hours
            drawCircle(
                color = MarkersColor,
                radius = radius * 0.01f,
                center = Offset(markerX, markerY)
            )
        }

        // Draw minute markers
        if (i < 11) {
            for (j in 1 until 5) {
                val minuteAngle = Math.PI / 30 * (i * 5 + j) - Math.PI / 2
                val minuteMarkerRadius = radius * 0.88f

                val minuteX = center.x + cos(minuteAngle).toFloat() * minuteMarkerRadius
                val minuteY = center.y + sin(minuteAngle).toFloat() * minuteMarkerRadius

                drawCircle(
                    color = MarkersColor,
                    radius = radius * 0.003f,
                    center = Offset(minuteX, minuteY)
                )
            }
        }
    }
}

private fun DrawScope.drawSubtlePattern(center: Offset, radius: Float) {
    // Vacheron Constantin watches often have subtle, elegant patterns
    val patternRadius = radius * 0.5f

    // Draw a subtle sunburst pattern
    for (angle in 0 until 360 step 6) {
        val radians = angle * Math.PI / 180
        val startX = center.x + cos(radians).toFloat() * (radius * 0.1f)
        val startY = center.y + sin(radians).toFloat() * (radius * 0.1f)
        val endX = center.x + cos(radians).toFloat() * patternRadius
        val endY = center.y + sin(radians).toFloat() * patternRadius

        drawLine(
            color = Color(0xFFF5F0E0),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 0.5f
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
    // Hour hand - Dauphine-style (diamond-shaped)
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.35f) // Right shoulder
            lineTo(center.x + radius * 0.01f, center.y) // Right base
            lineTo(center.x - radius * 0.01f, center.y) // Left base
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.35f) // Left shoulder
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand - Dauphine-style (diamond-shaped)
    val minuteAngle = minute * 6f
    rotate(minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.025f, center.y - radius * 0.5f) // Right shoulder
            lineTo(center.x + radius * 0.008f, center.y) // Right base
            lineTo(center.x - radius * 0.008f, center.y) // Left base
            lineTo(center.x - radius * 0.025f, center.y - radius * 0.5f) // Left shoulder
            close()
        }
        drawPath(path, MinuteHandColor)
    }

    // Second hand - thin with counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle, pivot = center) {
        // Main hand
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )

        // Counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

private fun DrawScope.drawLogo(center: Offset, radius: Float) {
    // Draw Maltese cross (Vacheron Constantin's logo)
    val crossSize = radius * 0.12f
    val crossPath = Path().apply {
        // Create a Maltese cross shape
        moveTo(center.x, center.y - radius * 0.3f - crossSize)
        lineTo(center.x + crossSize * 0.3f, center.y - radius * 0.3f - crossSize * 0.7f)
        lineTo(center.x + crossSize, center.y - radius * 0.3f - crossSize * 0.3f)
        lineTo(center.x + crossSize * 0.7f, center.y - radius * 0.3f)
        lineTo(center.x + crossSize, center.y - radius * 0.3f + crossSize * 0.3f)
        lineTo(center.x + crossSize * 0.3f, center.y - radius * 0.3f + crossSize * 0.7f)
        lineTo(center.x, center.y - radius * 0.3f + crossSize)
        lineTo(center.x - crossSize * 0.3f, center.y - radius * 0.3f + crossSize * 0.7f)
        lineTo(center.x - crossSize, center.y - radius * 0.3f + crossSize * 0.3f)
        lineTo(center.x - crossSize * 0.7f, center.y - radius * 0.3f)
        lineTo(center.x - crossSize, center.y - radius * 0.3f - crossSize * 0.3f)
        lineTo(center.x - crossSize * 0.3f, center.y - radius * 0.3f - crossSize * 0.7f)
        close()
    }

    drawPath(
        path = crossPath,
        color = AccentColor
    )

    // Draw brand name
    val brandPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }

    drawContext.canvas.nativeCanvas.drawText(
        "VACHERON CONSTANTIN",
        center.x,
        center.y - radius * 0.15f,
        brandPaint
    )

    // Draw "GENEVE" text
    val genevePaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "GENÈVE",
        center.x,
        center.y + radius * 0.4f,
        genevePaint
    )

    // Draw "SWISS MADE" text
    val swissMadePaint = Paint().apply {
        color = NumbersColor.hashCode()
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
}

@Preview(showBackground = true)
@Composable
fun VacheronConstantinPatrimonyPreview() {
    SwissTimeTheme {
        VacheronConstantinPatrimony()
    }
}
