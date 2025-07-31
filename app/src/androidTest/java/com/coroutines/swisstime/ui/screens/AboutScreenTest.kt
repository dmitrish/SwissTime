package com.coroutines.swisstime.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // No setup needed here, each test will set its own content
    }

    @Test
    fun testAboutScreenContent() {
        // Set content for this test
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen()
            }
        }

        // Verify header is displayed
        composeTestRule
            .onNodeWithText("About")
            .assertIsDisplayed()

        // Verify app description section
        composeTestRule
            .onNodeWithText("World Timezone Clock with Fun Mechanical Watchfaces")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("World Timezone Clock is an elegant app that displays the time across different timezones with beautifully crafted mechanical watch faces. Each watch face is designed with attention to detail, mimicking the craftsmanship of real luxury timepieces.")
            .assertIsDisplayed()

        // Verify Rate the App section
        composeTestRule
            .onNodeWithText("Rate the App")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Enjoying World Timezone Clock App? Let us know what you think!")
            .assertIsDisplayed()

        // Use the test tag to specifically target the button
        composeTestRule
            .onNodeWithTag("rate_app_button")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Verify App Version section
        composeTestRule
            .onNodeWithText("App Version")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Check for Updates")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Test Check for Updates button interaction
        composeTestRule
            .onNodeWithText("Check for Updates")
            .performClick()
    }

    @Test
    fun testRateAppButtonInteraction() {
        // Set content for this test
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen()
            }
        }

        // Click Rate the App button using the test tag
        composeTestRule
            .onNodeWithTag("rate_app_button")
            .performClick()
    }
}