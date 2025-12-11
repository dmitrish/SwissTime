package com.coroutines.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their
 * performance. Refer to the
 * [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles) for
 * more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android
 * Studio or the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 *
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check
 * [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks]
 * benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are
 * supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @get:Rule val rule = BaselineProfileRule()

  @Test
  fun generate() {
    // The application id for the running build variant is read from the instrumentation arguments.
    val targetPackage =
      InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: throw Exception("targetAppId not passed as instrumentation runner arg")

    // Workaround INSTALL_FAILED_UPDATE_INCOMPATIBLE: if a Play-installed or differently
    // signed version of the target app already exists on the device, uninstall it first.
    // BaselineProfileRule will install the test build afterwards.
    try {
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
      // Ignore the result; if the app isn't installed this will be a no-op.
      device.executeShellCommand("pm uninstall ${'$'}targetPackage")
      // Give PackageManager a brief moment to settle before install
      Thread.sleep(250)
    } catch (t: Throwable) {
      // Swallow any errors here; we'll let the rule attempt installation regardless.
    }

    rule.collect(
      packageName = targetPackage,
      // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
      includeInStartupProfile = true
    ) {
      // This block defines the app's critical user journey. Here we are interested in
      // optimizing for app startup. But you can also navigate and scroll through your most
      // important UI.

      // Start default activity for your app
      pressHome()
      startActivityAndWait()

      // Wait until the main content is loaded. We use UIAutomator to ensure the Welcome screen
      // with the SwissTimePager is visible and settled. The Welcome screen shows a "Tap to zoom"
      // text when the pager is ready for interaction.
      val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

      // Wait for the target package to be in foreground
      device.wait(Until.hasObject(By.pkg(targetPackage).depth(0)), 3_000)

      // Wait for the "Tap to zoom" text which indicates pager + content composed
      val appeared = device.wait(Until.hasObject(By.text("Tap to zoom")), 3_000)

      // As a safety net, wait for UI idle if the text isn't found (e.g., localized build)
      if (!appeared) {
        device.waitForIdle(2_000)
        // small settle pause
        Thread.sleep(250)
      }

      // Optional: ensure no pending animations interfere with trace segmentation
      device.waitForIdle(1_000)

      // 1) Tap the focused (center) watch to trigger zoom
      val cx = device.displayWidth / 2
      val cy = device.displayHeight / 2
      // device.click(cx, cy)
      device.click(cx, (cy * 1.3).toInt())

      // 2) Wait for the "Select this watch" button to appear, then click it
      val buttonAppeared = device.wait(Until.hasObject(By.text("Select this watch")), 3_000)
      if (buttonAppeared) {
        device.findObject(By.text("Select this watch"))?.click()
      } else {
        // Fallback for non-English locale: wait a bit and tap slightly below center
        device.waitForIdle(1_000)
        device.click(cx, (cy * 1.3).toInt())
      }

      // 3) Wait for TimeScreen to be visible
      // Prefer a semantics content description if available; otherwise, fall back to heuristics
      // Try multiple selectors in sequence to avoid BySelector.or() compatibility issues
      var timeScreenShown = device.wait(Until.hasObject(By.desc("TimeScreen")), 3_000)
      if (!timeScreenShown) timeScreenShown = device.wait(Until.hasObject(By.text("Time")), 2_000)
      if (!timeScreenShown)
        timeScreenShown = device.wait(Until.hasObject(By.text("World Time")), 2_000)
      if (!timeScreenShown)
        timeScreenShown = device.wait(Until.hasObject(By.text("Time zones")), 2_000)
      if (!timeScreenShown)
        timeScreenShown = device.wait(Until.hasObject(By.textContains(":")), 2_000)

      if (!timeScreenShown) {
        // Ensure previous button is gone and UI is idle before finishing collection
        device.wait(Until.gone(By.text("Select this watch")), 5_000)
        device.waitForIdle(1_000)
      }

      // Example: navigate to Watch List
      device.findObject(androidx.test.uiautomator.By.text("Watches"))?.click()

      device.waitForIdle(2_000)

      // Scroll the list a bit (uses a generic scrollable container)
      // Or more explicitly for your use case:
      device.findObject(By.res("watchList"))?.apply {
        setGestureMargin(device.displayWidth / 5) // optional: set margins
        fling(Direction.DOWN, 5000)
        fling(Direction.UP, 5000)
      }

      // Open a watch detail (tap first visible item by text or use resource-id if available)
      // Adjust text to one that’s always present on a watch tile
      device.findObject(androidx.test.uiautomator.By.textContains("Jurgsen"))?.click()

      // Back to list
      device.pressBack()

      // Go to Settings
      device.findObject(androidx.test.uiautomator.By.text("Settings"))?.click()

      // Toggle Theme (match the exact label as shown on device)
      device.findObject(androidx.test.uiautomator.By.textContains("Theme"))?.click()
      device.findObject(androidx.test.uiautomator.By.text("System default"))?.click()
      device.pressBack()

      // Navigate to World Map
      device.findObject(androidx.test.uiautomator.By.text("World Map"))?.click()

      // Return home
      device.pressBack()
      device.pressBack()
    }
  }
}
