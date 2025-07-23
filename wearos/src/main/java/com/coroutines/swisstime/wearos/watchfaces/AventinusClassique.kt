package com.coroutines.swisstime.wearos.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight
import com.coroutines.worldclock.common.watchface.BaseWatch
import com.coroutines.worldclock.common.watchface.WorldClockWatchTheme
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

// Colors inspired by AVENTINUS Classique
private val ClockFaceColor = Color(0xFFF5F5F0) // Off-white dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold border
private val HourHandColor = Color(0xFF000080) // Blue hour hand (blued steel)
private val MinuteHandColor = Color(0xFF000080) // Blue minute hand
private val SecondHandColor = Color(0xFF000080) // Blue second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black roman numerals
private val AccentColor = Color(0xFFD4AF37) // Gold accent

private object AventinusClassiqueTheme : WorldClockWatchTheme() {
    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawClockFace(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) },
        { center: Offset, radius: Float -> drawGuillochePattern(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Hour hand - AVENTINUS-style with hollow moon tip
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.5f) // Tip
                quadraticTo(
                    center.x + radius * 0.03f, center.y - radius * 0.48f,
                    center.x + radius * 0.02f, center.y - radius * 0.45f
                )
                lineTo(center.x + radius * 0.02f, center.y)
                lineTo(center.x - radius * 0.02f, center.y)
                lineTo(center.x - radius * 0.02f, center.y - radius * 0.45f)
                quadraticTo(
                    center.x - radius * 0.03f, center.y - radius * 0.48f,
                    center.x, center.y - radius * 0.5f
                )
                close()
            }
            drawPath(path, HourHandColor)
        }
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Minute hand - AVENTINUS-style with hollow moon tip
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.7f) // Tip
                quadraticTo(
                    center.x + radius * 0.025f, center.y - radius * 0.68f,
                    center.x + radius * 0.015f, center.y - radius * 0.65f
                )
                lineTo(center.x + radius * 0.015f, center.y)
                lineTo(center.x - radius * 0.015f, center.y)
                lineTo(center.x - radius * 0.015f, center.y - radius * 0.65f)
                quadraticTo(
                    center.x - radius * 0.025f, center.y - radius * 0.68f,
                    center.x, center.y - radius * 0.7f
                )
                close()
            }
            drawPath(path, MinuteHandColor)
        }
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Second hand - thin with counterbalance
            // Main hand
            drawLine(
                color = SecondHandColor,
                start = Offset(center.x, center.y + radius * 0.2f),
                end = Offset(center.x, center.y - radius * 0.8f),
                strokeWidth = 1f,
                cap = StrokeCap.Round
            )
            
            // Counterbalance
            drawCircle(
                color = SecondHandColor,
                radius = radius * 0.03f,
                center = Offset(center.x, center.y + radius * 0.15f)
            )
        }
    }

    override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            drawCircle(
                color = AccentColor,
                radius = radius * 0.02f,
                center = center
            )
        }
    }

    private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        
        // Draw the outer border (fluted bezel)
        drawCircle(
            color = ClockBorderColor,
            radius = scaledRadius,
            center = center,
            style = Stroke(width = scaledRadius * 0.08f)
        )
        
        // Draw the inner fluted pattern
        val flutesCount = 60
        for (i in 0 until flutesCount) {
            val angle = (i * 360f / flutesCount) * (Math.PI / 180f)
            val outerRadius = scaledRadius * 0.96f
            val innerRadius = scaledRadius * 0.92f
            
            val startX = center.x + cos(angle).toFloat() * innerRadius
            val startY = center.y + sin(angle).toFloat() * innerRadius
            val endX = center.x + cos(angle).toFloat() * outerRadius
            val endY = center.y + sin(angle).toFloat() * outerRadius
            
            drawLine(
                color = ClockBorderColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2f
            )
        }
        
        // Draw the main face
        drawCircle(
            color = ClockFaceColor,
            radius = scaledRadius * 0.9f,
            center = center
        )
    }

    private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        
        // AVENTINUS is known for its elegant roman numerals
        val textPaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = scaledRadius * 0.15f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.NORMAL)
        }
        
        // Roman numerals
        val romanNumerals = listOf("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")
        
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
            val numberRadius = scaledRadius * 0.75f
            
            val numberX = center.x + cos(angle).toFloat() * numberRadius
            val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3
            
            // Draw roman numerals
            drawContext.canvas.nativeCanvas.drawText(
                romanNumerals[i],
                numberX,
                numberY,
                textPaint
            )
            
            // Draw minute markers
            for (j in 0 until 5) {
                val minuteAngle = Math.PI / 30 * (i * 5 + j) - Math.PI / 2
                val innerRadius = scaledRadius * 0.85f
                val outerRadius = scaledRadius * 0.88f
                
                val startX = center.x + cos(minuteAngle).toFloat() * innerRadius
                val startY = center.y + sin(minuteAngle).toFloat() * innerRadius
                val endX = center.x + cos(minuteAngle).toFloat() * outerRadius
                val endY = center.y + sin(minuteAngle).toFloat() * outerRadius
                
                drawLine(
                    color = MarkersColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )
            }
        }
    }

    private fun drawGuillochePattern(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        
        // AVENTINUS is famous for its guilloche patterns
        val guillocheRadius = scaledRadius * 0.6f
        val circleCount = 15
        val circleSpacing = guillocheRadius / circleCount
        
        for (i in 1..circleCount) {
            drawCircle(
                color = Color(0xFFEEEEE0),
                radius = guillocheRadius - (i * circleSpacing),
                center = center,
                style = Stroke(width = 0.5f)
            )
        }
        
        // Add cross-hatching
        for (angle in 0 until 360 step 10) {
            val radians = angle * Math.PI / 180
            val startX = center.x + cos(radians).toFloat() * (scaledRadius * 0.1f)
            val startY = center.y + sin(radians).toFloat() * (scaledRadius * 0.1f)
            val endX = center.x + cos(radians).toFloat() * guillocheRadius
            val endY = center.y + sin(radians).toFloat() * guillocheRadius
            
            drawLine(
                color = Color(0xFFEEEEE0),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 0.5f
            )
        }
    }

    fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        
        val logoPaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = scaledRadius * 0.1f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC)
        }
        
        // Draw "AVENTINUS" text
        drawContext.canvas.nativeCanvas.drawText(
            "AVENTINUS",
            center.x,
            center.y - scaledRadius * 0.3f,
            logoPaint
        )
        
        // Draw signature (Aventinus watches often have a secret signature)
        val signaturePaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = scaledRadius * 0.06f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC)
        }
        
        drawContext.canvas.nativeCanvas.drawText(
            "No. 1947",
            center.x,
            center.y + scaledRadius * 0.4f,
            signaturePaint
        )
    }
}

