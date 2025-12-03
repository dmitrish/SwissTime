package com.coroutines.swisstime.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.coroutines.swisstime.R
import com.coroutines.swisstime.utils.TimingLogger
import com.coroutines.swisstime.ui.theme.DarkNavy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

private const val TAG = "LazyWorldMap"
/*
/**
 * A lazy-loading world map with day/night visualization that doesn't block page transitions.
 * This component uses a two-phase rendering approach:
 * 1. Initially renders just the world map image without day/night shading
 * 2. After a delay (or when the page transition is complete), renders the full day/night shading
 */
@Composable
fun LazyWorldMapWithDayNight(
    currentTime: Calendar,
    modifier: Modifier = Modifier,
    isPageTransitioning: Boolean = false,
    onRenderComplete: () -> Unit = {}
) {
    // Convert to GMT time for day/night visualization
    val gmtTime = remember(currentTime) {
        val gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        gmt.timeInMillis = currentTime.timeInMillis
        gmt
    }

    // Constants for the terminator
    val blur = 4f  // blur angle for terminator

    // Extract time components once - only care about hour and 10-minute intervals for caching
    // This reduces recalculations to at most 144 times per day (24 hours * 6 ten-minute intervals)
    val hour = gmtTime.get(Calendar.HOUR_OF_DAY)
    val tenMinuteInterval = gmtTime.get(Calendar.MINUTE) / 10
    val minute = gmtTime.get(Calendar.MINUTE)
    val second = gmtTime.get(Calendar.SECOND)
    val hourDecimal = hour + minute / 60f + second / 3600f

    // Cache key for calculations - only changes every 10 minutes
    val cacheKey = "${hour}_${tenMinuteInterval}"

    // State to track if we should render the full map or just the placeholder
    var renderFullMap by remember { mutableStateOf(false) }

    // State to hold calculation results
    var calculationResults by remember { mutableStateOf<LazyCalculationResults?>(null) }

    // Calculate sun position only when time changes significantly (every 10 minutes)
    // This is a major optimization as sun position calculation is expensive
    val sunPosition: SunParams by remember(cacheKey) {
        derivedStateOf { calculateSunPositionNew(gmtTime) }
    }

    // Delay full rendering if we're in a page transition
    LaunchedEffect(isPageTransitioning, cacheKey) {
        if (isPageTransitioning) {
            // During page transition, don't render the full map
            renderFullMap = false
        } else {
            // After page transition, wait a bit then render the full map
            // This ensures the transition is smooth
            delay(100) // Short delay to ensure UI thread is free

            // Perform heavy calculations in background thread
            withContext(Dispatchers.Default) {
                Log.d(TAG, "Starting background calculations")
                val startTime = System.currentTimeMillis()

                // Calculate the terminator curve points with optimized resolution
                val numPoints = 45 // Fixed lower resolution for better performance
                val terminatorPoints = calculateTerminatorPoints(
                    width = 1000f, // Use fixed size for calculations
                    height = 500f,
                    sunPosition = sunPosition,
                    hourDecimal = hourDecimal,
                    numPoints = numPoints
                )

                // Use a larger step size for better performance
                val stepSize = 4

                // Pre-calculate altitude lookup table
                val altitudeLookupTable = createAltitudeLookupTable(
                    width = 1000f,
                    height = 500f,
                    sunPosition = sunPosition,
                    hourDecimal = hourDecimal,
                    stepSize = stepSize,
                    terminatorPoints = terminatorPoints
                )

                // Store calculation results
                calculationResults = LazyCalculationResults(
                    terminatorPoints = terminatorPoints,
                    altitudeLookupTable = altitudeLookupTable,
                    stepSize = stepSize
                )

                val endTime = System.currentTimeMillis()
                Log.d(TAG, "Background calculations completed in ${endTime - startTime}ms")
            }

            // Now enable full rendering
            renderFullMap = true
            onRenderComplete()
        }
    }

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
            // World map image - always render this immediately
            Image(
                painter = painterResource(id = R.drawable.world),
                contentDescription = "Earth Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            // Only render the day/night shading if we're not in a page transition
            // and calculations are complete
            if (renderFullMap && calculationResults != null) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val width = size.width
                    val height = size.height

                    // Draw the night side shading with optimized algorithm
                    calculationResults?.let { results ->
                        drawOptimizedNightShading(
                            width, height, results.stepSize, results.terminatorPoints,
                            results.altitudeLookupTable, blur
                        )
                    }

                    // Log completion
                    TimingLogger.logEndTime()
                }
            }
        }
    }
}

