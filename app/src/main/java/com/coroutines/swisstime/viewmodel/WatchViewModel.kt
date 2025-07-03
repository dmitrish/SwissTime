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

    // Get the TimeZone object for a specific watch
    fun getWatchTimeZone(watchName: String): StateFlow<TimeZone> {
        return watchPreferencesRepository.getWatchTimeZoneId(watchName, viewModelScope)
            .map { timeZoneId ->
                if (timeZoneId != null) {
                    TimeZone.getTimeZone(timeZoneId)
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

    // Get the TimeZoneInfo for a specific watch
    fun getWatchTimeZoneInfo(watchName: String): StateFlow<TimeZoneInfo> {
        return watchPreferencesRepository.getWatchTimeZoneId(watchName, viewModelScope)
            .map { timeZoneId ->
                if (timeZoneId != null) {
                    val timeZone = TimeZone.getTimeZone(timeZoneId)
                    val isDaylightTime = timeZone.inDaylightTime(Date())

                    // Get all time zones to find the matching one
                    val allTimeZones = timeZoneRepository.getAllTimeZones()
                    allTimeZones.find { it.id == timeZoneId } ?: TimeZoneInfo(
                        id = timeZoneId,
                        displayName = timeZone.getDisplayName(isDaylightTime, TimeZone.LONG)
                    )
                } else {
                    // Default to the selected time zone info if no specific time zone is set for this watch
                    selectedTimeZone.value
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = selectedTimeZone.value
            )
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
