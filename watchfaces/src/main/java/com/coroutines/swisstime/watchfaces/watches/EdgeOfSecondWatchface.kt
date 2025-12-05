package com.coroutines.swisstime.watchfaces.watches

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.coroutines.swisstime.watchfaces.scaffold.WatchTime
import com.coroutines.swisstime.watchfaces.scaffold.WatchfaceScaffold
import com.coroutines.swisstime.watchfaces.scaffold.toWatchTime
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Colors inspired by Edge of Second
private val ClockFaceColor = Color(0xFFF5F5F5) // Silver-white dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold case
private val HourHandColor = Color(0xFF000000) // Black hour hand
private val MinuteHandColor = Color(0xFF000000) // Black minute hand
private val SecondHandColor = Color(0xFF8B0000) // Dark red second hand
private val MarkersColor = Color(0xFFD4AF37) // Gold markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val SubdialBorderColor = Color(0xFFD4AF37) // Gold subdial border
private val SubdialColor = Color(0xFFE0E0E0) // Light gray subdial
private val LogoColor = Color(0xFF000000) // Black logo

@Composable
fun EdgeOfSecondWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawEdgeClockFace(center, radius)
            drawEdgeMinuteTrack(center, radius)
            drawEdgeHourMarkersAndNumbers(center, radius)
            drawEdgeSubdials(center, radius)
            drawEdgeLogo(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawEdgeClockHands(center, radius, time)
            drawEdgeSmallSecondsHand(center, radius, time)
            drawEdgeCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawEdgeClockFace(center: Offset, radius: Float) {
    // Draw outer circle (case)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 4f,
        center = center
    )
}

private fun DrawScope.drawEdgeMinuteTrack(center: Offset, radius: Float) {
    // Carl F. Bucherer often uses a railway track minute scale
    val trackRadius = radius * 0.9f
    val innerTrackRadius = radius * 0.85f

    // Draw the outer and inner circles of the track
    drawCircle(
        color = Color.Black,
        radius = trackRadius,
        center = center,
        style = Stroke(width = 1f)
    )

    drawCircle(
        color = Color.Black,
        radius = innerTrackRadius,
        center = center,
        style = Stroke(width = 1f)
    )

    // Draw the minute markers
    for (i in 0 until 60) {
        val angle = 2 * PI * i / 60
        val isHourMarker = i % 5 == 0

        val startX = center.x + cos(angle).toFloat() * innerTrackRadius
        val startY = center.y + sin(angle).toFloat() * innerTrackRadius
        val endX = center.x + cos(angle).toFloat() * trackRadius
        val endY = center.y + sin(angle).toFloat() * trackRadius

        drawLine(
            color = Color.Black,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (isHourMarker) 1.5f else 0.5f
        )
    }
}

private fun DrawScope.drawEdgeHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Carl F. Bucherer Manero typically uses applied gold indices and Roman numerals

    // Draw applied gold indices
    for (i in 0 until 12) {
        val angle = PI / 6 * i
        val markerLength = radius * 0.08f

        // Skip positions where subdials are (typically at 3, 6, and 9 o'clock)
        if (i == 3 || i == 6 || i == 9) continue

        val startX = center.x + cos(angle).toFloat() * (radius * 0.75f)
        val startY = center.y + sin(angle).toFloat() * (radius * 0.75f)
        val endX = center.x + cos(angle).toFloat() * (radius * 0.75f - markerLength)
        val endY = center.y + sin(angle).toFloat() * (radius * 0.75f - markerLength)

        // Draw applied indices (markers)
        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    // Draw Roman numerals at 12 position
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    // Draw "XII" at 12 o'clock
    val numberX = center.x
    val numberY = center.y - radius * 0.6f + textPaint.textSize / 3

    drawContext.canvas.nativeCanvas.drawText(
        "XII",
        numberX,
        numberY,
        textPaint
    )
}

