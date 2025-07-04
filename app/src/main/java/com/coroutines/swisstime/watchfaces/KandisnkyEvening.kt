package com.coroutines.swisstime.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*

// Kandinsky's "Circles in a Circle" inspired colors
private val BackgroundColor = Color(0xFFF5F5F5) // Light background like in the painting
private val ClockBorderColor = Color(0xFF000000) // Black border
private val HourHandColor = Color(0xFFFF7F50) // Orange hour hand
private val MinuteHandColor = Color(0xFFFF7F50) // Orange minute hand
private val SecondHandColor = Color(0xFFD13B40) // Red second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val LogoColor = Color(0xFF000000) // Black logo

// Circle colors from Kandinsky's palette
private val KandinskyBlue = Color(0xFF1D5DC7) // Deep blue
private val KandinskyRed = Color(0xFFD13B40) // Vibrant red
private val KandinskyYellow = Color(0xFFFFC857) // Bright yellow
private val KandinskyGreen = Color(0xFF2E8B57) // Sea green
private val KandinskyPurple = Color(0xFF9370DB) // Medium purple
private val KandinskyOrange = Color(0xFFFF7F50) // Coral orange

@Composable
fun KandinskyEvening(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }
    val timeZoneState by rememberUpdatedState(timeZone)

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance(timeZoneState)
            delay(1000)
        }
    }

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        StaticWatchElements()
        HourHand(currentTime)
        MinuteHand(currentTime)
        SecondHand(currentTime)
        CenterDot()
    }
}

@Composable
private fun StaticWatchElements() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = min(size.width, size.height) / 2 * 0.8f

                val cache = buildList {
                    add(drawClockFace(center, radius))
                    add(drawHourMarkers(center, radius))
                    add(drawWatchLogo(center, radius))
                }

                onDrawBehind {
                    cache.forEach { drawOperation ->
                        drawOperation.invoke(this)
                    }
                }
            }
    ) {}
}

@Composable
private fun HourHand(currentTime: Calendar) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val hour = currentTime.get(Calendar.HOUR)
                val minute = currentTime.get(Calendar.MINUTE)
                rotationZ = (hour * 30 + minute * 0.5f)
            }
    ) {
        drawHourHand(center, size.minDimension / 2 * 0.8f)
    }
}

@Composable
private fun MinuteHand(currentTime: Calendar) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = currentTime.get(Calendar.MINUTE) * 6f
            }
    ) {
        drawMinuteHand(center, size.minDimension / 2 * 0.8f)
    }
}

@Composable
private fun SecondHand(currentTime: Calendar) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = currentTime.get(Calendar.SECOND) * 6f
            }
    ) {
        drawSecondHand(center, size.minDimension / 2 * 0.8f)
    }
}

@Composable
private fun CenterDot() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        color = HourHandColor,
                        radius = size.minDimension / 2 * 0.8f * 0.02f,
                        center = center
                    )
                }
            }
    ) {}
}

