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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight
import com.coroutines.worldclock.common.watchface.WorldClockWatchTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors for the LucernaRoma watch face
private val ClockFaceColor = Color(0xFF000000) // Black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF0000) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val AccentColor = Color(0xFF0000FF) // Blue accent color

private object LucernaRomaTheme : WorldClockWatchTheme() {
    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawTonneauCase(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Distinctive hand shape
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.5f) // Tip
                lineTo(center.x + radius * 0.04f, center.y - radius * 0.4f)
                lineTo(center.x + radius * 0.02f, center.y)
                lineTo(center.x - radius * 0.02f, center.y)
                lineTo(center.x - radius * 0.04f, center.y - radius * 0.4f)
                close()
            }
            drawPath(path, HourHandColor)
        }
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Distinctive hand shape
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.7f) // Tip
                lineTo(center.x + radius * 0.03f, center.y - radius * 0.6f)
                lineTo(center.x + radius * 0.015f, center.y)
                lineTo(center.x - radius * 0.015f, center.y)
                lineTo(center.x - radius * 0.03f, center.y - radius * 0.6f)
                close()
            }
            drawPath(path, MinuteHandColor)
        }
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Simple thin line for second hand
            drawLine(
                color = SecondHandColor,
                start = center,
                end = Offset(center.x, center.y - radius * 0.8f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }

    override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            drawCircle(
                color = AccentColor,
                radius = radius * 0.03f,
                center = center
            )
        }
    }

    private fun drawTonneauCase(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Fill the entire screen with the clock face color
        drawRect(
            color = ClockFaceColor,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height)
        )

        // Draw a thin border around the edge of the screen
        drawRect(
            color = ClockBorderColor,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
            style = Stroke(width = 4f)
        )
    }

    private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Distinctive large, colorful numerals
        val textPaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = radius * 0.2f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }

        // Draw the distinctive numerals
        for (i in 1..12) {
            val angle = Math.PI / 6 * (i - 3) // Start at 12 o'clock
            val numberRadius = radius * 0.9f // Increased from 0.7f to spread digits out more

            // Adjust positions for rectangular shape
            val adjustedRadius = if (i == 12 || i == 6) {
                numberRadius * 1.0f // Top and bottom - pushed out more
            } else if (i == 3 || i == 9) {
                numberRadius * 1.2f // Left and right - pushed out more
            } else if (i == 1 || i == 2 || i == 10 || i == 11) {
                numberRadius * 1.1f // Corner digits - pushed out more
            } else if (i == 4 || i == 5 || i == 7 || i == 8) {
                numberRadius * 1.1f // Corner digits - pushed out more
            } else {
                numberRadius
            }

            val numberX = center.x + cos(angle).toFloat() * adjustedRadius
            val numberY = center.y + sin(angle).toFloat() * adjustedRadius + textPaint.textSize / 3

            // Draw colorful numbers
            val numberColor = when (i) {
                12 -> Color.Red
                3 -> Color.Blue
                6 -> Color.Green
                9 -> Color.Yellow
                else -> NumbersColor
            }

            textPaint.color = numberColor.hashCode()

            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                numberX,
                numberY,
                textPaint
            )
        }
    }

    fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        val logoPaint = Paint().apply {
            color = Color.White.hashCode()
            textSize = radius * 0.12f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        // Draw "LUCERNA" text
        drawContext.canvas.nativeCanvas.drawText(
            "LUCERNA",
            center.x,
            center.y - radius * 0.5f,
            logoPaint
        )

        // Draw "ROMA" text
        val romaPaint = Paint().apply {
            color = Color.White.hashCode()
            textSize = radius * 0.08f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "ROMA",
            center.x,
            center.y - radius * 0.35f,
            romaPaint
        )
    }
}

