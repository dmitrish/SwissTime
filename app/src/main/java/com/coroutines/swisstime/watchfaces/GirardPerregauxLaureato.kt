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
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// Colors inspired by Girard-Perregaux Laureato
private val ClockFaceColor = Color(0xFF2F4F4F) // Dark slate gray dial (hobnail pattern)
private val ClockBorderColor = Color(0xFFC0C0C0) // Silver border
private val HourHandColor = Color(0xFFE0E0E0) // Light silver hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Light silver minute hand
private val SecondHandColor = Color(0xFFFF4500) // Orange-red second hand
private val MarkersColor = Color(0xFFE0E0E0) // Light silver markers
private val NumbersColor = Color(0xFFE0E0E0) // Light silver numbers
private val CenterDotColor = Color(0xFFE0E0E0) // Light silver center dot

@Composable
fun GirardPerregauxLaureato(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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
    // Draw octagonal bezel (characteristic of Laureato)
    val octagonPath = Path().apply {
        val octagonRadius = radius
        val innerRadius = radius * 0.92f
        
        // Draw octagon
        for (i in 0 until 8) {
            val angle = Math.PI / 4 * i
            val x = center.x + cos(angle).toFloat() * octagonRadius
            val y = center.y + sin(angle).toFloat() * octagonRadius
            
            if (i == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
        close()
    }
    
    // Draw octagonal bezel
    drawPath(
        path = octagonPath,
        color = ClockBorderColor
    )
    
    // Draw inner octagonal face
    val innerOctagonPath = Path().apply {
        val innerRadius = radius * 0.92f
        
        // Draw inner octagon
        for (i in 0 until 8) {
            val angle = Math.PI / 4 * i
            val x = center.x + cos(angle).toFloat() * innerRadius
            val y = center.y + sin(angle).toFloat() * innerRadius
            
            if (i == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
        close()
    }
    
    drawPath(
        path = innerOctagonPath,
        color = ClockFaceColor
    )
    
    // Draw hobnail pattern (characteristic of Laureato)
    val patternRadius = radius * 0.8f
    val gridSize = 12 // Number of squares in each direction
    val squareSize = patternRadius * 2 / gridSize
    
    for (i in 0 until gridSize) {
        for (j in 0 until gridSize) {
            val x = center.x - patternRadius + i * squareSize
            val y = center.y - patternRadius + j * squareSize
            
            // Skip squares outside the circle
            val distanceFromCenter = sqrt(
                (x + squareSize/2 - center.x).pow(2) + 
                (y + squareSize/2 - center.y).pow(2)
            )
            if (distanceFromCenter > patternRadius) continue
            
            // Draw raised pyramid effect for hobnail pattern
            val pyramidPath = Path().apply {
                // Base square
                moveTo(x, y)
                lineTo(x + squareSize, y)
                lineTo(x + squareSize, y + squareSize)
                lineTo(x, y + squareSize)
                close()
            }
            
            // Draw with slight highlight to create 3D effect
            drawPath(
                path = pyramidPath,
                color = ClockFaceColor.copy(alpha = 0.8f)
            )
            
            // Draw highlight on top-left
            val highlightPath = Path().apply {
                moveTo(x, y)
                lineTo(x + squareSize, y)
                lineTo(x + squareSize/2, y + squareSize/2)
                close()
            }
            
            drawPath(
                path = highlightPath,
                color = ClockFaceColor.copy(alpha = 0.6f)
            )
            
            // Draw shadow on bottom-right
            val shadowPath = Path().apply {
                moveTo(x + squareSize, y)
                lineTo(x + squareSize, y + squareSize)
                lineTo(x + squareSize/2, y + squareSize/2)
                close()
            }
            
            drawPath(
                path = shadowPath,
                color = ClockFaceColor.copy(alpha = 1.0f)
            )
        }
    }
    
    // Draw Girard-Perregaux logo
    val logoPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "ROMA-MARINA",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "LAUREATO" text
    val modelPaint = Paint().apply {
        color = Color.White.hashCode()
        textSize = radius * 0.07f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "MILITARE",
        center.x,
        center.y - radius * 0.15f,
        modelPaint
    )
    
    // Draw date window at 3 o'clock
    val dateAngle = Math.PI / 2
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
    // Laureato uses applied baton markers
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i
        val markerLength = radius * 0.1f
        val markerWidth = radius * 0.02f
        
        // Skip 3 o'clock where the date window is
        if (i == 3) continue
        
        val markerX = center.x + cos(angle).toFloat() * radius * 0.7f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.7f
        
        // Draw rectangular marker with 3D effect
        rotate(
            degrees = i * 30f,
            pivot = Offset(markerX, markerY)
        ) {
            // Main marker
            drawRect(
                color = MarkersColor,
                topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
                size = Size(markerWidth, markerLength)
            )
            
            // Highlight for 3D effect
            drawRect(
                color = Color.White,
                topLeft = Offset(markerX - markerWidth / 2 + 1f, markerY - markerLength / 2 + 1f),
                size = Size(markerWidth / 2, markerLength - 2f)
            )
        }
    }
    
    // Draw double marker at 12 o'clock (characteristic of Laureato)
    val angle12 = Math.PI * 1.5 // 12 o'clock
    val marker12X = center.x + cos(angle12).toFloat() * radius * 0.7f
    val marker12Y = center.y + sin(angle12).toFloat() * radius * 0.7f
    val markerWidth = radius * 0.02f
    val markerLength = radius * 0.1f
    val markerGap = radius * 0.01f
    
    // Left marker at 12
    drawRect(
        color = MarkersColor,
        topLeft = Offset(marker12X - markerWidth * 1.5f - markerGap/2, marker12Y - markerLength / 2),
        size = Size(markerWidth, markerLength)
    )
    
    // Right marker at 12
    drawRect(
        color = MarkersColor,
        topLeft = Offset(marker12X + markerGap/2, marker12Y - markerLength / 2),
        size = Size(markerWidth, markerLength)
    )
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - sword-shaped with center groove
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        // Main hour hand
        val hourHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            lineTo(center.x + radius * 0.04f, center.y - radius * 0.2f) // Right shoulder
            lineTo(center.x + radius * 0.02f, center.y) // Right base
            lineTo(center.x - radius * 0.02f, center.y) // Left base
            lineTo(center.x - radius * 0.04f, center.y - radius * 0.2f) // Left shoulder
            close()
        }
        drawPath(hourHandPath, HourHandColor)
        
        // Center groove
        drawLine(
            color = ClockFaceColor,
            start = Offset(center.x, center.y - radius * 0.45f),
            end = Offset(center.x, center.y - radius * 0.05f),
            strokeWidth = 1f
        )
    }

    // Minute hand - longer sword-shaped with center groove
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        // Main minute hand
        val minuteHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.2f) // Right shoulder
            lineTo(center.x + radius * 0.015f, center.y) // Right base
            lineTo(center.x - radius * 0.015f, center.y) // Left base
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.2f) // Left shoulder
            close()
        }
        drawPath(minuteHandPath, MinuteHandColor)
        
        // Center groove
        drawLine(
            color = ClockFaceColor,
            start = Offset(center.x, center.y - radius * 0.65f),
            end = Offset(center.x, center.y - radius * 0.05f),
            strokeWidth = 1f
        )
    }

    // Second hand - thin with distinctive arrow tip
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
        val arrowSize = radius * 0.04f
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

// Extension function to calculate power of 2
private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}

@Preview(showBackground = true)
@Composable
fun GirardPerregauxLaureatoPreview() {
    SwissTimeTheme {
        GirardPerregauxLaureato()
    }
}