private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Draw light background like in Kandinsky's painting
    drawCircle(
        color = BackgroundColor,
        radius = radius * 0.95f,
        center = center
    )

    // Draw the main outer circle (black border)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )

    // Draw Kandinsky's "Circles in a Circle" inspired design

    // Draw intersecting lines
    val lineWidth = 3f

    // Diagonal line from top-left to bottom-right
    drawLine(
        color = ClockBorderColor,
        start = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f),
        end = Offset(center.x + radius * 0.7f, center.y + radius * 0.7f),
        strokeWidth = lineWidth
    )

    // Diagonal line from top-right to bottom-left
    drawLine(
        color = ClockBorderColor,
        start = Offset(center.x + radius * 0.7f, center.y - radius * 0.7f),
        end = Offset(center.x - radius * 0.7f, center.y + radius * 0.7f),
        strokeWidth = lineWidth
    )

    // Horizontal line
    drawLine(
        color = ClockBorderColor,
        start = Offset(center.x - radius * 0.7f, center.y),
        end = Offset(center.x + radius * 0.7f, center.y),
        strokeWidth = lineWidth
    )

    // Vertical line
    drawLine(
        color = ClockBorderColor,
        start = Offset(center.x, center.y - radius * 0.7f),
        end = Offset(center.x, center.y + radius * 0.7f),
        strokeWidth = lineWidth
    )

    // Draw multiple colored circles of various sizes

    // Large blue circle in top-left quadrant
    drawCircle(
        color = KandinskyBlue,
        radius = radius * 0.25f,
        center = Offset(center.x - radius * 0.4f, center.y - radius * 0.4f)
    )

    // Medium red circle in top-right quadrant
    drawCircle(
        color = KandinskyRed,
        radius = radius * 0.2f,
        center = Offset(center.x + radius * 0.5f, center.y - radius * 0.3f)
    )

    // Small yellow circle in bottom-left quadrant
    drawCircle(
        color = KandinskyYellow,
        radius = radius * 0.15f,
        center = Offset(center.x - radius * 0.5f, center.y + radius * 0.4f)
    )

    // Medium green circle in bottom-right quadrant
    drawCircle(
        color = KandinskyGreen,
        radius = radius * 0.18f,
        center = Offset(center.x + radius * 0.4f, center.y + radius * 0.5f)
    )

    // Small purple circle near center
    drawCircle(
        color = KandinskyPurple,
        radius = radius * 0.12f,
        center = Offset(center.x + radius * 0.1f, center.y - radius * 0.2f)
    )

    // Small orange circle near center
    drawCircle(
        color = KandinskyOrange,
        radius = radius * 0.1f,
        center = Offset(center.x - radius * 0.2f, center.y + radius * 0.1f)
    )

    // Additional smaller circles for more detail
    drawCircle(
        color = KandinskyBlue.copy(alpha = 0.7f),
        radius = radius * 0.08f,
        center = Offset(center.x + radius * 0.3f, center.y + radius * 0.1f)
    )

    drawCircle(
        color = KandinskyRed.copy(alpha = 0.7f),
        radius = radius * 0.06f,
        center = Offset(center.x - radius * 0.3f, center.y - radius * 0.2f)
    )

    drawCircle(
        color = KandinskyYellow.copy(alpha = 0.7f),
        radius = radius * 0.07f,
        center = Offset(center.x, center.y + radius * 0.3f)
    )
}

private fun drawHourMarkers(center: Offset, radius: Float): DrawScope.() -> Unit = {
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i
        val markerLength = radius * 0.1f
        val startRadius = radius * 0.8f

        val startX = center.x + cos(angle).toFloat() * startRadius
        val startY = center.y + sin(angle).toFloat() * startRadius
        val endX = center.x + cos(angle).toFloat() * (startRadius - markerLength)
        val endY = center.y + sin(angle).toFloat() * (startRadius - markerLength)

        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 3f else 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun drawWatchLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    val yearPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "W  K",
        center.x - 2,
        center.y + radius * 0.5f,
        yearPaint
    )
}

private fun DrawScope.drawHourHand(center: Offset, radius: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius * 0.5f)
        quadraticBezierTo(
            center.x + radius * 0.03f, center.y - radius * 0.25f,
            center.x + radius * 0.015f, center.y
        )
        quadraticBezierTo(
            center.x, center.y + radius * 0.05f,
            center.x - radius * 0.015f, center.y
        )
        quadraticBezierTo(
            center.x - radius * 0.03f, center.y - radius * 0.25f,
            center.x, center.y - radius * 0.5f
        )
        close()
    }
    drawPath(path, HourHandColor)
}

private fun DrawScope.drawMinuteHand(center: Offset, radius: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius * 0.7f)
        quadraticBezierTo(
            center.x + radius * 0.025f, center.y - radius * 0.35f,
            center.x + radius * 0.01f, center.y
        )
        quadraticBezierTo(
            center.x, center.y + radius * 0.05f,
            center.x - radius * 0.01f, center.y
        )
        quadraticBezierTo(
            center.x - radius * 0.025f, center.y - radius * 0.35f,
            center.x, center.y - radius * 0.7f
        )
        close()
    }
    drawPath(path, MinuteHandColor)
}

private fun DrawScope.drawSecondHand(center: Offset, radius: Float) {
    drawLine(
        color = SecondHandColor,
        start = Offset(center.x, center.y + radius * 0.2f),
        end = Offset(center.x, center.y - radius * 0.8f),
        strokeWidth = 1f,
        cap = StrokeCap.Round
    )
}

@Preview(showBackground = true)
@Composable
fun KandinskyEveningPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        KandinskyEvening()
    }
}
