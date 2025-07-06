package com.coroutines.swisstime.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


private val ClockFaceColor = Color(0xFFFFFFFF) // White dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFF000000) // Black hour hand
private val MinuteHandColor = Color(0xFF000000) // Black minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val SubdialColor = Color(0xFFE0E0E0) // Light gray subdial
private val SubdialHandColor = Color(0xFF000000) // Black subdial hand
private val CenterDotColor = Color(0xFF000000) // Black center dot
private val TachymeterColor = Color(0xFF000000) // Black tachymeter scale

@Composable
fun Tokinoha(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone )) }

    val timeZoneX by rememberUpdatedState(timeZone)
    // Update time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = Calendar.getInstance(timeZoneX)
            delay(1000) // Update every second
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Draw the clock
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 0.8f

            // Draw clock face
            drawClockFace(center, radius)

            // Get current time values
            val hour = currentTime.get(Calendar.HOUR)
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw hour markers and numbers
            drawHourMarkersAndNumbers(center, radius, timeZoneX)

            // Draw clock hands
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = CenterDotColor,
                radius = radius * 0.03f,
                center = center
            )
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw outer circle (border)
    drawCircle(
        color = ClockBorderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 6f)
    )

    // Draw tachymeter scale (characteristic of racing chronographs)
    drawCircle(
        color = TachymeterColor,
        radius = radius * 0.95f,
        center = center,
        style = Stroke(width = 10f)
    )
    
    // Draw tachymeter markings and numbers
    val tachymeterPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    // Simplified tachymeter scale
    val tachyValues = listOf(60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170)
    for (i in tachyValues.indices) {
        val angle = Math.PI * 2 * i / tachyValues.size
        val numberX = center.x + cos(angle).toFloat() * radius * 0.95f
        val numberY = center.y + sin(angle).toFloat() * radius * 0.95f + tachymeterPaint.textSize / 3
        
        drawContext.canvas.nativeCanvas.drawText(
            tachyValues[i].toString(),
            numberX,
            numberY,
            tachymeterPaint
        )
    }

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.85f,
        center = center
    )
    
    // Draw TAG Heuer logo
    val logoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "時の葉",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    

    val modelPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.07f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "Tokinoha",
        center.x,
        center.y - radius * 0.15f,
        modelPaint
    )
    
    // Draw "CHRONOGRAPH" text
    val chronoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "CHRONOGRAPH",
        center.x,
        center.y + radius * 0.15f,
        chronoPaint
    )
    
    // Draw three subdials (characteristic of chronographs)
    val subdialRadius = radius * 0.15f
    
    // Left subdial (9 o'clock position - running seconds)
    val leftSubdialCenter = Offset(center.x - radius * 0.35f, center.y)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = leftSubdialCenter
    )
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = leftSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Right subdial (3 o'clock position - chronograph minutes)
    val rightSubdialCenter = Offset(center.x + radius * 0.35f, center.y)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = rightSubdialCenter
    )
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = rightSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Bottom subdial (6 o'clock position - chronograph hours)
    val bottomSubdialCenter = Offset(center.x, center.y + radius * 0.35f)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = bottomSubdialCenter
    )
    drawCircle(
        color = Color.Black,
        radius = subdialRadius,
        center = bottomSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Draw subdial markers
    for (subdialCenter in listOf(leftSubdialCenter, rightSubdialCenter, bottomSubdialCenter)) {
        for (i in 0 until 12) {
            val angle = Math.PI * 2 * i / 12
            val markerLength = subdialRadius * 0.2f
            val startX = subdialCenter.x + cos(angle).toFloat() * (subdialRadius - markerLength)
            val startY = subdialCenter.y + sin(angle).toFloat() * (subdialRadius - markerLength)
            val endX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.9f
            val endY = subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.9f
            
            drawLine(
                color = Color.Black,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1f
            )
        }
    }
    
    // Draw running seconds hand in left subdial
    val second = Calendar.getInstance().get(Calendar.SECOND)
    val secondAngle = second * 6f
    
    rotate(secondAngle, pivot = leftSubdialCenter) {
        drawLine(
            color = SecondHandColor,
            start = leftSubdialCenter,
            end = Offset(leftSubdialCenter.x, leftSubdialCenter.y - subdialRadius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float, timeZone: TimeZone ) {

    
    // Draw hour markers (applied baton style)
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerLength = radius * 0.08f
        val markerWidth = radius * 0.02f
        
        val markerX = center.x + cos(angle).toFloat() * radius * 0.7f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.7f
        
        // Skip markers where subdials are
        if (i == 3 || i == 6 || i == 9) continue
        
        // Draw rectangular marker with 3D effect
        rotate(
            degrees = (i - 3) * 30f,
            pivot = Offset(markerX, markerY)
        ) {
            // Main marker
            drawRect(
                color = MarkersColor,
                topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
                size = Size(markerWidth, markerLength)
            )
            
            // Shadow/highlight for 3D effect
            drawRect(
                color = Color.LightGray,
                topLeft = Offset(markerX - markerWidth / 2 + 1f, markerY - markerLength / 2 + 1f),
                size = Size(markerWidth - 2f, markerLength - 2f)
            )
        }
    }
    
    // Draw date window at 4:30 position
    val dateAngle = Math.PI / 6 * 4.5 // Between 4 and 5
    val dateX = center.x + cos(dateAngle).toFloat() * radius * 0.55f
    val dateY = center.y + sin(dateAngle).toFloat() * radius * 0.55f
    
    // Date window
    drawRect(
        color = Color.White,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f)
    )
    drawRect(
        color = Color.Black,
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
    
    val day = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH).toString()
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.03f,
        datePaint
    )
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - faceted sword-shaped
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Main hour hand
        drawRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.4f),
            size = Size(radius * 0.06f, radius * 0.4f)
        )
        
        // Highlight for faceted effect
        drawRect(
            color = Color.LightGray,
            topLeft = Offset(center.x - radius * 0.015f, center.y - radius * 0.4f),
            size = Size(radius * 0.03f, radius * 0.4f)
        )
    }

    // Minute hand - longer faceted sword-shaped
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Main minute hand
        drawRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.6f),
            size = Size(radius * 0.04f, radius * 0.6f)
        )
        
        // Highlight for faceted effect
        drawRect(
            color = Color.LightGray,
            topLeft = Offset(center.x - radius * 0.01f, center.y - radius * 0.6f),
            size = Size(radius * 0.02f, radius * 0.6f)
        )
    }

    // Chronograph second hand - thin with distinctive counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.15f),
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        

        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.04f,
            center = Offset(center.x, center.y + radius * 0.1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TokinohaPreview() {
    SwissTimeTheme {
        Tokinoha()
    }
}