package com.coroutines.swisstime.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.coroutines.swisstime.R
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldMapScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
  var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

  // Update time every second
  LaunchedEffect(key1 = true) {
    while (true) {
      currentTime = Calendar.getInstance()
      delay(1000) // Update every second
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("World Map") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
          )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      // Top two-thirds - Chronomagus Regum watch
      Box(modifier = Modifier.fillMaxWidth().weight(2f), contentAlignment = Alignment.Center) {
        ChronomagusRegum(modifier = Modifier.fillMaxSize(0.8f))
      }

      // Bottom one-third - World map with day/night visualization
      Box(
        modifier = Modifier.fillMaxWidth().weight(1f)
        // Removed blue background as per requirements
      ) {
        WorldMapWithDayNight(currentTime = currentTime, modifier = Modifier.fillMaxSize())
      }
    }
  }
}

@Composable
fun WorldMapWithDayNight(currentTime: Calendar, modifier: Modifier = Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    // Display the world map image as SVG vector drawable
    // Use ContentScale.FillWidth to maintain aspect ratio while taking full width
    Image(
      painter = painterResource(id = R.drawable.world),
      contentDescription = "World Map",
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth
    )

    // Overlay the day/night terminator - use fillMaxWidth to match the image width
    // and set a fixed aspect ratio based on the SVG dimensions (2000/857)
    Canvas(
      modifier = Modifier.fillMaxWidth().aspectRatio(2000f / 857f) // SVG width / height
    ) {
      val width = size.width
      val height = size.height

      // Draw grid lines for longitude and latitude
      drawGridLines(width, height)

      // Calculate and draw day/night terminator
      drawDayNightTerminator(width, height, currentTime)
    }
  }
}

private fun DrawScope.drawGridLines(width: Float, height: Float) {
  // Draw grid lines for longitude and latitude
  val gridColor = Color(0x33FFFFFF) // Semi-transparent white

  // Longitude lines (vertical)
  for (i in 0..12) {
    val x = width * i / 12
    drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, height), strokeWidth = 1f)
  }

  // Latitude lines (horizontal)
  for (i in 0..6) {
    val y = height * i / 6
    drawLine(color = gridColor, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1f)
  }

  // Draw equator with a different color
  drawLine(
    color = Color(0x66FFFFFF), // More visible white
    start = Offset(0f, height / 2),
    end = Offset(width, height / 2),
    strokeWidth = 1.5f
  )
}

