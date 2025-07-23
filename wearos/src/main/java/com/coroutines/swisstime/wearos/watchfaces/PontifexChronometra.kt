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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.math.min
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

// Colors for the Pontifex Chronometra watch face
private val ClockFaceColor = Color(0xFF1E2C4A) // Deep blue dial
private val ClockBorderColor = Color(0xFFD4AF37) // Gold border
private val HourHandColor = ClockBorderColor// Color(0xFFE0E0E0) // Silver hour hand
private val MinuteHandColor = ClockBorderColor// Color(0xFFE0E0E0) // Silver minute hand
private val SecondHandColor = ClockBorderColor// Color(0xFFD4AF37) // Gold second hand
private val MarkersColor = Color(0xFFE0E0E0) // Silver markers
private val NumbersColor = Color(0xFFE0E0E0) // Silver numbers
private val AccentColor = Color(0xFFD4AF37) // Gold accent

private object PontifexChronometraTheme : WorldClockWatchTheme() {
    override val staticElementsDrawer = listOf(
        { center: Offset, radius: Float -> drawClockFace(center, radius) },
        { center: Offset, radius: Float -> drawHourMarkers(center, radius) },
        { center: Offset, radius: Float -> drawGuilloche(center, radius) },
        { center: Offset, radius: Float -> drawDateWindow(center, radius) }
    )

