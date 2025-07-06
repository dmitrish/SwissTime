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
import java.util.Random
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Longines Master Collection
private val ClockFaceColor = Color(0xFFF5F5F5) // Silver-white dial
private val ClockBorderColor = Color(0xFF8B4513) // Brown border (leather strap color)
private val HourHandColor = Color(0xFF00008B) // Dark blue hour hand (blued steel)
private val MinuteHandColor = Color(0xFF00008B) // Dark blue minute hand
private val SecondHandColor = Color(0xFF00008B) // Dark blue second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val MoonphaseColor = Color(0xFF000080) // Navy blue moonphase background
private val MoonColor = Color(0xFFFFFACD) // Light yellow moon
private val CenterDotColor = Color(0xFF00008B) // Dark blue center dot

@Composable
fun LeonardAutomatic(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) {
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
    
    // Draw subtle guilloche pattern (concentric circles)
    for (i in 1..8) {
        drawCircle(
            color = Color.Black.copy(alpha = 0.03f),
            radius = radius * (0.9f - i * 0.1f),
            center = center,
            style = Stroke(width = 1f)
        )
    }
    
    // Draw Longines logo
    val logoPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "LÉONARD",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "AUTOMATIC" text
    val automaticPaint = Paint().apply {
        color = Color.Black.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "AUTOMATIC",
        center.x,
        center.y - radius * 0.15f,
        automaticPaint
    )
    
    // Draw moonphase display at 6 o'clock
    val moonphaseY = center.y + radius * 0.4f
    val moonphaseWidth = radius * 0.4f
    val moonphaseHeight = radius * 0.2f
    
    // Moonphase background (night sky)
    drawRect(
        color = MoonphaseColor,
        topLeft = Offset(center.x - moonphaseWidth / 2, moonphaseY - moonphaseHeight / 2),
        size = Size(moonphaseWidth, moonphaseHeight)
    )
    
    // Add stars to the night sky
    val random = Random(1234) // Fixed seed for consistent star pattern
    for (i in 0 until 20) {
        val starX = center.x - moonphaseWidth / 2 + random.nextFloat() * moonphaseWidth
        val starY = moonphaseY - moonphaseHeight / 2 + random.nextFloat() * moonphaseHeight
        val starSize = radius * 0.005f + random.nextFloat() * radius * 0.005f
        
        drawCircle(
            color = Color.White,
            radius = starSize,
            center = Offset(starX, starY)
        )
    }
    
    // Draw moon (position based on lunar phase)
    // For simplicity, we'll just draw a full moon
    val moonRadius = moonphaseHeight * 0.4f
    val moonX = center.x
    
    // Full moon
    drawCircle(
        color = MoonColor,
        radius = moonRadius,
        center = Offset(moonX, moonphaseY)
    )
    
    // Add some craters to the moon for detail
    val craters = listOf(
        Triple(0.3f, 0.2f, 0.1f),
        Triple(-0.2f, -0.3f, 0.15f),
        Triple(0.1f, -0.1f, 0.08f)
    )
    
    for ((xOffset, yOffset, sizeRatio) in craters) {
        drawCircle(
            color = MoonColor.copy(alpha = 0.7f),
            radius = moonRadius * sizeRatio,
            center = Offset(
                moonX + moonRadius * xOffset,
                moonphaseY + moonRadius * yOffset
            )
        )
    }
    
    // Draw decorative frame around moonphase
    drawRect(
        color = ClockBorderColor,
        topLeft = Offset(center.x - moonphaseWidth / 2, moonphaseY - moonphaseHeight / 2),
        size = Size(moonphaseWidth, moonphaseHeight),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Longines Master Collection typically uses Roman numerals
    val romanNumerals = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
    
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false // Elegant thin font for Roman numerals
        isAntiAlias = true
    }
    
    // Draw Roman numerals
    for (i in 0 until 12) {
        // Skip VI (6 o'clock) where the moonphase is
        if (i == 5) continue
        
        val angle = Math.PI / 6 * i
        val numberRadius = radius * 0.75f
        val numberX = center.x + cos(angle).toFloat() * numberRadius
        val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

        drawContext.canvas.nativeCanvas.drawText(
            romanNumerals[i],
            numberX,
            numberY,
            textPaint
        )
    }
    
    // Draw minute markers (small dots)
    for (i in 0 until 60) {
        if (i % 5 == 0) continue // Skip where hour markers are
        
        val angle = Math.PI * 2 * i / 60
        val markerRadius = radius * 0.01f
        val markerX = center.x + cos(angle).toFloat() * radius * 0.85f
        val markerY = center.y + sin(angle).toFloat() * radius * 0.85f
        
        drawCircle(
            color = MarkersColor,
            radius = markerRadius,
            center = Offset(markerX, markerY)
        )
    }
    
    // Draw date window at 3 o'clock
    val dateX = center.x + radius * 0.6f
    val dateY = center.y
    
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

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - elegant leaf shape (blued steel)
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        val hourHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.04f, center.y - radius * 0.25f, // Control point
                center.x + radius * 0.02f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f, // Control point
                center.x - radius * 0.02f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.04f, center.y - radius * 0.25f, // Control point
                center.x, center.y - radius * 0.5f // End point (back to start)
            )
            close()
        }
        drawPath(hourHandPath, HourHandColor)
    }

    // Minute hand - longer leaf shape
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        val minuteHandPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            quadraticBezierTo(
                center.x + radius * 0.03f, center.y - radius * 0.35f, // Control point
                center.x + radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.1f, // Control point
                center.x - radius * 0.015f, center.y // End point
            )
            quadraticBezierTo(
                center.x - radius * 0.03f, center.y - radius * 0.35f, // Control point
                center.x, center.y - radius * 0.7f // End point (back to start)
            )
            close()
        }
        drawPath(minuteHandPath, MinuteHandColor)
    }

    // Second hand - thin with small counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.15f),
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
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
fun LonginesMasterCollectionPreview() {
    SwissTimeTheme {
        LeonardAutomatic()
    }
}