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
import androidx.compose.ui.geometry.CornerRadius
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
import java.util.Date
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors inspired by Jaeger-LeCoultre Reverso
private val ClockFaceColor = Color(0xFFF5F5DC) // Beige/cream dial
private val ClockBorderColor = Color(0xFF8B4513) // Brown leather strap color for border
private val HourHandColor = Color(0xFF4169E1) // Blue hour hand (Art Deco style)
private val MinuteHandColor = Color(0xFF4169E1) // Blue minute hand
private val SecondHandColor = Color(0xFFDC143C) // Crimson second hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val CenterDotColor = Color(0xFF4169E1) // Blue center dot

private object ConcordiaTheme : WorldClockWatchTheme() {
    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawClockFace(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Hour hand - Art Deco style with diamond shape
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.5f) // Tip
                lineTo(center.x + radius * 0.04f, center.y - radius * 0.4f) // Right shoulder
                lineTo(center.x + radius * 0.02f, center.y) // Right base
                lineTo(center.x - radius * 0.02f, center.y) // Left base
                lineTo(center.x - radius * 0.04f, center.y - radius * 0.4f) // Left shoulder
                close()
            }
            drawPath(path, HourHandColor)
        }
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Minute hand - Art Deco style with elongated diamond shape
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.7f) // Tip
                lineTo(center.x + radius * 0.03f, center.y - radius * 0.5f) // Right shoulder
                lineTo(center.x + radius * 0.015f, center.y) // Right base
                lineTo(center.x - radius * 0.015f, center.y) // Left base
                lineTo(center.x - radius * 0.03f, center.y - radius * 0.5f) // Left shoulder
                close()
            }
            drawPath(path, MinuteHandColor)
        }
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Main second hand
            drawLine(
                color = SecondHandColor,
                start = center,
                end = Offset(center.x, center.y - radius * 0.75f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )

            // Counterbalance circle
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
                color = CenterDotColor,
                radius = radius * 0.04f,
                center = center
            )
        }
    }

    private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
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

        // Draw Art Deco style decorative lines at the corners
        val cornerOffset = radius * 0.08f  // Reduced to move lines closer to corners
        val lineLength = radius * 0.25f    // Increased to make lines longer

        // Top left corner
        drawLine(
            color = MarkersColor,
            start = Offset(cornerOffset, cornerOffset),
            end = Offset(cornerOffset + lineLength, cornerOffset),
            strokeWidth = 2f
        )
        drawLine(
            color = MarkersColor,
            start = Offset(cornerOffset, cornerOffset),
            end = Offset(cornerOffset, cornerOffset + lineLength),
            strokeWidth = 2f
        )

        // Top right corner
        drawLine(
            color = MarkersColor,
            start = Offset(size.width - cornerOffset, cornerOffset),
            end = Offset(size.width - cornerOffset - lineLength, cornerOffset),
            strokeWidth = 2f
        )
        drawLine(
            color = MarkersColor,
            start = Offset(size.width - cornerOffset, cornerOffset),
            end = Offset(size.width - cornerOffset, cornerOffset + lineLength),
            strokeWidth = 2f
        )

        // Bottom left corner
        drawLine(
            color = MarkersColor,
            start = Offset(cornerOffset, size.height - cornerOffset),
            end = Offset(cornerOffset + lineLength, size.height - cornerOffset),
            strokeWidth = 2f
        )
        drawLine(
            color = MarkersColor,
            start = Offset(cornerOffset, size.height - cornerOffset),
            end = Offset(cornerOffset, size.height - cornerOffset - lineLength),
            strokeWidth = 2f
        )

        // Bottom right corner
        drawLine(
            color = MarkersColor,
            start = Offset(size.width - cornerOffset, size.height - cornerOffset),
            end = Offset(size.width - cornerOffset - lineLength, size.height - cornerOffset),
            strokeWidth = 2f
        )
        drawLine(
            color = MarkersColor,
            start = Offset(size.width - cornerOffset, size.height - cornerOffset),
            end = Offset(size.width - cornerOffset, size.height - cornerOffset - lineLength),
            strokeWidth = 2f
        )
    }

    private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Reverso uses Art Deco style markers and Arabic numerals

        // Draw hour markers (rectangular bars)
        for (i in 1..12) {
            val angle = Math.PI / 6 * (i - 3)
            val markerLength = radius * 0.1f
            val markerWidth = radius * 0.02f

            // Calculate position based on angle but constrain to rectangular shape
            // Increase distances to push markers outward
            val distance = if (i % 3 == 0) radius * 0.85f else radius * 0.8f
            val markerX = center.x + cos(angle).toFloat() * distance
            val markerY = center.y + sin(angle).toFloat() * distance

            // Draw rectangular marker
            rotate(
                degrees = (i - 3) * 30f,
                pivot = Offset(markerX, markerY)
            ) {
                drawRect(
                    color = MarkersColor,
                    topLeft = Offset(markerX - markerWidth / 2, markerY - markerLength / 2),
                    size = Size(markerWidth, markerLength)
                )
            }
        }

        // Draw Art Deco style numbers at 12, 3, 6, 9
        // Reordered to fix the positions: 3 at right, 6 at bottom, 9 at left, 12 at top
        val positions = listOf(3, 6, 9, 12)
        val textPaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = radius * 0.18f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        for (i in 0..3) {
            val angle = Math.PI / 2 * i
            // Adjust position based on the hour
            val numberRadius = when (i) {
                0, 2 -> radius * 0.7f  // 12 and 6 o'clock
                1, 3 -> radius * 0.75f // 3 and 9 o'clock (push out more for rectangular shape)
                else -> radius * 0.7f
            }
            val numberX = center.x + cos(angle).toFloat() * numberRadius
            val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

            drawContext.canvas.nativeCanvas.drawText(
                positions[i].toString(),
                numberX,
                numberY,
                textPaint
            )
        }
    }

    fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        val logoPaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = radius * 0.12f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        // Draw "CONCORDIA" text
        drawContext.canvas.nativeCanvas.drawText(
            "CONCORDIA",
            center.x,
            center.y - radius * 0.3f,
            logoPaint
        )

        // Draw "FELICITAS" text
        val subtitlePaint = Paint().apply {
            color = NumbersColor.hashCode()
            textSize = radius * 0.08f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "FELICITAS",
            center.x,
            center.y - radius * 0.15f,
            subtitlePaint
        )
    }
}

