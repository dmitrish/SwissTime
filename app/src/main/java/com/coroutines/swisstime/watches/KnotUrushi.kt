package com.coroutines.swisstime.watches

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Colors inspired by Knot Urushi lacquer dial
private val UrushiBlackColor = Color(0xFF000000)
private val GoldPowderColor = Color(0xFFD4AF37)
private val GoldPowderHighlightColor = Color(0xFFFFD700)
private val SilverCaseColor = Color(0xFFE0E0E0)
private val SilverHandsColor = Color(0xFFE0E0E0)
private val MarkersColor = Color(0xFFF5F5F5)
private val LogoColor = Color(0xFFE0E0E0)

@Composable
fun KnotUrushi(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchFaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, currentTime ->
            drawKnotClockFace(center, radius)
            drawKnotHourMarkers(center, radius, currentTime, timeZone)
            drawKnotUrushiTexture(center, radius)
            drawKnotLogo(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawKnotClockHands(center, radius, time)
            drawCircle(
                color = SilverHandsColor,
                radius = radius * 0.02f,
                center = center
            )
        }
    )
}

private fun DrawScope.drawKnotClockFace(center: Offset, radius: Float) {
    drawCircle(
        color = SilverCaseColor,
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.05f)
    )

    drawCircle(
        color = UrushiBlackColor,
        radius = radius * 0.95f,
        center = center
    )
}

private fun DrawScope.drawKnotHourMarkers(
    center: Offset,
    radius: Float,
    currentTime: Calendar,
    timeZone: TimeZone
) {
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i - Math.PI / 2
        val markerRadius = radius * 0.85f
        val markerX = center.x + cos(angle).toFloat() * markerRadius
        val markerY = center.y + sin(angle).toFloat() * markerRadius

        if (i % 3 == 0) {
            val markerWidth = radius * 0.05f
            val markerHeight = radius * 0.14f
            val rotationAngle = Math.toDegrees(angle).toFloat() + 90

            rotate(rotationAngle, Offset(markerX, markerY)) {
                drawRect(
                    color = MarkersColor,
                    topLeft = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    size = Size(markerWidth, markerHeight)
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )

                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )
            }
        } else {
            val markerWidth = radius * 0.04f
            val markerHeight = radius * 0.1f
            val rotationAngle = Math.toDegrees(angle).toFloat() + 90

            rotate(rotationAngle, Offset(markerX, markerY)) {
                drawRect(
                    color = MarkersColor,
                    topLeft = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    size = Size(markerWidth, markerHeight)
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )

                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )
            }
        }
    }

    // Date window at 3 o'clock
    val dateX = center.x + radius * 0.6f
    val dateY = center.y

    drawRect(
        color = Color.White,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f)
    )

    drawRect(
        color = SilverCaseColor,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f),
        style = Stroke(width = 1f)
    )

    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    val day = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH).toString()

    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.03f,
        datePaint
    )
}

private fun DrawScope.drawKnotUrushiTexture(center: Offset, radius: Float) {
    val random = Random(0)
    val particleCount = 500

    for (i in 0 until particleCount) {
        val angle = random.nextDouble(0.0, 2 * Math.PI)
        val distance = random.nextDouble(0.0, 0.85) * radius

        val x = center.x + (cos(angle) * distance).toFloat()
        val y = center.y + (sin(angle) * distance).toFloat()

        val particleSize = random.nextFloat() * 1.5f + 0.5f

        val goldShade = if (random.nextFloat() > 0.7f) {
            GoldPowderHighlightColor
        } else {
            GoldPowderColor.copy(alpha = random.nextFloat() * 0.5f + 0.1f)
        }

        drawCircle(
            color = goldShade,
            radius = particleSize,
            center = Offset(x, y)
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF050505),
                UrushiBlackColor
            ),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center,
        alpha = 0.7f
    )
}

private fun DrawScope.drawKnotClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand
    rotate(time.hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.45f)
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.1f)
            lineTo(center.x + radius * 0.015f, center.y)
            lineTo(center.x - radius * 0.015f, center.y)
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.1f)
            close()
        }
        drawPath(path, SilverHandsColor)
    }

    // Minute hand
    rotate(time.minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f)
            lineTo(center.x + radius * 0.02f, center.y - radius * 0.1f)
            lineTo(center.x + radius * 0.01f, center.y)
            lineTo(center.x - radius * 0.01f, center.y)
            lineTo(center.x - radius * 0.02f, center.y - radius * 0.1f)
            close()
        }
        drawPath(path, SilverHandsColor)
    }

    // Second hand
    rotate(time.secondAngle, pivot = center) {
        drawLine(
            color = SilverHandsColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = SilverHandsColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

private fun DrawScope.drawKnotLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.SANS_SERIF,
            android.graphics.Typeface.NORMAL
        )
    }

    drawContext.canvas.nativeCanvas.drawText(
        "KNOT",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    val subtitlePaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.SANS_SERIF,
            android.graphics.Typeface.NORMAL
        )
    }

    drawContext.canvas.nativeCanvas.drawText(
        "URUSHI",
        center.x,
        center.y + radius * 0.4f,
        subtitlePaint
    )
}

@Preview(showBackground = true)
@Composable
fun KnotUrushiPreview() {
    SwissTimeTheme {
        KnotUrushi()
    }
}