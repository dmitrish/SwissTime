package com.coroutines.swisstime.ui.screens
/*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.theme.DarkNavyTriadic
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.worldclock.common.model.WatchInfo
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

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
                    // For the default case, create a dummy WatchInfo for Chronomagus Regum
                    val defaultWatch = WatchInfo(
                        name = "Chronomagus Regum",
                        description = "Default watch",
                        composable = { modifier, timeZone -> ChronomagusRegum(modifier = modifier, timeZone = TimeZone.getDefault() )  }
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

 */