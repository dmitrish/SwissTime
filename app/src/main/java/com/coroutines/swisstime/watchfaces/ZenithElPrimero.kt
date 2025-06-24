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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Zenith El Primero Chronomaster
private val ClockFaceColor = Color(0xFFF0F0F0) // Silver-white dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFF000000) // Black hour hand
private val MinuteHandColor = Color(0xFF000000) // Black minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val SubdialBorderColor = Color(0xFF000000) // Black subdial border
private val LeftSubdialColor = Color(0xFF0000FF) // Blue subdial (9 o'clock)
private val RightSubdialColor = Color(0xFF808080) // Gray subdial (3 o'clock)
private val BottomSubdialColor = Color(0xFF404040) // Dark gray subdial (6 o'clock)
private val SubdialHandColor = Color(0xFFFFFFFF) // White subdial hand
private val CenterDotColor = Color(0xFF000000) // Black center dot
private val TachymeterColor = Color(0xFF000000) // Black tachymeter scale

@Composable
fun ZenithElPrimero(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    // Update time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = Calendar.getInstance()
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
            drawHourMarkersAndNumbers(center, radius)

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

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 3f,
        center = center
    )
    
    // Draw Zenith star logo
    val starSize = radius * 0.15f
    val starY = center.y - radius * 0.25f
    
    // Draw simplified star (5 points)
    val starPoints = 5
    val outerRadius = starSize
    val innerRadius = starSize * 0.4f
    
    val path = Path()
    for (i in 0 until starPoints * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = Math.PI * i / starPoints - Math.PI / 2
        val x = center.x + cos(angle).toFloat() * radius
        val y = starY + sin(angle).toFloat() * radius
        
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    
    drawPath(
        path = path,
        color = Color(0xFFFFD700) // Gold star
    )
    
    // Draw "ZENITH" text
    val logoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "ZENITH",
        center.x,
        center.y - radius * 0.1f,
        logoPaint
    )
    
    // Draw "EL PRIMERO" text
    val modelPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.07f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "EL PRIMERO",
        center.x,
        center.y + radius * 0.1f,
        modelPaint
    )
    
    // Draw three overlapping subdials (characteristic of El Primero)
    val subdialRadius = radius * 0.2f
    val subdialDistance = radius * 0.3f
    
    // Left subdial (9 o'clock position - running seconds) - Blue
    val leftSubdialCenter = Offset(center.x - subdialDistance, center.y)
    drawCircle(
        color = LeftSubdialColor,
        radius = subdialRadius,
        center = leftSubdialCenter
    )
    drawCircle(
        color = SubdialBorderColor,
        radius = subdialRadius,
        center = leftSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Right subdial (3 o'clock position - chronograph minutes) - Gray
    val rightSubdialCenter = Offset(center.x + subdialDistance, center.y)
    drawCircle(
        color = RightSubdialColor,
        radius = subdialRadius,
        center = rightSubdialCenter
    )
    drawCircle(
        color = SubdialBorderColor,
        radius = subdialRadius,
        center = rightSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Bottom subdial (6 o'clock position - chronograph hours) - Dark Gray
    val bottomSubdialCenter = Offset(center.x, center.y + subdialDistance)
    drawCircle(
        color = BottomSubdialColor,
        radius = subdialRadius,
        center = bottomSubdialCenter
    )
    drawCircle(
        color = SubdialBorderColor,
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
                color = Color.White,
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
            color = SubdialHandColor,
            start = leftSubdialCenter,
            end = Offset(leftSubdialCenter.x, leftSubdialCenter.y - subdialRadius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
    
    // Draw date window at 4:30 position
    val dateAngle = Math.PI / 6 * 4.5 // Between 4 and 5
    val dateX = center.x + cos(dateAngle).toFloat() * radius * 0.6f
    val dateY = center.y + sin(dateAngle).toFloat() * radius * 0.6f
    
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
    
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.03f,
        datePaint
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // El Primero uses applied hour markers
    
    // Draw hour markers (applied baton style)
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerLength = radius * 0.08f
        val markerWidth = radius * 0.02f
        
        val markerX = center.x + cos(angle).toFloat() * radius * 0.75f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.75f
        
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
    
    // Draw small minute markers
    for (i in 0 until 60) {
        if (i % 5 == 0) continue // Skip where hour markers are
        
        val angle = Math.PI * 2 * i / 60
        val markerLength = radius * 0.03f
        
        val startX = center.x + cos(angle).toFloat() * radius * 0.85f
        val startY = center.y + sin(angle).toFloat() * radius * 0.85f
        val endX = center.x + cos(angle).toFloat() * (radius * 0.85f - markerLength)
        val endY = center.y + sin(angle).toFloat() * (radius * 0.85f - markerLength)
        
        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - dauphine-style with polished edges
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Main hour hand
        val hourHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            lineTo(center.x + radius * 0.04f, center.y) // Right corner
            lineTo(center.x, center.y + radius * 0.1f) // Bottom
            lineTo(center.x - radius * 0.04f, center.y) // Left corner
            close()
        }
        drawPath(hourHandPath, HourHandColor)
        
        // Polished edge effect
        val hourHandHighlightPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            lineTo(center.x + radius * 0.02f, center.y) // Right corner (inner)
            lineTo(center.x, center.y + radius * 0.05f) // Bottom (inner)
            close()
        }
        drawPath(hourHandHighlightPath, Color.LightGray)
    }

    // Minute hand - longer dauphine-style
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Main minute hand
        val minuteHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.03f, center.y) // Right corner
            lineTo(center.x, center.y + radius * 0.1f) // Bottom
            lineTo(center.x - radius * 0.03f, center.y) // Left corner
            close()
        }
        drawPath(minuteHandPath, MinuteHandColor)
        
        // Polished edge effect
        val minuteHandHighlightPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.015f, center.y) // Right corner (inner)
            lineTo(center.x, center.y + radius * 0.05f) // Bottom (inner)
            close()
        }
        drawPath(minuteHandHighlightPath, Color.LightGray)
    }

    // Chronograph second hand - thin with distinctive arrow tip
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
        
        // Arrow tip
        val arrowSize = radius * 0.05f
        val arrowPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.75f - arrowSize) // Tip
            lineTo(center.x + arrowSize / 2, center.y - radius * 0.75f) // Right corner
            lineTo(center.x - arrowSize / 2, center.y - radius * 0.75f) // Left corner
            close()
        }
        drawPath(arrowPath, SecondHandColor)
        
        // Counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ZenithElPrimeroPreview() {
    SwissTimeTheme {
        ZenithElPrimero()
    }
}