@Composable
fun AventinusClassique(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault(),
    watchFaceRepository: WatchFaceRepository? = null,
    onSelectTimeZone: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Base watch face using the theme
        BaseWatch(
            modifier = Modifier.fillMaxSize(),
            timeZone = timeZone,
            theme = AventinusClassiqueTheme
        )

        // Add the world map component in the middle layer (bottom half)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // Take up only the bottom half of the screen
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp), // Add bottom padding of 30.dp
            contentAlignment = Alignment.Center
        ) {
            CustomWorldMapWithDayNight(
                modifier = Modifier
                    .fillMaxWidth(0.55f) // Make the map 45% smaller in width
                    .fillMaxHeight(0.55f) // Make the map 45% smaller in height while maintaining aspect ratio
                    .offset(y = (-10).dp), // Raise it by approximately 10% of the bottom half's height
                nightOverlayColor = Color(0xFFEEEEE0) // Use a light color for the night overlay to match the watch face
            )
        }

        // Draw the logo on top of the watchface and map but below the hands
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f
            AventinusClassiqueTheme.drawLogo(center, radius)(this)
        }

        // Draw the timezone selection UI on top of the watchface but below the hands
        if (watchFaceRepository != null) {
            // Get the selected timezone
            val selectedTimeZoneId = watchFaceRepository.getSelectedTimeZoneId().collectAsState()

            // Get the timezone display name using the selectedTimeZoneId state
            val timeZones = remember { watchFaceRepository.getAllTimeZones() }
            val timeZoneInfo = remember(selectedTimeZoneId.value) {
                timeZones.find { it.id == selectedTimeZoneId.value } ?: TimeZoneInfo(
                    id = selectedTimeZoneId.value,
                    displayName = selectedTimeZoneId.value
                )
            }

            // Add a clickable area at the top of the screen where the timezone name is displayed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Create a clickable row with the timezone name and an icon
                Row(
                    modifier = Modifier
                        .clickable(onClick = onSelectTimeZone)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the timezone name
                    Text(
                        text = timeZoneInfo.displayName,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Add an icon to indicate it's tappable
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Change Timezone",
                        tint = Color.Black // Use black to match the watch face
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AventinusClassiquePreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AventinusClassique()
    }
}