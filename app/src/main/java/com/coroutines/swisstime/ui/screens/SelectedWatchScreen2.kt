package com.coroutines.swisstime.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.ui.theme.DarkNavyTriadic
import com.coroutines.swisstime.viewmodel.WatchViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedWatchScreen2(
    onBackClick: () -> Unit,
    selectedWatch: WatchInfo? = null,
    modifier: Modifier = Modifier,
    watchViewModel: WatchViewModel? = null,
    isPageTransitioning: Boolean = false
) {
    val TAG = "Performance:SelectedWatchScreen2"
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

    // Get the time zone info for this watch - use remember to avoid recomposition
    // This is more efficient than getting the selectedTimeZone
    val watchTimeZoneInfo = if (watchViewModel != null && selectedWatch != null) {
        // Use remember to avoid recreating the flow on each recomposition
        val watchTimeZoneInfoFlow = remember(selectedWatch.name) {
            Log.d(TAG, "Creating watchTimeZoneInfoFlow for ${selectedWatch.name}")
            watchViewModel.getWatchTimeZoneInfo(selectedWatch.name)
        }
        // Collect the flow as state
        watchTimeZoneInfoFlow.collectAsState().value
    } else {
        // Fallback to the selected time zone if no watch is selected
        watchViewModel?.selectedTimeZone?.collectAsState()?.value
    }

    // Log completion of time zone info retrieval
    Log.d(TAG, "Retrieved time zone info: ${watchTimeZoneInfo?.id}")

    // State for dropdown expanded
    // Use selectedWatch.name as key to ensure it's re-initialized when the watch changes
    var expanded by remember(selectedWatch?.name) { mutableStateOf(false) }

    // State for confirmation dialog
    var showRemoveConfirmation by remember { mutableStateOf(false) }

    // State for dropdown menu position
    var rowSize by remember { mutableStateOf(Size.Zero) }

    // Get the current density for converting between px and dp
    val density = LocalDensity.current

    // Get the haptic feedback instance
    val haptic = LocalHapticFeedback.current

    // Get the context for vibration
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Get the watch removal gesture preference
    val useDoubleTapForRemoval = watchViewModel?.useDoubleTapForRemoval?.collectAsState()?.value ?: false



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

            // Display the time zone dropdown
            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // White X icon at the top right that when tapped will remove the watch from selected watches
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, end = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove Watch",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                showRemoveConfirmation = true
                            }
                    )
                } */

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
                           // .padding(top = 30.dp)
                            .onGloballyPositioned { coordinates ->
                                // Save the size of the Row to position the dropdown menu
                                rowSize = coordinates.size.toSize()
                            }
                    ) {
                        // X icon on the left of the timezone dropdown
                       /* Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove Watch",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 8.dp)
                                .clickable(onClick = {
                                    showRemoveConfirmation = true
                                })
                        ) */

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

                    // Create ZoneId only once and remember it - avoid logging during page transitions
                    val targetZoneId = remember(watchTimeZoneInfo?.id) {
                        ZoneId.of(watchTimeZoneInfo?.id ?: "GMT")
                    }

                    // Create a state to hold the current time that will be updated every second
                    var currentTime by remember { mutableStateOf(ZonedDateTime.now(targetZoneId)) }

                    // Get the time format preference
                    val useUsTimeFormat by watchViewModel.useUsTimeFormat.collectAsState()

                    // Update the time every second - use targetZoneId as key to restart when it changes
                    // Use a key that includes the watch name to ensure proper recomposition
                    LaunchedEffect(targetZoneId, selectedWatch?.name) {
                        // Delay the first update to reduce work during page transition
                        delay(100) // Small delay to prioritize rendering

                        while(true) {
                            currentTime = ZonedDateTime.now(targetZoneId)
                            delay(1000) // Update every second
                        }
                    }

                    // Dropdown menu for time zone selection
                    if (expanded) {
                        // Only create the dropdown menu when it's actually expanded
                        // This defers the expensive operation until it's needed
                        DropdownMenu(
                            expanded = true, // Always true since we only create it when expanded
                            onDismissRequest = { expanded = false },
                            // Use a much larger offset to position the dropdown menu
                            offset = DpOffset(x = 0.dp, y = 500.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Make it smaller in width
                                .fillMaxHeight(0.9f), // Make it 80% of the screen height
                            containerColor = Color(DarkNavyTriadic.toArgb()), // Complementary color to DarkNavy
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // Add rounded corners
                        ) {
                            // Get pre-sorted time zones from the view model - use remember to cache the result
                            // This is now only done when the dropdown is expanded, not during page transitions
                            val sortedTimeZones = remember { 
                                watchViewModel.getSortedTimeZones() 
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
                                        // Trigger vibration feedback
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                        } else {
                                            // Deprecated in API 26
                                            @Suppress("DEPRECATION")
                                            vibrator.vibrate(50)
                                        }

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

                    Text(
                        text = currentTime.format(
                            DateTimeFormatter.ofPattern(
                                if (useUsTimeFormat) "h:mm:ss a" else "HH:mm:ss"
                            )
                        ),
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

                    // The reddish line has been moved to the left of the timezone dropdown

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
                    .aspectRatio(1f) // Square aspect ratio for the watch
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                if (!useDoubleTapForRemoval) {
                                    // Trigger vibration feedback
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        // Deprecated in API 26
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(50)
                                    }
                                    // Show confirmation dialog
                                    showRemoveConfirmation = true
                                }
                            },
                            onDoubleTap = {
                                if (useDoubleTapForRemoval) {
                                    // Trigger vibration feedback
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        // Deprecated in API 26
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(50)
                                    }
                                    // Show confirmation dialog
                                    showRemoveConfirmation = true
                                }
                            }
                        )
                    },
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

            // Spacer after the watch (reduced weight to make room for the world map)
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.1f)
            )

            // Current time for the world map
            val currentTime = remember { Calendar.getInstance() }

            // Update time every second
            LaunchedEffect(key1 = true) {
                while (true) {
                    currentTime.timeInMillis = System.currentTimeMillis()
                    delay(1000) // Update every second
                }
            }


            // Small spacer at the bottom
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.weight(0.05f)
            )
        }
    }
}
