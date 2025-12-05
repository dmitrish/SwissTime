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
import kotlin.math.cos
import kotlin.math.sin

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
fun HorologiaRomanumWatchface(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    WatchfaceScaffold(
        modifier = modifier,
        timeZone = timeZone,
        staticContent = { center, radius, _ ->
            drawHorologiaStaticElements(center, radius)
            drawHorologiaHourMarkersAndNumbers(center, radius)
            drawHorologiaSubdialBackground(center, radius)
        },
        animatedContent = { center, radius, currentTime, _ ->
            val time = currentTime.toWatchTime()
            drawHorologiaHourHand(center, radius, time)
            drawHorologiaMinuteHand(center, radius, time)
            drawHorologiaSubdialSecondHand(center, radius, time)
            drawHorologiaCenterDot(center, radius)
        }
    )
}

private fun DrawScope.drawHorologiaStaticElements(center: Offset, radius: Float) {
    // Draw outer border
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 6f)
    )

    // Draw main face
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 3f,
        center = center
    )

    // Draw logo
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

    // Draw origin text
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

private fun DrawScope.drawHorologiaSubdialBackground(center: Offset, radius: Float) {
    val subdialCenter = Offset(center.x, center.y + radius * 0.4f)
    val subdialRadius = radius * 0.2f

    // Draw subdial face
    drawCircle(color = SubdialColor, radius = subdialRadius, center = subdialCenter)
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = subdialCenter,
        style = Stroke(width = 2f)
    )

    // Draw subdial markers
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60
        val markerLength = if (i % 15 == 0) {
            subdialRadius * 0.2f
        } else if (i % 5 == 0) {
            subdialRadius * 0.15f
        } else {
            subdialRadius * 0.05f
        }

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

    // Draw subdial numbers (60, 15, 30, 45)
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

private fun DrawScope.drawHorologiaHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Draw minute markers (skip subdial area)
    for (i in 1..60) {
        val angle = Math.PI / 30 * (i - 15)
        val markerLength = if (i % 5 == 0) radius * 0.05f else radius * 0.02f
        val strokeWidth = if (i % 5 == 0) 2f else 1f

        // Skip markers in subdial area (bottom section)
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

    // Draw hour numbers (skip 6 because of subdial)
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
        val angle = Math.PI / 6 * positions[i] - Math.PI / 2
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

private fun DrawScope.drawHorologiaHourHand(center: Offset, radius: Float, time: WatchTime) {
    rotate(time.hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f)
            quadraticTo(
                center.x + radius * 0.04f, center.y - radius * 0.25f,
                center.x + radius * 0.02f, center.y
            )
            quadraticTo(
                center.x, center.y + radius * 0.1f,
                center.x - radius * 0.02f, center.y
            )
            quadraticTo(
                center.x - radius * 0.04f, center.y - radius * 0.25f,
                center.x, center.y - radius * 0.5f
            )
            close()
        }
        drawPath(path, HourHandColor)
    }
}

private fun DrawScope.drawHorologiaMinuteHand(center: Offset, radius: Float, time: WatchTime) {
    rotate(time.minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f)
            quadraticTo(
                center.x + radius * 0.03f, center.y - radius * 0.35f,
                center.x + radius * 0.015f, center.y
            )
            quadraticTo(
                center.x, center.y + radius * 0.1f,
                center.x - radius * 0.015f, center.y
            )
            quadraticTo(
                center.x - radius * 0.03f, center.y - radius * 0.35f,
                center.x, center.y - radius * 0.7f
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }
}

private fun DrawScope.drawHorologiaSubdialSecondHand(center: Offset, radius: Float, time: WatchTime) {
    val subdialCenter = Offset(center.x, center.y + radius * 0.4f)
    val subdialRadius = radius * 0.2f

    rotate(degrees = time.secondAngle, pivot = subdialCenter) {
        // Main hand pointing up
        drawLine(
            color = SubdialHandColor,
            start = subdialCenter,
            end = Offset(subdialCenter.x, subdialCenter.y - subdialRadius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        // Counterbalance pointing down
        drawLine(
            color = SubdialHandColor,
            start = subdialCenter,
            end = Offset(subdialCenter.x, subdialCenter.y + subdialRadius * 0.2f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHorologiaCenterDot(center: Offset, radius: Float) {
    drawCircle(
        color = CenterDotColor,
        radius = radius * 0.03f,
        center = center
    )
}

@Preview(showBackground = true)
@Composable
fun HorologiaRomanumPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorologiaRomanumWatchface()
    }
}