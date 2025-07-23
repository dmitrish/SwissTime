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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
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

// Colors for the Zeitwerk watch face
private val ClockFaceColor = Color(0xFF1A3A5A) // Deep Atlantic blue dial
private val ClockBorderColor = Color(0xFFD0D0D0) // Silver stainless steel border
private val HourHandColor = Color(0xFFE0E0E0) // Silver hour hand
private val MinuteHandColor = Color(0xFFE0E0E0) // Silver minute hand
private val SecondHandColor = Color(0xFFE63946) // Red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val LumeColor = Color(0xFF90EE90) // Light green lume for hands and markers
private val CenterDotColor = Color(0xFFE0E0E0) // Silver center dot
private val LogoColor = Color(0xFFFFFFFF) // White logo text

private object ZeitwerkTheme : WorldClockWatchTheme() {
    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawClockFace(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Hour hand - straight with lume
            // Main hour hand - straight and thin
            drawRoundRect(
                color = HourHandColor,
                topLeft = Offset(center.x - radius * 0.02f, center.y - radius * 0.5f),
                size = Size(radius * 0.04f, radius * 0.5f),
                cornerRadius = CornerRadius(radius * 0.01f)
            )

            // Lume on hour hand tip
            drawCircle(
                color = LumeColor,
                radius = radius * 0.03f,
                center = Offset(center.x, center.y - radius * 0.45f)
            )
        }
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Minute hand - longer and thinner
            // Main minute hand - straight and thin
            drawRoundRect(
                color = MinuteHandColor,
                topLeft = Offset(center.x - radius * 0.015f, center.y - radius * 0.7f),
                size = Size(radius * 0.03f, radius * 0.7f),
                cornerRadius = CornerRadius(radius * 0.01f)
            )

