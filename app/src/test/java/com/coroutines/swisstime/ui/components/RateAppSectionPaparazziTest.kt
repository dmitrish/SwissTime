package com.coroutines.swisstime.ui.components

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.coroutines.swisstime.utils.AppReviewManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for the RateAppSection component.
 * 
 * This test class demonstrates how to test a UI component in isolation
 * using the real component from the app, with mockK to mock Android dependencies.
 * 
 * The approach:
 * 1. Use the real RateAppSection component from the app
 * 2. Use mockK to mock Android dependencies (Context and AppReviewManager)
 * 3. Use CompositionLocalProvider to inject the mock Context
 * 4. Test the component in both light and dark themes
 */
class RateAppSectionPaparazziTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material3.Light.NoActionBar"
    )
    
    // Mock objects
    private lateinit var mockContext: Context
    
    // Test data
    private val testPackageName = "com.coroutines.swisstime"
    
    @Before
    fun setup() {
        // Create a mock Context
        mockContext = mockk<Context>().apply {
            every { packageName } returns testPackageName
        }
        
        // Mock AppReviewManager constructor and methods
        mockkConstructor(AppReviewManager::class)
        every { anyConstructed<AppReviewManager>().requestReview() } returns Unit
    }
    
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
     * Tests the RateAppSection in light theme.
     * 
     * This verifies:
     * - The card appearance in light theme
     * - The "Rate the App" text is displayed correctly
     * - The description text is displayed correctly
     * - The "Rate the App" button is displayed and enabled
     * 
     * This test uses mockK to mock Android dependencies:
     * - Context is mocked to provide a package name
     * - AppReviewManager is mocked to do nothing when requestReview is called
     * 
     * CompositionLocalProvider is used to inject the mock Context into the composable.
     */
    @Test
    fun testRateAppSection_lightTheme() {
        paparazzi.snapshot {
            Surface(color = Color(0xFFFAFAFA)) {
                ThemedContent(darkTheme = false) {
                    // Inject the mock Context into the composable
                    CompositionLocalProvider(LocalContext provides mockContext) {
                        RateAppSection(
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Tests the RateAppSection in dark theme.
     * 
     * This verifies:
     * - The card appearance in dark theme
     * - The "Rate the App" text is displayed correctly
     * - The description text is displayed correctly
     * - The "Rate the App" button is displayed and enabled
     * - Text and background colors are properly contrasted
     * 
     * This test uses mockK to mock Android dependencies:
     * - Context is mocked to provide a package name
     * - AppReviewManager is mocked to do nothing when requestReview is called
     * 
     * CompositionLocalProvider is used to inject the mock Context into the composable.
     */
    @Test
    fun testRateAppSection_darkTheme() {
        paparazzi.snapshot {
            Surface(color = Color(0xFF121212)) {
                ThemedContent(darkTheme = true) {
                    // Inject the mock Context into the composable
                    CompositionLocalProvider(LocalContext provides mockContext) {
                        RateAppSection(
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}