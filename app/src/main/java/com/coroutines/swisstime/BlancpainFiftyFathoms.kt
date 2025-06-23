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

// Colors inspired by Blancpain Fifty Fathoms
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
fun BlancpainFiftyFathoms(modifier: Modifier = Modifier) {
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
                val triangleWidth = radius * 0.04f
                val leftX = center.x + cos(angle - 0.04).toFloat() * markerEnd
                val leftY = center.y + sin(angle - 0.04).toFloat() * markerEnd
                val rightX = center.x + cos(angle + 0.04).toFloat() * markerEnd
                val rightY = center.y + sin(angle + 0.04).toFloat() * markerEnd
                
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
        "BLANCPAIN",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "Fifty Fathoms" text
    val modelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "FIFTY FATHOMS",
        center.x,
        center.y + radius * 0.3f,
        modelPaint
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
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
                size = androidx.compose.ui.geometry.Size(radius * 0.12f, radius * 0.12f)
            )
            
            // Lume inside
            drawRect(
                color = LumeColor,
                topLeft = Offset(center.x - radius * 0.05f, center.y - radius * 0.7f - radius * 0.05f),
                size = androidx.compose.ui.geometry.Size(radius * 0.1f, radius * 0.1f)
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
        size = androidx.compose.ui.geometry.Size(radius * 0.16f, radius * 0.12f)
    )
    
    // Date text
    val datePaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.035f,
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
    // Hour hand - broad sword-shaped with lume
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Main hour hand
        drawRoundRect(
            color = HourHandColor,
            topLeft = Offset(center.x - radius * 0.04f, center.y - radius * 0.5f),
            size = androidx.compose.ui.geometry.Size(radius * 0.08f, radius * 0.5f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.02f)
        )
        
        // Lume on hour hand
        drawRoundRect(
            color = LumeColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.48f),
            size = androidx.compose.ui.geometry.Size(radius * 0.06f, radius * 0.4f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.015f)
        )
    }

    // Minute hand - longer sword-shaped with lume
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Main minute hand
        drawRoundRect(
            color = MinuteHandColor,
            topLeft = Offset(center.x - radius * 0.03f, center.y - radius * 0.7f),
            size = androidx.compose.ui.geometry.Size(radius * 0.06f, radius * 0.7f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.015f)
        )
        
        // Lume on minute hand
        drawRoundRect(
            color = LumeColor,
            topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.68f),
            size = androidx.compose.ui.geometry.Size(radius * 0.04f, radius * 0.6f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.01f)
        )
    }

    // Second hand - thin with distinctive circle near tip
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

@Preview(showBackground = true)
@Composable
fun BlancpainFiftyFathomsPreview() {
    SwissTimeTheme {
        BlancpainFiftyFathoms()
    }
}