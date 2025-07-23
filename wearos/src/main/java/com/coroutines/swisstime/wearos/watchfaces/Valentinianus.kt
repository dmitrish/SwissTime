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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight
import com.coroutines.swisstime.wearos.ui.theme.DarkNavy
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors for the watch face
private val ClockFaceColor = DarkNavy// Color(0xFFF8F5E6) // Ivory/cream dial
private val ClockBorderColor = Color(0xFFB27D4B) // Rose gold border
private val HourHandColor = Color(0xFFB27D4B) // Rose gold hour hand
private val MinuteHandColor = Color(0xFFB27D4B) // Rose gold minute hand
private val SecondHandColor = Color(0xFF8B4513) // Brown second hand
private val MarkersColor = Color(0xFFB27D4B) // Rose gold markers
private val NumbersColor = Color(0xFFB27D4B) // Rose gold numbers
private val CenterDotColor = Color(0xFFB27D4B) // Rose gold center dot

@Composable
fun Valentinianus(
    modifier: Modifier = Modifier, 
    timeZone: TimeZone = TimeZone.getDefault(),
    watchFaceRepository: WatchFaceRepository? = null,
    onSelectTimeZone: () -> Unit = {}
) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

    val timeZoneState by rememberUpdatedState(timeZone)

    // Update time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            currentTime = Calendar.getInstance(timeZoneState)
            delay(1000) // Update every second
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Draw the clock face at the bottom layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 1.0f

            // Draw clock face (no transparency needed as it's the bottom layer)
            drawClockFace(center, radius)

            // Draw hour markers and numbers
            drawHourMarkersAndNumbers(center, radius)
        }

        // Add the world map component in the middle layer (bottom half)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // Take up only the bottom half of the screen
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            CustomWorldMapWithDayNight(
                modifier = Modifier
                    .fillMaxWidth(0.55f) // Make the map 45% smaller in width
                    .fillMaxHeight(0.55f) // Make the map 45% smaller in height while maintaining aspect ratio
                    .offset(y = (-10).dp), // Raise it by approximately 10% of the bottom half's height
                nightOverlayColor = ClockFaceColor // Use the watch face color for the night overlay
            )
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
                        tint = Color.White
                    )
                }
            }
        } else {
            // Fallback to the simple timezone name display if repository is not provided
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = min(size.width, size.height) / 2 * 1.0f

                // Draw timezone name at the top
                drawTimeZoneName(center, radius, timeZoneState)
            }
        }

        // Draw the clock hands on the top layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 1.0f

            // Get current time values
            val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

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

    // Draw inner circle (face) - no transparency needed as the map is now on top
    drawCircle(
        color = ClockFaceColor,
        radius = radius - 3f,
        center = center
    )
}

private fun DrawScope.drawTimeZoneName(center: Offset, radius: Float, timeZone: TimeZone) {
    // Create paint for timezone text
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
        isAntiAlias = true
    }

    // Get timezone display name
    val timeZoneName = timeZone.getDisplayName(timeZone.inDaylightTime(java.util.Date()), TimeZone.SHORT)

    // Draw timezone name at the top
    drawContext.canvas.nativeCanvas.drawText(
        timeZoneName,
        center.x,
        center.y - radius * 0.5f,
        textPaint
    )
}

private fun DrawScope.drawHourMarkersAndNumbers(center: Offset, radius: Float) {
    // Draw hour markers (slim lines)
    for (i in 1..60) {
        val angle = Math.PI / 30 * (i - 15)
        val markerLength = if (i % 5 == 0) radius * 0.08f else radius * 0.03f
        val strokeWidth = if (i % 5 == 0) 2f else 1f

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

    // Draw Roman numerals at 12, 3, 6, 9
    val romanNumerals = listOf("XII", "III", "VI", "IX")
    val textPaint = Paint().apply {
        color = NumbersColor.hashCode()
        textSize = radius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false // Slim font for elegance
        isAntiAlias = true
    }

    for (i in 0..3) {
        // Start from 12 o'clock (top) and rotate clockwise
        val angle = Math.PI / 2 * ((i + 3) % 4)
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
}

private fun DrawScope.drawClockHands(
    center: Offset,
    radius: Float,
    hour: Int,
    minute: Int,
    second: Int
) {
    // Hour hand - slim and elegant
    val hourAngle = (hour * 30 + minute * 0.5f)
    rotate(hourAngle) {
        drawLine(
            color = HourHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.5f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }

    // Minute hand - slim and elegant
    val minuteAngle = minute * 6f
    rotate(minuteAngle) {
        drawLine(
            color = MinuteHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.7f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    // Second hand - very thin with a distinctive counterbalance
    val secondAngle = second * 6f
    rotate(secondAngle) {
        // Main second hand
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        // Counterbalance
        drawLine(
            color = SecondHandColor,
            start = center,
            end = Offset(center.x, center.y + radius * 0.2f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ValentinianusPreview() {
    Valentinianus()
}
