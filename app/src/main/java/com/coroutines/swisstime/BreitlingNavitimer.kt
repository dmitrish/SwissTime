package com.coroutines.swisstime

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Breitling Navitimer
private val ClockFaceColor = Color(0xFF000000) // Black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val OuterBezelColor = Color(0xFF8B4513) // Brown outer bezel
private val InnerBezelColor = Color(0xFFFFFFFF) // White inner bezel
private val BezelMarkersColor = Color(0xFF000000) // Black bezel markers
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val SubdialColor = Color(0xFF333333) // Dark gray subdial
private val SubdialHandColor = Color(0xFFFFFFFF) // White subdial hand
private val CenterDotColor = Color(0xFFFFFFFF) // White center dot
private val LogoColor = Color(0xFFFFD700) // Gold logo color

@Composable
fun BreitlingNavitimer(modifier: Modifier = Modifier) {
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
                radius = radius * 0.04f,
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
        style = Stroke(width = 8f)
    )

    // Draw outer slide rule bezel (characteristic of Navitimer)
    drawCircle(
        color = OuterBezelColor,
        radius = radius * 0.95f,
        center = center
    )
    
    // Draw inner slide rule bezel
    drawCircle(
        color = InnerBezelColor,
        radius = radius * 0.85f,
        center = center
    )
    
    // Draw slide rule markings (simplified)
    for (i in 0 until 60) {
        val angle = Math.PI * 2 * i / 60
        
        // Outer bezel markings
        val outerMarkerLength = if (i % 5 == 0) radius * 0.05f else radius * 0.02f
        val outerStartX = center.x + cos(angle).toFloat() * (radius * 0.95f - outerMarkerLength)
        val outerStartY = center.y + sin(angle).toFloat() * (radius * 0.95f - outerMarkerLength)
        val outerEndX = center.x + cos(angle).toFloat() * radius * 0.95f
        val outerEndY = center.y + sin(angle).toFloat() * radius * 0.95f
        
        drawLine(
            color = if (i % 5 == 0) Color.White else Color.White.copy(alpha = 0.7f),
            start = Offset(outerStartX, outerStartY),
            end = Offset(outerEndX, outerEndY),
            strokeWidth = if (i % 5 == 0) 2f else 1f
        )
        
        // Inner bezel markings
        val innerMarkerLength = if (i % 5 == 0) radius * 0.04f else radius * 0.02f
        val innerStartX = center.x + cos(angle).toFloat() * (radius * 0.85f - innerMarkerLength)
        val innerStartY = center.y + sin(angle).toFloat() * (radius * 0.85f - innerMarkerLength)
        val innerEndX = center.x + cos(angle).toFloat() * radius * 0.85f
        val innerEndY = center.y + sin(angle).toFloat() * radius * 0.85f
        
        drawLine(
            color = BezelMarkersColor,
            start = Offset(innerStartX, innerStartY),
            end = Offset(innerEndX, innerEndY),
            strokeWidth = if (i % 5 == 0) 2f else 1f
        )
    }
    
    // Draw slide rule numbers (simplified)
    val bezelNumberPaint = Paint().apply {
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    // Outer bezel numbers
    bezelNumberPaint.color = Color.White.hashCode()
    for (i in 0 until 12) {
        val angle = Math.PI * 2 * i / 12
        val numberX = center.x + cos(angle).toFloat() * radius * 0.9f
        val numberY = center.y + sin(angle).toFloat() * radius * 0.9f + bezelNumberPaint.textSize / 3
        
        drawContext.canvas.nativeCanvas.drawText(
            (i * 10).toString(),
            numberX,
            numberY,
            bezelNumberPaint
        )
    }
    
    // Inner bezel numbers
    bezelNumberPaint.color = Color.Black.hashCode()
    for (i in 0 until 12) {
        val angle = Math.PI * 2 * i / 12
        val numberX = center.x + cos(angle).toFloat() * radius * 0.78f
        val numberY = center.y + sin(angle).toFloat() * radius * 0.78f + bezelNumberPaint.textSize / 3
        
        drawContext.canvas.nativeCanvas.drawText(
            (i * 10).toString(),
            numberX,
            numberY,
            bezelNumberPaint
        )
    }

    // Draw inner circle (face)
    drawCircle(
        color = ClockFaceColor,
        radius = radius * 0.7f,
        center = center
    )
    
    // Draw Breitling logo and wings
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "B",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw simplified wings
    val wingWidth = radius * 0.25f
    val wingHeight = radius * 0.05f
    val wingY = center.y - radius * 0.3f - wingHeight / 2
    
    // Left wing
    drawLine(
        color = LogoColor,
        start = Offset(center.x - radius * 0.05f, wingY),
        end = Offset(center.x - wingWidth, wingY),
        strokeWidth = wingHeight
    )
    
    // Right wing
    drawLine(
        color = LogoColor,
        start = Offset(center.x + radius * 0.05f, wingY),
        end = Offset(center.x + wingWidth, wingY),
        strokeWidth = wingHeight
    )
    
    // Draw "NAVITIMER" text
    val modelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "NAVITIMER",
        center.x,
        center.y - radius * 0.15f,
        modelPaint
    )
    
    // Draw three subdials (characteristic of Navitimer)
    val subdialRadius = radius * 0.15f
    val subdialDistance = radius * 0.35f
    
    // Left subdial (9 o'clock position)
    val leftSubdialCenter = Offset(center.x - subdialDistance, center.y)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = leftSubdialCenter
    )
    drawCircle(
        color = Color.White,
        radius = subdialRadius,
        center = leftSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Right subdial (3 o'clock position)
    val rightSubdialCenter = Offset(center.x + subdialDistance, center.y)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = rightSubdialCenter
    )
    drawCircle(
        color = Color.White,
        radius = subdialRadius,
        center = rightSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Bottom subdial (6 o'clock position)
    val bottomSubdialCenter = Offset(center.x, center.y + subdialDistance)
    drawCircle(
        color = SubdialColor,
        radius = subdialRadius,
        center = bottomSubdialCenter
    )
    drawCircle(
        color = Color.White,
        radius = subdialRadius,
        center = bottomSubdialCenter,
        style = Stroke(width = 2f)
    )
    
    // Draw subdial markers (simplified)
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
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Navitimer uses stick markers and Arabic numerals
    
    // Draw hour markers
    for (i in 1..12) {
        val angle = Math.PI / 6 * (i - 3)
        val markerLength = radius * 0.08f
        val markerWidth = radius * 0.015f
        
        val markerX = center.x + cos(angle).toFloat() * radius * 0.6f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.6f
        
        // Skip markers where subdials are
        if (i == 3 || i == 6 || i == 9) continue
        
        // Draw rectangular marker
        rotate(
            degrees = (i - 3) * 30f,
            pivot = Offset(markerX, markerY)
        ) {
            drawRect(
                color = MarkersColor,
                topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
                size = androidx.compose.ui.geometry.Size(markerWidth, markerLength)
            )
        }
    }
    
    // Draw hour numbers
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    // Only draw numbers at 12, 2, 4, 8, 10 positions (avoiding subdials)
    val hours = listOf(12, 2, 4, 8, 10)
    val positions = listOf(0, 2, 4, 8, 10)
    
    for (i in hours.indices) {
        val angle = Math.PI / 6 * positions[i]
        val numberRadius = radius * 0.45f
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

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - broad sword-shaped
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Main hour hand
        drawRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.4f),
            size = androidx.compose.ui.geometry.Size(radius * 0.06f, radius * 0.4f)
        )
        
        // Hour hand tip
        drawRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.015f, center.y - radius * 0.5f),
            size = androidx.compose.ui.geometry.Size(radius * 0.03f, radius * 0.1f)
        )
    }

    // Minute hand - longer sword-shaped
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Main minute hand
        drawRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.6f),
            size = androidx.compose.ui.geometry.Size(radius * 0.04f, radius * 0.6f)
        )
        
        // Minute hand tip
        drawRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.01f, center.y - radius * 0.65f),
            size = androidx.compose.ui.geometry.Size(radius * 0.02f, radius * 0.05f)
        )
    }

    // Second hand - thin with distinctive arrow tip and counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.15f),
            end = Offset(center.x, center.y - radius * 0.6f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        
        // Arrow tip
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y - radius * 0.55f)
        )
        
        // Counterbalance
        drawCircle(
            color = SecondHandColor,
            radius = radius * 0.02f,
            center = Offset(center.x, center.y + radius * 0.1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BreitlingNavitimerPreview() {
    SwissTimeTheme {
        BreitlingNavitimer()
    }
}