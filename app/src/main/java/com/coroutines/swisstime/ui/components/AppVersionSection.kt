package com.coroutines.swisstime.ui.components

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.update.AppUpdateManager
import com.coroutines.swisstime.utils.darken
import com.coroutines.swisstime.utils.getApplicationVersionInfo

/**
 * A composable that displays the app version information and provides a button to check for updates.
 * 
 * This component:
 * 1. Shows the current app version name and code
 * 2. Provides a button to check for updates
 * 3. Displays the status of the update check process
 * 4. Handles the update check process using the AppUpdateManager
 * 
 * The update check process is implemented using the AppUpdateManager class, which:
 * - Checks for available updates using the Google Play Core library
 * - Handles the update flow (immediate or flexible)
 * - Provides status updates through a StateFlow
 * - Manages lifecycle events
 * 
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun AppVersionSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Access shared max width for settings buttons
    val buttonsMaxWidthState = com.coroutines.swisstime.ui.components.LocalSettingsButtonsMaxWidth.current
    val density = androidx.compose.ui.platform.LocalDensity.current

    // Create an instance of AppUpdateManager with the current activity and coroutine scope
    // The AppUpdateManager needs an Activity to show the update dialog and a CoroutineScope
    // to launch coroutines for asynchronous operations
    val appUpdateManager = remember { AppUpdateManager(context as Activity, coroutineScope) }

    // State for tracking update status and displaying appropriate UI
    var isCheckingForUpdates by remember { mutableStateOf(false) }
    var updateStatusMessage by remember { mutableStateOf<String?>(null) }

    // Add lifecycle observer to handle lifecycle events
    // This ensures that the AppUpdateManager is properly initialized and cleaned up
    // when the composable enters and leaves the composition
    DisposableEffect(lifecycleOwner) {
        // Add the AppUpdateManager as a lifecycle observer
        lifecycleOwner.lifecycle.addObserver(appUpdateManager)

        // Clean up when the composable leaves the composition
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(appUpdateManager)
        }
    }

    // Collect update status from the AppUpdateManager's updateStatus flow
    // This allows us to react to changes in the update status and update the UI accordingly
    LaunchedEffect(appUpdateManager) {
        appUpdateManager.updateStatus.collect { status ->
            when (status) {
                // Update is being checked
                is AppUpdateManager.UpdateStatus.Checking -> {
                    isCheckingForUpdates = true
                    updateStatusMessage = "Checking for updates..."
                }

                // No update is required
                is AppUpdateManager.UpdateStatus.NotRequired -> {
                    isCheckingForUpdates = false
                    updateStatusMessage = "No updates available"
                }

                // Update has been downloaded and is ready to install
                is AppUpdateManager.UpdateStatus.Downloaded -> {
                    isCheckingForUpdates = false
                    updateStatusMessage = "Update downloaded. Ready to install."
                }

                // Update check failed
                is AppUpdateManager.UpdateStatus.Failed -> {
                    isCheckingForUpdates = false
                    updateStatusMessage = "Update check failed: ${status.reason}"
                }

                // Update is being downloaded
                is AppUpdateManager.UpdateStatus.Downloading -> {
                    isCheckingForUpdates = true
                    updateStatusMessage = "Downloading update: ${status.progress}%"
                }

                // Update flow has started
                is AppUpdateManager.UpdateStatus.Started -> {
                    isCheckingForUpdates = true
                    updateStatusMessage = "Update started"
                }

                // Handle other states if needed
                else -> {
                    // No action needed for other states
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("version_section_card"),
        colors = CardDefaults.cardColors(
            containerColor = DarkNavy.darken(0.3f)// MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "App Version",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            val (versionName, versionCode) = getApplicationVersionInfo(context)

            Text(
                text = "Version $versionName ($versionCode)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show update status message if available
            updateStatusMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Check for updates button with shared width handling
                val widthModifier = if (buttonsMaxWidthState.value > 0.dp) Modifier.width(buttonsMaxWidthState.value) else Modifier
                Button(
                    onClick = {
                        // Call the real appUpdateManager.checkForUpdate() method
                        // This will:
                        // 1. Check if an update is available from Google Play
                        // 2. Determine the appropriate update type (immediate or flexible)
                        // 3. Start the update flow if an update is available
                        // 4. Update the updateStatus flow with the current status
                        // The UI will be updated automatically based on the collected status
                        appUpdateManager.checkForUpdate()
                    },
                    modifier = Modifier.fillMaxWidth(0.6f),
                    // Disable the button while checking for updates to prevent multiple requests
                    enabled = !isCheckingForUpdates
                ) {
                    Text(text = "Check for Updates")
                }
              }
        }
    }
}

/*
 /* SwissTimeGradientButton(
                    text = "Check for Updates",
                    onClick = {
                        appUpdateManager.checkForUpdate()
                    },
                    modifier = widthModifier.then(
                        Modifier.onGloballyPositioned { coords ->
                            val measured = with(density) { coords.size.width.toDp() }
                            if (measured > buttonsMaxWidthState.value) {
                                buttonsMaxWidthState.value = measured
                            }
                        }
                    )


 */