# Testing In-App Updates with FakeAppUpdateManager

This document explains how to use the `FakeAppUpdateManager` to test in-app updates in the SwissTime app without requiring the app to be installed from the Play Store.

## Overview

The SwissTime app uses Google Play's in-app update API to provide seamless updates to users. Testing this functionality can be challenging because:

1. The real `AppUpdateManager` requires the app to be installed from the Play Store
2. It's difficult to simulate different update scenarios (download progress, failures, etc.)

To address these challenges, we've implemented a testing approach using Google's `FakeAppUpdateManager` from the Play Core testing library.

## Implementation

The testing implementation consists of two main components:

1. **TestAppUpdateManager**: A test-specific version of our custom `AppUpdateManager` that accepts a `FakeAppUpdateManager` instance
2. **AppUpdateTest**: A test class that demonstrates how to use `TestAppUpdateManager` to test different update scenarios and UI interactions

### TestAppUpdateManager

The `TestAppUpdateManager` class:

- Accepts a `FakeAppUpdateManager` instance in its constructor
- Uses this fake manager for all update operations
- Defines its own `UpdateStatus` sealed class that mirrors the one in our real `AppUpdateManager`
- Implements the same interface and behavior as our real `AppUpdateManager`

This allows us to test the update functionality without requiring the app to be installed from the Play Store.

### AppUpdateTest

The `AppUpdateTest` class demonstrates how to test both the update manager state and the UI interactions:

- `testFlexibleUpdate_Completes`: Tests a flexible update flow where the update completes successfully
- `testImmediateUpdate_Completes`: Tests an immediate update flow where the update completes successfully
- `testFlexibleUpdate_DownloadFails`: Tests a flexible update flow where the download fails
- `testNoUpdateAvailable`: Tests a scenario where no update is available
- `testUpdateCanceled`: Tests a scenario where the user cancels the update

Each test verifies both the update manager state and the UI interactions, ensuring that our app's UI responds correctly to different update scenarios.

## Testing App Integration

Our tests verify the actual integration between our app and the update manager by:

1. **Testing UI Elements**: Verifying that the version section card and "Check for Updates" button are displayed
2. **Testing User Interactions**: Clicking the "Check for Updates" button and verifying that it initiates the update check
3. **Verifying UI Updates**: Checking that update status messages appear in the UI based on the update state
4. **Testing Complete Flows**: Verifying the entire flow from button click to update completion

This approach ensures that we're testing the actual integration between our app and the update manager, not just the update manager in isolation.

### Example: Testing UI Integration

```kotlin
// Navigate to the About screen by clicking on the navigation bar item
composeTestRule.onNodeWithText("About").performClick()

// Verify that the version section card is displayed
composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()

// Find and click the "Check for Updates" button
composeTestRule.onNodeWithText("Check for Updates").performClick()

// Verify that update status message changes to "Checking for updates..."
composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()

// Simulate download starting
fakeAppUpdateManager.downloadStarts()

// Verify that update status message shows download progress
composeTestRule.waitUntil(timeoutMillis = 5000) {
    composeTestRule.onAllNodesWithText(text = "Downloading update:", substring = true)
        .fetchSemanticsNodes().isNotEmpty()
}

// Simulate download completing
fakeAppUpdateManager.downloadCompletes()

// Verify that update status message changes to "Update downloaded. Ready to install."
composeTestRule.onNodeWithText("Update downloaded. Ready to install.").assertIsDisplayed()
```

## How to Use

### Setup

1. Make sure the Play Core testing library dependency is included in your `build.gradle.kts` file:

```kotlin
debugImplementation("com.google.android.play:app-update-testing:2.1.0")
```

2. Create a ComposeTestRule for testing Compose UI:

```kotlin
@get:Rule
val composeTestRule = createAndroidComposeRule<MainActivity>()
```

3. Create a `FakeAppUpdateManager` instance:

```kotlin
val fakeAppUpdateManager = FakeAppUpdateManager(composeTestRule.activity)
```

4. Create a `TestAppUpdateManager` instance:

```kotlin
val testAppUpdateManager = TestAppUpdateManager(
    activity = composeTestRule.activity,
    coroutineScope = CoroutineScope(Dispatchers.Main),
    fakeAppUpdateManager = fakeAppUpdateManager
)
```

