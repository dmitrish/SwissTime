package com.coroutines.swisstime.ui.screens

import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.coroutines.swisstime.TimingLogger
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.data.TimeZoneInfo
import com.coroutines.swisstime.data.TimeZoneProvider
import com.coroutines.swisstime.data.rememberCurrentTimeZoneCalendar
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.ui.theme.DarkNavyTriadic
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
//import kotlinx.datetime.TimeZone
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
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

// Object to store the time for each watch
object WatchTimeStore {
    // Map to store the time for each watch
    val watchTimeMap = mutableMapOf<String, Calendar>()

    // Map to store the timezone for each watch
    val watchTimeZoneMap = mutableMapOf<String, TimeZone>()

    // Map to store the last update time for each watch
    private val lastUpdateTimeMap = mutableMapOf<String, Long>()

    // Function to update the time for a specific watch
    fun updateTime(watchName: String, timeZone: TimeZone) {
        val now = System.currentTimeMillis()
        val lastUpdateTime = lastUpdateTimeMap[watchName] ?: 0L

        // Get the existing time or create a new one
        val existingTime = watchTimeMap[watchName]
        val newTime = if (existingTime != null) {
            // Calculate how much time has passed since the last update
            val timeDiff = now - lastUpdateTime
            if (timeDiff > 0) {
                // Add the time difference to the existing time
                // Create a new Calendar instance to avoid modifying the existing one
                val updatedTime = Calendar.getInstance(timeZone)
                // Set the time to the existing time plus the time difference
                updatedTime.timeInMillis = existingTime.timeInMillis + timeDiff
                updatedTime
            } else {
                // If no time has passed, create a new Calendar with the same time
                val updatedTime = Calendar.getInstance(timeZone)
                updatedTime.timeInMillis = existingTime.timeInMillis
                updatedTime
            }
        } else {
            // If no existing time, create a new one
            Calendar.getInstance(timeZone)
        }

        // Update the maps with the new time
        watchTimeMap[watchName] = newTime
        watchTimeZoneMap[watchName] = timeZone
        lastUpdateTimeMap[watchName] = now

        // Debug log
        println("[DEBUG_LOG] Updated time for $watchName: ${newTime.time}, timezone: ${timeZone.id}")
    }

