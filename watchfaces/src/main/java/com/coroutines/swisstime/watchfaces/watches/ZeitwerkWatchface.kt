package com.coroutines.swisstime.watchfaces.watches


import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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

private val ClockFaceColor = Color(0xFF1A3A5A) // Deep Atlantic blue dial
private val ClockBorderColor = Color(0xFFD0D0D0) // Silver stainless steel border
private val HourHandColor = Color(0xFFE0E0E0) // Silver hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Silver minute hand
private val SecondHandColor = Color(0xFFE63946) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val LumeColor = Color(0xFF90EE90) // Light green lume for hands and markers
private val CenterDotColor = Color(0xFFE0E0E0) // Silver center dot

@Composable
fun ZeitwerkWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawZeitwerkClockFace(center, radius, timeZone)
            drawZeitwerkHourMarkersAndNumbers(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawZeitwerkClockHands(center, radius, time)
            drawZeitwerkCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawZeitwerkClockFace(center: Offset, radius: Float, timeZone: TimeZone) {
    // Draw outer circle (border) - stainless steel case
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )

    // Draw inner circle (face) - Atlantic blue dial
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.95f,
        center = center
    )

    // Draw "Zeitwerk" logo
    val logoPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Zeitwerk",
        center.x,
        center.y - radius * 0.15f,
        logoPaint
    )

    // Draw "Alpenglühen" text
    val locationPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Alpenglühen",
        center.x,
        center.y - radius * 0.05f,
        locationPaint
    )

    // Draw "ZEIT" text
    val modelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "ZEIT",
        center.x,
        center.y + radius * 0.6f,
        modelPaint
    )

    // Draw "AUTOMATIC" text
    val subModelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "AUTOMATIC",
        center.x,
        center.y + radius * 0.8f,
        subModelPaint
    )

    // Draw date window at 6 o'clock
    val dateAngle = Math.PI * 1.5 // 6 o'clock
    val dateX = center.x + cos(dateAngle).toFloat() * radius * 0.7f
    val dateY = center.y + sin(dateAngle).toFloat() * radius * 0.7f

    // Date window - rectangular with rounded corners
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f),
        cornerRadius = CornerRadius(radius * 0.01f)
    )

    // Date text
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
        dateY + radius * 0.035f,
        datePaint
    )
}

private fun DrawScope.drawZeitwerkHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Draw hour markers
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i

        // Skip 6 o'clock where the date window is
        if (i == 6) continue

        val markerLength = if (i % 3 == 0) radius * 0.1f else radius * 0.05f // Longer at 12, 3, 9
        val markerWidth = if (i % 3 == 0) radius * 0.02f else radius * 0.01f // Thicker at 12, 3, 9

        val outerX = center.x + cos(angle).toFloat() * radius * 0.85f
        val outerY = center.y + sin(angle).toFloat() * radius * 0.85f
        val innerX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
        val innerY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)

        // Draw hour marker
        drawLine(
            color = MarkersColor,
            start = Offset(innerX, innerY),
            end = Offset(outerX, outerY),
            strokeWidth = markerWidth,
            cap = StrokeCap.Round
        )

        // Add lume dot at the end of the marker
        if (i % 3 == 0) {
            drawCircle(
                color = LumeColor,
                radius = markerWidth * 0.8f,
                center = Offset(outerX, outerY)
            )
        }
    }

    // Draw minute markers (smaller lines)
    for (i in 0 until 60) {
        // Skip positions where hour markers are
        if (i % 5 == 0) continue

        val angle = Math.PI * 2 * i / 60
        val markerLength = radius * 0.02f

        val outerX = center.x + cos(angle).toFloat() * radius * 0.85f
        val outerY = center.y + sin(angle).toFloat() * radius * 0.85f
        val innerX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
        val innerY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)

        // Draw minute marker
        drawLine(
            color = MarkersColor,
            start = Offset(innerX, innerY),
            end = Offset(outerX, outerY),
            strokeWidth = radius * 0.005f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawZeitwerkClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand - straight with lume
    rotate(time.hourAngle, pivot = center) {
        // Main hour hand - straight and thin
        drawRoundRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.5f),
            size = Size(radius * 0.04f, radius * 0.5f),
            cornerRadius = CornerRadius(radius * 0.01f)
        )

        // Lume on hour hand tip
        drawCircle(
            color = LumeColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y - radius * 0.45f)
        )
    }

    // Minute hand - longer and thinner
    rotate(time.minuteAngle, pivot = center) {
        // Main minute hand - straight and thin
        drawRoundRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.015f, center.y - radius * 0.7f),
            size = Size(radius * 0.03f, radius * 0.7f),
            cornerRadius = CornerRadius(radius * 0.01f)
        )

        // Lume on minute hand tip
        drawCircle(
            color = LumeColor,
            radius = radius * 0.025f,
            center = Offset(center.x, center.y - radius * 0.65f)
        )
    }

    // Second hand - thin red with distinctive circle near tip
    rotate(time.secondAngle, pivot = center) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.15f),
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Distinctive circle near tip
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y - radius * 0.65f)
        )

        // Counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.02f,
            center = Offset(center.x, center.y + radius * 0.1f)
        )
    }
}

private fun DrawScope.drawZeitwerkCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = CenterDotColor,
        radius = radius * 0.03f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun ZeitwerkPreview() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ZeitwerkWatchface()
        }
}