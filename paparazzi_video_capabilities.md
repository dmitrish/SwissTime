# Paparazzi Video Capabilities

## Summary

Yes, Paparazzi can capture videos of test execution, specifically in the form of animated GIFs. The "videos" directory you noticed is created to store these GIF animations, although it remains empty if you don't explicitly use Paparazzi's GIF capture functionality.

## How Paparazzi's Video Capture Works

Paparazzi provides a `gif()` method that captures multiple frames of a view over time and combines them into an animated GIF. This is different from the `snapshot()` method we used in our tests, which only captures a single static image.

The `gif()` method has the following signature:

```kotlin
public fun gif(view: View, name: String? = null, start: Long = 0L, end: Long = 500L, fps: Int = 30)
```

Parameters:
- `view`: The Android View to capture
- `name`: Optional name for the GIF (default: null)
- `start`: Start time in milliseconds (default: 0)
- `end`: End time in milliseconds (default: 500)
- `fps`: Frames per second (default: 30)

When you call this method, Paparazzi:
1. Calculates the number of frames needed based on the duration and fps
2. Captures frames of the view at regular intervals
3. Combines the frames into an animated GIF
4. Stores the GIF in the "videos" directory

## Why Our Tests Didn't Generate Videos

In our SettingsScreenPaparazziTest, we only used the `snapshot()` method to capture static screenshots:

```kotlin
@Test
fun testSettingsScreen_lightTheme() {
    paparazzi.snapshot {
        SimpleSettingsScreen(
            darkMode = false,
            useUsTimeFormat = true,
            useDoubleTapForRemoval = false
        )
    }
}
```

Since we didn't use the `gif()` method, no animated GIFs were generated, which is why the "videos" directory remains empty.

## How to Capture Videos with Paparazzi

To capture videos (animated GIFs) with Paparazzi, you would need to:

1. Create an animated view (e.g., using ObjectAnimator)
2. Call the `gif()` method instead of `snapshot()`

Example:
```kotlin
@Test
fun testAnimatedSettings() {
    // Create a view with animation
    val view = MyAnimatedView(context)
    
    // Start the animation
    view.startAnimation()
    
    // Capture the animation as a GIF
    paparazzi.gif(view, start = 0L, end = 1000L, fps = 30)
}
```

For Compose UI, you would need to convert your Composable to a View first, as the `gif()` method currently only accepts View parameters, not Composables directly.

## Conclusion

Paparazzi does have the capability to capture videos of test execution in the form of animated GIFs. The "videos" directory is created to store these GIF animations, but it remains empty if you don't explicitly use the `gif()` method in your tests.

If you want to capture animations of your UI components, you can use the `gif()` method instead of `snapshot()` in your Paparazzi tests.