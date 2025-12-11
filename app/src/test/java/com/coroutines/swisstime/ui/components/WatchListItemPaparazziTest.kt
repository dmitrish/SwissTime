package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.coroutines.worldclock.common.model.WatchInfo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for the WatchListItem component.
 *
 * This test class demonstrates how to test a UI component in isolation using the real component
 * from the app.
 *
 * The approach:
 * 1. Use the real WatchListItem component from the app
 * 2. Create a real WatchInfo object with a simple watch face composable
 * 3. Test the component in both light and dark themes and with different states
 */
class WatchListItemPaparazziTest {

  @get:Rule
  val paparazzi =
    Paparazzi(
      deviceConfig = DeviceConfig.PIXEL_5,
      theme = "android:Theme.Material3.Light.NoActionBar"
    )

  // Test data
  private val testWatchName = "Omega Seamaster"
  private val testWatchDescription =
    "A classic dive watch with a blue dial and wave pattern. " +
      "Water resistant to 300 meters with a co-axial escapement movement."

  // Create a real WatchInfo object with a simple watch face composable
  private lateinit var testWatchInfo: WatchInfo

  @Before
  fun setup() {
    // Create a real WatchInfo object with a simple watch face composable
    testWatchInfo =
      WatchInfo(
        name = testWatchName,
        description = testWatchDescription,
        composable = { modifier, _ ->
          // Simple watch face composable for testing
          Canvas(modifier = modifier.background(Color(0xFF0A4D8C))) {
            // Draw watch face
            drawCircle(color = Color.White, radius = size.minDimension / 2.2f)
            // Draw hour and minute hands
            drawLine(
              color = Color.Black,
              start = center,
              end = center.copy(y = center.y - size.height / 3),
              strokeWidth = 4f
            )
            drawLine(
              color = Color.Black,
              start = center,
              end = center.copy(x = center.x + size.width / 4),
              strokeWidth = 3f
            )
          }
        }
      )
  }

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
   * Tests the WatchListItem in light theme with isSelectedForWidget = false.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The watch face is displayed correctly
   * - The watch name and description are displayed correctly
   * - The "Add to widget" icon is displayed
   */
  @Test
  fun testWatchListItem_lightTheme_notSelected() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          WatchListItem(
            watch = testWatchInfo,
            onClick = {},
            onTitleClick = {},
            isSelectedForWidget = false,
            modifier = Modifier
          )
        }
      }
    }
  }

  /**
   * Tests the WatchListItem in light theme with isSelectedForWidget = true.
   *
   * This verifies:
   * - The card appearance in light theme
   * - The watch face is displayed correctly
   * - The watch name and description are displayed correctly
   * - The "Selected for widget" checkmark icon is displayed
   */
  @Test
  fun testWatchListItem_lightTheme_selected() {
    paparazzi.snapshot {
      Surface(color = Color(0xFFFAFAFA)) {
        ThemedContent(darkTheme = false) {
          WatchListItem(
            watch = testWatchInfo,
            onClick = {},
            onTitleClick = {},
            isSelectedForWidget = true,
            modifier = Modifier
          )
        }
      }
    }
  }

  /**
   * Tests the WatchListItem in dark theme with isSelectedForWidget = false.
   *
   * This verifies:
   * - The card appearance in dark theme
   * - The watch face is displayed correctly
   * - The watch name and description are displayed correctly
   * - The "Add to widget" icon is displayed
   * - Text and background colors are properly contrasted
   */
  @Test
  fun testWatchListItem_darkTheme_notSelected() {
    paparazzi.snapshot {
      Surface(color = Color(0xFF121212)) {
        ThemedContent(darkTheme = true) {
          WatchListItem(
            watch = testWatchInfo,
            onClick = {},
            onTitleClick = {},
            isSelectedForWidget = false,
            modifier = Modifier
          )
        }
      }
    }
  }

  /**
   * Tests the WatchListItem in dark theme with isSelectedForWidget = true.
   *
   * This verifies:
   * - The card appearance in dark theme
   * - The watch face is displayed correctly
   * - The watch name and description are displayed correctly
   * - The "Selected for widget" checkmark icon is displayed
   * - Text and background colors are properly contrasted
   */
  @Test
  fun testWatchListItem_darkTheme_selected() {
    paparazzi.snapshot {
      Surface(color = Color(0xFF121212)) {
        ThemedContent(darkTheme = true) {
          WatchListItem(
            watch = testWatchInfo,
            onClick = {},
            onTitleClick = {},
            isSelectedForWidget = true,
            modifier = Modifier
          )
        }
      }
    }
  }
}
