package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for the WatchRemovalGestureSettingsCard component.
 * 
 * This test class demonstrates how to test a UI component in isolation
 * using the real component from the app.
 * 
 * The approach:
 * 1. Use the real WatchRemovalGestureSettingsCard component from the app
 * 2. Provide a MaterialTheme wrapper with appropriate color schemes
 * 3. Test different combinations of watch removal gesture settings in both light and dark themes
 */
class WatchRemovalGestureSettingsCardPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material3.Light.NoActionBar"
    )
    
    // Define color schemes for light and dark themes
    private val lightColors = lightColorScheme(
        primary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC6),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )
    
    private val darkColors = darkColorScheme(
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
    private fun ThemedContent(
        darkTheme: Boolean,
        content: @Composable () -> Unit
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColors else lightColors,
            content = content
        )
    }
    
    /**
     * Tests the WatchRemovalGestureSettingsCard in light theme with double tap removal gesture.
     * 
     * This verifies:
     * - The card appearance in light theme
     * - The gesture toggle is on
     * - The description text shows "Double tap to remove watch"
     */
    @Test
    fun testWatchRemovalGestureSettingsCard_lightTheme_doubleTap() {
        paparazzi.snapshot {
            Surface(color = Color(0xFFFAFAFA)) {
                ThemedContent(darkTheme = false) {
                    WatchRemovalGestureSettingsCard(
                        useDoubleTapForRemoval = true,
                        onRemovalGestureChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the WatchRemovalGestureSettingsCard in light theme with long press removal gesture.
     * 
     * This verifies:
     * - The card appearance in light theme
     * - The gesture toggle is off
     * - The description text shows "Long press to remove watch"
     */
    @Test
    fun testWatchRemovalGestureSettingsCard_lightTheme_longPress() {
        paparazzi.snapshot {
            Surface(color = Color(0xFFFAFAFA)) {
                ThemedContent(darkTheme = false) {
                    WatchRemovalGestureSettingsCard(
                        useDoubleTapForRemoval = false,
                        onRemovalGestureChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the WatchRemovalGestureSettingsCard in dark theme with double tap removal gesture.
     * 
     * This verifies:
     * - The card appearance in dark theme
     * - The gesture toggle is on
     * - The description text shows "Double tap to remove watch"
     * - Text and background colors are properly contrasted
     */
    @Test
    fun testWatchRemovalGestureSettingsCard_darkTheme_doubleTap() {
        paparazzi.snapshot {
            Surface(color = Color(0xFF121212)) {
                ThemedContent(darkTheme = true) {
                    WatchRemovalGestureSettingsCard(
                        useDoubleTapForRemoval = true,
                        onRemovalGestureChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the WatchRemovalGestureSettingsCard in dark theme with long press removal gesture.
     * 
     * This verifies:
     * - The card appearance in dark theme
     * - The gesture toggle is off
     * - The description text shows "Long press to remove watch"
     * - Text and background colors are properly contrasted
     */
    @Test
    fun testWatchRemovalGestureSettingsCard_darkTheme_longPress() {
        paparazzi.snapshot {
            Surface(color = Color(0xFF121212)) {
                ThemedContent(darkTheme = true) {
                    WatchRemovalGestureSettingsCard(
                        useDoubleTapForRemoval = false,
                        onRemovalGestureChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }


}