    override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Hour hand - Delta-shaped (Pontifex style)
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.45f) // Tip
                lineTo(center.x + radius * 0.04f, center.y - radius * 0.2f) // Right shoulder
                lineTo(center.x + radius * 0.015f, center.y) // Right base
                lineTo(center.x - radius * 0.015f, center.y) // Left base
                lineTo(center.x - radius * 0.04f, center.y - radius * 0.2f) // Left shoulder
                close()
            }
            drawPath(path, HourHandColor)
        }
    }

    override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Minute hand - Delta-shaped (Pontifex style)
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 0.65f) // Tip
                lineTo(center.x + radius * 0.03f, center.y - radius * 0.3f) // Right shoulder
                lineTo(center.x + radius * 0.01f, center.y) // Right base
                lineTo(center.x - radius * 0.01f, center.y) // Left base
                lineTo(center.x - radius * 0.03f, center.y - radius * 0.3f) // Left shoulder
                close()
            }
            drawPath(path, MinuteHandColor)
        }
    }

    override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
        {
            // Main hand
            drawLine(
                color = SecondHandColor,
                start = Offset(center.x, center.y + radius * 0.2f),
                end = Offset(center.x, center.y - radius * 0.75f),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )

            // Oval counterbalance
            val ovalPath = Path().apply {
                val ovalWidth = radius * 0.05f
                val ovalHeight = radius * 0.1f

                // Draw oval
                moveTo(center.x, center.y + ovalHeight)
                quadraticTo(
                    center.x + ovalWidth, center.y + ovalHeight,
                    center.x + ovalWidth, center.y + ovalHeight / 2
                )
                quadraticTo(
                    center.x + ovalWidth, center.y,
                    center.x, center.y
                )
                quadraticTo(
                    center.x - ovalWidth, center.y,
                    center.x - ovalWidth, center.y + ovalHeight / 2
                )
                quadraticTo(
                    center.x - ovalWidth, center.y + ovalHeight,
                    center.x, center.y + ovalHeight
                )
                close()
            }

            drawPath(
                path = ovalPath,
                color = SecondHandColor
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

        // Draw the outer border
        drawCircle(
            color = ClockBorderColor,
            radius = scaledRadius,
            center = center,
            style = Stroke(width = scaledRadius * 0.03f)
        )

        // Draw the main face
        drawCircle(
            color = ClockFaceColor,
            radius = scaledRadius * 0.97f,
            center = center
        )

        // Draw a subtle inner ring
        drawCircle(
            color = ClockBorderColor,
            radius = scaledRadius * 0.9f,
            center = center,
            style = Stroke(width = scaledRadius * 0.005f)
        )
    }

    private fun drawHourMarkers(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
            val markerRadius = scaledRadius * 0.85f

            val markerX = center.x + cos(angle).toFloat() * markerRadius
            val markerY = center.y + sin(angle).toFloat() * markerRadius

            // Draw applied markers
            if (i % 3 == 0) {
                // Double marker for 12, 3, 6, 9
                drawLine(
                    color = MarkersColor,
                    start = Offset(
                        markerX - cos(angle + Math.PI/2).toFloat() * scaledRadius * 0.04f,
                        markerY - sin(angle + Math.PI/2).toFloat() * scaledRadius * 0.04f
                    ),
                    end = Offset(
                        markerX + cos(angle + Math.PI/2).toFloat() * scaledRadius * 0.04f,
                        markerY + sin(angle + Math.PI/2).toFloat() * scaledRadius * 0.04f
                    ),
                    strokeWidth = scaledRadius * 0.02f,
                    cap = StrokeCap.Round
                )
            } else {
                // Teardrop markers for other hours (distinctive Pontifex style)
                val teardropPath = Path().apply {
                    moveTo(
                        markerX + cos(angle).toFloat() * scaledRadius * 0.03f,
                        markerY + sin(angle).toFloat() * scaledRadius * 0.03f
                    )

                    // Create teardrop shape pointing toward center
                    val angleToCenter = Math.atan2(
                        (center.y - markerY).toDouble(),
                        (center.x - markerX).toDouble()
                    ).toFloat()

                    // Control points for the teardrop curve
                    val controlX1 = markerX + cos(angleToCenter + 0.5f).toFloat() * scaledRadius * 0.02f
                    val controlY1 = markerY + sin(angleToCenter + 0.5f).toFloat() * scaledRadius * 0.02f
                    val controlX2 = markerX + cos(angleToCenter - 0.5f).toFloat() * scaledRadius * 0.02f
                    val controlY2 = markerY + sin(angleToCenter - 0.5f).toFloat() * scaledRadius * 0.02f

                    // Draw the teardrop
                    quadraticTo(
                        controlX1,
                        controlY1,
                        markerX,
                        markerY
                    )

                    quadraticTo(
                        controlX2,
                        controlY2,
                        markerX + cos(angle).toFloat() * scaledRadius * 0.03f,
                        markerY + sin(angle).toFloat() * scaledRadius * 0.03f
                    )

                    close()
                }

                drawPath(
                    path = teardropPath,
                    color = MarkersColor
                )
            }
        }
    }

    private fun drawGuilloche(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        val patternRadius = scaledRadius * 0.7f

        // Draw a wave pattern (simplified guilloche)
        for (angle in 0 until 360 step 5) {
            val radians = angle * Math.PI / 180

            // Create a wave effect
            val waveAmplitude = scaledRadius * 0.02f
            val waveFrequency = 8f

            val path = Path().apply {
                val startX = center.x + cos(radians).toFloat() * (scaledRadius * 0.2f)
                val startY = center.y + sin(radians).toFloat() * (scaledRadius * 0.2f)

                moveTo(startX, startY)

                for (i in 0..100) {
                    val t = i / 100f
                    val distance = scaledRadius * 0.2f + t * (patternRadius - scaledRadius * 0.2f)
                    val waveOffset = sin(t * waveFrequency * Math.PI).toFloat() * waveAmplitude

                    val x = center.x + cos(radians).toFloat() * distance + 
                            cos(radians + Math.PI/2).toFloat() * waveOffset
                    val y = center.y + sin(radians).toFloat() * distance +
                            sin(radians + Math.PI/2).toFloat() * waveOffset

                    lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = Color(0xFF2A3C5A),
                style = Stroke(width = 0.5f)
            )
        }
    }

    private fun drawDateWindow(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        val dateWindowX = center.x + scaledRadius * 0.6f
        val dateWindowY = center.y
        val dateWindowWidth = scaledRadius * 0.15f
        val dateWindowHeight = scaledRadius * 0.1f

        // Draw date window background
        drawRect(
            color = Color(0xFF0A1525),
            topLeft = Offset(
                dateWindowX - dateWindowWidth / 2,
                dateWindowY - dateWindowHeight / 2
            ),
            size = Size(
                dateWindowWidth,
                dateWindowHeight
            )
        )

        // Draw date window border
        drawRect(
            color = AccentColor,
            topLeft = Offset(
                dateWindowX - dateWindowWidth / 2,
                dateWindowY - dateWindowHeight / 2
            ),
            size = Size(
                dateWindowWidth,
                dateWindowHeight
            ),
            style = Stroke(width = 1f)
        )

        // Draw date text
        val datePaint = Paint().apply {
            color = MarkersColor.hashCode()
            textSize = scaledRadius * 0.06f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Get current date
        val date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        drawContext.canvas.nativeCanvas.drawText(
            date.toString(),
            dateWindowX,
            dateWindowY + scaledRadius * 0.02f,
            datePaint
        )
    }

    fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
        // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
        val scaledRadius = radius * 1.25f

        val brandPaint = Paint().apply {
            color = MarkersColor.hashCode()
            textSize = scaledRadius * 0.07f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "PONTIFEX",
            center.x,
            center.y - scaledRadius * 0.25f,
            brandPaint
        )

        val fleurierPaint = Paint().apply {
            color = MarkersColor.hashCode()
            textSize = scaledRadius * 0.05f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "CHRONOMETRA",
            center.x,
            center.y - scaledRadius * 0.15f,
            fleurierPaint
        )

        // Draw "SWISS MADE" text
        val swissMadePaint = Paint().apply {
            color = MarkersColor.hashCode()
            textSize = scaledRadius * 0.04f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "SWISS MADE",
            center.x,
            center.y + scaledRadius * 0.7f,
            swissMadePaint
        )
    }
}

@Composable
fun PontifexChronometra(
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
            PontifexChronometraTheme.staticElementsDrawer.forEach { drawer ->
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
            PontifexChronometraTheme.drawLogo(center, radius)(this)
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
                PontifexChronometraTheme.hourHandDrawer(center, radius)(this)
            }

            // Draw minute hand
            val minuteAngle = minute * 6f
            rotate(minuteAngle) {
                PontifexChronometraTheme.minuteHandDrawer(center, radius)(this)
            }

            // Draw second hand
            val secondAngle = second * 6f
            rotate(secondAngle) {
                PontifexChronometraTheme.secondHandDrawer(center, radius)(this)
            }

            // Draw center dot
            PontifexChronometraTheme.centerDotDrawer(center, radius)(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PontifexChronometraPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PontifexChronometra()
    }
}