### Testing Update Scenarios with UI Verification

#### Important Note

The version section card and update functionality are located on the About Screen. You must navigate to this screen before testing the update functionality:

```kotlin
// Navigate to the About screen by clicking on the navigation bar item
composeTestRule.onNodeWithText("About").performClick()
```

#### 1. Flexible Update

```kotlin
// Setup flexible update
fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
fakeAppUpdateManager.setUpdatePriority(2) // Medium priority (will trigger flexible update)

// Navigate to the About screen by clicking on the navigation bar item
composeTestRule.onNodeWithText("About").performClick()

// Verify that the version section card is displayed
composeTestRule.onNodeWithTag("version_section_card").assertIsDisplayed()

// Find and click the "Check for Updates" button
composeTestRule.onNodeWithText("Check for Updates").performClick()

// Verify that update status message changes to "Checking for updates..."
composeTestRule.onNodeWithText("Checking for updates...").assertIsDisplayed()

// Simulate user accepting the update
fakeAppUpdateManager.userAcceptsUpdate()

// Simulate download starting
fakeAppUpdateManager.downloadStarts()

// Verify that update status message shows download progress
composeTestRule.waitUntil(timeoutMillis = 5000) {
    composeTestRule.onAllNodesWithText(text = "Downloading update:", substring = true)
        .fetchSemanticsNodes().isNotEmpty()
}

// Simulate download completing
fakeAppUpdateManager.downloadCompletes()

// Verify that update status message changes to "Update downloaded. Ready to install."
composeTestRule.onNodeWithText("Update downloaded. Ready to install.").assertIsDisplayed()

// Complete the update
testAppUpdateManager.completeUpdate()

// Simulate install completing
fakeAppUpdateManager.installCompletes()
```

#### 2. Failed Update

```kotlin
// Setup flexible update
fakeAppUpdateManager.setUpdateAvailable(2) // Higher version code
fakeAppUpdateManager.setUpdatePriority(2) // Medium priority (will trigger flexible update)

// Find and click the "Check for Updates" button
composeTestRule.onNodeWithText("Check for Updates").performClick()

// Simulate download starting and then failing
fakeAppUpdateManager.downloadStarts()
fakeAppUpdateManager.downloadFails()

// Verify that update status message shows failure
composeTestRule.waitUntil(timeoutMillis = 5000) {
    composeTestRule.onAllNodesWithText(text = "Update check failed:", substring = true)
        .fetchSemanticsNodes().isNotEmpty()
}

// Verify that the "Check for Updates" button is enabled again after failure
composeTestRule.onNodeWithText("Check for Updates").assertIsEnabled()
```

## Troubleshooting

### Common Issues

1. **Test not running**: Make sure you have a connected device or emulator with the app installed.

2. **UI elements not found**: Check that you're using the correct test tags or text for finding UI elements.
   - If you can't find the "version_section_card", make sure you've navigated to the About Screen first.
   - If you can't find the "About" navigation item, check if the bottom navigation bar is visible in your test.

3. **Compose tests failing**: Ensure you're using the correct Compose testing APIs and waiting for UI updates when needed.
   - Navigation might take some time to complete. Consider adding a short delay or using waitUntil if needed.

4. **Update flow not triggering**: Check that you've set the update priority and version code correctly.

5. **Navigation issues**: If you're having trouble navigating to the About Screen:
   - Make sure the bottom navigation bar is visible (not hidden by keyboard or other UI elements)
   - Try using a semantics matcher instead of text: `composeTestRule.onNodeWithContentDescription("About").performClick()`
   - Check if the navigation structure has changed and update your tests accordingly

## Conclusion

Using `FakeAppUpdateManager` with Compose testing allows us to test the in-app update functionality without requiring the app to be installed from the Play Store. This approach provides a way to simulate different update scenarios and verify that our app's UI responds correctly to each scenario.

For more information, refer to:
- [Google's Play Core testing documentation](https://developer.android.com/guide/playcore/in-app-updates/test)
- [Jetpack Compose testing documentation](https://developer.android.com/jetpack/compose/testing)
- The `AppUpdateTest` class for example test cases
- The `TestAppUpdateManager` class for implementation details