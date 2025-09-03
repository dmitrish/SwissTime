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
import androidx.compose.ui.graphics.Brush
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
import kotlin.random.Random

// Colors inspired by Knot Urushi lacquer dial
private val UrushiBlackColor = Color(0xFF000000) // Deep jet black for the Urushi lacquer dial
private val GoldPowderColor = Color(0xFFD4AF37) // Gold powder accent
private val GoldPowderHighlightColor = Color(0xFFFFD700) // Brighter gold for highlights
private val SilverCaseColor = Color(0xFFE0E0E0) // Silver polished case
private val SilverHandsColor = Color(0xFFE0E0E0) // Silver hands
private val MarkersColor = Color(0xFFF5F5F5) // Bright silver hour markers for better contrast
private val LogoColor = Color(0xFFE0E0E0) // Silver for the logo


@Composable
fun Knot(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault()
) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

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

            // Draw clock face (Urushi lacquer dial with gold powder)
            drawClockFace(center, radius)

            // Get current time values
            val hour = currentTime.get(Calendar.HOUR)
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw hour markers (simple silver markers for minimalist design) and day aperture
            drawHourMarkers(center, radius, timeZoneX)

            // Draw Urushi lacquer texture with gold powder effect
            drawUrushiTexture(center, radius)

            // Draw clock hands (silver hands)
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = SilverHandsColor,
                radius = radius * 0.02f,
                center = center
            )
            
            // Draw Knot logo
            drawLogo(center, radius)
        }
    }
}

private fun DrawScope.drawClockFace(center: Offset, radius: Float) {
    // Draw the outer silver case
    drawCircle(
        color = SilverCaseColor,
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.05f)
    )
    
    // Draw the main face with deep black Urushi lacquer
    drawCircle(
        color = UrushiBlackColor,
        radius = radius * 0.95f,
        center = center
    )
}

private fun DrawScope.drawHourMarkers(center: Offset, radius: Float, timeZone: TimeZone = TimeZone.getDefault()) {
    // Knot watches typically have applied hour markers that are more substantial
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
        
        // Position for the marker
        val markerRadius = radius * 0.85f
        val markerX = center.x + cos(angle).toFloat() * markerRadius
        val markerY = center.y + sin(angle).toFloat() * markerRadius
        
        if (i % 3 == 0) {
            // Major markers (12, 6, 9) - larger rectangular markers
            // Calculate the rectangle dimensions and position
            val markerWidth = radius * 0.05f  // Increased width for more prominence
            val markerHeight = radius * 0.14f // Increased height for more prominence
            
            // Calculate the rotation for the rectangle
            val rotationAngle = Math.toDegrees(angle).toFloat() + 90
            
            // Draw the marker with rotation
            rotate(rotationAngle, Offset(markerX, markerY)) {
                // Draw silver marker with slight 3D effect
                // Main body of the marker
                drawRect(
                    color = MarkersColor,
                    topLeft = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    size = Size(markerWidth, markerHeight)
                )
                
                // Add a subtle highlight on the top/left edge for 3D effect
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )
                
                // Add a subtle shadow on the bottom/right edge for 3D effect
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1.5f
                )
            }
        } else {
            // Minor markers - smaller rectangular markers
            val markerWidth = radius * 0.04f  // Increased width for more prominence
            val markerHeight = radius * 0.1f  // Increased height for more prominence
            
            // Calculate the rotation for the rectangle
            val rotationAngle = Math.toDegrees(angle).toFloat() + 90
            
            // Draw the marker with rotation
            rotate(rotationAngle, Offset(markerX, markerY)) {
                // Draw silver marker with slight 3D effect
                // Main body of the marker
                drawRect(
                    color = MarkersColor,
                    topLeft = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    size = Size(markerWidth, markerHeight)
                )
                
                // Add a subtle highlight on the top/left edge for 3D effect
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(markerX - markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )
                
                // Add a subtle shadow on the bottom/right edge for 3D effect
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX - markerWidth / 2, markerY + markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(markerX + markerWidth / 2, markerY - markerHeight / 2),
                    end = Offset(markerX + markerWidth / 2, markerY + markerHeight / 2),
                    strokeWidth = 1f
                )
            }
        }
    }
    
    // Draw date window at 3 o'clock position
    val dateX = center.x + radius * 0.6f
    val dateY = center.y
    
    // Date window with white background
    drawRect(
        color = Color.White,
        topLeft = Offset(dateX - radius * 0.08f, dateY - radius * 0.06f),
        size = Size(radius * 0.16f, radius * 0.12f)
    )
    
    // Add a thin silver border around the date window
    drawRect(
        color = SilverCaseColor,
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
    
    // Get current day of month
    val day = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH).toString()
    
    // Draw the day number
    drawContext.canvas.nativeCanvas.drawText(
        day,
        dateX,
        dateY + radius * 0.03f,
        datePaint
    )
}

