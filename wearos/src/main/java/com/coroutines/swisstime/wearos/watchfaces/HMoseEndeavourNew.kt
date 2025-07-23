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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import java.util.*
import kotlin.math.*
import com.coroutines.worldclock.common.watchface.WorldClockWatchTheme
import com.coroutines.worldclock.common.watchface.BaseWatch
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight



private object HMoserTheme : WorldClockWatchTheme() {
    // Colors
    private val ClockFaceStartColor = Color(0xFF1E5631)
    private val ClockFaceEndColor = Color(0xFF0A2714)
    private val ClockBorderColor = Color(0xFFE0E0E0)
    private val HourHandColor = Color(0xFFE0E0E0)
    private val MinuteHandColor = Color(0xFFE0E0E0)
    private val SecondHandColor = Color(0xFFE0E0E0)
    private val MarkersColor = Color(0xFFE0E0E0)
    private val LogoColor = Color(0xFFE0E0E0)

    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawClockFace(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkers(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
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
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
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
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            drawLine(
                color = SecondHandColor,
                start = Offset(center.x, center.y + radius * 0.2f),
                end = Offset(center.x, center.y - radius * 0.8f),
                strokeWidth = 1f,
                cap = StrokeCap.Round
            )
        }
    }

    override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            drawCircle(
                color = HourHandColor,
                radius = radius * 0.02f,
                center = center
            )
        }
    }

    private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ClockFaceStartColor, ClockFaceEndColor),
                center = center,
                radius = scaledRadius
            ),
            radius = scaledRadius,
            center = center
        )

        drawCircle(
            color = ClockBorderColor,
            radius = scaledRadius,
            center = center,
            style = Stroke(width = 8f)
        )
    }

    private fun drawHourMarkers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i
            val markerLength = scaledRadius * 0.1f
            val startRadius = scaledRadius * 0.85f

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

    fun drawWatchLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f
        val logoPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.1f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "H. MOSER & CIE",
            center.x,
            center.y - scaledRadius * 0.1f,
            logoPaint
        )

        val yearPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.06f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "1828",
            center.x,
            center.y + scaledRadius * 0.65f,
            yearPaint
        )
    }
}

@Composable
fun HMoserEndeavour(
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
            theme = HMoserTheme
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
                nightOverlayColor = Color(0xFF0A2714) // Use the dark green color for the night overlay
            )
        }

        // Draw the logo on top of the watchface and map but below the hands
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f
            HMoserTheme.drawWatchLogo(center, radius)(this)
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
    }
}

@Preview(showBackground = true)
@Composable
fun HMoserEndeavourNewPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HMoserEndeavour()
    }
}
