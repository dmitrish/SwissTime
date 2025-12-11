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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight
import com.coroutines.worldclock.common.watchface.WorldClockWatchTheme
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

// Colors inspired by Jurgsen Zenithor
private val ClockFaceColor = Color(0xFF000000) // Deep black dial
private val ClockBorderColor = Color(0xFF303030) // Dark gray border
private val BezelColor = Color(0xFF000080) // Navy blue bezel
private val BezelMarkersColor = Color(0xFFFFFFFF) // White bezel markers
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFF4500) // Orange-red second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val NumbersColor = Color(0xFFFFFFFF) // White numbers
private val LumeColor = Color(0xFF90EE90) // Light green lume
private val CenterDotColor = Color(0xFFFFFFFF) // White center dot

private object JurgsenZenithorTheme : WorldClockWatchTheme() {
  override val staticElementsDrawer =
    listOf(
      { center: Offset, radius: Float -> drawClockFace(center, radius) },
      { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) }
    )

  override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Hour hand - broad sword-shaped with lume
      val path =
        Path().apply {
          moveTo(center.x - radius * 0.04f, center.y)
          lineTo(center.x + radius * 0.04f, center.y)
          lineTo(center.x + radius * 0.04f, center.y - radius * 0.5f)
          lineTo(center.x, center.y - radius * 0.5f)
          lineTo(center.x - radius * 0.04f, center.y - radius * 0.5f)
          close()
        }
      drawPath(path, HourHandColor)

      // Lume on hour hand
      val lumePath =
        Path().apply {
          moveTo(center.x - radius * 0.03f, center.y)
          lineTo(center.x + radius * 0.03f, center.y)
          lineTo(center.x + radius * 0.03f, center.y - radius * 0.48f)
          lineTo(center.x, center.y - radius * 0.48f)
          lineTo(center.x - radius * 0.03f, center.y - radius * 0.48f)
          close()
        }
      drawPath(lumePath, LumeColor)
    }
  }

  override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Minute hand - longer sword-shaped with lume
      val path =
        Path().apply {
          moveTo(center.x - radius * 0.03f, center.y)
          lineTo(center.x + radius * 0.03f, center.y)
          lineTo(center.x + radius * 0.03f, center.y - radius * 0.7f)
          lineTo(center.x, center.y - radius * 0.7f)
          lineTo(center.x - radius * 0.03f, center.y - radius * 0.7f)
          close()
        }
      drawPath(path, MinuteHandColor)

      // Lume on minute hand
      val lumePath =
        Path().apply {
          moveTo(center.x - radius * 0.02f, center.y)
          lineTo(center.x + radius * 0.02f, center.y)
          lineTo(center.x + radius * 0.02f, center.y - radius * 0.68f)
          lineTo(center.x, center.y - radius * 0.68f)
          lineTo(center.x - radius * 0.02f, center.y - radius * 0.68f)
          close()
        }
      drawPath(lumePath, LumeColor)
    }
  }

  override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
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
        radius = radius * 0.04f,
        center = Offset(center.x, center.y - radius * 0.6f)
      )

      // Counterbalance
      drawCircle(
        color = SecondHandColor,
        radius = radius * 0.03f,
        center = Offset(center.x, center.y + radius * 0.1f)
      )
    }
  }

  override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Draw center dot
      drawCircle(color = CenterDotColor, radius = radius * 0.04f, center = center)
    }
  }
}

private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
  // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
  val scaledRadius = radius * 1.25f

  // Draw outer circle (border)
  drawCircle(
    color = ClockBorderColor,
    radius = scaledRadius,
    center = center,
    style = Stroke(width = 8f)
  )

  // Draw rotating bezel (characteristic of dive watches)
  drawCircle(color = BezelColor, radius = scaledRadius * 0.95f, center = center)

  // Draw bezel markers (minute markers for diving)
  for (i in 0 until 60) {
    val angle = Math.PI * 2 * i / 60

    if (i % 5 == 0) {
      // Draw larger markers at 5-minute intervals
      val markerLength =
        if (i == 0) scaledRadius * 0.08f else scaledRadius * 0.06f // Bigger triangle at 12 o'clock
      val markerStart = scaledRadius * 0.95f - markerLength
      val markerEnd = scaledRadius * 0.95f

      val startX = center.x + cos(angle).toFloat() * markerStart
      val startY = center.y + sin(angle).toFloat() * markerStart
      val endX = center.x + cos(angle).toFloat() * markerEnd
      val endY = center.y + sin(angle).toFloat() * markerEnd

      // Draw triangle at 12 o'clock (0 minutes)
      if (i == 0) {
        drawCircle(color = LumeColor, radius = scaledRadius * 0.03f, center = Offset(endX, endY))
      } else {
        // Draw dot markers for other 5-minute intervals
        drawCircle(
          color = BezelMarkersColor,
          radius = scaledRadius * 0.02f,
          center = Offset(endX, endY)
        )
      }
    } else {
      // Draw smaller markers for minutes
      val dotX = center.x + cos(angle).toFloat() * scaledRadius * 0.95f
      val dotY = center.y + sin(angle).toFloat() * scaledRadius * 0.95f

      drawCircle(
        color = BezelMarkersColor,
        radius = scaledRadius * 0.005f,
        center = Offset(dotX, dotY)
      )
    }
  }

  // Draw inner circle (face)
  drawCircle(color = ClockFaceColor, radius = scaledRadius * 0.85f, center = center)

  // Draw Jurgsen logo text
  val logoPaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = scaledRadius * 0.12f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "Zénithor",
    center.x,
    center.y - scaledRadius * 0.3f,
    logoPaint
  )

  // Draw "JÜRGSEN GENÈVE" text
  val modelPaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = scaledRadius * 0.08f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = false
      isAntiAlias = true
    }

  drawContext.canvas.nativeCanvas.drawText(
    "JÜRGSEN GENÈVE",
    center.x,
    center.y + scaledRadius * 0.3f,
    modelPaint
  )

  // Draw date window at 4:30 position
  val dateAngle = Math.PI / 6 * 4.5 // Between 4 and 5
  val dateX = center.x + cos(dateAngle).toFloat() * scaledRadius * 0.55f
  val dateY = center.y + sin(dateAngle).toFloat() * scaledRadius * 0.55f

  // White date window
  drawRect(
    color = Color.White,
    topLeft = Offset(dateX - scaledRadius * 0.08f, dateY - scaledRadius * 0.06f),
    size = Size(scaledRadius * 0.16f, scaledRadius * 0.12f)
  )

  // Date text
  val datePaint =
    Paint().apply {
      color = Color.Black.hashCode()
      textSize = scaledRadius * 0.1f
      textAlign = Paint.Align.CENTER
      isFakeBoldText = true
      isAntiAlias = true
    }

  val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
  drawContext.canvas.nativeCanvas.drawText(day, dateX, dateY + scaledRadius * 0.035f, datePaint)
}

