package com.coroutines.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
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
 * This test class benchmarks the speed of app startup.
 * Run this benchmark to verify how effective a Baseline Profile is.
 * It does this by comparing [CompilationMode.None], which represents the app with no Baseline
 * Profiles optimizations, and [CompilationMode.Partial], which uses Baseline Profiles.
 *
 * Run this benchmark to see startup measurements and captured system traces for verifying
 * the effectiveness of your Baseline Profiles. You can run it directly from Android
 * Studio as an instrumentation test, or run all benchmarks for a variant, for example benchmarkRelease,
 * with this Gradle task:
 * ```
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
 * ```
 *
 * You should run the benchmarks on a physical device, not an Android emulator, because the
 * emulator doesn't represent real world performance and shares system resources with its host.
 *
 * For more information, see the [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * and the [instrumentation arguments documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args).
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() =
        benchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmark(compilationMode: CompilationMode) {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()


                // Wait until the main content is loaded. We use UIAutomator to ensure the Welcome screen
                // with the SwissTimePager is visible and settled. The Welcome screen shows a "Tap to zoom"
                // text when the pager is ready for interaction.
                val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

                // Wait for the target package to be in foreground
             //   device.wait(Until.hasObject(By.pkg(targetPackage).depth(0)), 5_000)

                // Wait for the "Tap to zoom" text which indicates pager + content composed
                val appeared = device.wait(Until.hasObject(By.text("Tap to zoom")), 10_000)

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
                val buttonAppeared = device.wait(Until.hasObject(By.text("Select this watch")), 10_000)
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
                var timeScreenShown = device.wait(Until.hasObject(By.desc("TimeScreen")), 4_000)
                if (!timeScreenShown) timeScreenShown = device.wait(Until.hasObject(By.text("Time")), 2_000)
                if (!timeScreenShown) timeScreenShown = device.wait(Until.hasObject(By.text("World Time")), 2_000)
                if (!timeScreenShown) timeScreenShown = device.wait(Until.hasObject(By.text("Time zones")), 2_000)
                if (!timeScreenShown) timeScreenShown = device.wait(Until.hasObject(By.textContains(":")), 2_000)

                if (!timeScreenShown) {
                    // Ensure previous button is gone and UI is idle before finishing collection
                    device.wait(Until.gone(By.text("Select this watch")), 5_000)
                    device.waitForIdle(1_000)
                }

                // Example: navigate to Watch List
                device.findObject(
                    androidx.test.uiautomator.By.text("Watches")
                )?.click()

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
                device.findObject(
                    androidx.test.uiautomator.By.textContains("Jurgsen")
                )?.click()

                // Back to list
                device.pressBack()

                // Go to Settings
                device.findObject(
                    androidx.test.uiautomator.By.text("Settings")
                )?.click()

                // Toggle Theme (match the exact label as shown on device)
                device.findObject(
                    androidx.test.uiautomator.By.textContains("Theme")
                )?.click()
                device.findObject(
                    androidx.test.uiautomator.By.text("System default")
                )?.click()
                device.pressBack()

                // Navigate to World Map
                device.findObject(
                    androidx.test.uiautomator.By.text("World Map")
                )?.click()

                // Return home
                device.pressBack()
                device.pressBack()

                // TODO Add interactions to wait for when your app is fully drawn.
                // The app is fully drawn when Activity.reportFullyDrawn is called.
                // For Jetpack Compose, you can use ReportDrawn, ReportDrawnWhen and ReportDrawnAfter
                // from the AndroidX Activity library.

                // Check the UiAutomator documentation for more information on how to
                // interact with the app.
                // https://d.android.com/training/testing/other-components/ui-automator
            }
        )
    }
}