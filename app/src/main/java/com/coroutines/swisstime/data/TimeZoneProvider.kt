package com.coroutines.swisstime.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import java.util.Calendar
import java.util.TimeZone

/**
 * CompositionLocal to provide the current timezone to all composables in the hierarchy This avoids
 * the need to modify TimeZone.getDefault() which causes race conditions
 */
val LocalTimeZone = compositionLocalOf { TimeZone.getDefault() }

/**
 * CompositionLocal to provide a Calendar factory that uses the current timezone This ensures all
 * Calendar instances use the correct timezone without relying on TimeZone.getDefault()
 */
val LocalCalendarProvider = compositionLocalOf<() -> Calendar> { { Calendar.getInstance() } }

/**
 * A composable that provides a timezone context to its content All composables within this context
 * will have access to the timezone via LocalTimeZone and can create Calendar instances with the
 * correct timezone via LocalCalendarProvider
 */
@Composable
fun TimeZoneProvider(timeZone: TimeZone, content: @Composable () -> Unit) {
  // Create a Calendar factory that uses the specified timezone
  val calendarProvider = remember(timeZone) { { Calendar.getInstance(timeZone) } }

  // Provide the timezone and Calendar factory to all composables in the hierarchy
  CompositionLocalProvider(
    LocalTimeZone provides timeZone,
    LocalCalendarProvider provides calendarProvider
  ) {
    content()
  }
}

/**
 * Get a Calendar instance with the current timezone from the CompositionLocal This should be used
 * instead of Calendar.getInstance() to ensure the correct timezone is used
 */
@Composable
fun rememberCurrentTimeZoneCalendar(): Calendar {
  val calendarProvider = LocalCalendarProvider.current
  return remember { calendarProvider() }
}
