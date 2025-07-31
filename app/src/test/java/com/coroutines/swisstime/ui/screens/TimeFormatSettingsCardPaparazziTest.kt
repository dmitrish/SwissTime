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
import com.coroutines.swisstime.ui.components.TimeFormatSettingsCard
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for the TimeFormatSettingsCard component.
 * 
 * This test class demonstrates how to test a UI component in isolation
 * using the real component from the app.
 * 
 * The approach:
 * 1. Use the real TimeFormatSettingsCard component from the app
 * 2. Provide a MaterialTheme wrapper with appropriate color schemes
 * 3. Test different combinations of time format settings in both light and dark themes
 */
class TimeFormatSettingsCardPaparazziTest {

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
     * Tests the TimeFormatSettingsCard in light theme with US time format (12-hour with AM/PM).
     * 
     * This verifies:
     * - The card appearance in light theme
     * - The time format toggle is on
     * - The description text shows "12-hour format with AM/PM"
     */
    @Test
    fun testTimeFormatSettingsCard_lightTheme_usTimeFormat() {
        paparazzi.snapshot {
            Surface(color = Color(0xFFFAFAFA)) {
                ThemedContent(darkTheme = false) {
                    TimeFormatSettingsCard(
                        useUsTimeFormat = true,
                        onTimeFormatChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the TimeFormatSettingsCard in light theme with 24-hour format.
     * 
     * This verifies:
     * - The card appearance in light theme
     * - The time format toggle is off
     * - The description text shows "24-hour format"
     */
    @Test
    fun testTimeFormatSettingsCard_lightTheme_24HourFormat() {
        paparazzi.snapshot {
            Surface(color = Color(0xFFFAFAFA)) {
                ThemedContent(darkTheme = false) {
                    TimeFormatSettingsCard(
                        useUsTimeFormat = false,
                        onTimeFormatChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the TimeFormatSettingsCard in dark theme with US time format (12-hour with AM/PM).
     * 
     * This verifies:
     * - The card appearance in dark theme
     * - The time format toggle is on
     * - The description text shows "12-hour format with AM/PM"
     * - Text and background colors are properly contrasted
     */
    @Test
    fun testTimeFormatSettingsCard_darkTheme_usTimeFormat() {
        paparazzi.snapshot {
            Surface(color = Color(0xFF121212)) {
                ThemedContent(darkTheme = true) {
                    TimeFormatSettingsCard(
                        useUsTimeFormat = true,
                        onTimeFormatChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
    
    /**
     * Tests the TimeFormatSettingsCard in dark theme with 24-hour format.
     * 
     * This verifies:
     * - The card appearance in dark theme
     * - The time format toggle is off
     * - The description text shows "24-hour format"
     * - Text and background colors are properly contrasted
     */
    @Test
    fun testTimeFormatSettingsCard_darkTheme_24HourFormat() {
        paparazzi.snapshot {
            Surface(color = Color(0xFF121212)) {
                ThemedContent(darkTheme = true) {
                    TimeFormatSettingsCard(
                        useUsTimeFormat = false,
                        onTimeFormatChange = { /* No-op for testing */ }
                    )
                }
            }
        }
    }
}