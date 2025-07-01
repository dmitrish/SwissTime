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
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Jaeger-LeCoultre Reverso
private val ClockFaceColor = Color(0xFFF5F5DC) // Beige/cream dial
private val ClockBorderColor = Color(0xFF8B4513) // Brown leather strap color for border
private val HourHandColor = Color(0xFF4169E1) // Blue hour hand (Art Deco style)
private val MinuteHandColor = Color(0xFF4169E1) // Blue minute hand
private val SecondHandColor = Color(0xFFDC143C) // Crimson second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val CenterDotColor = Color(0xFF4169E1) // Blue center dot

@Composable
fun JaegerLeCoultreReverso(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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
                radius = radius * 0.04f,
                center = center
            )
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Reverso has a rectangular case, so we'll draw a rounded rectangle
    val rectWidth = radius * 1.8f
    val rectHeight = radius * 2.2f
    
    // Draw outer rectangle (border)
    drawRoundRect(
        color = ClockBorderColor,
        topLeft = Offset(center.x - rectWidth / 2, center.y - rectHeight / 2),
        size = Size(rectWidth, rectHeight),
        cornerRadius = CornerRadius(radius * 0.1f),
        style = Stroke(width = 8f)
    )

    // Draw inner rectangle (face)
    drawRoundRect(
        color = ClockFaceColor,
        topLeft = Offset(center.x - rectWidth / 2 + 4f, center.y - rectHeight / 2 + 4f),
        size = Size(rectWidth - 8f, rectHeight - 8f),
        cornerRadius = CornerRadius(radius * 0.09f)
    )
    
    // Draw Art Deco style decorative lines at the corners
    val cornerOffset = radius * 0.15f
    val lineLength = radius * 0.2f
    
    // Top left corner
    drawLine(
        color = MarkersColor,
        start = Offset(center.x - rectWidth / 2 + cornerOffset, center.y - rectHeight / 2 + cornerOffset),
        end = Offset(center.x - rectWidth / 2 + cornerOffset + lineLength, center.y - rectHeight / 2 + cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = MarkersColor,
        start = Offset(center.x - rectWidth / 2 + cornerOffset, center.y - rectHeight / 2 + cornerOffset),
        end = Offset(center.x - rectWidth / 2 + cornerOffset, center.y - rectHeight / 2 + cornerOffset + lineLength),
        strokeWidth = 2f
    )
    
    // Top right corner
    drawLine(
        color = MarkersColor,
        start = Offset(center.x + rectWidth / 2 - cornerOffset, center.y - rectHeight / 2 + cornerOffset),
        end = Offset(center.x + rectWidth / 2 - cornerOffset - lineLength, center.y - rectHeight / 2 + cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = MarkersColor,
        start = Offset(center.x + rectWidth / 2 - cornerOffset, center.y - rectHeight / 2 + cornerOffset),
        end = Offset(center.x + rectWidth / 2 - cornerOffset, center.y - rectHeight / 2 + cornerOffset + lineLength),
        strokeWidth = 2f
    )
    
    // Bottom left corner
    drawLine(
        color = MarkersColor,
        start = Offset(center.x - rectWidth / 2 + cornerOffset, center.y + rectHeight / 2 - cornerOffset),
        end = Offset(center.x - rectWidth / 2 + cornerOffset + lineLength, center.y + rectHeight / 2 - cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = MarkersColor,
        start = Offset(center.x - rectWidth / 2 + cornerOffset, center.y + rectHeight / 2 - cornerOffset),
        end = Offset(center.x - rectWidth / 2 + cornerOffset, center.y + rectHeight / 2 - cornerOffset - lineLength),
        strokeWidth = 2f
    )
    
    // Bottom right corner
    drawLine(
        color = MarkersColor,
        start = Offset(center.x + rectWidth / 2 - cornerOffset, center.y + rectHeight / 2 - cornerOffset),
        end = Offset(center.x + rectWidth / 2 - cornerOffset - lineLength, center.y + rectHeight / 2 - cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = MarkersColor,
        start = Offset(center.x + rectWidth / 2 - cornerOffset, center.y + rectHeight / 2 - cornerOffset),
        end = Offset(center.x + rectWidth / 2 - cornerOffset, center.y + rectHeight / 2 - cornerOffset - lineLength),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Reverso uses Art Deco style markers and Arabic numerals
    
    // Draw hour markers (rectangular bars)
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerLength = radius * 0.1f
        val markerWidth = radius * 0.02f
        
        // Calculate position based on angle but constrain to rectangular shape
        val distance = if (i % 3 == 0) radius * 0.7f else radius * 0.65f
        val markerX = center.x + cos(angle).toFloat() * distance
        val markerY = center.y + sin(angle).toFloat() * distance
        
        // Draw rectangular marker
        rotate(
            degrees = (i - 3) * 30f,
            pivot = Offset(markerX, markerY)
        ) {
            drawRect(
                color = MarkersColor,
                topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
                size = Size(markerWidth, markerLength)
            )
        }
    }
    
    // Draw Art Deco style numbers at 12, 3, 6, 9
    val positions = listOf(12, 3, 6, 9)
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.18f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    for (i in 0..3) {
        val angle = Math.PI / 2 * i
        val numberRadius = radius * 0.45f
        val numberX = center.x + cos(angle).toFloat() * numberRadius
        val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

        drawContext.canvas.nativeCanvas.drawText(
            positions[i].toString(),
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
    // Hour hand - Art Deco style with diamond shape
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            lineTo(center.x + radius * 0.04f, center.y - radius * 0.4f) // Right shoulder
            lineTo(center.x + radius * 0.02f, center.y) // Right base
            lineTo(center.x - radius * 0.02f, center.y) // Left base
            lineTo(center.x - radius * 0.04f, center.y - radius * 0.4f) // Left shoulder
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand - Art Deco style with elongated diamond shape
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.5f) // Right shoulder
            lineTo(center.x + radius * 0.015f, center.y) // Right base
            lineTo(center.x - radius * 0.015f, center.y) // Left base
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.5f) // Left shoulder
            close()
        }
        drawPath(path, MinuteHandColor)
    }

    // Second hand - thin with a small circle counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
        
        // Counterbalance circle
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JaegerLeCoultreReversoPreview() {
    SwissTimeTheme {
        JaegerLeCoultreReverso()
    }
}