package com.coroutines.swisstime.watchfaces.watches

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
import com.coroutines.swisstime.watchfaces.scaffold.WatchTime
import com.coroutines.swisstime.watchfaces.scaffold.WatchfaceScaffold
import com.coroutines.swisstime.watchfaces.scaffold.toWatchTime
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

// Colors for Vostok Russian Military Automatik Diver Watch
private val VostokBlueDialColor = Color(0xFF0A3875) // Deep blue dial color
private val VostokCaseColor = Color(0xFFD0D0D0) // Stainless steel case
private val VostokBezelColor = Color(0xFF1A4A8C) // Blue bezel
private val VostokBezelMarkerColor = Color(0xFFFFFFFF) // White bezel markers
private val VostokHandsColor = Color(0xFFF5F5F5) // Silver/white hands
private val VostokSecondHandColor = Color(0xFFFF3A30) // Red second hand
private val VostokMarkersColor = Color(0xFFF5F5F5) // White/luminous markers
private val VostokLumeColor = Color(0xFFB4FFB4) // Light green lume color
private val VostokDateColor = Color(0xFFFFFFFF) // White date background

@Composable
fun VostokRussianMilitaryWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawVostokClockFace(center, radius)
            drawVostokBezel(center, radius)
            drawVostokHourMarkers(center, radius, timeZone)
            drawVostokLogo(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawVostokClockHands(center, radius, time)
            drawVostokCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawVostokClockFace(center: Offset, radius: Float) {
    // Draw the outer stainless steel case
    drawCircle(
        color = VostokCaseColor,
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.05f)
    )

    // Draw the main face with deep blue dial
    drawCircle(
        color = VostokBlueDialColor,
        radius = radius * 0.95f,
        center = center
    )

    // Add subtle radial gradient to simulate depth
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                VostokBlueDialColor.copy(alpha = 0.8f), // Slightly lighter at center
                VostokBlueDialColor                     // Deep blue at edges
            ),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center
    )
}

private fun DrawScope.drawVostokBezel(center: Offset, radius: Float) {
    // Draw the rotating bezel
    val bezelOuterRadius = radius * 0.95f
    val bezelInnerRadius = radius * 0.8f

    // Draw bezel background
    drawCircle(
        color = VostokBezelColor,
        radius = bezelOuterRadius,
        center = center,
        style = Stroke(width = bezelOuterRadius - bezelInnerRadius)
    )

    // Draw bezel markers (60 minute/diving scale)
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60 - Math.PI / 2 // Start at 12 o'clock

        val markerLength = if (i % 5 == 0) {
            // Major markers at 5-minute intervals
            (bezelOuterRadius - bezelInnerRadius) * 0.8f
        } else {
            // Minor markers
            (bezelOuterRadius - bezelInnerRadius) * 0.4f
        }

        val markerStart = bezelInnerRadius + (bezelOuterRadius - bezelInnerRadius) * 0.1f
        val markerEnd = markerStart + markerLength

        val startX = center.x + cos(angle).toFloat() * markerStart
        val startY = center.y + sin(angle).toFloat() * markerStart
        val endX = center.x + cos(angle).toFloat() * markerEnd
        val endY = center.y + sin(angle).toFloat() * markerEnd

        drawLine(
            color = VostokBezelMarkerColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 5 == 0) 2.5f else 1.5f
        )

        // Draw triangle marker at 12 o'clock position (0 minutes)
        if (i == 0) {
            val triangleSize = (bezelOuterRadius - bezelInnerRadius) * 0.5f
            val trianglePath = Path().apply {
                moveTo(
                    center.x,
                    center.y - bezelOuterRadius + (bezelOuterRadius - bezelInnerRadius) * 0.2f
                )
                lineTo(
                    center.x - triangleSize / 2,
                    center.y - bezelOuterRadius + (bezelOuterRadius - bezelInnerRadius) * 0.7f
                )
                lineTo(
                    center.x + triangleSize / 2,
                    center.y - bezelOuterRadius + (bezelOuterRadius - bezelInnerRadius) * 0.7f
                )
                close()
            }
            drawPath(trianglePath, VostokBezelMarkerColor)
        }
    }
}