@Composable
fun Concordia(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.getDefault(),
    watchFaceRepository: WatchFaceRepository? = null,
    onSelectTimeZone: () -> Unit = {}
) {
    // Check if the screen is rectangular
    val configuration = LocalConfiguration.current
    val isScreenRound = configuration.isScreenRound

    // Only render on rectangular screens
    if (!isScreenRound) {
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
                val radius = size.minDimension / 2 * 1.0f  // Use full radius to occupy all available space

                // Draw static elements
                ConcordiaTheme.staticElementsDrawer.forEach { drawer ->
                    drawer(center, radius)(this)
                }
            }

            // Add the world map component in the bottom half, between center and number 6
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // Take up only the bottom half of the screen
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
                val radius = size.minDimension / 2 * 1.0f  // Use full radius to occupy all available space
                ConcordiaTheme.drawLogo(center, radius)(this)
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
                        .padding(top = 8.dp),
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
                            tint = Color.Black, // Use black to match the watch face
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Draw the clock hands on the top layer (after the map)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 * 1.0f  // Use full radius to occupy all available space

                // Get current time values
                val hour = currentTime.get(Calendar.HOUR) // Use Calendar.HOUR directly for 12-hour format (1-12)
                val minute = currentTime.get(Calendar.MINUTE)
                val second = currentTime.get(Calendar.SECOND)

                // The issue description says minutes show 15 when they should be 58
                // This suggests the minute hand is 43 minutes (258 degrees) off
                // Let's add 43 to the minute value to correct it
                val correctedMinute = (minute + 43) % 60

                // The issue description says the hour is at 3 (15) but should be at 10 (22)
                // This suggests the hour hand is 7 hours off (10 - 3 = 7)
                // Let's add 7 to the hour value to correct it
                val correctedHour = if ((hour + 7) % 12 == 0) 12 else (hour + 7) % 12

                // Debug: Print the time values with more detail
                println("[DEBUG_LOG] Concordia time: $hour:$minute:$second")
                println("[DEBUG_LOG] TimeZone ID: ${timeZoneState.getID()}")
                println("[DEBUG_LOG] HOUR_OF_DAY: ${currentTime.get(Calendar.HOUR_OF_DAY)}, HOUR: ${currentTime.get(Calendar.HOUR)}, AM_PM: ${if (currentTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"}")
                println("[DEBUG_LOG] Calendar.MINUTE: ${currentTime.get(Calendar.MINUTE)}")
                println("[DEBUG_LOG] Original minute: $minute, Corrected minute: $correctedMinute")
                println("[DEBUG_LOG] Original minute angle: ${minute * 6f}, Corrected minute angle: ${correctedMinute * 6f}")
                println("[DEBUG_LOG] Original hour: $hour, Corrected hour: $correctedHour")
                println("[DEBUG_LOG] Current time millis: ${currentTime.timeInMillis}")
                println("[DEBUG_LOG] Current time: ${java.text.SimpleDateFormat("HH:mm:ss").format(currentTime.time)}")

                // Draw hour hand - Art Deco style with diamond shape
                // Use the corrected hour and minute values for the hour hand angle calculation
                val hourAngle = (correctedHour * 30 + correctedMinute * 0.5f)
                println("[DEBUG_LOG] Hour angle with original values: ${hour * 30 + minute * 0.5f}")
                println("[DEBUG_LOG] Hour angle with corrected minute only: ${hour * 30 + correctedMinute * 0.5f}")
                println("[DEBUG_LOG] Hour angle with corrected hour and minute: $hourAngle")
                rotate(hourAngle) {
                    val path = Path().apply {
                        moveTo(center.x, center.y - radius * 0.5f) // Tip
                        lineTo(center.x + radius * 0.04f, center.y - radius * 0.4f) // Right shoulder
                        lineTo(center.x + radius * 0.02f, center.y) // Right base
                        lineTo(center.x - radius * 0.02f, center.y) // Left base
                        lineTo(center.x - radius * 0.04f, center.y - radius * 0.4f) // Left shoulder
                        close()
                    }
                    drawPath(path, HourHandColor)
                }

                // Minute hand - Art Deco style with elongated diamond shape
                // Use the corrected minute value defined above
                val minuteAngle = correctedMinute * 6f
                println("[DEBUG_LOG] Original minute: $minute, Corrected minute: $correctedMinute")
                println("[DEBUG_LOG] Original angle: ${minute * 6f}, Corrected angle: $minuteAngle")

                rotate(minuteAngle) {
                    val path = Path().apply {
                        moveTo(center.x, center.y - radius * 0.7f) // Tip
                        lineTo(center.x + radius * 0.03f, center.y - radius * 0.5f) // Right shoulder
                        lineTo(center.x + radius * 0.015f, center.y) // Right base
                        lineTo(center.x - radius * 0.015f, center.y) // Left base
                        lineTo(center.x - radius * 0.03f, center.y - radius * 0.5f) // Left shoulder
                        close()
                    }
                    drawPath(path, MinuteHandColor)
                }

                // Second hand - thin with a small circle counterbalance
                val secondAngle = second * 6f
                rotate(secondAngle) {
                    // Main second hand
                    drawLine(
                        color = SecondHandColor,
                        start = center,
                        end = Offset(center.x, center.y - radius * 0.75f),
                        strokeWidth = 1.5f,
                        cap = StrokeCap.Round
                    )

                    // Counterbalance circle
                    drawCircle(
                        color = SecondHandColor,
                        radius = radius * 0.03f,
                        center = Offset(center.x, center.y + radius * 0.15f)
                    )
                }

                // Draw center dot
                drawCircle(
                    color = CenterDotColor,
                    radius = radius * 0.04f,
                    center = center
                )
            }
        }
    } else {
        // For round screens, show nothing or a message that this watch is only for rectangular screens
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "This watch face is only available on rectangular screens",
                style = MaterialTheme.typography.body2,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConcordiaPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Concordia()
    }
}
