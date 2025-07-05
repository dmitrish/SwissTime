package com.coroutines.swisstime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.data.TimeZoneInfo
import com.coroutines.swisstime.data.TimeZoneRepository
import com.coroutines.swisstime.data.WatchPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone

/**
 * ViewModel for managing watch selection and time zone
 */
class WatchViewModel(
    private val watchPreferencesRepository: WatchPreferencesRepository,
    private val timeZoneRepository: TimeZoneRepository,
    val watches: List<WatchInfo>
) : ViewModel() {

    // Get the selected watch as a StateFlow
    val selectedWatch: StateFlow<WatchInfo?> = watchPreferencesRepository.selectedWatchName
        .map { watchName ->
            // Find the watch with the matching name
            watchName?.let { name ->
                watches.find { it.name == name }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Get the list of selected watches (watches that have been explicitly selected by the user)
    private val _selectedWatches = kotlinx.coroutines.flow.MutableStateFlow<List<WatchInfo>>(emptyList())
    val selectedWatches: StateFlow<List<WatchInfo>> = _selectedWatches

    init {
        // Initialize selected watches from preferences
        viewModelScope.launch {
            watchPreferencesRepository.selectedWatchNames.collect { watchNames ->
                val selectedWatchesList = watchNames.mapNotNull { name ->
                    watches.find { it.name == name }
                }
                _selectedWatches.value = selectedWatchesList
            }
        }
    }

    // Get all available time zones
    val allTimeZones: List<TimeZoneInfo> = timeZoneRepository.getAllTimeZones()

    // Cache for sorted time zones to avoid expensive sorting operations
    private var sortedTimeZonesCache: List<TimeZoneInfo>? = null

    // Get time zones sorted by their distance from GMT
    fun getSortedTimeZones(): List<TimeZoneInfo> {
        // Return cached result if available
        sortedTimeZonesCache?.let { return it }

        // Sort time zones by their distance from GMT
        val sorted = allTimeZones.sortedBy { timeZoneInfo ->
            // Check if we already have this time zone in the cache
            timeZoneCache[timeZoneInfo.id]?.rawOffset ?: run {
                // Get the raw offset in milliseconds from GMT
                val timeZone = TimeZone.getTimeZone(timeZoneInfo.id)
                // Cache the time zone for future use
                timeZoneCache[timeZoneInfo.id] = timeZone
                timeZone.rawOffset
            }
        }

        // Cache the result
        sortedTimeZonesCache = sorted
        return sorted
    }

    // Get the selected time zone as a StateFlow
    val selectedTimeZone: StateFlow<TimeZoneInfo> = timeZoneRepository.selectedTimeZoneInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimeZoneInfo(
                id = TimeZone.getDefault().id,
                displayName = TimeZone.getDefault().getDisplayName(
                    TimeZone.getDefault().inDaylightTime(java.util.Date()),
                    TimeZone.LONG
                )
            )
        )

    // Get the TimeZone object for the selected time zone
    val selectedTimeZoneObject: StateFlow<TimeZone> = timeZoneRepository.selectedTimeZoneId
        .map { timeZoneId ->
            TimeZone.getTimeZone(timeZoneId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimeZone.getDefault()
        )

    // Get the time format preference
    val useUsTimeFormat: StateFlow<Boolean> = watchPreferencesRepository.useUsTimeFormat
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // Default to US format
        )

    // Cache for TimeZone objects to avoid repeated lookups
    private val timeZoneCache = mutableMapOf<String, TimeZone>()

    // Get the TimeZone object for a specific watch
    fun getWatchTimeZone(watchName: String): StateFlow<TimeZone> {
        return watchPreferencesRepository.getWatchTimeZoneId(watchName, viewModelScope)
            .map { timeZoneId ->
                if (timeZoneId != null) {
                    // Check cache first
                    timeZoneCache[timeZoneId]?.let { return@map it }

                    // Create a new TimeZone object
                    val timeZone = TimeZone.getTimeZone(timeZoneId)

                    // Cache the result
                    timeZoneCache[timeZoneId] = timeZone
                    timeZone
                } else {
                    // Default to the selected time zone if no specific time zone is set for this watch
                    selectedTimeZoneObject.value
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = selectedTimeZoneObject.value
            )
    }

    // Cache for time zone info to avoid repeated expensive lookups
    private val timeZoneInfoCache = mutableMapOf<String, TimeZoneInfo>()

    // Get the TimeZoneInfo for a specific watch
    fun getWatchTimeZoneInfo(watchName: String): StateFlow<TimeZoneInfo> {
        // Eagerly try to get the time zone ID and create the TimeZoneInfo for initial value
        // This helps with performance during page transitions
        val initialTimeZoneId = watchPreferencesRepository.getWatchTimeZoneIdBlocking(watchName)
        val initialTimeZoneInfo = if (initialTimeZoneId != null) {
            // Check if we already have this time zone in the cache
            timeZoneInfoCache[initialTimeZoneId]?.let { return@let it } ?: run {
                // If not in cache, create it now and cache it
                val timeZone = TimeZone.getTimeZone(initialTimeZoneId)
                val isDaylightTime = timeZone.inDaylightTime(Date())
                val timeZoneInfo = TimeZoneInfo(
                    id = initialTimeZoneId,
                    displayName = timeZone.getDisplayName(isDaylightTime, TimeZone.LONG)
                )
                timeZoneInfoCache[initialTimeZoneId] = timeZoneInfo
                timeZoneInfo
            }
        } else {
            selectedTimeZone.value
        }

        // Use Flow for immediate updates when timezone is changed in the dropdown
        return watchPreferencesRepository.getWatchTimeZoneId(watchName, viewModelScope)
            .map { tzId ->
                if (tzId != null) {
                    // Check cache first
                    timeZoneInfoCache[tzId]?.let { return@map it }

                    val timeZone = TimeZone.getTimeZone(tzId)
                    val isDaylightTime = timeZone.inDaylightTime(Date())

                    // Create a new TimeZoneInfo directly without searching through all time zones
                    val timeZoneInfo = TimeZoneInfo(
                        id = tzId,
                        displayName = timeZone.getDisplayName(isDaylightTime, TimeZone.LONG)
                    )

                    // Cache the result
                    timeZoneInfoCache[tzId] = timeZoneInfo
                    timeZoneInfo
                } else {
                    // Default to the selected time zone info if no specific time zone is set for this watch
                    selectedTimeZone.value
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initialTimeZoneInfo
            )
    }

    // Get a cached TimeZoneInfo by ID, or create a new one if not in cache
    fun getCachedTimeZoneInfo(timeZoneId: String): TimeZoneInfo {
        // Check cache first
        timeZoneInfoCache[timeZoneId]?.let { return it }

        // If not in cache, create a new TimeZoneInfo
        val timeZone = TimeZone.getTimeZone(timeZoneId)
        val isDaylightTime = timeZone.inDaylightTime(Date())
        val timeZoneInfo = TimeZoneInfo(
            id = timeZoneId,
            displayName = timeZone.getDisplayName(isDaylightTime, TimeZone.LONG)
        )

        // Cache the result
        timeZoneInfoCache[timeZoneId] = timeZoneInfo
        return timeZoneInfo
    }

    // Get the time zone ID for a watch directly (blocking)
    // This is more efficient than using a flow during page transitions
    fun getWatchTimeZoneIdDirect(watchName: String): String? {
        return watchPreferencesRepository.getWatchTimeZoneIdBlocking(watchName)
    }

    // Get a TimeZone object directly without using flows
    // This is more efficient during page transitions
    fun getTimeZoneDirect(watchName: String): TimeZone {
        val timeZoneId = getWatchTimeZoneIdDirect(watchName)
        if (timeZoneId != null) {
            // Check cache first
            timeZoneCache[timeZoneId]?.let { return it }

            // Create a new TimeZone object
            val timeZone = TimeZone.getTimeZone(timeZoneId)

            // Cache the result
            timeZoneCache[timeZoneId] = timeZone
            return timeZone
        }

        // Fallback to default
        return TimeZone.getDefault()
    }

    // Save the time zone for a specific watch
    fun saveWatchTimeZone(watchName: String, timeZoneId: String) {
        viewModelScope.launch {
            watchPreferencesRepository.saveWatchTimeZone(watchName, timeZoneId)
        }
    }

    // Save the selected watch
    fun saveSelectedWatch(watch: WatchInfo) {
        viewModelScope.launch {
            watchPreferencesRepository.saveSelectedWatch(watch.name)

            // Add the watch to the selected watches list if it's not already there
            val currentWatches = _selectedWatches.value
            if (!currentWatches.contains(watch)) {
                _selectedWatches.value = currentWatches + watch

                // Persist the updated list of selected watches
                watchPreferencesRepository.addSelectedWatch(watch.name)
            }
        }
    }

    // Clear the selected watch
    fun clearSelectedWatch() {
        viewModelScope.launch {
            watchPreferencesRepository.clearSelectedWatch()

            // Also remove the watch from the selected watches list
            val selectedWatch = selectedWatch.value
            if (selectedWatch != null) {
                val currentWatches = _selectedWatches.value
                _selectedWatches.value = currentWatches.filter { it.name != selectedWatch.name }

                // Persist the removal of the watch from the selected watches list
                watchPreferencesRepository.removeSelectedWatch(selectedWatch.name)
            }
        }
    }

    // Clear all selected watches
    fun clearAllSelectedWatches() {
        viewModelScope.launch {
            _selectedWatches.value = emptyList()

            // Persist the clearing of all selected watches
            watchPreferencesRepository.clearAllSelectedWatches()
        }
    }

    // Save the selected time zone
    fun saveSelectedTimeZone(timeZoneId: String) {
        viewModelScope.launch {
            timeZoneRepository.saveSelectedTimeZone(timeZoneId)
        }
    }

    // Save the time format preference
    fun saveTimeFormat(useUsFormat: Boolean) {
        viewModelScope.launch {
            watchPreferencesRepository.saveTimeFormat(useUsFormat)
        }
    }

    /**
     * Factory for creating WatchViewModel instances
     */
    class Factory(
        private val watchPreferencesRepository: WatchPreferencesRepository,
        private val timeZoneRepository: TimeZoneRepository,
        private val watches: List<WatchInfo>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchViewModel::class.java)) {
                return WatchViewModel(watchPreferencesRepository, timeZoneRepository, watches) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
