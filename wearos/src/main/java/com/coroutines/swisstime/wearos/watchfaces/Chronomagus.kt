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
import com.coroutines.worldclock.common.watchface.WorldClockWatchTheme
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

// Colors inspired by Chronomagus Regum
private val ClockFaceColor = Color(0xFF000080) // Deep blue dial
private val ClockBorderColor = Color(0xFFFFFFFF) // White gold case
private val HourHandColor = Color(0xFFFFFFFF) // White hour hand
private val MinuteHandColor = Color(0xFFFFFFFF) // White minute hand
private val SecondHandColor = Color(0xFFFFFFFF) // White second hand
private val MarkersColor = Color(0xFFFFFFFF) // White markers
private val LogoColor = Color(0xFFFFDE21) // White logo

private object ChronomagusTheme : WorldClockWatchTheme() {
  override val staticElementsDrawer =
    listOf(
      { center: Offset, radius: Float -> drawClockFace(center, radius) },
      { center: Offset, radius: Float -> drawHourMarkers(center, radius) }
    )

  override val hourHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Simple thin line for hour hand
      drawLine(
        color = HourHandColor,
        start = center,
        end = Offset(center.x, center.y - radius * 0.5f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
      )
    }
  }

  override val minuteHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Simple thin line for minute hand
      drawLine(
        color = MinuteHandColor,
        start = center,
        end = Offset(center.x, center.y - radius * 0.7f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
      )
    }
  }

  override val secondHandDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    {
      // Ultra-thin line for second hand
      drawLine(
        color = SecondHandColor,
        start = center,
        end = Offset(center.x, center.y - radius * 0.8f),
        strokeWidth = 0.5f,
        cap = StrokeCap.Round
      )
    }
  }

  override val centerDotDrawer: (Offset, Float) -> DrawScope.() -> Unit = { center, radius ->
    { drawCircle(color = HourHandColor, radius = radius * 0.01f, center = center) }
  }

  private fun drawClockFace(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    // Draw outer circle (case) - very thin to represent the ultra-thin profile
    drawCircle(
      color = ClockBorderColor,
      radius = scaledRadius,
      center = center,
      style = Stroke(width = 4f)
    )

    // Draw inner circle (face)
    drawCircle(color = ClockFaceColor, radius = scaledRadius - 2f, center = center)
  }

  private fun drawHourMarkers(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    // Chronomagus Regum typically has very minimalist hour markers
    // Often just simple thin lines or small dots

    for (i in 0 until 12) {
      val angle = PI / 6 * i

      // For 3, 6, 9, and 12 o'clock, use slightly longer markers
      val markerLength = if (i % 3 == 0) scaledRadius * 0.05f else scaledRadius * 0.03f
      val markerWidth = if (i % 3 == 0) 1.5f else 1f

      val startX = center.x + cos(angle).toFloat() * (scaledRadius * 0.85f)
      val startY = center.y + sin(angle).toFloat() * (scaledRadius * 0.85f)
      val endX = center.x + cos(angle).toFloat() * (scaledRadius * 0.85f - markerLength)
      val endY = center.y + sin(angle).toFloat() * (scaledRadius * 0.85f - markerLength)

      // Draw minimalist markers
      drawLine(
        color = MarkersColor,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = markerWidth,
        cap = StrokeCap.Round
      )
    }

    // Add small dots at each hour position for a more refined look
    for (i in 0 until 12) {
      val angle = PI / 6 * i
      val dotRadius = if (i % 3 == 0) 1.5f else 1f

      val dotX = center.x + cos(angle).toFloat() * (scaledRadius * 0.9f)
      val dotY = center.y + sin(angle).toFloat() * (scaledRadius * 0.9f)

      drawCircle(color = MarkersColor, radius = dotRadius, center = Offset(dotX, dotY))
    }
  }

  fun drawLogo(center: Offset, radius: Float): DrawScope.() -> Unit = {
    // Scale up by 1.25 to compensate for the 0.8 scaling in BaseWatch
    val scaledRadius = radius * 1.25f

    val logoPaint =
      Paint().apply {
        color = LogoColor.hashCode()
        textSize = scaledRadius * 0.08f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false // Chronomagus Regum logo is typically thin and elegant
        isAntiAlias = true
      }

    // Draw "CHRONOMAGUS" text
    drawContext.canvas.nativeCanvas.drawText(
      "CHRONOMAGUS",
      center.x,
      center.y - scaledRadius * 0.15f,
      logoPaint
    )

    // Draw "REGIUM" text
    val modelPaint =
      Paint().apply {
        color = LogoColor.hashCode()
        textSize = scaledRadius * 0.06f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

    /* drawContext.canvas.nativeCanvas.drawText(
        "REGIUM",
        center.x,
        center.y - scaledRadius * 0.2f,
        modelPaint
    )*/

    // Draw "Fabricatum Romae" text
    val swissMadePaint =
      Paint().apply {
        color = LogoColor.hashCode()
        textSize = scaledRadius * 0.04f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

    drawContext.canvas.nativeCanvas.drawText(
      "Fabricatum Romae",
      center.x,
      center.y + scaledRadius * 0.6f,
      swissMadePaint
    )

    // Draw "ULTRA-THIN" text
    val ultraThinPaint =
      Paint().apply {
        color = LogoColor.hashCode()
        textSize = scaledRadius * 0.05f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

    drawContext.canvas.nativeCanvas.drawText(
      "ULTRA-THIN",
      center.x,
      center.y + scaledRadius * 0.7f,
      ultraThinPaint
    )
  }
}

@Composable
fun Chronomagus(
  modifier: Modifier = Modifier,
  timeZone: TimeZone = TimeZone.getDefault(),
  watchFaceRepository: WatchFaceRepository? = null,
  onSelectTimeZone: () -> Unit = {}
) {
  var currentTime by remember { mutableStateOf(java.util.Calendar.getInstance(timeZone)) }
  val timeZoneState by rememberUpdatedState(timeZone)

  // Update time every second
  LaunchedEffect(key1 = true) {
    while (true) {
      currentTime = java.util.Calendar.getInstance(timeZoneState)
      delay(1000) // Update every second
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    // Draw static elements of the watch face
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2 * 0.8f

      // Draw static elements
      ChronomagusTheme.staticElementsDrawer.forEach { drawer -> drawer(center, radius)(this) }
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
          Modifier.fillMaxWidth(0.55f) // Make the map 45% smaller in width
            .fillMaxHeight(
              0.55f
            ) // Make the map 45% smaller in height while maintaining aspect ratio
            .offset(y = (-10).dp), // Raise it by approximately 10% of the bottom half's height
        nightOverlayColor = ClockFaceColor // Use the watch face color for the night overlay
      )
    }

    // Draw the logo on top of the watchface and map but below the hands
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2 * 0.8f
      ChronomagusTheme.drawLogo(center, radius)(this)
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
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
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
      val hourOfDay = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
      val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
      val minute = currentTime.get(java.util.Calendar.MINUTE)
      val second = currentTime.get(java.util.Calendar.SECOND)

      // Draw hour hand
      val hourAngle = (hour * 30 + minute * 0.5f)
      rotate(hourAngle) { ChronomagusTheme.hourHandDrawer(center, radius)(this) }

      // Draw minute hand
      val minuteAngle = minute * 6f
      rotate(minuteAngle) { ChronomagusTheme.minuteHandDrawer(center, radius)(this) }

      // Draw second hand
      val secondAngle = second * 6f
      rotate(secondAngle) { ChronomagusTheme.secondHandDrawer(center, radius)(this) }

      // Draw center dot
      ChronomagusTheme.centerDotDrawer(center, radius)(this)
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ChronomagusPreview() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Chronomagus() }
}
