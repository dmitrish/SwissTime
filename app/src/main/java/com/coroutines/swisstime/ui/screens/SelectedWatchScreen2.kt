package com.coroutines.swisstime.ui.screens

import android.nfc.Tag
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupPositionProvider
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
    val TAG = "Performance:SelectedWatchScreen2"
    Log.d(TAG, "SelectedWatchScreen2:watchTimeZone:before}")
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

    val watchTimeZone = watchViewModel.selectedTimeZone.collectAsState().value


    val watchTimeZoneInfo = if (watchViewModel != null && selectedWatch != null) {
        val watchTimeZoneInfoFlow = remember(selectedWatch.name) {
            watchViewModel.getWatchTimeZoneInfo(selectedWatch.name)
        }
        watchTimeZoneInfoFlow.collectAsState().value
    } else {
        watchViewModel?.selectedTimeZone?.collectAsState()?.value
    }
    Log.d("TAG", "SelectedWatchScreen2:watchTimeZone:after}")
    Log.d("TAG", "SelectedWatchScreen2:allTimeZones:before}")
    // Get all available time zones
    val allTimeZones = remember { watchViewModel.allTimeZones}
    Log.d(TAG, "SelectedWatchScreen2:allTimeZones:after}")

    // State for dropdown expanded
    // Use selectedWatch.name as key to ensure it's re-initialized when the watch changes
    var expanded by remember(selectedWatch?.name) { mutableStateOf(false) }

    // State for confirmation dialog
    var showRemoveConfirmation by remember { mutableStateOf(false) }

    // State for dropdown menu position
    var rowSize by remember { mutableStateOf(Size.Zero) }

    // Get the current density for converting between px and dp
    val density = LocalDensity.current



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
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Top
                ) {
                    // Display the selected time zone as a clickable text with a dropdown icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(8.dp)
                            .onGloballyPositioned { coordinates ->
                                // Save the size of the Row to position the dropdown menu
                                rowSize = coordinates.size.toSize()
                            }
                    ) {
                        Text(
                            text = watchTimeZoneInfo?.displayName ?: "Select Time Zone",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    val targetZoneId: ZoneId = ZoneId.of(watchTimeZoneInfo?.id)

                  //  val targetZoneId: ZoneId = ZoneId.of("GMT")

                    // Create a state to hold the current time that will be updated every second
                    var currentTime by remember { mutableStateOf(ZonedDateTime.now(targetZoneId)) }

                    // Update the time every second
                    LaunchedEffect(targetZoneId) {
                        while(true) {
                            currentTime = ZonedDateTime.now(targetZoneId)
                            delay(1000) // Update every second
                        }
                    }

                    // Dropdown menu for time zone selection
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        // Use a much larger offset to position the dropdown menu
                        offset = DpOffset(x = 0.dp, y = 500.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Make it smaller in width
                            .fillMaxHeight(0.9f), // Make it 80% of the screen height
                        containerColor = Color(DarkNavyTriadic.toArgb()), // Complementary color to DarkNavy
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // Add rounded corners
                    ) {
                        // Sort timezones by their distance from GMT
                        val sortedTimeZones = remember(allTimeZones) {
                            allTimeZones.sortedBy { timeZoneInfo ->
                                // Get the raw offset in milliseconds from GMT
                                val offset = TimeZone.getTimeZone(timeZoneInfo.id).rawOffset
                                // Use absolute value to sort by distance from GMT
                                Math.abs(offset)
                            }
                        }

                        sortedTimeZones.forEach { timeZoneInfo ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = buildAnnotatedString {
                                            if (timeZoneInfo.displayName.isNotEmpty()) {
                                                // Apply bold style to the first letter
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(timeZoneInfo.displayName.substring(0, 1))
                                                }
                                                // Append the rest of the text with normal style
                                                append(timeZoneInfo.displayName.substring(1))
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White, // White text for better contrast
                                        modifier = Modifier.padding(start = 10.dp) // Add left padding of 10.dp
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

                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("H:mm:ss a")),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,

                        modifier = Modifier
                            .testTag("timeText")
                            .clickable { expanded = true }
                            .padding(8.dp)
                    )

                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("dd MMMM")),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(top = 8.dp, bottom = 16.dp)
                    )

                    // Reddish line that when tapped will remove the watch from selected watches
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .width(30.dp)
                            .height(5.dp)
                            .background(Color.Red.copy(alpha = 0.7f))
                            .clickable {
                                showRemoveConfirmation = true
                            }
                    )

                    // Confirmation dialog
                    if (showRemoveConfirmation) {
                        AlertDialog(
                            onDismissRequest = { showRemoveConfirmation = false },
                            title = { Text("Remove Watch") },
                            text = { Text("Are you sure you want to remove ${selectedWatch.name} from your selected watches?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        // Remove the watch from selected watches
                                        // First, get the current list of selected watches
                                        val currentWatches = watchViewModel.selectedWatches.value
                                        // Filter out the current watch
                                        val updatedWatches = currentWatches.filter { it.name != selectedWatch.name }
                                        // Clear all selected watches
                                        watchViewModel.clearAllSelectedWatches()
                                        // Add back all watches except the one to be removed
                                        updatedWatches.forEach { watch ->
                                            watchViewModel.saveSelectedWatch(watch)
                                        }
                                        showRemoveConfirmation = false
                                        onBackClick() // Navigate back after removing
                                    }
                                ) {
                                    Text("Remove")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showRemoveConfirmation = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
            }

            // Spacer between the time text and the watch
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.2f)
            )

          //  val currentTimeZone = watchViewModel.getWatchTimeZone(selectedWatch.name).collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f), // Square aspect ratio for the watch
                contentAlignment = Alignment.Center
            ) {
                //selectedWatch.composable(Modifier.fillMaxSize(0.8f), watchViewModel, currentTimeZone.value)
                TimeZoneAwareWatchFace2(
                    watchInfo = selectedWatch,
               //     timeZone = currentTimeZone.value,
                    viewModel = watchViewModel,
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
