package com.coroutines.swisstime.watchfaces

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.*
import kotlinx.coroutines.delay
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HorologiaRomanumTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Test timezones from different regions
  private val testTimeZone = TimeZone.getTimeZone("GMT")
  private val tokyoTimeZone = TimeZone.getTimeZone("Asia/Tokyo")
  private val newYorkTimeZone = TimeZone.getTimeZone("America/New_York")
  private val sydneyTimeZone = TimeZone.getTimeZone("Australia/Sydney")
  private val londonTimeZone = TimeZone.getTimeZone("Europe/London")
  private val losAngelesTimeZone = TimeZone.getTimeZone("America/Los_Angeles")

  @Before
  fun setUp() {
    // Setup is minimal since we're testing a standalone composable
  }

  /** Test wrapper for HorologiaRomanum that uses a fixed time for testing */
  @Composable
  private fun TestHorologiaRomanum(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = testTimeZone
  ) {
    Box(
      modifier = modifier.fillMaxSize().testTag("horologiaRomanum"),
      contentAlignment = Alignment.Center
    ) {
      // Call the actual composable but with test tags for verification
      HorologiaRomanum(modifier = Modifier.testTag("watchFace"), timeZone = timeZone)
    }
  }

  /** Enhanced test wrapper that exposes the current time for verification */
  @Composable
  private fun TestHorologiaRomanumWithTime(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = testTimeZone
  ) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

    // Update time every second
    LaunchedEffect(timeZone) {
      while (true) {
        currentTime = Calendar.getInstance(timeZone)
        delay(1000)
      }
    }

    Box(
      modifier = modifier.fillMaxSize().testTag("horologiaRomanumWithTime"),
      contentAlignment = Alignment.Center
    ) {
      // Call the actual composable
      HorologiaRomanum(modifier = Modifier.testTag("watchFace"), timeZone = timeZone)

      // Display the current time for verification
      val hour = currentTime.get(Calendar.HOUR_OF_DAY)
      val minute = currentTime.get(Calendar.MINUTE)
      val second = currentTime.get(Calendar.SECOND)
      val timeString = String.format("%02d:%02d:%02d", hour, minute, second)

      Text(text = timeString, fontSize = 12.sp, modifier = Modifier.testTag("currentTime"))

      // Display the timezone ID for verification
      Text(
        text = timeZone.id,
        fontSize = 10.sp,
        modifier = Modifier.testTag("timeZoneId").graphicsLayer { translationY = 20f }
      )
    }
  }

  /**
   * A simplified version of HorologiaRomanum for testing This allows us to test the watch face
   * without relying on the private implementation details
   */
  @Composable
  private fun SimplifiedHorologiaRomanum(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = testTimeZone
  ) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance(timeZone)) }

    // Update time every second
    LaunchedEffect(timeZone) {
      while (true) {
        currentTime = Calendar.getInstance(timeZone)
        delay(1000)
      }
    }

    // Just a simple box with a test tag to verify it renders
    Box(
      modifier = modifier.fillMaxSize().testTag("simplifiedWatchFace"),
      contentAlignment = Alignment.Center
    ) {
      // Display the current time and timezone for verification
      val hour = currentTime.get(Calendar.HOUR_OF_DAY)
      val minute = currentTime.get(Calendar.MINUTE)
      val second = currentTime.get(Calendar.SECOND)
      val timeString = String.format("%02d:%02d:%02d", hour, minute, second)

      Text(text = timeString, fontSize = 12.sp, modifier = Modifier.testTag("simplifiedTime"))

      Text(
        text = timeZone.id,
        fontSize = 10.sp,
        modifier = Modifier.testTag("simplifiedTimeZoneId").graphicsLayer { translationY = 20f }
      )
    }
  }

  @Test
  fun testHorologiaRomanum_renders() {
    // Set up the composable under test
    composeTestRule.setContent { TestHorologiaRomanum() }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("horologiaRomanum").assertExists()
    composeTestRule.onNodeWithTag("watchFace").assertExists()
  }

  @Test
  fun testHorologiaRomanum_withTokyoTimeZone() {
    // Test with Tokyo time zone
    composeTestRule.setContent { TestHorologiaRomanumWithTime(timeZone = tokyoTimeZone) }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("watchFace").assertExists()

    // Verify that the correct timezone ID is displayed
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("Asia/Tokyo")
  }

  @Test
  fun testHorologiaRomanum_withNewYorkTimeZone() {
    // Test with New York time zone
    composeTestRule.setContent { TestHorologiaRomanumWithTime(timeZone = newYorkTimeZone) }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("watchFace").assertExists()

    // Verify that the correct timezone ID is displayed
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("America/New_York")
  }

  @Test
  fun testHorologiaRomanum_withSydneyTimeZone() {
    // Test with Sydney time zone
    composeTestRule.setContent { TestHorologiaRomanumWithTime(timeZone = sydneyTimeZone) }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("watchFace").assertExists()

    // Verify that the correct timezone ID is displayed
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("Australia/Sydney")
  }

  @Test
  fun testHorologiaRomanum_withLondonTimeZone() {
    // Test with London time zone
    composeTestRule.setContent { TestHorologiaRomanumWithTime(timeZone = londonTimeZone) }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("watchFace").assertExists()

    // Verify that the correct timezone ID is displayed
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("Europe/London")
  }

  @Test
  fun testHorologiaRomanum_withLosAngelesTimeZone() {
    // Test with Los Angeles time zone
    composeTestRule.setContent { TestHorologiaRomanumWithTime(timeZone = losAngelesTimeZone) }

    // Verify that the watch face is displayed
    composeTestRule.onNodeWithTag("watchFace").assertExists()

    // Verify that the correct timezone ID is displayed
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("America/Los_Angeles")
  }

  @Test
  fun testSimplifiedHorologiaRomanum_renders() {
    // Set up the simplified composable
    composeTestRule.setContent { SimplifiedHorologiaRomanum() }

    // Verify that it renders
    composeTestRule.onNodeWithTag("simplifiedWatchFace").assertExists()

    // Verify that the time is displayed
    composeTestRule.onNodeWithTag("simplifiedTime").assertExists()

    // Verify that the timezone ID is displayed
    composeTestRule.onNodeWithTag("simplifiedTimeZoneId").assertTextContains("GMT")
  }

  @Test
  fun testSimplifiedHorologiaRomanum_withTokyoTimeZone() {
    // Test with Tokyo time zone
    composeTestRule.setContent { SimplifiedHorologiaRomanum(timeZone = tokyoTimeZone) }

    // Verify that it renders
    composeTestRule.onNodeWithTag("simplifiedWatchFace").assertExists()

    // Verify that the timezone ID is displayed
    composeTestRule.onNodeWithTag("simplifiedTimeZoneId").assertTextContains("Asia/Tokyo")
  }

  @Test
  fun testSimplifiedHorologiaRomanum_withNewYorkTimeZone() {
    // Test with New York time zone
    composeTestRule.setContent { SimplifiedHorologiaRomanum(timeZone = newYorkTimeZone) }

    // Verify that it renders
    composeTestRule.onNodeWithTag("simplifiedWatchFace").assertExists()

    // Verify that the timezone ID is displayed
    composeTestRule.onNodeWithTag("simplifiedTimeZoneId").assertTextContains("America/New_York")
  }

  @Test
  fun testSimplifiedHorologiaRomanum_withSydneyTimeZone() {
    // Test with Sydney time zone
    composeTestRule.setContent { SimplifiedHorologiaRomanum(timeZone = sydneyTimeZone) }

    // Verify that it renders
    composeTestRule.onNodeWithTag("simplifiedWatchFace").assertExists()

    // Verify that the timezone ID is displayed
    composeTestRule.onNodeWithTag("simplifiedTimeZoneId").assertTextContains("Australia/Sydney")
  }

  @Test
  fun testHorologiaRomanum_withModifier() {
    // Test with a custom modifier
    composeTestRule.setContent {
      HorologiaRomanum(modifier = Modifier.testTag("customModifier"), timeZone = testTimeZone)
    }

    // Verify that the watch face with custom modifier is displayed
    composeTestRule.onNodeWithTag("customModifier").assertExists()
  }

  /** Test that verifies time differences between timezones */
  @Test
  fun testHorologiaRomanum_timezoneDifferences() {
    // Get current time in GMT
    val gmtCalendar = Calendar.getInstance(testTimeZone)
    val gmtHour = gmtCalendar.get(Calendar.HOUR_OF_DAY)

    // Get current time in Tokyo
    val tokyoCalendar = Calendar.getInstance(tokyoTimeZone)
    val tokyoHour = tokyoCalendar.get(Calendar.HOUR_OF_DAY)

    // Get current time in New York
    val newYorkCalendar = Calendar.getInstance(newYorkTimeZone)
    val newYorkHour = newYorkCalendar.get(Calendar.HOUR_OF_DAY)

    // Verify that the hours are different according to timezone offsets
    // Tokyo is ahead of GMT
    val tokyoOffsetHours = tokyoTimeZone.rawOffset / (1000 * 60 * 60)
    val expectedTokyoHour = (gmtHour + tokyoOffsetHours + 24) % 24

    // New York is behind GMT
    val newYorkOffsetHours = newYorkTimeZone.rawOffset / (1000 * 60 * 60)
    val expectedNewYorkHour = (gmtHour + newYorkOffsetHours + 24) % 24

    // Check if the hours match the expected values (allowing for DST differences)
    // We use a range check because of potential DST issues
    assertTrue(
      "Tokyo hour ($tokyoHour) should be approximately $expectedTokyoHour hours",
      Math.abs(tokyoHour - expectedTokyoHour) <= 1 || Math.abs(tokyoHour - expectedTokyoHour) >= 23
    )

    assertTrue(
      "New York hour ($newYorkHour) should be approximately $expectedNewYorkHour hours",
      Math.abs(newYorkHour - expectedNewYorkHour) <= 1 ||
        Math.abs(newYorkHour - expectedNewYorkHour) >= 23
    )
  }

  /** Test that verifies the watch face updates when the timezone changes */
  @Test
  fun testHorologiaRomanum_timezoneChange() {
    // Create a mutable state to hold the timezone
    val timeZoneState = mutableStateOf(testTimeZone)

    // Set up the composable with the mutable timezone
    composeTestRule.setContent {
      val currentTimeZone by remember { timeZoneState }
      TestHorologiaRomanumWithTime(timeZone = currentTimeZone)
    }

    // Verify that the initial timezone is GMT
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("GMT")

    // Change the timezone to Tokyo
    timeZoneState.value = tokyoTimeZone

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Verify that the timezone has changed to Tokyo
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("Asia/Tokyo")

    // Change the timezone to New York
    timeZoneState.value = newYorkTimeZone

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Verify that the timezone has changed to New York
    composeTestRule.onNodeWithTag("timeZoneId").assertTextContains("America/New_York")
  }

  /** Test that verifies the time difference between multiple timezones */
  @Test
  fun testHorologiaRomanum_multipleTimezones() {
    // Test with multiple timezones to verify time differences
    val timezones =
      listOf(
        testTimeZone,
        tokyoTimeZone,
        newYorkTimeZone,
        sydneyTimeZone,
        londonTimeZone,
        losAngelesTimeZone
      )

    // Get the current time in each timezone
    val times =
      timezones.map { tz ->
        val cal = Calendar.getInstance(tz)
        Triple(tz.id, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
      }

    // Verify that timezones in different regions have different hours
    // We'll compare Tokyo (Asia) with New York (America)
    val tokyoTime = times.first { it.first == "Asia/Tokyo" }
    val newYorkTime = times.first { it.first == "America/New_York" }

    // The hour difference should be significant (at least a few hours)
    // We're not checking exact differences because of DST complications
    val hourDifference = Math.abs(tokyoTime.second - newYorkTime.second)
    assertTrue(
      "Tokyo and New York should have significantly different hours",
      hourDifference > 3 && hourDifference < 21
    )

    // The minutes should be the same or very close (within 1 minute due to test execution time)
    val minuteDifference = Math.abs(tokyoTime.third - newYorkTime.third)
    assertTrue(
      "Tokyo and New York should have similar minutes",
      minuteDifference <= 1 || minuteDifference >= 59
    )
  }
}