private fun DrawScope.drawVostokHourMarkers(center: Offset, radius: Float, timeZone: TimeZone) {
    val dialRadius = radius * 0.75f

    // Draw hour markers
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
        val markerX = center.x + cos(angle).toFloat() * dialRadius
        val markerY = center.y + sin(angle).toFloat() * dialRadius

        if (i == 0 || i == 3 || i == 6 || i == 9) {
            // Special markers at 12, 3, 6, 9 positions
            if (i == 3) {
                // Skip 3 o'clock position for date window
                continue
            }

            // Draw larger markers with military-style numerals
            val markerWidth = radius * 0.06f
            val markerHeight = radius * 0.12f

            // Draw the marker background
            drawRect(
                color = VostokMarkersColor,
                topLeft = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                size = Size(markerWidth, markerHeight)
            )

            // Add lume effect
            drawRect(
                color = VostokLumeColor.copy(alpha = 0.7f),
                topLeft = Offset(markerX - markerWidth / 2 + 1f, markerY - markerHeight / 2 + 1f),
                size = Size(markerWidth - 2f, markerHeight - 2f)
            )

            // Draw the numeral
            val textPaint = Paint().apply {
                color = Color.Black.hashCode()
                textSize = radius * 0.07f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
            }

            // Convert hour index to numeral (12, 6, 9)
            val numeral = when (i) {
                0 -> "12"
                6 -> "6"
                9 -> "9"
                else -> ""
            }

            // Draw the numeral
            drawContext.canvas.nativeCanvas.drawText(
                numeral,
                markerX,
                markerY + radius * 0.025f,
                textPaint
            )
        } else {
            // Standard dot markers for other positions
            val markerRadius = radius * 0.03f

            // Draw marker background
            drawCircle(
                color = VostokMarkersColor,
                radius = markerRadius,
                center = Offset(markerX, markerY)
            )

            // Add lume effect
            drawCircle(
                color = VostokLumeColor.copy(alpha = 0.7f),
                radius = markerRadius - 1f,
                center = Offset(markerX, markerY)
            )
        }
    }

    // Draw date window at 3 o'clock position
    val dateX = center.x + dialRadius
    val dateY = center.y

    // Date window with white background
    drawRect(
        color = VostokDateColor,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f)
    )

    // Add a thin border around the date window
    drawRect(
        color = VostokCaseColor,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f),
        style = Stroke(width = 1f)
    )

    // Date text
    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    // Get current day of month
    val day = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH).toString()

    // Draw the day number
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.03f,
        datePaint
    )
}

private fun DrawScope.drawVostokLogo(center: Offset, radius: Float) {
    // Draw "BOCTOK" text (Cyrillic for Vostok)
    val logoPaint = Paint().apply {
        color = VostokHandsColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
    }

    drawContext.canvas.nativeCanvas.drawText(
        "BOCTOK",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    // Draw "АВТОМАТИК" text (Automatic in Russian)
    val subtitlePaint = Paint().apply {
        color = VostokHandsColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
    }

    drawContext.canvas.nativeCanvas.drawText(
        "АВТОМАТИК",
        center.x,
        center.y + radius * 0.4f,
        subtitlePaint
    )

    // Draw "20ATM" text for water resistance
    val waterResistancePaint = Paint().apply {
        color = VostokHandsColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
    }

    drawContext.canvas.nativeCanvas.drawText(
        "20ATM",
        center.x,
        center.y + radius * 0.2f,
        waterResistancePaint
    )
}

private fun DrawScope.drawVostokClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand - arrow-shaped (typical for Vostok)
    rotate(time.hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.4f) // Tip
            lineTo(center.x + radius * 0.04f, center.y - radius * 0.3f) // Right arrow edge
            lineTo(center.x + radius * 0.02f, center.y) // Right base
            lineTo(center.x - radius * 0.02f, center.y) // Left base
            lineTo(center.x - radius * 0.04f, center.y - radius * 0.3f) // Left arrow edge
            close()
        }
        drawPath(path, VostokHandsColor)

        // Add lume to hour hand
        val lumePath = Path().apply {
            moveTo(center.x, center.y - radius * 0.38f) // Tip
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.3f) // Right arrow edge
            lineTo(center.x + radius * 0.015f, center.y - radius * 0.05f) // Right base
            lineTo(center.x - radius * 0.015f, center.y - radius * 0.05f) // Left base
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.3f) // Left arrow edge
            close()
        }
        drawPath(lumePath, VostokLumeColor.copy(alpha = 0.7f))
    }

    // Minute hand - straight with slight taper
    rotate(time.minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.65f) // Tip
            lineTo(center.x + radius * 0.025f, center.y - radius * 0.1f) // Right edge
            lineTo(center.x + radius * 0.015f, center.y) // Right base
            lineTo(center.x - radius * 0.015f, center.y) // Left base
            lineTo(center.x - radius * 0.025f, center.y - radius * 0.1f) // Left edge
            close()
        }
        drawPath(path, VostokHandsColor)

        // Add lume to minute hand
        val lumePath = Path().apply {
            moveTo(center.x, center.y - radius * 0.63f) // Tip
            lineTo(center.x + radius * 0.02f, center.y - radius * 0.1f) // Right edge
            lineTo(center.x + radius * 0.01f, center.y - radius * 0.05f) // Right base
            lineTo(center.x - radius * 0.01f, center.y - radius * 0.05f) // Left base
            lineTo(center.x - radius * 0.02f, center.y - radius * 0.1f) // Left edge
            close()
        }
        drawPath(lumePath, VostokLumeColor.copy(alpha = 0.7f))
    }

    // Second hand - red with counterbalance
    rotate(time.secondAngle, pivot = center) {
        // Main hand
        drawLine(
            color = VostokSecondHandColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        // Counterbalance
        drawCircle(
            color = VostokSecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

private fun DrawScope.drawVostokCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = VostokHandsColor,
        radius = radius * 0.02f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun VostokRussianMilitaryPreview() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VostokRussianMilitaryWatchface()
        }
}