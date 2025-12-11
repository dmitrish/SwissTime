# Testing In-App Updates in SwissTime App

This guide explains how to test the in-app update functionality implemented in the SwissTime app. The app uses Google Play's in-app update API to provide seamless updates to users.

## Understanding the Implementation

The SwissTime app implements in-app updates using:

1. A custom `AppUpdateManager` class that wraps Google's Play Core library
2. Integration in `MainActivity` to handle the update flow
3. Support for both flexible and immediate update types

## Testing Methods

There are several ways to test in-app updates, depending on your environment and needs.

### 1. Testing in Development Environment

Google provides a way to test in-app updates without actually publishing app updates to the Play Store.

#### Setting Up Internal App Sharing

1. **Upload a test build to Internal App Sharing**:
   - Go to Play Console > Internal App Sharing
   - Upload your APK/AAB with a higher version code than the currently installed app
   - Get the sharing link

2. **Install the base version**:
   - Install a lower version of your app on your test device
   - Make sure you're signed in with the same Google account that has access to the internal app sharing

3. **Enable developer settings**:
   - Open the Google Play Store app
   - Tap your profile icon > Settings > About > Tap "Play Store version" 7 times to enable developer options
   - Go back to Settings > Developer options
   - Enable "Internal app sharing testing"

4. **Test the update flow**:
   - Open the sharing link on your device to download the update
   - Launch your app to trigger the update check
   - The app should detect the update and start the update flow

#### Using Faked Updates with Testing API

For more controlled testing, you can use the Play Core testing API:

1. **Add the testing library to your project**:
   ```kotlin
   // Add to app/build.gradle.kts
   debugImplementation("com.google.android.play:app-update-testing:2.1.0")
   ```

2. **Create a test implementation**:
   ```kotlin
   // In a debug build variant
   // Use the FakeAppUpdateManager from the testing library
   val fakeAppUpdateManager = FakeAppUpdateManager(context)
   fakeAppUpdateManager.setUpdateAvailable(versionCode) // Higher version code

   // For immediate updates
   fakeAppUpdateManager.userAcceptsUpdate()
   fakeAppUpdateManager.downloadStarts()
   fakeAppUpdateManager.downloadCompletes()

   // For flexible updates
   fakeAppUpdateManager.downloadStarts()
   fakeAppUpdateManager.downloadCompletes()
   fakeAppUpdateManager.completeUpdate()
   ```

3. **Inject the fake manager**:
   - Modify your app to use dependency injection
   - In debug builds, inject the FakeAppUpdateManager instead of the real one
   - Control the update flow programmatically

### 2. Testing in Production Environment

To test with real updates from the Play Store:

#### Using Closed Testing Tracks

1. **Set up a closed testing track**:
   - Go to Play Console > Your app > Testing > Create new track (or use existing Alpha/Beta)
   - Add testers (email addresses or Google Groups)

2. **Upload a new version**:
   - Upload a new version with a higher version code
   - Roll out to 100% of testers

3. **Test the update flow**:
   - Install the previous version from the same track
   - Launch the app to trigger the update check
   - The app should detect and offer the update

#### Testing with Staged Rollouts

For production testing:

1. **Create a staged rollout**:
   - Upload a new version to production
   - Set a small percentage for the rollout (e.g., 5%)

2. **Join the rollout group**:
   - Make sure your test devices are in the rollout percentage
   - You can use the same Google account across multiple test devices

3. **Test the update flow**:
   - Install the previous production version
   - Launch the app to trigger the update check
   - The app should detect and offer the update

## Testing Specific Scenarios

### Testing Immediate Updates

To force immediate updates in your test environment:

1. **Modify the update priority**:
   - In your test code, set a high update priority (5)
   - Or set a high staleness value (> 7 days)

2. **Use the forceImmediateUpdate parameter**:
   ```kotlin
   appUpdateManager.checkForUpdate(forceImmediateUpdate = true)
   ```

### Testing Flexible Updates

To test flexible updates:

1. **Modify the update priority**:
   - Set a medium update priority (2-3)
   - Or set a medium staleness value (2-6 days)

2. **Monitor the download progress**:
   - The app should show download progress
   - After download completes, it should prompt to install

3. **Test deferring the update**:
   - When prompted to install, choose "Later"
   - The app should continue functioning
   - The prompt should reappear on next app launch

### Testing Edge Cases

1. **Network failures**:
   - Test with airplane mode or poor connectivity
   - Verify error handling and user feedback

2. **Update cancellation**:
   - Cancel the update during download
   - Verify the app handles this gracefully

3. **App backgrounding during update**:
   - Start an update and background the app
   - Return to the app and verify the update continues

## Troubleshooting

### Common Issues

1. **Updates not showing up**:
   - Verify the version code is higher in the new version
   - Check that your test device is eligible for the update
   - Ensure you're signed in with the correct Google account

2. **Failed updates**:
   - Check for network connectivity issues
   - Verify the APK/AAB is properly signed
   - Look for error messages in logcat

3. **Testing API not working**:
   - Ensure you're using the correct version of the testing library
   - Verify the fake update manager is properly initialized

### Logging and Debugging

Add additional logging to help debug update issues:

```kotlin
lifecycleScope.launch {
    appUpdateManager.updateStatus.collect { status ->
        Log.d("AppUpdate", "Update status: $status")
        // Handle status changes
    }
}
```

## Best Practices

1. **Test both update types**:
   - Test both immediate and flexible updates
   - Verify the correct UI is shown for each type

2. **Test on multiple devices**:
   - Test on different Android versions
   - Test on different device manufacturers

3. **Test the complete flow**:
   - From update detection to installation
   - Including all user interaction points

4. **Verify post-update behavior**:
   - Ensure the app works correctly after updating
   - Check that user data is preserved

## Conclusion

Testing in-app updates requires a combination of development-time testing with fake updates and real-world testing with actual Play Store updates. By following this guide, you can ensure your in-app update implementation works correctly in all scenarios.
