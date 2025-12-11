package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.coroutines.swisstime.ui.components.ThemeSettingsCard
import com.coroutines.worldclock.common.theme.ThemeMode
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for the ThemeSettingsCard component.
 *
 * This test class demonstrates how to test a UI component in isolation using the real component
 * from the app.
 *
 * The approach:
 * 1. Use the real ThemeSettingsCard component from the app
 * 2. Provide a MaterialTheme wrapper with appropriate color schemes
 * 3. Test different combinations of theme mode and dark mode in both light and dark themes
 */
class ThemeSettingsCardPaparazziTest {

  @get:Rule
  val paparazzi =
    Paparazzi(
      deviceConfig = DeviceConfig.PIXEL_5,
      theme = "android:Theme.Material3.Light.NoActionBar"
    )

  // Define color schemes for light and dark themes
  private val lightColors =
    lightColorScheme(
      primary = Color(0xFF6200EE),
      secondary = Color(0xFF03DAC6),
      background = Color.White,
      surface = Color.White,
      onPrimary = Color.White,
      onSecondary = Color.Black,
      onBackground = Color.Black,
      onSurface = Color.Black
    )

  private val darkColors =
    darkColorScheme(
      primary = Color(0xFFBB86FC),
      secondary = Color(0xFF03DAC6),
      background = Color(0xFF121212),
      surface = Color(0xFF2D2D2D),
      onPrimary = Color.Black,
      onSecondary = Color.Black,
      onBackground = Color.White,
      onSurface = Color.White
    )

  /**
   * A wrapper composable that provides a MaterialTheme with the appropriate color scheme.
   *
   * @param darkTheme Whether to use the dark theme color scheme
   * @param content The content to be rendered within the MaterialTheme
   */
  @Composable
  private fun ThemedContent(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) darkColors else lightColors, content = content)
  }

  /**
   * Tests the ThemeSettingsCard in light theme with DAY theme mode and dark mode disabled.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The theme text shows "Day Theme"
   * - The theme preview shows the light navy color
   * - The dark mode toggle is off
   */
  @Test
  fun testThemeSettingsCard_lightTheme_dayMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          ThemeSettingsCard(
            themeMode = ThemeMode.DAY,
            darkMode = false,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in light theme with NIGHT theme mode and dark mode disabled.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The theme text shows "Night Theme"
   * - The theme preview shows the black color
   * - The dark mode toggle is off
   */
  @Test
  fun testThemeSettingsCard_lightTheme_nightMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          ThemeSettingsCard(
            themeMode = ThemeMode.NIGHT,
            darkMode = false,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in light theme with SYSTEM theme mode and dark mode disabled.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The theme text shows "System Default"
   * - The theme preview shows the appropriate color
   * - The dark mode toggle is off
   */
  @Test
  fun testThemeSettingsCard_lightTheme_systemMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          ThemeSettingsCard(
            themeMode = ThemeMode.SYSTEM,
            darkMode = false,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in light theme with dark mode enabled.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The dark mode toggle is on
   * - The dark mode icon is shown
   */
  @Test
  fun testThemeSettingsCard_lightTheme_darkModeEnabled() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          ThemeSettingsCard(
            themeMode = ThemeMode.DAY,
            darkMode = true,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in dark theme with DAY theme mode.
   *
   * This verifies:
   * - The card appearance in dark theme
   * - The theme text shows "Day Theme"
   * - The theme preview shows the light navy color
   * - Text and background colors are properly contrasted
   */
  @Test
  fun testThemeSettingsCard_darkTheme_dayMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFF121212)) {
        ThemedContent(darkTheme = true) {
          ThemeSettingsCard(
            themeMode = ThemeMode.DAY,
            darkMode = true,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in dark theme with NIGHT theme mode.
   *
   * This verifies:
   * - The card appearance in dark theme
   * - The theme text shows "Night Theme"
   * - The theme preview shows the black color
   * - Text and background colors are properly contrasted
   */
  @Test
  fun testThemeSettingsCard_darkTheme_nightMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFF121212)) {
        ThemedContent(darkTheme = true) {
          ThemeSettingsCard(
            themeMode = ThemeMode.NIGHT,
            darkMode = true,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }

  /**
   * Tests the ThemeSettingsCard in dark theme with SYSTEM theme mode.
   *
   * This verifies:
   * - The card appearance in dark theme
   * - The theme text shows "System Default"
   * - The theme preview shows the appropriate color
   * - Text and background colors are properly contrasted
   */
  @Test
  fun testThemeSettingsCard_darkTheme_systemMode() {
    paparazzi.snapshot {
      Surface(color = Color(0xFF121212)) {
        ThemedContent(darkTheme = true) {
          ThemeSettingsCard(
            themeMode = ThemeMode.SYSTEM,
            darkMode = true,
            onThemeClick = { /* No-op for testing */},
            onDarkModeChange = { /* No-op for testing */}
          )
        }
      }
    }
  }
}