private fun DrawScope.drawDayNightTerminator(width: Float, height: Float, currentTime: Calendar) {
  // Calculate the position of the sun based on time
  val hour = currentTime.get(Calendar.HOUR_OF_DAY)
  val minute = currentTime.get(Calendar.MINUTE)
  val second = currentTime.get(Calendar.SECOND)

  // Convert time to angle (24 hours = 360 degrees)
  val timeAngle = (hour + minute / 60f + second / 3600f) * 15f // 15 degrees per hour

  // Berlin, Germany is at approximately 13.4° E longitude
  // We need to adjust the map so Berlin is in the middle (at 0° in our calculation)
  val berlinLongitude = 13.4f

  // Calculate the sun's position on the map, adjusted for Berlin being in the center
  // The sun moves from east to west, so we need to subtract the time angle from 180
  val sunLongitude = (180 - timeAngle + berlinLongitude) % 360 // Convert to map coordinates

  // Ensure sunLongitude is between 0 and 360
  val normalizedSunLongitude = if (sunLongitude < 0) sunLongitude + 360 else sunLongitude
  val sunX = width * (normalizedSunLongitude / 360f) // Map to canvas width

  // Draw a darker overlay for the night side
  val nightColor = Color(0xFF000000) // Fully opaque black for maximum visibility

  // Calculate the Earth's axial tilt
  // The Earth's axial tilt varies between 22.1° and 24.5° over a 41,000-year cycle
  // Currently it's about 23.44 degrees
  val axialTilt = 23.44f * (PI / 180f).toFloat() // Convert to radians

  // Calculate the day of year (0-365)
  val dayOfYear = currentTime.get(Calendar.DAY_OF_YEAR) - 1

  // Calculate the solar declination (the angle between the sun's rays and the equator)
  // This varies throughout the year due to the Earth's axial tilt
  val solarDeclination = axialTilt * sin(2 * PI * (dayOfYear - 81) / 365).toFloat()

  // Draw the terminator curve
  val path = androidx.compose.ui.graphics.Path()
  val points = mutableListOf<Offset>()

  // Calculate the terminator curve points
  for (y in 0..height.toInt()) {
    // Convert y-coordinate to latitude (-90° to 90°, with 0° at the equator)
    val latitude = ((height / 2 - y) / (height / 2)) * (PI / 2).toFloat()

    // Calculate the longitude of the terminator at this latitude
    // This is the formula for the terminator curve on a spherical Earth
    val longitudeShift =
      if (cos(latitude) != 0f) {
        atan2(-sin(solarDeclination) * sin(latitude), cos(latitude)).toFloat()
      } else {
        0f // At the poles, the longitude is undefined
      }

    // The terminator is 90° from the subsolar point (where the sun is directly overhead)
    val terminatorLongitude =
      (normalizedSunLongitude + 90 + longitudeShift * (180 / PI).toFloat()) % 360
    val x = width * (terminatorLongitude / 360f)

    points.add(Offset(x, y.toFloat()))

    // Add the point to the path
    if (y == 0) {
      path.moveTo(x, 0f)
    } else {
      path.lineTo(x, y.toFloat())
    }
  }

  // Draw the night side
  // We need to create a path that includes the terminator curve and the edge of the map
  val nightPath = androidx.compose.ui.graphics.Path()

  // Determine if the night side is on the left or right of the terminator
  val sunLongitudeRad = normalizedSunLongitude * (PI / 180f).toFloat()
  val isSunInEasternHemisphere = sunLongitudeRad < PI

  if (isSunInEasternHemisphere) {
    // Sun is in the eastern hemisphere, night is in the western hemisphere (left side)
    nightPath.moveTo(0f, 0f) // Start at top-left corner
    nightPath.lineTo(points.first().x, 0f) // Go to the top of the terminator

    // Follow the terminator curve down
    for (point in points) {
      nightPath.lineTo(point.x, point.y)
    }

    nightPath.lineTo(0f, height) // Go to bottom-left corner
    nightPath.close() // Close the path
  } else {
    // Sun is in the western hemisphere, night is in the eastern hemisphere (right side)
    nightPath.moveTo(points.first().x, 0f) // Start at the top of the terminator
    nightPath.lineTo(width, 0f) // Go to top-right corner
    nightPath.lineTo(width, height) // Go to bottom-right corner
    nightPath.lineTo(points.last().x, height) // Go to the bottom of the terminator

    // Follow the terminator curve up
    for (point in points.reversed()) {
      nightPath.lineTo(point.x, point.y)
    }

    nightPath.close() // Close the path
  }

  // Draw the night area
  drawPath(path = nightPath, color = nightColor)

  // Draw the terminator curve
  drawPath(
    path = path,
    color = Color(0xFFFFD700), // Gold color for terminator
    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
  )

  // Draw sun indicator
  drawCircle(
    color = Color(0xFFFFD700), // Gold for sun
    radius = 10f,
    center = Offset(sunX, height * 0.1f) // Position sun near the top
  )

  // Draw time text
  val timePaint =
    Paint().apply {
      color = Color.White.hashCode()
      textSize = 30f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

  val timeString = String.format("%02d:%02d:%02d", hour, minute, second)
  drawContext.canvas.nativeCanvas.drawText(timeString, width / 2, height * 0.9f, timePaint)
}
