package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coroutines.swisstime.ui.theme.ThemeMode
import com.coroutines.swisstime.viewmodel.ThemeViewModel
import com.coroutines.swisstime.viewmodel.WatchViewModel

/**
 * A card component that displays theme settings including theme selection and dark mode toggle.
 *
 * @param themeMode The current theme mode (DAY, NIGHT, or SYSTEM)
 * @param darkMode Whether dark mode is enabled
 * @param onThemeClick Callback for when the theme selection row is clicked
 * @param onDarkModeChange Callback for when the dark mode toggle is changed
 * @param modifier Optional modifier for the card
 */
@Composable
fun ThemeSettingsCard(
    themeMode: ThemeMode,
    darkMode: Boolean,
    onThemeClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when(themeMode) {
                            ThemeMode.DAY -> "Day Theme"
                            ThemeMode.NIGHT -> "Night Theme"
                            ThemeMode.SYSTEM -> "System Default"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Theme preview
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when(themeMode) {
                                ThemeMode.DAY -> Color(0xFF2F4F4F) // LightNavy
                                ThemeMode.NIGHT -> Color.Black
                                ThemeMode.SYSTEM -> if (darkMode) Color(0xFF2F4F4F) else Color(0xFF2F4F4F)
                            }
                        )
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Dark mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (darkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = "Dark Mode",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
        }
    }
}

/**
 * A card component that displays time format settings.
 *
 * @param useUsTimeFormat Whether to use US time format (12-hour with AM/PM) or 24-hour format
 * @param onTimeFormatChange Callback for when the time format toggle is changed
 * @param modifier Optional modifier for the card
 */
@Composable
fun TimeFormatSettingsCard(
    useUsTimeFormat: Boolean,
    onTimeFormatChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Time Format",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time format toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "US Time Format (AM/PM)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (useUsTimeFormat) "12-hour format with AM/PM" else "24-hour format",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Switch(
                    checked = useUsTimeFormat,
                    onCheckedChange = onTimeFormatChange
                )
            }
        }
    }
}

/**
 * A card component that displays watch removal gesture settings.
 *
 * @param useDoubleTapForRemoval Whether to use double tap (true) or long press (false) for watch removal
 * @param onRemovalGestureChange Callback for when the removal gesture toggle is changed
 * @param modifier Optional modifier for the card
 */
@Composable
fun WatchRemovalGestureSettingsCard(
    useDoubleTapForRemoval: Boolean,
    onRemovalGestureChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Watch Removal Gesture",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Watch removal gesture toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Use Double Tap",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (useDoubleTapForRemoval) "Double tap to remove watch" else "Long press to remove watch",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Switch(
                    checked = useDoubleTapForRemoval,
                    onCheckedChange = onRemovalGestureChange
                )
            }
        }
    }
}

/**
 * A dialog that allows the user to select a theme mode.
 *
 * @param themeMode The current theme mode (DAY, NIGHT, or SYSTEM)
 * @param onThemeModeChange Callback for when a theme mode is selected
 * @param onDismiss Callback for when the dialog is dismissed
 */
@Composable
fun ThemeSelectionDialog(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Theme",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Day Theme option
                ThemeOption(
                    title = "Day Theme",
                    description = "Default theme with navy background",
                    selected = themeMode == ThemeMode.DAY,
                    onClick = {
                        onThemeModeChange(ThemeMode.DAY)
                        onDismiss()
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Night Theme option
                ThemeOption(
                    title = "Night Theme",
                    description = "Dark theme with black background",
                    selected = themeMode == ThemeMode.NIGHT,
                    onClick = {
                        onThemeModeChange(ThemeMode.NIGHT)
                        onDismiss()
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // System Default option
                ThemeOption(
                    title = "System Default",
                    description = "Follow system dark mode settings",
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = {
                        onThemeModeChange(ThemeMode.SYSTEM)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

/**
 * The main settings screen that displays various settings options.
 * 
 * This screen is composed of several card components:
 * - ThemeSettingsCard: For theme and appearance settings
 * - TimeFormatSettingsCard: For time format settings
 * - WatchRemovalGestureSettingsCard: For watch removal gesture settings
 * 
 * It also includes a ThemeSelectionDialog that appears when the user clicks on the theme option.
 *
 * @param themeViewModel ViewModel for theme-related settings
 * @param watchViewModel ViewModel for watch-related settings
 * @param modifier Optional modifier for the screen
 */
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    watchViewModel: WatchViewModel,
    modifier: Modifier = Modifier
) {
    // Collect theme preferences from the ViewModel
    val themeMode by themeViewModel.themeMode.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()

    // Collect time format preference from the WatchViewModel
    val useUsTimeFormat by watchViewModel.useUsTimeFormat.collectAsState()

    // Collect watch removal gesture preference from the WatchViewModel
    val useDoubleTapForRemoval by watchViewModel.useDoubleTapForRemoval.collectAsState()

    // State for showing the theme selection dialog
    var showThemeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Divider()

        // Theme settings card
        ThemeSettingsCard(
            themeMode = themeMode,
            darkMode = darkMode,
            onThemeClick = { showThemeDialog = true },
            onDarkModeChange = { themeViewModel.saveDarkMode(it) }
        )

        // Time format settings card
        TimeFormatSettingsCard(
            useUsTimeFormat = useUsTimeFormat,
            onTimeFormatChange = { watchViewModel.saveTimeFormat(it) }
        )

        // Watch removal gesture settings card
        WatchRemovalGestureSettingsCard(
            useDoubleTapForRemoval = useDoubleTapForRemoval,
            onRemovalGestureChange = { watchViewModel.saveWatchRemovalGesture(it) }
        )
    }

    // Theme selection dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            themeMode = themeMode,
            onThemeModeChange = { themeViewModel.saveThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
