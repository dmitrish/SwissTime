package com.coroutines.swisstime.ui.screens

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.coroutines.swisstime.R
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorldMapScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                title = { Text("Custom World Map") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Flexible spacer to push content to the bottom
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(1f)
            )

            // Piaget Altiplano watch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Square aspect ratio for the watch
                contentAlignment = Alignment.Center
            ) {
                PiagetAltiplano(
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

            // Custom World map with day/night visualization - bottom aligned
            CustomWorldMapWithDayNight(
                currentTime = currentTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f) // 2:1 aspect ratio for the world map
            )
        }
    }
}

@Composable
fun CustomWorldMapWithDayNight(
    currentTime: Calendar,
    modifier: Modifier = Modifier
) {
    // Constants from earth.py
    val blur = 4f  // blur angle for terminator
    val phong = true  // enable Phong shading
    val shadDiv = 260f  // shading intensity dividend (higher value -> brighter shading)
    val diffInt = 1f  // diffuse intensity
    val specExp = 4f  // specular reflection exponent (0 = diffuse only; > 50 = metallic)

    // Calculate the position of the sun based on time
    val year = currentTime.get(Calendar.YEAR)
    val month = currentTime.get(Calendar.MONTH) + 1 // Calendar months are 0-based
    val day = currentTime.get(Calendar.DAY_OF_MONTH)
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    val second = currentTime.get(Calendar.SECOND)
    val hourDecimal = hour + minute / 60f + second / 3600f

    // Calculate days since J2000 (January 1, 2000 12:00 UTC)
    // This is a simplified version of the FNday function from earth.py
    val daysToJ2000 = 367 * year - 7 * (year + (month + 9) / 12) / 4 + 275 * month / 9 + day - 730530 + hourDecimal / 24f

    // Calculate the sun's position using the algorithm from earth.py
    // These calculations determine the Right Ascension (RA) and Declination (Decl) of the sun

    // Orbital elements of the Earth
    val w = 282.9404f + 4.70935E-5f * daysToJ2000  // longitude of perihelion
    val e = 0.016709f - 1.151E-9f * daysToJ2000    // eccentricity
    val M = rev(356.0470f + 0.9856002585f * daysToJ2000)  // mean anomaly
    val oblecl = 23.4393f - 3.563E-7f * daysToJ2000  // obliquity of the ecliptic

    // Sun's longitude
    val L = rev(w + M)

    // Eccentric anomaly
    val E = M + (180f / PI.toFloat()) * e * sin(M * (PI / 180f).toFloat()) * (1 + e * cos(M * (PI / 180f).toFloat()))

    // Sun's rectangular coordinates in the plane of the ecliptic
    val x = cos(E * (PI / 180f).toFloat()) - e
    val y = sin(E * (PI / 180f).toFloat()) * sqrt(1 - e * e)

    // Distance and true anomaly
    val r = sqrt(x * x + y * y)
    val v = atan2(y, x) * (180f / PI.toFloat())

    // Sun's longitude
    val sunLongitude = rev(v + w)

    // Sun's ecliptic rectangular coordinates
    val xeclip = r * cos(sunLongitude * (PI / 180f).toFloat())
    val yeclip = r * sin(sunLongitude * (PI / 180f).toFloat())

    // Rotate to equatorial coordinates
    val xequat = xeclip
    val yequat = yeclip * cos(oblecl * (PI / 180f).toFloat())
    val zequat = yeclip * sin(oblecl * (PI / 180f).toFloat())

    // Calculate Right Ascension and Declination
    val RA = atan2(yequat, xequat) * (180f / PI.toFloat()) / 15f
    val Decl = asin(zequat / r) * (180f / PI.toFloat())

    // Calculate Greenwich Mean Sidereal Time
    val GMST0 = (L * (PI / 180f).toFloat() + PI.toFloat()) / 15f * (180f / PI.toFloat())

    Box(
        modifier = modifier.background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // World map (SVG vector drawable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .background(Color.Transparent)
        ) {
            // World map
            Image(
                painter = painterResource(id = R.drawable.world),
                contentDescription = "Earth Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            // Canvas for drawing the terminator and masking the night side
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height

                // Calculate the terminator curve points
                val terminatorPoints = mutableListOf<Pair<Offset, Offset>>() // Pairs of (rising, setting) points
                val path = Path()

                // Calculate the number of points to generate based on screen height
                // More points for higher resolution screens, fewer for lower resolution
                val numPoints = (height / 2).coerceAtLeast(180f).toInt()
                val yStep = height / numPoints

                // For each pixel row in the image (with adaptive step size)
                for (i in 0..numPoints) {
                    val yPos = i * yStep

                    // Convert y-coordinate to latitude (-90° to 90°, with 0° at the equator)
                    val latitude = 90f - (yPos / height * 180f)
                    val latRad = latitude * (PI / 180f).toFloat()

                    // For each latitude, calculate the terminator longitude
                    // This is based on the calc_alt function in earth.py
                    val declRad = Decl * (PI / 180f).toFloat()

                    // Calculate the hour angle where the sun's altitude is 0 (the terminator)
                    // This is where we solve for the longitude where altitude = 0
                    val cosTerm = -tan(declRad) * tan(latRad)

                    // Check if there's a terminator at this latitude
                    if (abs(cosTerm) <= 1.0f) {
                        // Calculate the hour angle at the terminator (both rising and setting points)
                        val HAterm = acos(cosTerm)

                        // Apply correction factors to account for map projection distortion
                        // This helps to fine-tune the terminator position
                        // Different factors for rising and setting to maintain correct distance while positioning properly
                        val risingCorrectionFactor = 0.69f  // Further decreased to move the curve more to the left (1.04/1.5)
                        val settingCorrectionFactor = 0.39f // Further decreased to move the curve more to the left (0.58/1.5)

                        // Convert hour angle to longitude for rising point (eastern terminator)
                        val longTermRising = rev(RA * 15f - HAterm * (180f / PI.toFloat()) * risingCorrectionFactor - GMST0 * 15f - hourDecimal * 15f)

                        // Convert hour angle to longitude for setting point (western terminator)
                        val longTermSetting = rev(RA * 15f + HAterm * (180f / PI.toFloat()) * settingCorrectionFactor - GMST0 * 15f - hourDecimal * 15f)

                        // Map longitudes to x-coordinates
                        // For equirectangular projection, we need to map longitude from 0-360° to 0-width
                        // First, normalize longitudes to -180° to 180° range
                        val normalizedLongRising = if (longTermRising > 180f) longTermRising - 360f else longTermRising
                        val normalizedLongSetting = if (longTermSetting > 180f) longTermSetting - 360f else longTermSetting

                        // Then map to x-coordinates (0 to width)
                        val xRising = width * ((normalizedLongRising + 180f) / 360f)
                        val xSetting = width * ((normalizedLongSetting + 180f) / 360f)

                        // Apply a direct offset to shift the entire bell curve to the left
                        val xOffset = width * 0.1f // Shift left by 10% of the screen width (moved halfway back to the right)

                        terminatorPoints.add(Pair(Offset(xRising - xOffset, yPos), Offset(xSetting - xOffset, yPos)))
                    }
                }

                // Create the terminator path if we have points
                if (terminatorPoints.isNotEmpty()) {
                    // Start with the first rising point
                    path.moveTo(terminatorPoints.first().first.x, terminatorPoints.first().first.y)

                    // Draw the rising side (going down)
                    for (i in 1 until terminatorPoints.size) {
                        path.lineTo(terminatorPoints[i].first.x, terminatorPoints[i].first.y)
                    }

                    // Connect to the last setting point
                    path.lineTo(terminatorPoints.last().second.x, terminatorPoints.last().second.y)

                    // Draw the setting side (going up)
                    for (i in terminatorPoints.size - 2 downTo 0) {
                        path.lineTo(terminatorPoints[i].second.x, terminatorPoints[i].second.y)
                    }

                    // Close the path to complete the bell shape
                    path.close()
                }

                // Create paths for the day and night sides
              val dayPath = Path()
                val nightPath = Path()

                // Determine if the sun is in the eastern or western hemisphere
                val sunLong = rev(RA * 15f - GMST0 * 15f - hourDecimal * 15f)
                val isSunInEasternHemisphere = sunLong < 180f

                /* 
               if (terminatorPoints.isNotEmpty()) {
                   if (isSunInEasternHemisphere) {
                       // Sun is in the eastern hemisphere, day is in the eastern hemisphere (right side)

                       // Day path (right side)
                       dayPath.moveTo(terminatorPoints.first().first.x, 0f) // Start at the top-right of the terminator
                       dayPath.lineTo(width, 0f) // Go to top-right corner
                       dayPath.lineTo(width, height) // Go to bottom-right corner
                       dayPath.lineTo(terminatorPoints.last().first.x, height) // Go to the bottom-right of the terminator

                       // Follow the terminator curve up (right side)
                       for (i in terminatorPoints.size - 2 downTo 0) {
                           dayPath.lineTo(terminatorPoints[i].first.x, terminatorPoints[i].first.y)
                       }
                       dayPath.close() // Close the path

                       // Night path (left side)
                       nightPath.moveTo(0f, 0f) // Start at top-left corner
                       nightPath.lineTo(terminatorPoints.first().second.x, 0f) // Go to the top-left of the terminator

                       // Follow the terminator curve down (left side)
                       for (i in 1 until terminatorPoints.size) {
                           nightPath.lineTo(terminatorPoints[i].second.x, terminatorPoints[i].second.y)
                       }
                       nightPath.lineTo(0f, height) // Go to bottom-left corner
                       nightPath.close() // Close the path
                   } else {
                       // Sun is in the western hemisphere, day is in the western hemisphere (left side)

                       // Day path (left side)
                       dayPath.moveTo(0f, 0f) // Start at top-left corner
                       dayPath.lineTo(terminatorPoints.first().second.x, 0f) // Go to the top-left of the terminator

                       // Follow the terminator curve down (left side)
                       for (i in 1 until terminatorPoints.size) {
                           dayPath.lineTo(terminatorPoints[i].second.x, terminatorPoints[i].second.y)
                       }
                       dayPath.lineTo(0f, height) // Go to bottom-left corner
                       dayPath.close() // Close the path

                       // Night path (right side)
                       nightPath.moveTo(terminatorPoints.first().first.x, 0f) // Start at the top-right of the terminator
                       nightPath.lineTo(width, 0f) // Go to top-right corner
                       nightPath.lineTo(width, height) // Go to bottom-right corner
                       nightPath.lineTo(terminatorPoints.last().first.x, height) // Go to the bottom-right of the terminator

                       // Follow the terminator curve up (right side)
                       for (i in terminatorPoints.size - 2 downTo 0) {
                           nightPath.lineTo(terminatorPoints[i].first.x, terminatorPoints[i].first.y)
                       }
                       nightPath.close() // Close the path
                   }
               } else {
                   // If no terminator points (e.g., polar day/night), create simple paths
                   if (isSunInEasternHemisphere) {
                       // Day is on the right side
                       dayPath.moveTo(width / 2, 0f)
                       dayPath.lineTo(width, 0f)
                       dayPath.lineTo(width, height)
                       dayPath.lineTo(width / 2, height)
                       dayPath.close()

                       nightPath.moveTo(0f, 0f)
                       nightPath.lineTo(width / 2, 0f)
                       nightPath.lineTo(width / 2, height)
                       nightPath.lineTo(0f, height)
                       nightPath.close()
                   } else {
                       // Day is on the left side
                       dayPath.moveTo(0f, 0f)
                       dayPath.lineTo(width / 2, 0f)
                       dayPath.lineTo(width / 2, height)
                       dayPath.lineTo(0f, height)
                       dayPath.close()

                       nightPath.moveTo(width / 2, 0f)
                       nightPath.lineTo(width, 0f)
                       nightPath.lineTo(width, height)
                       nightPath.lineTo(width / 2, height)
                       nightPath.close()
                   }
               }

               // No overlay for the day side to avoid blue tint

               // Draw a very subtle dark overlay for the night side
               drawPath(
                   path = nightPath,
                   color = Color(0xFF000033).copy(alpha = 0.9f) // Subtle dark blue for night
               )

             */

                // Draw the blur effect at the terminator
                // This is based on the plot function in earth.py
                // We'll create a gradient effect near the terminator line

                // For each pixel in the image (simplified to reduce computation)
                // Adjust step size based on screen width to ensure consistent visual quality across devices
                val stepSize = (width / 300).coerceAtLeast(1f).toInt() // Scale step size with screen width
                for (y in 0 until height.toInt() step stepSize) {
                    for (x in 0 until width.toInt() step stepSize) {
                        // Convert x,y to longitude, latitude
                        // For equirectangular projection, x maps linearly to longitude from -180° to 180°
                        // Apply the same offset as used for the terminator points (10% of screen width)
                        val xOffset = width * 0.16f
                        val adjustedX = x + xOffset // Shift right to move shaded area away from USA
                        val longitude = (adjustedX / width * 360f) - 180f
                        // y maps linearly to latitude from 90° (top) to -90° (bottom)
                        val latitude = 90f - (y / height * 180f)

                        // Calculate the sun's altitude at this point
                        val latRad = latitude * (PI / 180f).toFloat()

                        val SIDTIME = GMST0 + hourDecimal + longitude/15f
                        val HA = rev((SIDTIME - RA)) * 15f
                        val HArad = HA * (PI / 180f).toFloat()
                        val declRad = Decl * (PI / 180f).toFloat()

                        val xval = cos(HArad) * cos(declRad)
                        val yval = sin(HArad) * cos(declRad)
                        val zval = sin(declRad)

                        val xhor = xval * sin(latRad) - zval * cos(latRad)
                        val yhor = yval
                        val zhor = xval * cos(latRad) + zval * sin(latRad)

                        val altitude = atan2(zhor, sqrt(xhor*xhor + yhor*yhor)) * (180f / PI.toFloat())

                        // Apply the blur effect at the terminator
                        if (altitude > blur && phong) {
                            // Day side with Phong shading - no tint
                            // No drawing here to avoid blue tint
                        } else if (altitude < -blur) {
                            // Night side - very subtle dark tint
                            drawCircle(
                                color = Color(DarkNavy.toArgb()).copy(alpha = 0.13f),
                              //  color = Color(0xFF000033).copy(alpha = 0.05f), // Very subtle dark blue for night
                                radius = stepSize.toFloat(),
                                center = Offset(x.toFloat(), y.toFloat())
                            )
                        } else {
                            // Terminator region - very subtle blend
                            val alpha = (altitude + blur) / (blur * 2f)
                            // Only apply a very subtle dark tint in the terminator region
                            if (alpha < 0.5f) {
                                drawCircle(
                                    color = Color(DarkNavy.toArgb()).copy(alpha = 0.13f * (1f - alpha.coerceIn(0f, 1f))),
                                  //  color = Color(0xFF000033).copy(alpha = 0.03f * (1f - alpha.coerceIn(0f, 1f))),
                                    radius = stepSize.toFloat(),
                                    center = Offset(x.toFloat(), y.toFloat())
                                )
                            }
                            // No drawing for the day side of the terminator to avoid blue tint
                        }
                    }
                }



               /*
                // Draw the terminator curve
                // Scale stroke width based on screen width for consistent appearance across devices
                val strokeWidth = (width / 600).coerceIn(1f, 3f) // Min 1, max 3

                // Create a new path for the terminator line that shows the complete bell shape
                val terminatorLinePath = Path()

                if (terminatorPoints.isNotEmpty()) {
                    // Start with the first rising point
                    terminatorLinePath.moveTo(terminatorPoints.first().first.x, terminatorPoints.first().first.y)

                    // Draw the rising side (going down)
                    for (i in 1 until terminatorPoints.size) {
                        terminatorLinePath.lineTo(terminatorPoints[i].first.x, terminatorPoints[i].first.y)
                    }

                    // Connect to the last setting point
                    terminatorLinePath.lineTo(terminatorPoints.last().second.x, terminatorPoints.last().second.y)

                    // Draw the setting side (going up)
                    for (i in terminatorPoints.size - 2 downTo 0) {
                        terminatorLinePath.lineTo(terminatorPoints[i].second.x, terminatorPoints[i].second.y)
                    }

                    // Close the path to complete the bell shape
                    terminatorLinePath.close()

                    // Gold terminator line removed as per requirement
                }
*/
                /*
                // Draw sun indicator
                // Calculate the sun's position on the map
                // Use the same mapping logic as the terminator calculation for consistency
                val normalizedSunLong = if (sunLong > 180f) sunLong - 360f else sunLong
                val sunX = width * ((normalizedSunLong + 180f) / 360f)

                // Scale the sun radius based on screen width for consistent appearance across devices
                val sunRadius = (width / 120).coerceIn(5f, 15f) // Min 5, max 15
                drawCircle(
                    color = Color(0xFFFFD700), // Gold for sun
                    radius = sunRadius,
                    center = Offset(sunX, height * 0.1f) // Position sun near the top
                )
*/
                // Time text removed as per requirement
            }
        }
    }
}

// Helper function to normalize an angle to the range [0, 360)
private fun rev(x: Float): Float {
    var rv = x - (x / 360f).toInt() * 360f
    if (rv < 0) rv += 360f
    return rv
}

// drawTimeText function removed as it's no longer used
