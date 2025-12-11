package com.coroutines.swisstime.ui.screens

import WatchRemovalGestureSettingsCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coroutines.swisstime.ui.components.ThemeSelectionDialog
import com.coroutines.swisstime.ui.components.ThemeSettingsCard
import com.coroutines.swisstime.ui.components.TimeFormatSettingsCard
import com.coroutines.swisstime.viewmodel.ThemeViewModel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.worldclock.common.theme.ThemeMode

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
 * This component has been refactored to accept the card components and dialog as parameters to make
 * it more flexible and testable. The refactoring uses function types as parameters with default
 * values that use the existing components. This approach allows the SettingsScreen to be more
 * flexible and testable while maintaining backward compatibility.
 *
 * @param themeViewModel ViewModel for theme-related settings
 * @param watchViewModel ViewModel for watch-related settings
 * @param modifier Optional modifier for the screen
 * @param themeSettingsCard Function that renders the theme settings card
 * @param timeFormatSettingsCard Function that renders the time format settings card
 * @param watchRemovalGestureSettingsCard Function that renders the watch removal gesture settings
 *   card
 * @param themeSelectionDialog Function that renders the theme selection dialog when showThemeDialog
 *   is true
 */
@Composable
fun SettingsScreen(
  themeViewModel: ThemeViewModel,
  watchViewModel: WatchViewModel,
  navController: NavController,
  modifier: Modifier = Modifier,
  themeSettingsCard:
    @Composable
    (
      themeMode: ThemeMode,
      darkMode: Boolean,
      onThemeClick: () -> Unit,
      onDarkModeChange: (Boolean) -> Unit
    ) -> Unit =
    { themeMode, darkMode, onThemeClick, onDarkModeChange ->
      ThemeSettingsCard(
        themeMode = themeMode,
        darkMode = darkMode,
        onThemeClick = onThemeClick,
        onDarkModeChange = onDarkModeChange
      )
    },
  timeFormatSettingsCard:
    @Composable
    (useUsTimeFormat: Boolean, onTimeFormatChange: (Boolean) -> Unit) -> Unit =
    { useUsTimeFormat, onTimeFormatChange ->
      TimeFormatSettingsCard(
        useUsTimeFormat = useUsTimeFormat,
        onTimeFormatChange = onTimeFormatChange
      )
    },
  watchRemovalGestureSettingsCard:
    @Composable
    (useDoubleTapForRemoval: Boolean, onRemovalGestureChange: (Boolean) -> Unit) -> Unit =
    { useDoubleTapForRemoval, onRemovalGestureChange ->
      WatchRemovalGestureSettingsCard(
        useDoubleTapForRemoval = useDoubleTapForRemoval,
        onRemovalGestureChange = onRemovalGestureChange
      )
    },
  themeSelectionDialog:
    @Composable
    (themeMode: ThemeMode, onThemeModeChange: (ThemeMode) -> Unit, onDismiss: () -> Unit) -> Unit =
    { themeMode, onThemeModeChange, onDismiss ->
      ThemeSelectionDialog(
        themeMode = themeMode,
        onThemeModeChange = onThemeModeChange,
        onDismiss = onDismiss
      )
    },
  wallpaperSelectionCard: @Composable (modifier: Modifier) -> Unit = { modifier -> }
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

  // Provide a shared max width state for Settings action buttons
  val buttonsMaxWidth = remember { androidx.compose.runtime.mutableStateOf(0.dp) }
  androidx.compose.runtime.CompositionLocalProvider(
    com.coroutines.swisstime.ui.components.LocalSettingsButtonsMaxWidth provides buttonsMaxWidth
  ) {
    Column(
      modifier =
        modifier
          .fillMaxSize()
          .verticalScroll(androidx.compose.foundation.rememberScrollState())
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
      themeSettingsCard(
        themeMode,
        darkMode,
        { showThemeDialog = true },
        { themeViewModel.saveDarkMode(it) }
      )

      // Time format settings card
      timeFormatSettingsCard(useUsTimeFormat, { watchViewModel.saveTimeFormat(it) })

      // Watch removal gesture settings card
      watchRemovalGestureSettingsCard(
        useDoubleTapForRemoval,
        { watchViewModel.saveWatchRemovalGesture(it) }
      )

      // wallpaperSelectionCard(Modifier.fillMaxWidth())
      WallpaperSelectionCard(
        modifier = Modifier.fillMaxWidth(),
        wallpaperPreferenceRepository =
          com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository(
            androidx.compose.ui.platform.LocalContext.current
          ),
        onNavigationRequested = { navController.navigate("wallpaper") }
      )
    }
  }

  // Theme selection dialog
  if (showThemeDialog) {
    themeSelectionDialog(
      themeMode,
      { themeViewModel.saveThemeMode(it) },
      { showThemeDialog = false }
    )
  }
}
