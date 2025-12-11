package com.coroutines.worldclock.common.service

import java.util.TimeZone

/** Service for providing time zone information */
class TimeZoneService {

  /**
   * Get all available time zones on the device
   *
   * @return List of time zone IDs
   */
  fun getAllTimeZones(): List<String> {
    return TimeZone.getAvailableIDs().sorted()
  }

  /**
   * Get the display name for a time zone
   *
   * @param timeZoneId The time zone ID
   * @param isDaylightTime Whether daylight saving time is in effect
   * @return The display name of the time zone
   */
  fun getTimeZoneDisplayName(timeZoneId: String, isDaylightTime: Boolean): String {
    val timeZone = TimeZone.getTimeZone(timeZoneId)
    return timeZone.getDisplayName(isDaylightTime, TimeZone.LONG)
  }

  /**
   * Get the current system time zone ID
   *
   * @return The current system time zone ID
   */
  fun getCurrentTimeZoneId(): String {
    return TimeZone.getDefault().id
  }
}
