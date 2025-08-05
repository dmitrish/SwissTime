package com.coroutines.swisstime.update

import android.app.Activity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.Role
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.coroutines.swisstime.MainActivity
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the app update functionality using FakeAppUpdateManager.
 * 
 * This test class demonstrates how to test the app update functionality without requiring
 * the app to be installed from the Play Store.
 */
@RunWith(AndroidJUnit4::class)
class AppUpdateTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var fakeAppUpdateManager: FakeAppUpdateManager
    private lateinit var testAppUpdateManager: TestAppUpdateManager
    private lateinit var activity: Activity

    @Before
    fun setup() {
        // Get the activity instance from the compose rule
        activity = composeTestRule.activity
        
        // Create a FakeAppUpdateManager with the activity context
        fakeAppUpdateManager = FakeAppUpdateManager(activity)
        
        // Create a TestAppUpdateManager with the FakeAppUpdateManager
        testAppUpdateManager = TestAppUpdateManager(
            activity = activity,
            coroutineScope = CoroutineScope(Dispatchers.Main),
            fakeAppUpdateManager = fakeAppUpdateManager
        )
    }

    /**
     * Tests a flexible update flow where the update completes successfully.
     * This test verifies both the update manager state and the UI interactions.
     */
    @Test
    fun testFlexibleUpdate_Completes() {
        runBlocking {
            // Setup flexible update
            fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
            fakeAppUpdateManager.setUpdatePriority(2) // Medium priority (will trigger flexible update)
            
            // Navigate to the About screen by clicking on the navigation bar item
            // Use a more robust method to find and click the about_menu_option
            composeTestRule.onNodeWithTag("about_menu_option", useUnmergedTree = true)
                .assertExists()
                .assertIsEnabled()
                .performClick()
            
            // Add a fixed delay to ensure navigation has time to complete
            kotlinx.coroutines.delay(5000) // 2 second delay
            
            // Wait for navigation to complete and the version section card to be displayed with increased timeout
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    // Try to find the version_section_card and ensure it's displayed
                    val nodes = composeTestRule.onAllNodesWithTag("version_section_card")
                        .fetchSemanticsNodes()
                    nodes.isNotEmpty()
                } catch (e: Exception) {
                    false
                }
            }
            
            // Verify that the version section card is displayed
            composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()
            
            // Find and click the "Check for Updates" button
            composeTestRule.onNodeWithText("Check for Updates").performClick()
            
            // Verify that update status message changes to "Checking for updates..."
            composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()
            
            // Verify that update status is "Checking"
            assertEquals(TestAppUpdateManager.UpdateStatus.Checking, testAppUpdateManager.updateStatus.first())
            
            // Simulate user accepting the update
            fakeAppUpdateManager.userAcceptsUpdate()
            
            // Simulate download starting
            fakeAppUpdateManager.downloadStarts()
            
            // Verify that update status message shows download progress
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(text = "Downloading update:", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            
            // Verify that update status is "Downloading"
            assertTrue(testAppUpdateManager.updateStatus.first() is TestAppUpdateManager.UpdateStatus.Downloading)
            
            // Simulate download completing
            fakeAppUpdateManager.downloadCompletes()
            
            // Verify that update status message changes to "Update downloaded. Ready to install."
            composeTestRule.onNodeWithText("Update downloaded. Ready to install.").assertIsDisplayed()
            
            // Verify that update status is "Downloaded"
            assertEquals(TestAppUpdateManager.UpdateStatus.Downloaded, testAppUpdateManager.updateStatus.first())
            
            // Complete the update
            testAppUpdateManager.completeUpdate()
            
            // Verify that update status is "Installing"
            assertEquals(TestAppUpdateManager.UpdateStatus.Installing, testAppUpdateManager.updateStatus.first())
            
            // Simulate install completing
            fakeAppUpdateManager.installCompletes()
            
            // Verify that update status is "Installed"
            assertEquals(TestAppUpdateManager.UpdateStatus.Installed, testAppUpdateManager.updateStatus.first())
        }
    }

    /**
     * Tests an immediate update flow where the update completes successfully.
     * This test verifies both the update manager state and the UI interactions.
     */
    @Test
    fun testImmediateUpdate_Completes() {
        runBlocking {
            // Setup immediate update
            fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
            fakeAppUpdateManager.setUpdatePriority(5) // High priority (will trigger immediate update)
            
            // Navigate to the About screen by clicking on the navigation bar item
            // Use a more robust method to find and click the about_menu_option
            composeTestRule.onNodeWithTag("about_menu_option", useUnmergedTree = true)
                .assertExists()
                .assertIsEnabled()
                .performClick()
            
            // Wait for navigation to complete and the version section card to be displayed with increased timeout
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("version_section_card").fetchSemanticsNode()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            
            // Verify that the version section card is displayed
            composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()
            
            // Find and click the "Check for Updates" button
            composeTestRule.onNodeWithText("Check for Updates").performClick()
            
            // Verify that update status message changes to "Checking for updates..."
            composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()
            
            // Verify that update status is "Checking"
            assertEquals(TestAppUpdateManager.UpdateStatus.Checking, testAppUpdateManager.updateStatus.first())
            
            // Simulate user accepting the update
            fakeAppUpdateManager.userAcceptsUpdate()
            
            // Simulate download starting
            fakeAppUpdateManager.downloadStarts()
            
            // Verify that update status message shows download progress
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(text = "Downloading update:", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            
            // Verify that update status is "Downloading"
            assertTrue(testAppUpdateManager.updateStatus.first() is TestAppUpdateManager.UpdateStatus.Downloading)
            
            // Simulate download completing
            fakeAppUpdateManager.downloadCompletes()
            
            // Simulate install completing
            fakeAppUpdateManager.installCompletes()
            
            // Verify that update status is "Installed"
            assertEquals(TestAppUpdateManager.UpdateStatus.Installed, testAppUpdateManager.updateStatus.first())
            
            // For immediate updates, the app would typically restart, so we don't expect to see
            // the "Update downloaded" message in the UI as we do with flexible updates
        }
    }

    /**
     * Tests a flexible update flow where the download fails.
     * This test verifies both the update manager state and the UI interactions.
     */
    @Test
    fun testFlexibleUpdate_DownloadFails() {
        runBlocking {
            // Setup flexible update
            fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
            fakeAppUpdateManager.setUpdatePriority(2) // Medium priority (will trigger flexible update)
            
            // Navigate to the About screen by clicking on the navigation bar item
            // Use a more robust method to find and click the about_menu_option
            composeTestRule.onNodeWithTag("about_menu_option", useUnmergedTree = true)
                .assertExists()
                .assertIsEnabled()
                .performClick()
            
            // Wait for navigation to complete and the version section card to be displayed with increased timeout
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("version_section_card").fetchSemanticsNode()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            
            // Verify that the version section card is displayed
            composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()
            
            // Find and click the "Check for Updates" button
            composeTestRule.onNodeWithText("Check for Updates").performClick()
            
            // Verify that update status message changes to "Checking for updates..."
            composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()
            
            // Verify that update status is "Checking"
            assertEquals(TestAppUpdateManager.UpdateStatus.Checking, testAppUpdateManager.updateStatus.first())
            
            // Simulate user accepting the update
            fakeAppUpdateManager.userAcceptsUpdate()
            
            // Simulate download starting
            fakeAppUpdateManager.downloadStarts()
            
            // Verify that update status message shows download progress
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(text = "Downloading update:", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            
            // Verify that update status is "Downloading"
            assertTrue(testAppUpdateManager.updateStatus.first() is TestAppUpdateManager.UpdateStatus.Downloading)
            
            // Simulate download failing
            fakeAppUpdateManager.downloadFails()
            
            // Verify that update status message shows failure
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText(text = "Update check failed:", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
            
            // Verify that update status is "Failed"
            assertTrue(testAppUpdateManager.updateStatus.first() is TestAppUpdateManager.UpdateStatus.Failed)
            
            // Verify that the "Check for Updates" button is enabled again after failure
            composeTestRule.onNodeWithText("Check for Updates").assertIsEnabled()
        }
    }

    /**
     * Tests a scenario where no update is available.
     * This test verifies both the update manager state and the UI interactions.
     */
    @Test
    fun testNoUpdateAvailable() {
        runBlocking {
            // Setup no update available
            fakeAppUpdateManager.setUpdateNotAvailable()
            
            // Navigate to the About screen by clicking on the navigation bar item
            // Use a more robust method to find and click the about_menu_option
            composeTestRule.onNodeWithTag("about_menu_option", useUnmergedTree = true)
                .assertExists()
                .assertIsEnabled()
                .performClick()
            
            // Wait for navigation to complete and the version section card to be displayed with increased timeout
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("version_section_card").fetchSemanticsNode()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            
            // Verify that the version section card is displayed
            composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()
            
            // Find and click the "Check for Updates" button
            composeTestRule.onNodeWithText("Check for Updates").performClick()
            
            // Verify that update status message changes to "Checking for updates..."
            composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()
            
            // Verify that update status is "Checking"
            assertEquals(TestAppUpdateManager.UpdateStatus.Checking, testAppUpdateManager.updateStatus.first())
            
            // Verify that update status message changes to "No updates available"
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("No updates available")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            
            // Verify that update status is "NotRequired"
            assertEquals(TestAppUpdateManager.UpdateStatus.NotRequired, testAppUpdateManager.updateStatus.first())
            
            // Verify that the "Check for Updates" button is enabled again
            composeTestRule.onNodeWithText("Check for Updates").assertIsEnabled()
        }
    }

    /**
     * Tests a scenario where the user cancels the update.
     * This test verifies both the update manager state and the UI interactions.
     */
    @Test
    fun testUpdateCanceled() {
        runBlocking {
            // Setup flexible update
            fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
            fakeAppUpdateManager.setUpdatePriority(2) // Medium priority (will trigger flexible update)
            
            // Navigate to the About screen by clicking on the navigation bar item
            // Use a more robust method to find and click the about_menu_option
            composeTestRule.onNodeWithTag("about_menu_option", useUnmergedTree = true)
                .assertExists()
                .assertIsEnabled()
                .performClick()
            
            // Wait for navigation to complete and the version section card to be displayed with increased timeout
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                try {
                    composeTestRule.onNodeWithTag("version_section_card").fetchSemanticsNode()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            
            // Verify that the version section card is displayed
            composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()
            
            // Find and click the "Check for Updates" button
            composeTestRule.onNodeWithText("Check for Updates").performClick()
            
            // Verify that update status message changes to "Checking for updates..."
            composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()
            
            // Verify that update status is "Checking"
            assertEquals(TestAppUpdateManager.UpdateStatus.Checking, testAppUpdateManager.updateStatus.first())
            
            // Simulate user canceling the update
            fakeAppUpdateManager.userRejectsUpdate()
            
            // Simulate activity result with RESULT_CANCELED
            testAppUpdateManager.onActivityResult(TestAppUpdateManager.UPDATE_REQUEST_CODE, Activity.RESULT_CANCELED)
            
            // Verify that update status is "Canceled"
            assertEquals(TestAppUpdateManager.UpdateStatus.Canceled, testAppUpdateManager.updateStatus.first())
            
            // Verify that the "Check for Updates" button is enabled again after cancellation
            composeTestRule.onNodeWithText("Check for Updates").assertIsEnabled()
        }
    }
}