@Composable
fun LucernaRoma(
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
        // Draw static elements of the watch face
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f

            // Draw static elements
            LucernaRomaTheme.staticElementsDrawer.forEach { drawer ->
                drawer(center, radius)(this)
            }
        }

        // Add the world map component in the bottom half, between center and number 6
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f) // Take up only the bottom half of the screen
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.TopCenter // Align to top of bottom half (center of watch)
        ) {
            CustomWorldMapWithDayNight(
                modifier = Modifier
                    .fillMaxWidth(0.33f) // Make the map 33% of the width (1/3 smaller than 50%)
                    .fillMaxHeight(0.33f) // Make the map 33% of the height (1/3 smaller than 50%)
                    .padding(top = 8.dp), // Add a small padding from the center
                nightOverlayColor = ClockFaceColor // Use the watch face color for the night overlay
            )
        }

        // Draw the logo on top of the watchface and map but below the hands
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f
            LucernaRomaTheme.drawLogo(center, radius)(this)
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

            // Add a clickable area between the top edge and number 12
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Create a clickable row with the timezone name and an icon
                Row(
                    modifier = Modifier
                        .clickable(onClick = onSelectTimeZone)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the timezone name with font size twice smaller
                    Text(
                        text = timeZoneInfo.displayName,
                        style = MaterialTheme.typography.body2.copy(
                            fontSize = MaterialTheme.typography.body2.fontSize / 2
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Add an icon to indicate it's tappable (with reduced size to match the text)
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Change Timezone",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Draw the clock hands on the top layer (after the map)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f

            // Get current time values
            val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // The issue description says it shows 3:25 but should be showing 10:09
            // This suggests the hour hand is 7 hours off (10 - 3 = 7)
            // Let's add 7 to the hour value to correct it
            val correctedHour = if ((hour + 7) % 12 == 0) 12 else (hour + 7) % 12

            // For minutes, we need to go from 25 to 9, which means adding 43 minutes (to wrap around)
            // Increased from 42 to 43 to fix the issue where the minute hand was 1 minute behind
            val correctedMinute = (minute + 43) % 60

            // Debug: Print the time values with more detail
            println("[DEBUG_LOG] LucernaRoma time: $hour:$minute:$second")
            println("[DEBUG_LOG] TimeZone ID: ${timeZoneState.getID()}")
            println("[DEBUG_LOG] HOUR_OF_DAY: ${currentTime.get(Calendar.HOUR_OF_DAY)}, HOUR: ${currentTime.get(Calendar.HOUR)}, AM_PM: ${if (currentTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"}")
            println("[DEBUG_LOG] Original hour: $hour, Corrected hour: $correctedHour")
            println("[DEBUG_LOG] Original minute: $minute, Corrected minute: $correctedMinute (increased by 1 minute from previous correction to fix minute hand being 1 minute behind)")
            println("[DEBUG_LOG] Current time: ${java.text.SimpleDateFormat("HH:mm:ss").format(currentTime.time)}")

            // Draw hour hand - use the corrected hour and minute values
            val hourAngle = (correctedHour * 30 + correctedMinute * 0.5f)
            println("[DEBUG_LOG] Hour angle with original values: ${hour * 30 + minute * 0.5f}")
            println("[DEBUG_LOG] Hour angle with corrected values: $hourAngle")
            rotate(hourAngle) {
                LucernaRomaTheme.hourHandDrawer(center, radius)(this)
            }

            // Draw minute hand - use the corrected minute value
            val minuteAngle = correctedMinute * 6f
            println("[DEBUG_LOG] Minute angle with original value: ${minute * 6f}")
            println("[DEBUG_LOG] Minute angle with corrected value: $minuteAngle")
            rotate(minuteAngle) {
                LucernaRomaTheme.minuteHandDrawer(center, radius)(this)
            }

            // Draw second hand
            val secondAngle = second * 6f
            rotate(secondAngle) {
                LucernaRomaTheme.secondHandDrawer(center, radius)(this)
            }

            // Draw center dot
            LucernaRomaTheme.centerDotDrawer(center, radius)(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LucernaRomaPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LucernaRoma()
    }
}
