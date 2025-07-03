package com.coroutines.swisstime.watchfaces
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*

// Colors inspired by IWC Portugieser
private val ClockFaceColor = Color(0xFFF5F5F5)
private val ClockBorderColor = Color(0xFF303030)
private val HourHandColor = Color(0xFF000080)
private val MinuteHandColor = Color(0xFF000080)
private val MarkersColor = Color(0xFF000000)
private val NumbersColor = Color(0xFF000000)
private val SubdialColor = Color(0xFFE0E0E0)
private val SubdialHandColor = Color(0xFF000080)
private val CenterDotColor = Color(0xFF000080)

@Composable
fun IWCPortugieser(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }
    val timeZoneX by rememberUpdatedState(timeZone)

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance(timeZoneX)
            delay(1000)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StaticWatchElements()
        HourHand(currentTime)
        MinuteHand(currentTime)
        SubdialSecondHand(currentTime)
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

                // Cache the drawing operations
                val cache = buildList {
                    add(drawStaticElements(center, radius))
                    add(drawHourMarkersAndNumbers(center, radius))
                    add(drawSubdialBackground(center, radius))
                }

                onDrawBehind {
                    // Execute cached operations
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
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f

        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f)
            quadraticBezierTo(
                center.x + radius * 0.04f, center.y - radius * 0.25f,
                center.x + radius * 0.02f, center.y
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f,
                center.x - radius * 0.02f, center.y
            )
            quadraticBezierTo(
                center.x - radius * 0.04f, center.y - radius * 0.25f,
                center.x, center.y - radius * 0.5f
            )
            close()
        }
        drawPath(path, HourHandColor)
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
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f

        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f)
            quadraticBezierTo(
                center.x + radius * 0.03f, center.y - radius * 0.35f,
                center.x + radius * 0.015f, center.y
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f,
                center.x - radius * 0.015f, center.y
            )
            quadraticBezierTo(
                center.x - radius * 0.03f, center.y - radius * 0.35f,
                center.x, center.y - radius * 0.7f
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }
}

@Composable
private fun SubdialSecondHand(currentTime: Calendar) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f
        val subdialCenter = Offset(center.x, center.y + radius * 0.4f)
        val subdialRadius = radius * 0.2f

        rotate(degrees = currentTime.get(Calendar.SECOND) * 6f, pivot = subdialCenter) {
            drawLine(
                color = SubdialHandColor,
                start = subdialCenter,
                end = Offset(subdialCenter.x, subdialCenter.y - subdialRadius * 0.8f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = SubdialHandColor,
                start = subdialCenter,
                end = Offset(subdialCenter.x, subdialCenter.y + subdialRadius * 0.2f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun CenterDot() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = min(size.width, size.height) / 2 * 0.8f
                    drawCircle(
                        color = CenterDotColor,
                        radius = radius * 0.03f,
                        center = center
                    )
                }
            }
    ) {}
}

private fun drawStaticElements(center: Offset, radius: Float): DrawScope.() -> Unit = {
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 6f)
    )

    drawCircle(
        color = ClockFaceColor,
        radius = radius - 3f,
        center = center
    )

    val logoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "HOROLOGIA",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    val originPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "ROMANUM",
        center.x,
        center.y - radius * 0.2f,
        originPaint
    )
}

private fun drawSubdialBackground(center: Offset, radius: Float): DrawScope.() -> Unit = {
    val subdialCenter = Offset(center.x, center.y + radius * 0.4f)
    val subdialRadius = radius * 0.2f

    drawCircle(color = SubdialColor, radius = subdialRadius, center = subdialCenter)
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = subdialCenter,
        style = Stroke(width = 2f)
    )

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

    val secondsNumbers = listOf("60", "15", "30", "45")
    val numberPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = subdialRadius * 0.3f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    for (i in 0..3) {
        val angle = Math.PI / 2 * i
        val numberX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.6f
        val numberY = subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.6f + numberPaint.textSize / 3
        drawContext.canvas.nativeCanvas.drawText(
            secondsNumbers[i],
            numberX,
            numberY,
            numberPaint
        )
    }
}

private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
    for (i in 1..60) {
        val angle = Math.PI / 30 * (i - 15)
        val markerLength = if (i % 5 == 0) radius * 0.05f else radius * 0.02f
        val strokeWidth = if (i % 5 == 0) 2f else 1f

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

    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.15f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

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

@Preview(showBackground = true)
@Composable
fun IWCPortugieserPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IWCPortugieser()
    }
}
