package com.coroutines.swisstime.watchfaces.watches


import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.watchfaces.scaffold.WatchfaceScaffold
import com.coroutines.swisstime.watchfaces.scaffold.toWatchTime
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

private val ClockFaceStartColor = Color(0xFF1E5631)
private val ClockFaceEndColor = Color(0xFF0A2714)
private val ClockBorderColor = Color(0xFFE0E0E0)
private val HourHandColor = Color(0xFFE0E0E0)
private val MinuteHandColor = Color(0xFFE0E0E0)
private val SecondHandColor = Color(0xFFE0E0E0)
private val MarkersColor = Color(0xFFE0E0E0)
private val LogoColor = Color(0xFFE0E0E0)

@Composable
fun CenturioLuminorWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier.size(300.dp),
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawCenturioClockFace(center, radius)
            drawCenturioHourMarkers(center, radius)
            drawCenturioLogo(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawCenturioHourHand(center, radius, time.hourAngle)
            drawCenturioMinuteHand(center, radius, time.minuteAngle)
            drawCenturioSecondHand(center, radius, time.secondAngle)
            drawCenturioCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawCenturioClockFace(center: Offset, radius: Float) {
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

private fun DrawScope.drawCenturioHourMarkers(center: Offset, radius: Float) {
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

private fun DrawScope.drawCenturioLogo(center: Offset, radius: Float) {
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

private fun DrawScope.drawCenturioHourHand(center: Offset, radius: Float, angle: Float) {
    rotate(angle, pivot = center) {
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
}

private fun DrawScope.drawCenturioMinuteHand(center: Offset, radius: Float, angle: Float) {
    rotate(angle, pivot = center) {
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
}

private fun DrawScope.drawCenturioSecondHand(center: Offset, radius: Float, angle: Float) {
    rotate(angle, pivot = center) {
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawCenturioCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = HourHandColor,
        radius = radius * 0.02f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun CenturiPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CenturioLuminorWatchface()
    }
}