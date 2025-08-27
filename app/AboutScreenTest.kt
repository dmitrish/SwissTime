
package com.coroutines.swisstime.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.coroutines.swisstime.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test

class AboutScreenTest : TestCase() {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testAboutScreenContent() = run {
        // Set up the AboutScreen
        before {
            composeTestRule.setContent {
                AboutScreen()
            }
        }

        step("Verify header is displayed") {
            composeTestRule
                .onNodeWithText("About")
                .assertIsDisplayed()
        }

        step("Verify app description section") {
            composeTestRule
                .onNodeWithText("World Timezone Clock with Fun Mechanical Watchfaces")
                .assertIsDisplayed()
            
            composeTestRule
                .onNodeWithText("World Timezone Clock is an elegant app that displays the time across different timezones with beautifully crafted mechanical watch faces. Each watch face is designed with attention to detail, mimicking the craftsmanship of real luxury timepieces.")
                .assertIsDisplayed()
        }

        step("Verify Rate the App section") {
            composeTestRule
                .onNodeWithText("Rate the App")
                .assertIsDisplayed()
            
            composeTestRule
                .onNodeWithText("Enjoying World Timezone Clock App? Let us know what you think!")
                .assertIsDisplayed()
            
            composeTestRule
                .onNodeWithText("Rate the App", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertIsEnabled()
        }

        step("Verify App Version section") {
            composeTestRule
                .onNodeWithText("App Version")
                .assertIsDisplayed()
            
            // Note: We can't test exact version number as it's dynamic,
            // but we can verify the button is present
            composeTestRule
                .onNodeWithText("Check for Updates")
                .assertIsDisplayed()
                .assertIsEnabled()
        }

        step("Test Check for Updates button interaction") {
            composeTestRule
                .onNodeWithText("Check for Updates")
                .performClick()
            
            // Verify toast message appears
            // Note: Toast verification requires additional setup in real tests
            // as it's not directly accessible through Compose testing
        }
    }

    @Test
    fun testRateAppButtonInteraction() = run {
        before {
            composeTestRule.setContent {
                AboutScreen()
            }
        }

        step("Click Rate the App button") {
            composeTestRule
                .onNodeWithText("Rate the App", useUnmergedTree = true)
                .performClick()
            
            // Note: Full verification of review flow would require
            // mocking AppReviewManager and additional integration testing
        }
    }
}