private fun DrawScope.drawEdgeSubdials(center: Offset, radius: Float) {
    // Power reserve indicator at 3 o'clock
    val powerReserveCenter = Offset(center.x + radius * 0.5f, center.y)
    val powerReserveRadius = radius * 0.15f

    // Draw power reserve subdial
    drawCircle(
        color = SubdialColor,
        radius = powerReserveRadius,
        center = powerReserveCenter
    )

    drawCircle(
        color = SubdialBorderColor,
        radius = powerReserveRadius,
        center = powerReserveCenter,
        style = Stroke(width = 2f)
    )

    // Draw power reserve scale (0-100%)
    val powerReservePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = powerReserveRadius * 0.4f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Draw "POWER RESERVE" text
    drawContext.canvas.nativeCanvas.drawText(
        "Пламя",
        powerReserveCenter.x,
        powerReserveCenter.y - powerReserveRadius * 0.3f,
        powerReservePaint
    )

    drawContext.canvas.nativeCanvas.drawText(
        "Лёд",
        powerReserveCenter.x,
        powerReserveCenter.y + powerReserveRadius * 0.5f,
        powerReservePaint
    )

    // Draw power reserve indicator hand (fixed at about 70% for display)
    val powerReserveAngle = 180f
    rotate(powerReserveAngle, pivot = powerReserveCenter) {
        drawLine(
            color = Color.Black,
            start = powerReserveCenter,
            end = Offset(powerReserveCenter.x, powerReserveCenter.y - powerReserveRadius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }

    // Small seconds subdial at 9 o'clock
    val smallSecondsCenter = Offset(center.x - radius * 0.5f, center.y)
    val smallSecondsRadius = radius * 0.15f

    // Draw small seconds subdial
    drawCircle(
        color = SubdialColor,
        radius = smallSecondsRadius,
        center = smallSecondsCenter
    )

    drawCircle(
        color = SubdialBorderColor,
        radius = smallSecondsRadius,
        center = smallSecondsCenter,
        style = Stroke(width = 2f)
    )

    // Draw small seconds markers
    for (i in 0 until 60) {
        val angle = 2 * PI * i / 60
        val isQuarterMarker = i % 15 == 0
        val isFiveSecondMarker = i % 5 == 0

        val markerLength = if (isQuarterMarker) {
            smallSecondsRadius * 0.3f
        } else if (isFiveSecondMarker) {
            smallSecondsRadius * 0.2f
        } else {
            smallSecondsRadius * 0.1f
        }

        val startX = smallSecondsCenter.x + cos(angle).toFloat() * (smallSecondsRadius - markerLength)
        val startY = smallSecondsCenter.y + sin(angle).toFloat() * (smallSecondsRadius - markerLength)
        val endX = smallSecondsCenter.x + cos(angle).toFloat() * smallSecondsRadius * 0.9f
        val endY = smallSecondsCenter.y + sin(angle).toFloat() * smallSecondsRadius * 0.9f

        drawLine(
            color = Color.Black,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (isQuarterMarker) 1.5f else 0.5f
        )
    }

    // Date subdial at 6 o'clock
    val dateCenter = Offset(center.x, center.y + radius * 0.5f)
    val dateRadius = radius * 0.15f

    // Draw date subdial
    drawCircle(
        color = SubdialColor,
        radius = dateRadius,
        center = dateCenter
    )

    drawCircle(
        color = SubdialBorderColor,
        radius = dateRadius,
        center = dateCenter,
        style = Stroke(width = 2f)
    )

    // Draw "DATE" text
    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = dateRadius * 0.4f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "DATE",
        dateCenter.x,
        dateCenter.y,
        datePaint
    )

    // Draw current date (fixed at "15" for display)
    val dateDayPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = dateRadius * 0.6f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "15",
        dateCenter.x,
        dateCenter.y + dateRadius * 0.5f,
        dateDayPaint
    )
}

private fun DrawScope.drawEdgeClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand - leaf-shaped
    rotate(time.hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.4f) // Tip
            quadraticTo(
                center.x + radius * 0.03f, center.y - radius * 0.2f, // Control point
                center.x + radius * 0.015f, center.y // End point
            )
            quadraticTo(
                center.x, center.y + radius * 0.05f, // Control point
                center.x - radius * 0.015f, center.y // End point
            )
            quadraticTo(
                center.x - radius * 0.03f, center.y - radius * 0.2f, // Control point
                center.x, center.y - radius * 0.4f // End point (back to start)
            )
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand - longer leaf-shaped
    rotate(time.minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.6f) // Tip
            quadraticTo(
                center.x + radius * 0.025f, center.y - radius * 0.3f, // Control point
                center.x + radius * 0.01f, center.y // End point
            )
            quadraticTo(
                center.x, center.y + radius * 0.05f, // Control point
                center.x - radius * 0.01f, center.y // End point
            )
            quadraticTo(
                center.x - radius * 0.025f, center.y - radius * 0.3f, // Control point
                center.x, center.y - radius * 0.6f // End point (back to start)
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }
}

private fun DrawScope.drawEdgeSmallSecondsHand(center: Offset, radius: Float, time: WatchTime) {
    // Small seconds subdial at 9 o'clock
    val smallSecondsCenter = Offset(center.x - radius * 0.5f, center.y)
    val smallSecondsRadius = radius * 0.15f

    // Draw small seconds hand
    rotate(time.secondAngle, pivot = smallSecondsCenter) {
        drawLine(
            color = SecondHandColor,
            start = smallSecondsCenter,
            end = Offset(smallSecondsCenter.x, smallSecondsCenter.y - smallSecondsRadius * 0.8f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawEdgeLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    // Draw "CARL F. BUCHERER" text
    drawContext.canvas.nativeCanvas.drawText(
        "Грань Секунды",
        center.x,
        center.y - radius * 0.25f,
        logoPaint
    )

    // Draw "MANERO" text
    val modelPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Cosmos",
        center.x,
        center.y - radius * 0.15f,
        modelPaint
    )

    // Draw "SWISS MADE" text
    val swissMadePaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Сделано в России",
        center.x,
        center.y + radius * 0.15f,
        swissMadePaint
    )
}

private fun DrawScope.drawEdgeCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = ClockBorderColor,
        radius = radius * 0.02f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun EdgeOfSecondPreview() {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EdgeOfSecondWatchface()
        }

}