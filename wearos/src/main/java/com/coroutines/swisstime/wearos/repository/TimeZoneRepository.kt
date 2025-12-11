package com.coroutines.swisstime.wearos.repository

import com.coroutines.swisstime.wearos.service.TimeZoneService
import java.util.Date
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Repository for managing time zone data */
class TimeZoneRepository(private val timeZoneService: TimeZoneService) {
  // Cache for all time zones to avoid expensive repeated calculations
  private var cachedTimeZones: List<TimeZoneInfo>? = null

  // Selected time zone ID
  private val _selectedTimeZoneId = MutableStateFlow(timeZoneService.getCurrentTimeZoneId())
  val selectedTimeZoneId: StateFlow<String> = _selectedTimeZoneId.asStateFlow()

  // Get all available time zones with their display names
  fun getAllTimeZones(): List<TimeZoneInfo> {
    // Return cached result if available
    cachedTimeZones?.let {
      return it
    }

    val timeZoneIds = timeZoneService.getAllTimeZones()
    val isDaylightTime = TimeZone.getDefault().inDaylightTime(Date())

    val result =
      timeZoneIds
        .map { id ->
          TimeZoneInfo(
            id = id,
            displayName = timeZoneService.getTimeZoneDisplayName(id, isDaylightTime)
          )
        }
        .distinctBy { it.displayName } // Filter out duplicates based on display name

    // Cache the result
    cachedTimeZones = result
    return result
  }

  // Get the selected time zone info
  fun getSelectedTimeZoneInfo(): TimeZoneInfo {
    val id = _selectedTimeZoneId.value
    val isDaylightTime = TimeZone.getTimeZone(id).inDaylightTime(Date())
    return TimeZoneInfo(
      id = id,
      displayName = timeZoneService.getTimeZoneDisplayName(id, isDaylightTime)
    )
  }

  // Save the selected time zone
  fun saveSelectedTimeZone(timeZoneId: String) {
    _selectedTimeZoneId.value = timeZoneId
  }

  // Get a TimeZone object for the selected time zone ID
  fun getSelectedTimeZone(): TimeZone {
    return TimeZone.getTimeZone(_selectedTimeZoneId.value)
  }

  // Get a TimeZone object for a specific time zone ID
  fun getTimeZone(timeZoneId: String): TimeZone {
    return TimeZone.getTimeZone(timeZoneId)
  }
}

/** Data class to hold time zone information */
data class TimeZoneInfo(val id: String, val displayName: String)
