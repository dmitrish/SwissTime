package com.coroutines.swisstime.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AboutScreenThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // No setup needed here, each test will set its own content
    }

    @Test
    fun testCardVisualProperties() {
        // Set content for this test
        composeTestRule.setContent {
            MaterialTheme {
                AboutScreen()
            }
        }

        // Verify the about section card
        composeTestRule
            .onNodeWithText("World Timezone Clock with Fun Mechanical Watchfaces")
            .assertIsDisplayed()
            .assert(hasParent(hasTestTag("about_section_card")))

        // Verify the rate app section card
        composeTestRule
            .onNode(hasTestTag("rate_app_section_card"))
            .assertIsDisplayed()
            
        // Verify the rate app button
        composeTestRule
            .onNodeWithTag("rate_app_button")
            .assertIsDisplayed()

        // Verify the version section card
        composeTestRule
            .onNodeWithText("App Version")
            .assertIsDisplayed()
            .assert(hasParent(hasTestTag("version_section_card")))
    }
}