    // Function to update all watch times
    fun updateAllTimes() {
        watchTimeZoneMap.forEach { (watchName, timeZone) ->
            updateTime(watchName, timeZone)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedWorldMapScreen(
    onBackClick: () -> Unit,
    selectedWatch: WatchInfo? = null,
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
                title = { Text(selectedWatch?.name ?: "Optimized World Map") },
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

            // Display the selected watch or default to Piaget Altiplano
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Square aspect ratio for the watch
                contentAlignment = Alignment.Center
            ) {
                if (selectedWatch != null) {
                    // Special handling for Piaget Altiplano to pass the timezone
                    if (selectedWatch.name == "Piaget Altiplano") {
                        PiagetAltiplano(
                            modifier = Modifier.fillMaxSize(0.8f),
                            timeZone = TimeZone.getDefault()
                        )
                    } else {
                        // For other watches, use the default composable
                        selectedWatch.composable(Modifier.fillMaxSize(0.8f))
                    }
                } else {
                    PiagetAltiplano(
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
            }

            // Optimized World map with day/night visualization - bottom aligned
            OptimizedWorldMapWithDayNight(
                currentTime = currentTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f) // 2:1 aspect ratio for the world map
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedWatchScreen(
    onBackClick: () -> Unit,
    selectedWatch: WatchInfo? = null,
    modifier: Modifier = Modifier,
    watchViewModel: WatchViewModel? = null
) {
    // Early return if no watch is selected or no view model is provided
    if (selectedWatch == null || watchViewModel == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No watch selected",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    // Get the watch-specific time zone if available, otherwise use the global selected time zone
    // Use a key for collectAsState to ensure it's updated when the watch name changes
    val watchTimeZone = run {
        val watchTimeZoneFlow = remember(selectedWatch.name) {
            watchViewModel.getWatchTimeZone(selectedWatch.name)
        }
        watchTimeZoneFlow.collectAsState().value
    }

    // Register this watch's timezone in the WatchTimeStore
    // This ensures the global timer knows about this watch
    LaunchedEffect(selectedWatch.name, watchTimeZone) {
        println("[DEBUG_LOG] Registering watch ${selectedWatch.name} with timezone ${watchTimeZone.id}")
        WatchTimeStore.watchTimeZoneMap[selectedWatch.name] = watchTimeZone
        // Initialize the time if it doesn't exist yet
        if (WatchTimeStore.watchTimeMap[selectedWatch.name] == null) {
            println("[DEBUG_LOG] Initializing time for ${selectedWatch.name}")
            WatchTimeStore.updateTime(selectedWatch.name, watchTimeZone)
        } else {
            println("[DEBUG_LOG] Watch ${selectedWatch.name} already has time: ${WatchTimeStore.watchTimeMap[selectedWatch.name]?.time}")
        }
    }

    // Get the time for this watch from the WatchTimeStore
    // Use selectedWatch.name as a key to ensure it's re-initialized when the watch changes
    // This is crucial for ensuring each watch has its own independent time state
    var currentTime by remember(selectedWatch.name) {
        // Initialize with the stored time or a new time if not available
        // Clone the Calendar to ensure we're working with a fresh instance
        val storedTime = WatchTimeStore.watchTimeMap[selectedWatch.name]
        val initialTime = if (storedTime != null) {
            // Clone the stored time to avoid modifying it
            val clonedTime = Calendar.getInstance(watchTimeZone)
            clonedTime.timeInMillis = storedTime.timeInMillis
            clonedTime
        } else {
            // Create a new time if not available
            val newTime = Calendar.getInstance(watchTimeZone)
            // Register it in the WatchTimeStore
            WatchTimeStore.watchTimeMap[selectedWatch.name] = newTime
            newTime
        }
        println("[DEBUG_LOG] Initial time for ${selectedWatch.name}: ${initialTime.time}, timezone: ${watchTimeZone.id}")
        mutableStateOf(initialTime)
    }

    // Update the local state from WatchTimeStore every frame
    // This ensures the watch display is always in sync with the global time
    // Use a global key that is the same for all watches
    // This ensures that the LaunchedEffect is not re-executed when the watch comes into focus
    val globalKey = "globalTimeUpdateLoop"
    LaunchedEffect(globalKey) {
        println("[DEBUG_LOG] Starting time update loop for ${selectedWatch.name}")
        while (true) {
            val storeTime = WatchTimeStore.watchTimeMap[selectedWatch.name]
            if (storeTime != null) {
                // Always update the time, even if it seems the same
                // This ensures continuous ticking regardless of page visibility
                println("[DEBUG_LOG] Updating time for ${selectedWatch.name} from ${currentTime.time} to ${storeTime.time}")
                currentTime = storeTime.clone() as Calendar
            }
            delay(16) // Update every frame (approximately 60 FPS)
        }
    }

    // Get all available time zones
    val allTimeZones = watchViewModel.allTimeZones

    // Get the watch-specific time zone info if available, otherwise use the global selected time zone info
    // Use a key for collectAsState to ensure it's updated when the watch name changes
    val watchTimeZoneInfo = if (watchViewModel != null && selectedWatch != null) {
        val watchTimeZoneInfoFlow = remember(selectedWatch.name) {
            watchViewModel.getWatchTimeZoneInfo(selectedWatch.name)
        }
        watchTimeZoneInfoFlow.collectAsState().value
    } else {
        watchViewModel?.selectedTimeZone?.collectAsState()?.value
    }

    // State for dropdown expanded
    // Use selectedWatch.name as key to ensure it's re-initialized when the watch changes
    var expanded by remember(selectedWatch?.name) { mutableStateOf(false) }

    Scaffold(
        topBar = {},
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Flexible spacer to push content to the bottom (half of it)
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.5f)
            )

            // Display the time zone dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display the selected time zone as a clickable text
                    Text(
                        text = watchTimeZoneInfo?.displayName ?: "Select Time Zone",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(8.dp)
                    )

                    val targetZoneId: ZoneId = ZoneId.of(watchTimeZoneInfo?.id)
                    val currentZonedDateTime: ZonedDateTime = ZonedDateTime.now(targetZoneId)

                    Text(
                        text = currentZonedDateTime.format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(8.dp)
                    )

                    // Dropdown menu for time zone selection
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        containerColor = Color(DarkNavyTriadic.toArgb()) // Complementary color to DarkNavy
                    ) {
                        allTimeZones.forEach { timeZoneInfo ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = timeZoneInfo.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White // White text for better contrast
                                    ) 
                                },
                                onClick = {
                                    // Save the timezone for the specific watch
                                    watchViewModel.saveWatchTimeZone(selectedWatch.name, timeZoneInfo.id)
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.White,
                                    leadingIconColor = Color.White,
                                    trailingIconColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Remaining half of the spacer to position the time zone halfway between top and watch
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.5f)
            )

            // Display the selected watch or default to Piaget Altiplano
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Square aspect ratio for the watch
                contentAlignment = Alignment.Center
            ) {
                if (selectedWatch != null) {
                    // Use the unified TimeZoneAwareWatchFace composable for all watches
                    TimeZoneAwareWatchFace(
                        watchInfo = selectedWatch,
                        timeZone = watchTimeZone,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                } else {
                    // For the default case, create a dummy WatchInfo for Piaget Altiplano
                    val defaultWatch = WatchInfo(
                        name = "Piaget Altiplano",
                        description = "Default watch",
                        composable = { modifier -> PiagetAltiplano(modifier = modifier) }
                    )
                    TimeZoneAwareWatchFace(
                        watchInfo = defaultWatch,
                        timeZone = watchTimeZone,
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }
            }

            // Optimized World map with day/night visualization - bottom aligned
            OptimizedWorldMapWithDayNight(
                currentTime = currentTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f) // 2:1 aspect ratio for the world map
            )
        }
    }
}
// Data class to hold sun position calculations to avoid recalculating
data class SunPosition(
    val RA: Float,
    val Decl: Float,
    val GMST0: Float,
    val sunLong: Float
)

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

// Data class to hold all calculation results for caching
private data class CalculationResults(
    val terminatorPoints: List<Pair<Offset, Offset>>,
    val altitudeLookupTable: Array<Array<Float>>,
    val stepSize: Int
) {
    // Override equals and hashCode to properly compare arrays
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalculationResults

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

@Composable
fun OptimizedWorldMapWithDayNight(
    currentTime: Calendar,
    modifier: Modifier = Modifier
) {
    // Convert to GMT time for day/night visualization
    val gmtTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    gmtTime.timeInMillis = currentTime.timeInMillis
    // Constants from earth.py
    val blur = 4f  // blur angle for terminator
    val phong = true  // enable Phong shading

    // Extract time components once - only care about hour and 10-minute intervals for caching
    // This reduces recalculations to at most 144 times per day (24 hours * 6 ten-minute intervals)
    val hour = gmtTime.get(Calendar.HOUR_OF_DAY)
    val tenMinuteInterval = gmtTime.get(Calendar.MINUTE) / 10
    val minute = gmtTime.get(Calendar.MINUTE)
    val second = gmtTime.get(Calendar.SECOND)
    val hourDecimal = hour + minute / 60f + second / 3600f

    // Calculate sun position only when time changes significantly (every 10 minutes)
    // This is a major optimization as sun position calculation is expensive
    val cacheKey = "${hour}_${tenMinuteInterval}"
    val sunPosition by remember(cacheKey) {
        derivedStateOf { calculateSunPosition(gmtTime) }
    }

    // Cache all calculation results to avoid recalculating on every recomposition
    // Only recalculate when the time changes significantly (every 10 minutes)
    val calculationResults = remember(cacheKey) {
        mutableStateOf<CalculationResults?>(null)
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
            // World map
            Image(
                painter = painterResource(id = R.drawable.world),
                contentDescription = "Earth Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            // Canvas for drawing the terminator and night shading
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height

                // Get or calculate the results
                val results = calculationResults.value ?: run {
                    // Calculate the terminator curve points with optimized resolution
                    // Reduce the number of points for better performance
                    val numPoints = (height / 8).coerceAtLeast(45f).toInt()
                    val terminatorPoints = calculateTerminatorPoints(width, height, sunPosition, hourDecimal, numPoints)

                    // Optimize step size based on screen width for better performance
                    val stepSize = (width / 150).coerceAtLeast(2f).toInt()

                    // Pre-calculate altitude lookup table for better performance
                    val altitudeLookupTable = createAltitudeLookupTable(
                        width, height, sunPosition, hourDecimal, stepSize, terminatorPoints
                    )

                    // Create and cache the results
                    CalculationResults(
                        terminatorPoints = terminatorPoints,
                        altitudeLookupTable = altitudeLookupTable,
                        stepSize = stepSize
                    ).also {
                        calculationResults.value = it
                    }
                }

                // Draw the night side shading with optimized algorithm
                drawOptimizedNightShading(
                    width, height, results.stepSize, results.terminatorPoints, 
                    results.altitudeLookupTable, blur
                )

                // Log the end time after the screen is completely drawn
                TimingLogger.logEndTime()
            }
        }
    }
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

    // Calculate approximate terminator region bounds for optimization
    val terminatorRegion = calculateTerminatorRegion(terminatorPoints, width, height)

    // Process the lookup table
    for (y in 0 until height.toInt() step stepSize) {
        val rowIndex = y / stepSize
        if (rowIndex >= rows) continue

        for (x in 0 until width.toInt() step stepSize) {
            val colIndex = x / stepSize
            if (colIndex >= cols) continue

            // Always calculate the altitude for all points
            // This is more accurate and ensures proper shading
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

// Calculate the approximate terminator region bounds
private fun calculateTerminatorRegion(
    terminatorPoints: List<Pair<Offset, Offset>>,
    width: Float,
    height: Float
): TerminatorRegion {
    if (terminatorPoints.isEmpty()) {
        // Default bounds that cover the entire map
        return TerminatorRegion(0f, width, 0f, height)
    }

    // Find the min/max x coordinates for both rising and setting points
    var minRisingX = Float.MAX_VALUE
    var maxRisingX = Float.MIN_VALUE
    var minSettingX = Float.MAX_VALUE
    var maxSettingX = Float.MIN_VALUE

    // Find the min/max y coordinates
    var minY = Float.MAX_VALUE
    var maxY = Float.MIN_VALUE

    // Process all terminator points to find the bounding box
    for (point in terminatorPoints) {
        // Rising point (first)
        val risingX = point.first.x
        minRisingX = minOf(minRisingX, risingX)
        maxRisingX = maxOf(maxRisingX, risingX)

        // Setting point (second)
        val settingX = point.second.x
        minSettingX = minOf(minSettingX, settingX)
        maxSettingX = maxOf(maxSettingX, settingX)

        // Y coordinate (same for both points)
        val y = point.first.y
        minY = minOf(minY, y)
        maxY = maxOf(maxY, y)
    }

    // Add some padding to ensure we cover the entire terminator region
    val padding = width * 0.05f

    return TerminatorRegion(
        minRisingX - padding,
        maxSettingX + padding,
        minY - padding,
        maxY + padding
    )
}

// Data class to hold the terminator region bounds
data class TerminatorRegion(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
)

// Check if a point is in the terminator region
private fun isInTerminatorRegion(
    x: Float,
    y: Float,
    region: TerminatorRegion
): Boolean {
    return x >= region.minX && x <= region.maxX && y >= region.minY && y <= region.maxY
}

// Check if a point is on the night side
private fun isNightSide(
    x: Float,
    y: Float,
    terminatorPoints: List<Pair<Offset, Offset>>
): Boolean {
    if (terminatorPoints.isEmpty()) {
        // If no terminator points, assume half the map is night
        return false
    }

    // Find the closest terminator points to this y-coordinate
    val closestPoints = findClosestTerminatorPoints(y, terminatorPoints)

    if (closestPoints != null) {
        // The night side is between the rising and setting points
        // Rising point is the first point (eastern terminator)
        // Setting point is the second point (western terminator)
        val risingX = closestPoints.first.x
        val settingX = closestPoints.second.x

        // Check if the point is between the rising and setting points
        return x >= risingX && x <= settingX
    }

    return false
}

// Find the closest terminator points to a given y-coordinate
private fun findClosestTerminatorPoints(
    y: Float,
    terminatorPoints: List<Pair<Offset, Offset>>
): Pair<Offset, Offset>? {
    if (terminatorPoints.isEmpty()) {
        return null
    }

    // Find the closest point by y-coordinate
    var closestIndex = 0
    var minDistance = Float.MAX_VALUE

    for (i in terminatorPoints.indices) {
        val distance = abs(terminatorPoints[i].first.y - y)
        if (distance < minDistance) {
            minDistance = distance
            closestIndex = i
        }
    }

    return terminatorPoints[closestIndex]
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

/**
 * A unified composable for all watch faces that handles timezone-aware time updates.
 * This composable works for all watches, regardless of whether they directly accept a timeZone parameter or not.
 * It directly sets the default timezone for the thread before rendering the watch face, ensuring
 * that all Calendar.getInstance() calls use the correct timezone.
 */
@Composable
fun TimeZoneAwareWatchFace(
    watchInfo: WatchInfo,
    timeZone: TimeZone,
    modifier: Modifier = Modifier
) {
    // Use the watch name as a stable key
    val watchName = watchInfo.name

    // Register this watch in the WatchTimeStore to ensure it keeps ticking even when not visible
    LaunchedEffect(watchName, timeZone.id) {
        println("[DEBUG_LOG] Registering watch $watchName with timezone ${timeZone.id} in TimeZoneAwareWatchFace")
        WatchTimeStore.watchTimeZoneMap[watchName] = timeZone
        if (WatchTimeStore.watchTimeMap[watchName] == null) {
            WatchTimeStore.updateTime(watchName, timeZone)
        }
    }

    // Get the current time from the WatchTimeStore or create a new one
    // Use both watchName and timeZone.id as keys to ensure it's re-initialized when either changes
    var currentTime by remember(watchName, timeZone.id) {
        // Force update the WatchTimeStore with the new timezone
        WatchTimeStore.updateTime(watchName, timeZone)

        val storedTime = WatchTimeStore.watchTimeMap[watchName]
        val initialTime = if (storedTime != null) {
            storedTime.clone() as Calendar
        } else {
            Calendar.getInstance(timeZone)
        }
        println("[DEBUG_LOG] Initial time for $watchName: ${initialTime.time}, timezone: ${timeZone.id}")
        mutableStateOf(initialTime)
    }

    // Update the time every frame from the WatchTimeStore
    // This ensures the watch keeps ticking even when not visible
    // Use both watchName and timeZone.id as keys to ensure it's re-launched when either changes
    LaunchedEffect(watchName, timeZone.id) {
        println("[DEBUG_LOG] Starting time update loop for $watchName with timezone ${timeZone.id}")
        while (true) {
            val storeTime = WatchTimeStore.watchTimeMap[watchName]
            if (storeTime != null) {
                currentTime = storeTime.clone() as Calendar
            }
            delay(16) // Update every frame (approximately 60 FPS)
        }
    }

    // Save the original default timezone
    val originalTimeZone = remember { TimeZone.getDefault() }

    // Set the default timezone for the current thread
    // This affects all Calendar.getInstance() calls without a timezone parameter
    DisposableEffect(watchName, timeZone.id) {
        // Set the default timezone before rendering
        println("[DEBUG_LOG] Setting default timezone to ${timeZone.id} for watch $watchName")
        TimeZone.setDefault(timeZone)

        onDispose {
            // Restore the original default timezone when the composable is disposed
            println("[DEBUG_LOG] Restoring default timezone to ${originalTimeZone.id} for watch $watchName")
            TimeZone.setDefault(originalTimeZone)
        }
    }

    // Special handling for watch faces that accept a timezone parameter directly
    if (watchName == "Zenith El Primero") {
        // For ZenithElPrimero, pass the timezone directly
        ZenithElPrimero(
            modifier = modifier,
            timeZone = timeZone
        )
    } else if (watchName == "Piaget Altiplano") {
        // For PiagetAltiplano, pass the timezone directly
        PiagetAltiplano(
            modifier = modifier,
            timeZone = timeZone
        )
    } else {
        // For other watches, just call the composable directly
        // The default timezone has already been set, so they will use the correct timezone
        watchInfo.composable(modifier)
    }
}