// Using SunPosition data class from OptimizedWorldMapScreen.kt

// Data class to hold all calculation results for caching
private data class LazyCalculationResults(
    val terminatorPoints: List<Pair<Offset, Offset>>,
    val altitudeLookupTable: Array<Array<Float>>,
    val stepSize: Int
) {
    // Override equals and hashCode to properly compare arrays
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LazyCalculationResults

        if (terminatorPoints != other.terminatorPoints) return false
        if (!altitudeLookupTable.contentDeepEquals(other.altitudeLookupTable)) return false
        if (stepSize != other.stepSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = terminatorPoints.hashCode()
        result = 31 * result + altitudeLookupTable.contentDeepHashCode()
        result = 31 * result + stepSize
        return result
    }
}

// Calculate sun position based on time
private fun calculateSunPosition(currentTime: Calendar): SunPosition {
    val year = currentTime.get(Calendar.YEAR)
    val month = currentTime.get(Calendar.MONTH) + 1 // Calendar months are 0-based
    val day = currentTime.get(Calendar.DAY_OF_MONTH)
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    val second = currentTime.get(Calendar.SECOND)
    val hourDecimal = hour + minute / 60f + second / 3600f

    // Calculate days since J2000 (January 1, 2000 12:00 UTC)
    val daysToJ2000 = 367 * year - 7 * (year + (month + 9) / 12) / 4 + 275 * month / 9 + day - 730530 + hourDecimal / 24f

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

    // Calculate sun longitude for hemisphere determination
    val sunLong = rev(RA * 15f - GMST0 * 15f - hourDecimal * 15f)

    return SunPosition(RA, Decl, GMST0, sunLong)
}

// Calculate terminator points with optimized resolution
private fun calculateTerminatorPoints(
    width: Float,
    height: Float,
    sunPosition: SunPosition,
    hourDecimal: Float,
    numPoints: Int
): List<Pair<Offset, Offset>> {
    // Pre-allocate the list with capacity to avoid reallocations
    val terminatorPoints = ArrayList<Pair<Offset, Offset>>(numPoints + 1)
    val yStep = height / numPoints

    // Pre-calculate constants to avoid repeated calculations
    val piOver180 = (PI / 180f).toFloat()
    val declRad = sunPosition.Decl * piOver180
    val tanDeclRad = tan(declRad)
    val ra15 = sunPosition.RA * 15f
    val gmst15 = sunPosition.GMST0 * 15f
    val hourDecimal15 = hourDecimal * 15f
    val xOffset = width * 0.1f // Shift left by 10% of the screen width

    // Correction factors
    val risingCorrectionFactor = 0.69f
    val settingCorrectionFactor = 0.39f

    // For each pixel row in the image (with adaptive step size)
    for (i in 0..numPoints) {
        val yPos = i * yStep

        // Convert y-coordinate to latitude (-90° to 90°, with 0° at the equator)
        val latitude = 90f - (yPos / height * 180f)
        val latRad = latitude * piOver180

        // Calculate the hour angle where the sun's altitude is 0 (the terminator)
        val cosTerm = -tanDeclRad * tan(latRad)

        // Check if there's a terminator at this latitude
        if (abs(cosTerm) <= 1.0f) {
            // Calculate the hour angle at the terminator (both rising and setting points)
            val HAterm = acos(cosTerm)

            // Convert hour angle to longitude for rising point (eastern terminator)
            val longTermRising = rev(ra15 - HAterm * (180f / PI.toFloat()) * risingCorrectionFactor - gmst15 - hourDecimal15)

            // Convert hour angle to longitude for setting point (western terminator)
            val longTermSetting = rev(ra15 + HAterm * (180f / PI.toFloat()) * settingCorrectionFactor - gmst15 - hourDecimal15)

            // Map longitudes to x-coordinates
            // First, normalize longitudes to -180° to 180° range
            val normalizedLongRising = if (longTermRising > 180f) longTermRising - 360f else longTermRising
            val normalizedLongSetting = if (longTermSetting > 180f) longTermSetting - 360f else longTermSetting

            // Then map to x-coordinates (0 to width)
            val xRising = width * ((normalizedLongRising + 180f) / 360f)
            val xSetting = width * ((normalizedLongSetting + 180f) / 360f)

            terminatorPoints.add(Pair(Offset(xRising - xOffset, yPos), Offset(xSetting - xOffset, yPos)))
        }
    }

    return terminatorPoints
}

