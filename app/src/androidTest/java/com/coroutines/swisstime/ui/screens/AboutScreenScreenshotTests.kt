package com.coroutines.swisstime.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class AboutScreenScreenshotTest : ScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAboutScreenLightTheme() {
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen()
            }
        }
        compareScreenshot(composeTestRule, "about_screen_light_theme")
    }

    @Test
    fun testAboutScreenDarkTheme() {
        composeTestRule.setContent {
            MaterialTheme(
                colorScheme = androidx.compose.material3.darkColorScheme()
            ) {
                AboutScreen()
            }
        }
        compareScreenshot(composeTestRule, "about_screen_dark_theme")
    }

    @Test
    fun testAboutScreenDifferentFontSizes() {
        composeTestRule.setContent {
            MaterialTheme(
                typography = MaterialTheme.typography.copy(
                    titleLarge = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.5
                    )
                )
            ) {
                AboutScreen()
            }
        }
        compareScreenshot(composeTestRule, "about_screen_large_font")
    }
}
