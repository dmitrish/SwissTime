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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.coroutines.swisstime.wearos.ui.CustomWorldMapWithDayNight
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// Colors for the watch face
private val ClockFaceStartColor = Color(0xFF1E5631)
private val ClockFaceEndColor = Color(0xFF0A2714)
private val ClockBorderColor = Color(0xFFE0E0E0)
private val HourHandColor = Color(0xFFE0E0E0)
private val MinuteHandColor = Color(0xFFE0E0E0)
private val SecondHandColor = Color(0xFFE0E0E0)
private val MarkersColor = Color(0xFFE0E0E0)
private val LogoColor = Color(0xFFE0E0E0)

@Composable
fun CenturioLuminor(
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
            val radius = min(size.width, size.height) / 2 * 0.95f

            // Draw clock face (no transparency needed as it's the bottom layer)
            drawClockFace(center, radius)

            // Draw hour markers
            drawHourMarkers(center, radius)

            // Draw watch logo and year
            drawWatchLogo(center, radius)
        }

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
                nightOverlayColor = ClockFaceEndColor // Use the darker watch face color for the night overlay
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
        }

        // Draw the clock hands on the top layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 0.95f

            // Get current time values
            val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw clock hands
            drawClockHands(center, radius, hour, minute, second)

            // Draw center dot
            drawCircle(
                color = HourHandColor,
                radius = radius * 0.02f,
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

    // Draw inner circle (face) with gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(ClockFaceStartColor, ClockFaceEndColor),
            center = center,
            radius = radius * 0.95f
        ),
        radius = radius * 0.95f,
        center = center
    )
}

private fun DrawScope.drawHourMarkers(center: Offset, radius: Float) {
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i
        val markerLength = radius * 0.1f
        val startRadius = radius * 0.8f

        val startX = center.x + cos(angle).toFloat() * startRadius
        val startY = center.y + sin(angle).toFloat() * startRadius
        val endX = center.x + cos(angle).toFloat() * (startRadius - markerLength)
        val endY = center.y + sin(angle).toFloat() * (startRadius - markerLength)

        drawLine(
            color = MarkersColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 3f else 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawWatchLogo(center: Offset, radius: Float) {
    val logoPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.1f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "Centurio Luminor",
        center.x,
        center.y - radius * 0.3f,
        logoPaint
    )

    val yearPaint = Paint().apply {
        color = LogoColor.hashCode()
        textSize = radius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.drawText(
        "1728",
        center.x,
        center.y + radius * 0.4f,
        yearPaint
    )
}

private fun DrawScope.drawClockHands(center: Offset, radius: Float, hour: Int, minute: Int, second: Int) {
    // Hour hand
    rotate(degrees = (hour * 30 + minute * 0.5f)) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.5f)
            quadraticBezierTo(
                center.x + radius * 0.03f, center.y - radius * 0.25f,
                center.x + radius * 0.015f, center.y
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.05f,
                center.x - radius * 0.015f, center.y
            )
            quadraticBezierTo(
                center.x - radius * 0.03f, center.y - radius * 0.25f,
                center.x, center.y - radius * 0.5f
            )
            close()
        }
        drawPath(path, HourHandColor)
    }

    // Minute hand
    rotate(degrees = minute * 6f) {
        val path = Path().apply {
            moveTo(center.x, center.y - radius * 0.7f)
            quadraticBezierTo(
                center.x + radius * 0.025f, center.y - radius * 0.35f,
                center.x + radius * 0.01f, center.y
            )
            quadraticBezierTo(
                center.x, center.y + radius * 0.05f,
                center.x - radius * 0.01f, center.y
            )
            quadraticBezierTo(
                center.x - radius * 0.025f, center.y - radius * 0.35f,
                center.x, center.y - radius * 0.7f
            )
            close()
        }
        drawPath(path, MinuteHandColor)
    }

    // Second hand
    rotate(degrees = second * 6f) {
        drawLine(
            color = SecondHandColor,
            start = Offset(center.x, center.y + radius * 0.2f),
            end = Offset(center.x, center.y - radius * 0.8f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CenturioLuminorPreview() {
    CenturioLuminor()
}
