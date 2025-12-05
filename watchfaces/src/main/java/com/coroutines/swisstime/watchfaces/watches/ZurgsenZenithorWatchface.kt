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

// Colors inspired by Jurgsen Zenithor
private val ClockFaceColor = Color(0xFF000000) // Deep black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val BezelColor = Color(0xFF000080) // Navy blue bezel
private val BezelMarkersColor = Color(0xFFFFFFFF) // White bezel markers
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF4500) // Orange-red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val LumeColor = Color(0xFF90EE90) // Light green lume
private val CenterDotColor = Color(0xFFFFFFFF) // White center dot

@Composable
fun JurgsenZenithorWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawJurgsenClockFace(center, radius)
            drawJurgsenHourMarkersAndNumbers(center, radius, timeZone)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawJurgsenClockHands(center, radius, time)
            drawJurgsenCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawJurgsenClockFace(center: Offset, radius: Float) {
    // Draw outer circle (border)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 8f)
    )

    // Draw rotating bezel (characteristic of dive watches)
    drawCircle(
        color = BezelColor,
        radius = radius * 0.95f,
        center = center
    )

    // Draw bezel markers (minute markers for diving)
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60

        if (i % 5 == 0) {
            // Draw larger markers at 5-minute intervals
            val markerLength = if (i == 0) radius * 0.08f else radius * 0.06f // Bigger triangle at 12 o'clock
            val markerStart = radius * 0.95f - markerLength
            val markerEnd = radius * 0.95f

            val startX = center.x + cos(angle).toFloat() * markerStart
            val startY = center.y + sin(angle).toFloat() * markerStart
            val endX = center.x + cos(angle).toFloat() * markerEnd
            val endY = center.y + sin(angle).toFloat() * markerEnd

            // Draw triangle at 12 o'clock (0 minutes)
            if (i == 0) {
                drawCircle(
                    color = LumeColor,
                    radius = radius * 0.03f,
                    center = Offset(endX, endY)
                )
            } else {
                // Draw dot markers for other 5-minute intervals
                drawCircle(
                    color = BezelMarkersColor,
                    radius = radius * 0.02f,
                    center = Offset(endX, endY)
                )
            }
        } else {
            // Draw smaller markers for minutes
            val dotX = center.x + cos(angle).toFloat() * radius * 0.95f
            val dotY = center.y + sin(angle).toFloat() * radius * 0.95f

            drawCircle(
                color = BezelMarkersColor,
                radius = radius * 0.005f,
                center = Offset(dotX, dotY)
            )
        }
    }

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.85f,
        center = center
    )

    // Draw Blancpain logo text
    val logoPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Zénithor",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    // Draw "JÜRGSEN GENÈVE" text
    val modelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "JÜRGSEN GENÈVE",
        center.x,
        center.y + radius * 0.3f,
        modelPaint
    )
}

private fun DrawScope.drawJurgsenHourMarkersAndNumbers(center: Offset, radius: Float, timeZone: TimeZone) {
    // Fifty Fathoms uses large, luminous hour markers
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)

        // Draw circular hour markers with lume
        val markerRadius = if (i % 3 == 0) radius * 0.06f else radius * 0.05f
        val markerX = center.x + cos(angle).toFloat() * radius * 0.7f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.7f

        // White outer circle
        drawCircle(
            color = MarkersColor,
            radius = markerRadius,
            center = Offset(markerX, markerY)
        )

        // Lume inner circle (slightly smaller)
        drawCircle(
            color = LumeColor,
            radius = markerRadius * 0.8f,
            center = Offset(markerX, markerY)
        )

        // Special rectangular marker at 12 o'clock
        if (i == 12) {
            drawRect(
                color = MarkersColor,
                topLeft = Offset(center.x - radius * 0.06f, center.y - radius * 0.7f - radius * 0.06f),
                size = Size(radius * 0.12f, radius * 0.12f)
            )

            // Lume inside
            drawRect(
                color = LumeColor,
                topLeft = Offset(center.x - radius * 0.05f, center.y - radius * 0.7f - radius * 0.05f),
                size = Size(radius * 0.1f, radius * 0.1f)
            )
        }
    }

    // Draw date window at 4:30 position
    val dateAngle = Math.PI / 6 * 4.5 // Between 4 and 5
    val dateX = center.x + cos(dateAngle).toFloat() * radius * 0.55f
    val dateY = center.y + sin(dateAngle).toFloat() * radius * 0.55f

    // White date window
    drawRect(
        color = Color.White,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f)
    )

    // Date text
    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.1f
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

private fun DrawScope.drawJurgsenClockHands(center: Offset, radius: Float, time: WatchTime) {
    // Hour hand - broad sword-shaped with lume
    rotate(time.hourAngle, pivot = center) {
        // Main hour hand
        drawRoundRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.04f, center.y - radius * 0.5f),
            size = Size(radius * 0.08f, radius * 0.5f),
            cornerRadius = CornerRadius(radius * 0.02f)
        )

        // Lume on hour hand
        drawRoundRect(
            color = LumeColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.48f),
            size = Size(radius * 0.06f, radius * 0.4f),
            cornerRadius = CornerRadius(radius * 0.015f)
        )
    }

    // Minute hand - longer sword-shaped with lume
    rotate(time.minuteAngle, pivot = center) {
        // Main minute hand
        drawRoundRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.7f),
            size = Size(radius * 0.06f, radius * 0.7f),
            cornerRadius = CornerRadius(radius * 0.015f)
        )

        // Lume on minute hand
        drawRoundRect(
            color = LumeColor,
            topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.68f),
            size = Size(radius * 0.04f, radius * 0.6f),
            cornerRadius = CornerRadius(radius * 0.01f)
        )
    }

    // Second hand - thin with distinctive circle near tip
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
            radius = radius * 0.04f,
            center = Offset(center.x, center.y - radius * 0.6f)
        )

        // Counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.1f)
        )
    }
}

private fun DrawScope.drawJurgsenCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = CenterDotColor,
        radius = radius * 0.04f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun JurgsenZenithorPreview() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            JurgsenZenithorWatchface()
        }

}