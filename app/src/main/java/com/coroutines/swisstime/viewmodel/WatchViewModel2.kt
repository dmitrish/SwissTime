package com.coroutines.swisstime.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coroutines.worldclock.common.model.WatchInfo
import com.coroutines.worldclock.common.repository.TimeZoneInfo
import com.coroutines.worldclock.common.repository.TimeZoneRepository
import com.coroutines.worldclock.common.repository.WatchPreferencesRepository
import java.util.Date
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchViewModel(
  private val watchPreferencesRepository: WatchPreferencesRepository,
  private val timeZoneRepository: TimeZoneRepository,
  val watches: List<WatchInfo>
) : ViewModel() {

  // Selected watch name mapped to WatchInfo
  val selectedWatch: StateFlow<WatchInfo?> =
    watchPreferencesRepository.selectedWatchName
      .map { watchName -> watchName?.let { name -> watches.find { it.name == name } } }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
      )

  // Explicitly selected watches list
  private val _selectedWatches = MutableStateFlow<List<WatchInfo>>(emptyList())
  val selectedWatches: StateFlow<List<WatchInfo>> = _selectedWatches

  // Flag when selectedWatches loaded
  private val _selectedWatchesLoaded = MutableStateFlow(false)
  val selectedWatchesLoaded: StateFlow<Boolean> = _selectedWatchesLoaded

  init {
    // Load selected watches from repository
    viewModelScope.launch {
      watchPreferencesRepository.selectedWatchNames.collect { watchNames ->
        val list = watchNames.mapNotNull { n -> watches.find { it.name == n } }
        _selectedWatches.value = list
        if (!_selectedWatchesLoaded.value) _selectedWatchesLoaded.value = true
      }
    }
  }

  // Current watch in focus (used by landscape UI)
  var currentWatchInFocus: WatchInfo? by mutableStateOf(null)
    private set

  fun updateCurrentWatch(watchInfo: WatchInfo) {
    currentWatchInFocus = watchInfo
  }

  // All time zones and a cached sorted list
  val allTimeZones: List<TimeZoneInfo> = timeZoneRepository.getAllTimeZones()
  private var sortedTimeZonesCache: List<TimeZoneInfo>? = null

  fun getSortedTimeZones(): List<TimeZoneInfo> {
    sortedTimeZonesCache?.let {
      return it
    }
    val sorted =
      allTimeZones.sortedBy { tzInfo ->
        timeZoneCache[tzInfo.id]?.rawOffset
          ?: run {
            val tz = TimeZone.getTimeZone(tzInfo.id)
            timeZoneCache[tzInfo.id] = tz
            tz.rawOffset
          }
      }
    sortedTimeZonesCache = sorted
    return sorted
  }

  // Selected timezone (global) and object form
  val selectedTimeZone: StateFlow<TimeZoneInfo> =
    timeZoneRepository.selectedTimeZoneInfo.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue =
        TimeZoneInfo(
          id = TimeZone.getDefault().id,
          displayName =
            TimeZone.getDefault()
              .getDisplayName(TimeZone.getDefault().inDaylightTime(Date()), TimeZone.LONG)
        )
    )

  val selectedTimeZoneObject: StateFlow<TimeZone> =
    timeZoneRepository.selectedTimeZoneId
      .map { id -> TimeZone.getTimeZone(id) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimeZone.getDefault()
      )

  // Preferences
  val useUsTimeFormat: StateFlow<Boolean> =
    watchPreferencesRepository.useUsTimeFormat.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      true
    )

  val useDoubleTapForRemoval: StateFlow<Boolean> =
    watchPreferencesRepository.useDoubleTapForRemoval.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      false
    )

  // Internal caches
  private val timeZoneCache = mutableMapOf<String, TimeZone>()
  private val timeZoneInfoCache = mutableMapOf<String, TimeZoneInfo>()

  // Per-watch timezone StateFlows seeded with direct value and never falling back to global/default
  private val watchZoneFlows = mutableMapOf<String, MutableStateFlow<TimeZone>>()

  fun getWatchTimeZone(watchName: String): StateFlow<TimeZone> {
    return synchronized(watchZoneFlows) {
      watchZoneFlows.getOrPut(watchName) {
        val seed = getTimeZoneDirect(watchName)
        MutableStateFlow(seed).also { state ->
          viewModelScope.launch {
            // Use a direct DataStore flow to avoid replay=0 gaps; add validation and distinct
            watchPreferencesRepository
              .watchTimeZoneIdFlow(watchName)
              .map { id ->
                if (id != null && java.time.ZoneId.getAvailableZoneIds().contains(id)) {
                  // Cache TimeZone objects by id
                  timeZoneCache[id]
                    ?: TimeZone.getTimeZone(id).also { tz -> timeZoneCache[id] = tz }
                } else {
                  // keep current (no fallback to global)
                  state.value
                }
              }
              .distinctUntilChanged { a, b -> a.id == b.id }
              .collect { tz -> state.value = tz }
          }
        }
      }
    }
  }

  // TimeZoneInfo per watch built from the per-watch timezone flow
  fun getWatchTimeZoneInfo(watchName: String): StateFlow<TimeZoneInfo> {
    val seedTz = getTimeZoneDirect(watchName)
    val seedInfo = getCachedTimeZoneInfo(seedTz.id)
    return getWatchTimeZone(watchName)
      .map { tz ->
        val id = tz.id
        timeZoneInfoCache[id]
          ?: run {
            val isDst = tz.inDaylightTime(Date())
            TimeZoneInfo(id = id, displayName = tz.getDisplayName(isDst, TimeZone.LONG)).also {
              timeZoneInfoCache[id] = it
            }
          }
      }
      .distinctUntilChanged { a, b -> a.id == b.id }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), seedInfo)
  }

  fun getCachedTimeZoneInfo(timeZoneId: String): TimeZoneInfo {
    timeZoneInfoCache[timeZoneId]?.let {
      return it
    }
    val tz =
      timeZoneCache[timeZoneId]
        ?: TimeZone.getTimeZone(timeZoneId).also { timeZoneCache[timeZoneId] = it }
    val isDst = tz.inDaylightTime(Date())
    return TimeZoneInfo(timeZoneId, tz.getDisplayName(isDst, TimeZone.LONG)).also {
      timeZoneInfoCache[timeZoneId] = it
    }
  }

  fun getWatchTimeZoneIdDirect(watchName: String): String? =
    watchPreferencesRepository.getWatchTimeZoneIdBlocking(watchName)

  fun getTimeZoneDirect(watchName: String): TimeZone {
    val id = getWatchTimeZoneIdDirect(watchName)
    if (id != null && java.time.ZoneId.getAvailableZoneIds().contains(id)) {
      timeZoneCache[id]?.let {
        return it
      }
      return TimeZone.getTimeZone(id).also { timeZoneCache[id] = it }
    }
    return TimeZone.getDefault()
  }

  fun saveWatchTimeZone(watchName: String, timeZoneId: String) {
    viewModelScope.launch { watchPreferencesRepository.saveWatchTimeZone(watchName, timeZoneId) }
  }

  fun saveSelectedWatch(watch: WatchInfo) {
    viewModelScope.launch {
      watchPreferencesRepository.saveSelectedWatch(watch.name)
      val current = _selectedWatches.value
      if (!current.any { it.name == watch.name }) {
        _selectedWatches.value = current + watch
        watchPreferencesRepository.addSelectedWatch(watch.name)
      }
    }
  }

  fun clearSelectedWatch() {
    viewModelScope.launch {
      val selected = selectedWatch.value
      watchPreferencesRepository.clearSelectedWatch()
      selected?.let {
        val current = _selectedWatches.value
        _selectedWatches.value = current.filter { w -> w.name != it.name }
        watchPreferencesRepository.removeSelectedWatch(it.name)
      }
    }
  }

  fun clearAllSelectedWatches() {
    viewModelScope.launch {
      _selectedWatches.value = emptyList()
      watchPreferencesRepository.clearAllSelectedWatches()
    }
  }

  fun saveSelectedTimeZone(timeZoneId: String) {
    viewModelScope.launch { timeZoneRepository.saveSelectedTimeZone(timeZoneId) }
  }

  fun saveTimeFormat(useUsFormat: Boolean) {
    viewModelScope.launch { watchPreferencesRepository.saveTimeFormat(useUsFormat) }
  }

  fun saveWatchRemovalGesture(useDoubleTap: Boolean) {
    viewModelScope.launch { watchPreferencesRepository.saveWatchRemovalGesture(useDoubleTap) }
  }

  fun toggleWatchSelection(watch: WatchInfo): Boolean {
    val current = _selectedWatches.value
    val isSelected = current.any { it.name == watch.name }
    viewModelScope.launch {
      if (isSelected) {
        val updated = current.filter { it.name != watch.name }
        _selectedWatches.value = updated
        watchPreferencesRepository.clearAllSelectedWatches()
        updated.forEach { watchPreferencesRepository.addSelectedWatch(it.name) }
      } else {
        _selectedWatches.value = current + watch
        watchPreferencesRepository.addSelectedWatch(watch.name)
      }
    }
    return !isSelected
  }

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
