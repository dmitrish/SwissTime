package com.coroutines.swisstime.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.TimeZone

/**
 * Repository for managing time zone data
 */
class TimeZoneRepository(
    private val timeZoneService: TimeZoneService,
    private val watchPreferencesRepository: WatchPreferencesRepository
) {
    // Cache for all time zones to avoid expensive repeated calculations
    private var cachedTimeZones: List<TimeZoneInfo>? = null

    // Get all available time zones with their display names
    fun getAllTimeZones(): List<TimeZoneInfo> {
        // Return cached result if available
        cachedTimeZones?.let { return it }

        val timeZoneIds = timeZoneService.getAllTimeZones()
        val isDaylightTime = TimeZone.getDefault().inDaylightTime(Date())

        val result = timeZoneIds.map { id ->
            TimeZoneInfo(
                id = id,
                displayName = timeZoneService.getTimeZoneDisplayName(id, isDaylightTime)
            )
        }.distinctBy { it.displayName } // Filter out duplicates based on display name

        // Cache the result
        cachedTimeZones = result
        return result
    }

    // Get the selected time zone ID
    val selectedTimeZoneId: Flow<String> = watchPreferencesRepository.selectedTimeZoneId
        .map { it ?: timeZoneService.getCurrentTimeZoneId() }

    // Get the selected time zone info
    val selectedTimeZoneInfo: Flow<TimeZoneInfo> = selectedTimeZoneId.map { id ->
        val isDaylightTime = TimeZone.getTimeZone(id).inDaylightTime(Date())
        TimeZoneInfo(
            id = id,
            displayName = timeZoneService.getTimeZoneDisplayName(id, isDaylightTime)
        )
    }

    // Save the selected time zone
    suspend fun saveSelectedTimeZone(timeZoneId: String) {
        watchPreferencesRepository.saveSelectedTimeZone(timeZoneId)
    }

    // Get a TimeZone object for the selected time zone ID
    fun getTimeZone(timeZoneId: String): TimeZone {
        return TimeZone.getTimeZone(timeZoneId)
    }
}

/**
 * Data class to hold time zone information
 */
data class TimeZoneInfo(
    val id: String,
    val displayName: String
)
