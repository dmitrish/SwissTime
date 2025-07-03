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

private val ClockFaceStartColor = Color(0xFF1E5631)
private val ClockFaceEndColor = Color(0xFF0A2714)
private val ClockBorderColor = Color(0xFFE0E0E0)
private val HourHandColor = Color(0xFFE0E0E0)
private val MinuteHandColor = Color(0xFFE0E0E0)
private val SecondHandColor = Color(0xFFE0E0E0)
private val MarkersColor = Color(0xFFE0E0E0)
private val LogoColor = Color(0xFFE0E0E0)

@Composable
fun HMoserEndeavour(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ClockFaceStartColor, ClockFaceEndColor),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center
    )

    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
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
        "Centurio Luminor",
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
        "1728",
        center.x,
        center.y + radius * 0.4f,
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
fun HMoserEndeavourPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HMoserEndeavour()
    }
}