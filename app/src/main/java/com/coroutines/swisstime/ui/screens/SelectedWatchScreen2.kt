package com.coroutines.swisstime.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.ui.theme.DarkNavyTriadic
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedWatchScreen2(
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

   // val watchTimeZone = watchViewModel.selectedTimeZone.collectAsState().value

    val watchTimeZoneInfo = if (watchViewModel != null && selectedWatch != null) {
        val watchTimeZoneInfoFlow = remember(selectedWatch.name) {
            watchViewModel.getWatchTimeZoneInfo(selectedWatch.name)
        }
        watchTimeZoneInfoFlow.collectAsState().value
    } else {
        watchViewModel?.selectedTimeZone?.collectAsState()?.value
    }
    // Get all available time zones
    val allTimeZones = watchViewModel.allTimeZones

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
            // Spacer to push content down from the top
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.3f)
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

                    Text(
                        text = currentZonedDateTime.format(DateTimeFormatter.ofPattern("H:mm:ss a")),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,

                        modifier = Modifier
                            .testTag("timeText")
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

            // Spacer between the time text and the watch
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.2f)
            )

            val currentTimeZone = watchViewModel.getWatchTimeZone(selectedWatch.name).collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Square aspect ratio for the watch
                contentAlignment = Alignment.Center
            ) {
                //selectedWatch.composable(Modifier.fillMaxSize(0.8f), watchViewModel, currentTimeZone.value)
                TimeZoneAwareWatchFace2(
                    watchInfo = selectedWatch,
                    timeZone = currentTimeZone.value,
                    watchViewModel,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

            // Spacer after the watch to balance with the top
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.3f)
            )
        }
    }

}
