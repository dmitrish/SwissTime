package com.coroutines.swisstime.wearos.watchfaces

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

// Colors inspired by IWC Portugieser
private val ClockFaceColor = Color(0xFF627373)
private val ClockBorderColor = Color(0xFF303030) // Dark border
private val HourHandColor = Color(0xFF000080) // Blue hour hand
private val MinuteHandColor = Color(0xFF000080) // Blue minute hand
private val MarkersColor = Color(0xFF000000) // Black markers
private val NumbersColor = Color(0xFF000000) // Black numbers
private val SubdialColor = Color(0xFFE0E0E0) // Light gray subdial
private val SubdialHandColor = Color(0xFF000080) // Blue subdial hand
private val CenterDotColor = Color(0xFF000080) // Blue center dot

private object HorologiaRomanumTheme : WorldClockWatchTheme() {
  override val staticElementsDrawer =
    listOf(
      { center: Offset, radius: Float -> drawStaticElements(center, radius) },
      { center: Offset, radius: Float -> drawHourMarkersAndNumbers(center, radius) },
      { center: Offset, radius: Float -> drawSubdialBackground(center, radius) }
    )

  override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      val path =
        Path().apply {
          moveTo(center.x, center.y - radius * 0.5f)
          quadraticBezierTo(
            center.x + radius * 0.04f,
            center.y - radius * 0.25f,
            center.x + radius * 0.02f,
            center.y
          )
          quadraticBezierTo(center.x, center.y + radius * 0.1f, center.x - radius * 0.02f, center.y)
          quadraticBezierTo(
            center.x - radius * 0.04f,
            center.y - radius * 0.25f,
            center.x,
            center.y - radius * 0.5f
          )
          close()
        }
      drawPath(path, HourHandColor)
    }
  }

  override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      val path =
        Path().apply {
          moveTo(center.x, center.y - radius * 0.7f)
          quadraticBezierTo(
            center.x + radius * 0.03f,
            center.y - radius * 0.35f,
            center.x + radius * 0.015f,
            center.y
          )
          quadraticBezierTo(
            center.x,
            center.y + radius * 0.1f,
            center.x - radius * 0.015f,
            center.y
          )
          quadraticBezierTo(
            center.x - radius * 0.03f,
            center.y - radius * 0.35f,
            center.x,
            center.y - radius * 0.7f
          )
          close()
        }
      drawPath(path, MinuteHandColor)
    }
  }

  override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // This is a special case - we don't draw a second hand on the main dial
      // Instead, we'll handle the seconds in the subdial
    }
  }

  override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    { drawCircle(color = CenterDotColor, radius = radius * 0.03f, center = center) }
  }

  private fun drawStaticElements(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    drawCircle(
      color = ClockBorderColor,
      radius = scaledRadius,
      center = center,
      style = Stroke(width = 6f)
    )

    drawCircle(color = ClockFaceColor, radius = scaledRadius - 3f, center = center)
  }

  private fun drawHourMarkersAndNumbers(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    for (i in 1..60) {
      val angle = Math.PI / 30 * (i - 15)
      val markerLength = if (i % 5 == 0) scaledRadius * 0.05f else scaledRadius * 0.02f
      val strokeWidth = if (i % 5 == 0) 2f else 1f

      if (i >= 25 && i <= 35) continue // Skip markers where the subdial is

      val startX = center.x + cos(angle).toFloat() * (scaledRadius - markerLength)
      val startY = center.y + sin(angle).toFloat() * (scaledRadius - markerLength)
      val endX = center.x + cos(angle).toFloat() * scaledRadius * 0.9f
      val endY = center.y + sin(angle).toFloat() * scaledRadius * 0.9f

      drawLine(
        color = MarkersColor,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
      )
    }

    val textPaint =
      Paint().apply {
        color = NumbersColor.hashCode()
        textSize = scaledRadius * 0.15f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
      }

    val hours = listOf(12, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11)
    val positions = listOf(9, 10, 11, 0, 1, 2, 4, 5, 6, 7, 8)

    for (i in hours.indices) {
      val angle = Math.PI / 6 * positions[i]
      val numberRadius = scaledRadius * 0.75f
      val numberX = center.x + cos(angle).toFloat() * numberRadius
      val numberY = center.y + sin(angle).toFloat() * numberRadius + textPaint.textSize / 3

      drawContext.canvas.nativeCanvas.drawText(hours[i].toString(), numberX, numberY, textPaint)
    }
  }

  fun drawSubdialBackground(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    val subdialCenter = Offset(center.x, center.y + scaledRadius * 0.4f)
    val subdialRadius = scaledRadius * 0.2f

    drawCircle(color = SubdialColor, radius = subdialRadius, center = subdialCenter)
    drawCircle(
      color = Color.Black,
      radius = subdialRadius,
      center = subdialCenter,
      style = Stroke(width = 2f)
    )

    for (i in 0 until 60) {
      val angle = Math.PI * 2 * i / 60
      val markerLength =
        if (i % 15 == 0) subdialRadius * 0.2f
        else if (i % 5 == 0) subdialRadius * 0.15f else subdialRadius * 0.05f
      val startX = subdialCenter.x + cos(angle).toFloat() * (subdialRadius - markerLength)
      val startY = subdialCenter.y + sin(angle).toFloat() * (subdialRadius - markerLength)
      val endX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.9f
      val endY = subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.9f

      drawLine(
        color = Color.Black,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = if (i % 15 == 0) 1.5f else 1f
      )
    }

    val secondsNumbers = listOf("60", "15", "30", "45")
    val numberPaint =
      Paint().apply {
        color = Color.Black.hashCode()
        textSize = subdialRadius * 0.3f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

    for (i in 0..3) {
      val angle = Math.PI / 2 * i
      val numberX = subdialCenter.x + cos(angle).toFloat() * subdialRadius * 0.6f
      val numberY =
        subdialCenter.y + sin(angle).toFloat() * subdialRadius * 0.6f + numberPaint.textSize / 3
      drawContext.canvas.nativeCanvas.drawText(secondsNumbers[i], numberX, numberY, numberPaint)
    }
  }

  fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    val logoPaint =
      Paint().apply {
        color = Color.Black.hashCode()
        textSize = scaledRadius * 0.12f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
      }

    drawContext.canvas.nativeCanvas.drawText(
      "HOROLOGIA",
      center.x,
      center.y - scaledRadius * 0.3f,
      logoPaint
    )

    val originPaint =
      Paint().apply {
        color = Color.Black.hashCode()
        textSize = scaledRadius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

    /*drawContext.canvas.nativeCanvas.drawText(
        "ROMANUM",
        center.x,
        center.y - scaledRadius * 0.2f,
        originPaint
    )*/
  }

  fun drawSubdialSecondHand(center: Offset, radius: Float, seconds: Int): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    val subdialCenter = Offset(center.x, center.y + scaledRadius * 0.4f)
    val subdialRadius = scaledRadius * 0.2f

    rotate(degrees = seconds * 6f, pivot = subdialCenter) {
      drawLine(
        color = SubdialHandColor,
        start = subdialCenter,
        end = Offset(subdialCenter.x, subdialCenter.y - subdialRadius * 0.8f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
      )

      drawLine(
        color = SubdialHandColor,
        start = subdialCenter,
        end = Offset(subdialCenter.x, subdialCenter.y + subdialRadius * 0.2f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
      )
    }
  }
}

@Composable
fun HorologiaRomanum(
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
    // 1. Draw static elements of the watch face (background)
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2 * 0.8f

      // Draw static elements (watchface background)
      HorologiaRomanumTheme.staticElementsDrawer.forEach { drawer -> drawer(center, radius)(this) }
    }

    // 2. Add the world map component - centered vertically
    Box(
      modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f), // Use full size of the parent
      contentAlignment = Alignment.Center // Center the map in the box
    ) {
      CustomWorldMapWithDayNight(
        modifier =
          Modifier.fillMaxWidth(0.55f) // Make the map 55% of the width
            .fillMaxHeight(
              0.3f
            ), // Make the map 30% of the height to avoid overlapping with subdial
        nightOverlayColor = ClockFaceColor // Use the watch face color for the night overlay
      )
    }

    // 3. Draw the subdial background
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2 * 0.8f
      HorologiaRomanumTheme.drawSubdialBackground(center, radius)(this)
    }

    // 4. Draw the logo
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2 * 0.8f
      HorologiaRomanumTheme.drawLogo(center, radius)(this)
    }

    // 5. Draw the timezone selection UI
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
            tint = Color.Black // Use black to match the watch face
          )
        }
      }
    }

    // 6. Draw the clock hands on the top layer
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
      rotate(hourAngle) { HorologiaRomanumTheme.hourHandDrawer(center, radius)(this) }

      // Draw minute hand
      val minuteAngle = minute * 6f
      rotate(minuteAngle) { HorologiaRomanumTheme.minuteHandDrawer(center, radius)(this) }

      // Draw subdial second hand
      HorologiaRomanumTheme.drawSubdialSecondHand(center, radius, second)(this)

      // Draw center dot
      HorologiaRomanumTheme.centerDotDrawer(center, radius)(this)
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HorologiaRomanumPreview() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { HorologiaRomanum() }
}