// Create a pre-calculated altitude lookup table for better performance
private fun createAltitudeLookupTable(
    width: Float,
    height: Float,
    sunPosition: SunPosition,
    hourDecimal: Float,
    stepSize: Int,
    terminatorPoints: List<Pair<Offset, Offset>>
): Array<Array<Float>> {
    // Calculate dimensions of the lookup table
    val rows = (height.toInt() / stepSize) + 1
    val cols = (width.toInt() / stepSize) + 1
    val table = Array(rows) { Array(cols) { 0f } }

    // Pre-calculate constants to avoid repeated calculations
    val xOffset = width * 0.16f // Use the same offset as in CustomWorldMapScreen.kt
    val piOver180 = (PI / 180f).toFloat()
    val cosDeclRad = cos(sunPosition.Decl * piOver180)
    val sinDeclRad = sin(sunPosition.Decl * piOver180)

    // Process the lookup table
    for (y in 0 until height.toInt() step stepSize) {
        val rowIndex = y / stepSize
        if (rowIndex >= rows) continue

        for (x in 0 until width.toInt() step stepSize) {
            val colIndex = x / stepSize
            if (colIndex >= cols) continue

            // Convert x,y to longitude, latitude
            val adjustedX = x + xOffset // Shift right to move shaded area away from USA
            val longitude = (adjustedX / width * 360f) - 180f
            val latitude = 90f - (y / height * 180f)

            // Calculate the sun's altitude at this point
            val latRad = latitude * piOver180
            val sinLatRad = sin(latRad)
            val cosLatRad = cos(latRad)

            val SIDTIME = sunPosition.GMST0 + hourDecimal + longitude/15f
            val HA = rev((SIDTIME - sunPosition.RA)) * 15f
            val HArad = HA * piOver180

            // Optimized calculation with fewer trig operations
            val cosHArad = cos(HArad)
            val sinHArad = sin(HArad)

            val xval = cosHArad * cosDeclRad
            val yval = sinHArad * cosDeclRad
            val zval = sinDeclRad

            val xhor = xval * sinLatRad - zval * cosLatRad
            val yhor = yval
            val zhor = xval * cosLatRad + zval * sinLatRad

            val altitude = atan2(zhor, sqrt(xhor*xhor + yhor*yhor)) * (180f / PI.toFloat())

            table[rowIndex][colIndex] = altitude
        }
    }

    return table
}

// Optimized drawing of night shading
private fun DrawScope.drawOptimizedNightShading(
    width: Float,
    height: Float,
    stepSize: Int,
    terminatorPoints: List<Pair<Offset, Offset>>,
    altitudeLookupTable: Array<Array<Float>>,
    blur: Float
) {
    // Pre-calculate the colors for different regions to avoid creating new Color objects in the loop
    val nightColor = Color(DarkNavy.toArgb()).copy(alpha = 0.5f) // Further increased alpha for better visibility

    // Draw the night side based on the altitude lookup table
    // This is the most accurate way to determine the night side
    for (y in 0 until height.toInt() step stepSize) {
        val rowIndex = y / stepSize
        if (rowIndex >= altitudeLookupTable.size) continue

        for (x in 0 until width.toInt() step stepSize) {
            val colIndex = x / stepSize
            if (colIndex >= altitudeLookupTable[rowIndex].size) continue

            // Get the pre-calculated altitude from the lookup table
            val altitude = altitudeLookupTable[rowIndex][colIndex]

            // Fast path for night side
            if (altitude < -blur) {
                // Night side - dark tint
                drawRect(
                    color = nightColor,
                    topLeft = Offset(x.toFloat(), y.toFloat()),
                    size = Size(stepSize.toFloat(), stepSize.toFloat())
                )
            } 
            // Only do the more complex calculation for terminator region
            else if (altitude <= blur) {
                // Terminator region - blend
                val alpha = (altitude + blur) / (blur * 2f)
                // Only apply a dark tint in the terminator region on the night side
                if (alpha < 0.5f) {
                    // Calculate the alpha for this specific point
                    val blendAlpha = 0.5f * (1f - alpha.coerceIn(0f, 1f))
                    drawRect(
                        color = Color(DarkNavy.toArgb()).copy(alpha = blendAlpha),
                        topLeft = Offset(x.toFloat(), y.toFloat()),
                        size = Size(stepSize.toFloat(), stepSize.toFloat())
                    )
                }
            }
            // Day side - no drawing needed
        }
    }
}

// Helper function to normalize an angle to the range [0, 360)
private fun rev(x: Float): Float {
    var rv = x - (x / 360f).toInt() * 360f
    if (rv < 0) rv += 360f
    return rv
}
*/