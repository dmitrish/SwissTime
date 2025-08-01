package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
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

    /**
     * Custom implementation of RateAppSection for testing
     */
    @Composable
    private fun TestRateAppSection() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rate_app_section_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Rate the App",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("rate_app_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enjoying World Timezone Clock App? Let us know what you think!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("rate_app_description")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rate the app button
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .testTag("rate_app_button")
                    ) {
                        Text(text = "Rate the App")
                    }
                }
            }
        }
    }

    /**
     * Custom implementation of AppVersionSection for testing
     */
    @Composable
    private fun TestAppVersionSection() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("version_section_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "App Version",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("app_version_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Version 1.0.0 (1)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("app_version_info")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .testTag("check_updates_button")
                    ) {
                        Text(text = "Check for Updates")
                    }
                }
            }
        }
    }

    @Test
    fun testAboutScreenContent() {
        // Set content for this test
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen(
                    rateAppSection = { TestRateAppSection() },
                    appVersionSection = { TestAppVersionSection() }
                )
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

        // Verify Rate the App section using test tags
        composeTestRule
            .onNodeWithTag("rate_app_title")
            .assertIsDisplayed()

        // Find the rate app description using the test tag
        composeTestRule
            .onNodeWithTag("rate_app_description")
            .assertIsDisplayed()

        // Use the test tag to specifically target the rate app button
        composeTestRule
            .onNodeWithTag("rate_app_button")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Verify App Version section using test tags
        composeTestRule
            .onNodeWithTag("app_version_title")
            .assertIsDisplayed()

        // Verify version info using test tag
        composeTestRule
            .onNodeWithTag("app_version_info")
            .assertIsDisplayed()

        // Verify Check for Updates button using test tag
        composeTestRule
            .onNodeWithTag("check_updates_button")
            .assertIsDisplayed()
            .assertIsEnabled()

        // Test Check for Updates button interaction using test tag
        composeTestRule
            .onNodeWithTag("check_updates_button")
            .performClick()
    }

    @Test
    fun testRateAppButtonInteraction() {
        // Set content for this test
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen(
                    rateAppSection = { TestRateAppSection() },
                    appVersionSection = { TestAppVersionSection() }
                )
            }
        }

        // Click Rate the App button using the test tag
        composeTestRule
            .onNodeWithTag("rate_app_button")
            .performClick()
    }
}