private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
  // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
  val scaledRadius = radius * 1.25f

  // Fifty Fathoms uses large, luminous hour markers
  for (i in 1..12) {
    val angle = Math.PI / 6 * (i - 3)

    // Draw circular hour markers with lume
    val markerRadius = if (i % 3 == 0) scaledRadius * 0.06f else scaledRadius * 0.05f
    val markerX = center.x + cos(angle).toFloat() * scaledRadius * 0.7f
    val markerY = center.y + sin(angle).toFloat() * scaledRadius * 0.7f

    // White outer circle
    drawCircle(color = MarkersColor, radius = markerRadius, center = Offset(markerX, markerY))

    // Lume inner circle (slightly smaller)
    drawCircle(color = LumeColor, radius = markerRadius * 0.8f, center = Offset(markerX, markerY))

    // Special rectangular marker at 12 o'clock
    if (i == 12) {
      drawRect(
        color = MarkersColor,
        topLeft =
          Offset(
            center.x - scaledRadius * 0.06f,
            center.y - scaledRadius * 0.7f - scaledRadius * 0.06f
          ),
        size = Size(scaledRadius * 0.12f, scaledRadius * 0.12f)
      )

      // Lume inside
      drawRect(
        color = LumeColor,
        topLeft =
          Offset(
            center.x - scaledRadius * 0.05f,
            center.y - scaledRadius * 0.7f - scaledRadius * 0.05f
          ),
        size = Size(scaledRadius * 0.1f, scaledRadius * 0.1f)
      )
    }
  }
}

@Composable
fun JurgsenZenithor(
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
      JurgsenZenithorTheme.staticElementsDrawer.forEach { drawer -> drawer(center, radius)(this) }
    }

    // Add the world map component in the middle layer (bottom half)
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .fillMaxHeight(0.5f) // Take up only the bottom half of the screen
          .align(Alignment.BottomCenter)
          .padding(bottom = 30.dp), // Add bottom padding of 30.dp
      contentAlignment = Alignment.Center
    ) {
      CustomWorldMapWithDayNight(
        modifier =
          Modifier.fillMaxWidth(0.55f) // Make the map 55% of the available width
            .fillMaxHeight(
              0.55f
            ) // Make the map 55% of the available height while maintaining aspect ratio
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
      val timeZoneInfo =
        remember(selectedTimeZoneId.value) {
          timeZones.find { it.id == selectedTimeZoneId.value }
            ?: TimeZoneInfo(id = selectedTimeZoneId.value, displayName = selectedTimeZoneId.value)
        }

      // Add a clickable area at the top of the screen where the timezone name is displayed
      Box(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        contentAlignment = Alignment.TopCenter
      ) {
        // Create a clickable row with the timezone name and an icon
        Row(
          modifier = Modifier.clickable(onClick = onSelectTimeZone).padding(8.dp),
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
      val radius = size.minDimension / 2 * 0.8f

      // Get current time values
      val hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY)
      val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
      val minute = currentTime.get(Calendar.MINUTE)
      val second = currentTime.get(Calendar.SECOND)

      // Draw hour hand
      rotate(degrees = (hour * 30f) + (minute * 0.5f)) {
        JurgsenZenithorTheme.hourHandDrawer(center, radius)(this)
      }

      // Draw minute hand
      rotate(degrees = minute * 6f) { JurgsenZenithorTheme.minuteHandDrawer(center, radius)(this) }

      // Draw second hand
      rotate(degrees = second * 6f) { JurgsenZenithorTheme.secondHandDrawer(center, radius)(this) }

      // Draw center dot
      JurgsenZenithorTheme.centerDotDrawer(center, radius)(this)
    }
  }
}

@Preview(widthDp = 300, heightDp = 300, showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun JurgsenZenithorPreview() {
  JurgsenZenithor()
}