            // Lume on minute hand tip
            drawCircle(
                color = LumeColor,
                radius = radius * 0.025f,
                center = Offset(center.x, center.y - radius * 0.65f)
            )
        }
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Second hand - thin red with distinctive circle near tip
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
                radius = radius * 0.03f,
                center = Offset(center.x, center.y - radius * 0.65f)
            )

            // Counterbalance
            drawCircle(
                color = SecondHandColor,
                radius = radius * 0.02f,
                center = Offset(center.x, center.y + radius * 0.1f)
            )
        }
    }

    override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            drawCircle(
                color = CenterDotColor,
                radius = radius * 0.03f,
                center = center
            )
        }
    }

    private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        // Draw outer circle (border) - stainless steel case
        drawCircle(
            color = ClockBorderColor,
            radius = scaledRadius,
            center = center,
            style = Stroke(width = 8f)
        )

        // Draw inner circle (face) - Atlantic blue dial
        drawCircle(
            color = ClockFaceColor,
            radius = scaledRadius * 0.95f,
            center = center
        )
    }

    private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        // Ahoi uses simple line markers for hours
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i

            // Skip 6 o'clock where the date window is
            if (i == 6) continue

            val markerLength = if (i % 3 == 0) scaledRadius * 0.1f else scaledRadius * 0.05f // Longer at 12, 3, 9
            val markerWidth = if (i % 3 == 0) scaledRadius * 0.02f else scaledRadius * 0.01f // Thicker at 12, 3, 9

            val outerX = center.x + cos(angle).toFloat() * scaledRadius * 0.85f
            val outerY = center.y + sin(angle).toFloat() * scaledRadius * 0.85f
            val innerX = center.x + cos(angle).toFloat() * (scaledRadius * 0.85f - markerLength)
            val innerY = center.y + sin(angle).toFloat() * (scaledRadius * 0.85f - markerLength)

            // Draw hour marker
            drawLine(
                color = MarkersColor,
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = markerWidth,
                cap = StrokeCap.Round
            )

            // Add lume dot at the end of the marker
            if (i % 3 == 0) {
                drawCircle(
                    color = LumeColor,
                    radius = markerWidth * 0.8f,
                    center = Offset(outerX, outerY)
                )
            }
        }

        // Draw minute markers (smaller lines)
        for (i in 0 until 60) {
            // Skip positions where hour markers are
            if (i % 5 == 0) continue

            val angle = Math.PI * 2 * i / 60
            val markerLength = scaledRadius * 0.02f

            val outerX = center.x + cos(angle).toFloat() * scaledRadius * 0.85f
            val outerY = center.y + sin(angle).toFloat() * scaledRadius * 0.85f
            val innerX = center.x + cos(angle).toFloat() * (scaledRadius * 0.85f - markerLength)
            val innerY = center.y + sin(angle).toFloat() * (scaledRadius * 0.85f - markerLength)

            // Draw minute marker
            drawLine(
                color = MarkersColor,
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = scaledRadius * 0.005f,
                cap = StrokeCap.Round
            )
        }

        // Draw date window at 6 o'clock
        val dateAngle = Math.PI * 1.5 // 6 o'clock
        val dateX = center.x + cos(dateAngle).toFloat() * scaledRadius * 0.7f
        val dateY = center.y + sin(dateAngle).toFloat() * scaledRadius * 0.7f

        // Date window - rectangular with rounded corners
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(dateX - scaledRadius * 0.08f, dateY - scaledRadius * 0.06f),
            size = Size(scaledRadius * 0.16f, scaledRadius * 0.12f),
            cornerRadius = CornerRadius(scaledRadius * 0.01f)
        )

        // Date text
        val datePaint = Paint().apply {
            color = Color.Black.hashCode()
            textSize = scaledRadius * 0.08f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        drawContext.canvas.nativeCanvas.drawText(
            day,
            dateX,
            dateY + scaledRadius * 0.035f,
            datePaint
        )
    }

    fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        val logoPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.12f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "Zeitwerk",
            center.x,
            center.y - scaledRadius * 0.3f,
            logoPaint
        )

        // Draw "Alpenglühen" text
        val locationPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.06f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = false
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "Alpenglühen",
            center.x,
            center.y - scaledRadius * 0.2f,
            locationPaint
        )

        val modelPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.08f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = false
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "ZEIT",
            center.x,
            center.y + scaledRadius * 0.56f,
            modelPaint
        )

        // Draw "AUTOMATIC" text
        val subModelPaint = Paint().apply {
            color = LogoColor.hashCode()
            textSize = scaledRadius * 0.06f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = false
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "AUTOMATIC",
            center.x,
            center.y + scaledRadius * 0.67f,
            subModelPaint
        )
    }
}

@Composable
fun Zeitwerk(
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
            ZeitwerkTheme.staticElementsDrawer.forEach { drawer ->
                drawer(center, radius)(this)
            }
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
                nightOverlayColor = ClockFaceColor // Use the watch face color for the night overlay
            )
        }

        // Draw the logo on top of the watchface and map but below the hands
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f
            ZeitwerkTheme.drawLogo(center, radius)(this)
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

        // Draw the clock hands on the top layer (after the map)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f

            // Get current time values
            val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val minute = currentTime.get(Calendar.MINUTE)
            val second = currentTime.get(Calendar.SECOND)

            // Draw hour hand
            val hourAngle = (hour * 30 + minute * 0.5f)
            rotate(hourAngle) {
                ZeitwerkTheme.hourHandDrawer(center, radius)(this)
            }

            // Draw minute hand
            val minuteAngle = minute * 6f
            rotate(minuteAngle) {
                ZeitwerkTheme.minuteHandDrawer(center, radius)(this)
            }

            // Draw second hand
            val secondAngle = second * 6f
            rotate(secondAngle) {
                ZeitwerkTheme.secondHandDrawer(center, radius)(this)
            }

            // Draw center dot
            ZeitwerkTheme.centerDotDrawer(center, radius)(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ZeitwerkPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Zeitwerk()
    }
}