private fun DrawScope.drawUrushiTexture(center: Offset, radius: Float) {
    // Create the Urushi lacquer texture with gold powder effect
    // This simulates the depth and subtle texture of Urushi lacquer with gold powder
    
    // Generate random gold powder particles
    val random = Random(0) // Fixed seed for consistent pattern
    val particleCount = 500
    
    for (i in 0 until particleCount) {
        // Random position within the dial
        val angle = random.nextDouble(0.0, 2 * Math.PI)
        val distance = random.nextDouble(0.0, 0.85) * radius
        
        val x = center.x + (cos(angle) * distance).toFloat()
        val y = center.y + (sin(angle) * distance).toFloat()
        
        // Random size for gold particles
        val particleSize = random.nextFloat() * 1.5f + 0.5f
        
        // Random gold shade
        val goldShade = if (random.nextFloat() > 0.7f) {
            GoldPowderHighlightColor
        } else {
            GoldPowderColor.copy(alpha = random.nextFloat() * 0.5f + 0.1f)
        }
        
        drawCircle(
            color = goldShade,
            radius = particleSize,
            center = Offset(x, y)
        )
    }
    
    // Add subtle radial gradient to simulate the depth of Urushi lacquer
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF050505), // Slightly lighter at center
                UrushiBlackColor   // Deep black at edges
            ),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center,
        alpha = 0.7f
    )
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - simple silver hand with slight taper
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.45f) // Tip
            lineTo(center.x + radius * 0.03f, center.y - radius * 0.1f) // Right edge
            lineTo(center.x + radius * 0.015f, center.y) // Right base
            lineTo(center.x - radius * 0.015f, center.y) // Left base
            lineTo(center.x - radius * 0.03f, center.y - radius * 0.1f) // Left edge
            close()
        }
        drawPath(path, SilverHandsColor)
    }

    // Minute hand - simple silver hand with slight taper
    val minuteAngle = minute * 6f
    rotate(minuteAngle, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f) // Tip
            lineTo(center.x + radius * 0.02f, center.y - radius * 0.1f) // Right edge
            lineTo(center.x + radius * 0.01f, center.y) // Right base
            lineTo(center.x - radius * 0.01f, center.y) // Left base
            lineTo(center.x - radius * 0.02f, center.y - radius * 0.1f) // Left edge
            close()
        }
        drawPath(path, SilverHandsColor)
    }

    // Second hand - thin silver with counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle, pivot = center) {
        // Main hand
        drawLine(
            color = SilverHandsColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.75f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
        
        // Counterbalance
        drawCircle(
            color = SilverHandsColor,
            radius = radius * 0.03f,
            center = Offset(center.x, center.y + radius * 0.15f)
        )
    }
}

private fun DrawScope.drawLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
    }
    
    // Draw "KNOT" text
    drawContext.canvas.nativeCanvas.drawText(
        "KNOT",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )
    
    // Draw "URUSHI" text
    val subtitlePaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
    }
    
    drawContext.canvas.nativeCanvas.drawText(
        "URUSHI",
        center.x,
        center.y + radius * 0.4f,
        subtitlePaint
    )
}

@Preview(showBackground = true)
@Composable
fun KnotPreview() {
    SwissTimeTheme {
        Knot()
    }
}