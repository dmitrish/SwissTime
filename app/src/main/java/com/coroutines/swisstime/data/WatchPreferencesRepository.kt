package com.coroutines.swisstime.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "watch_preferences")

/**
 * Repository for managing watch preferences
 */
class WatchPreferencesRepository(private val context: Context) {

    // Define preference keys
    companion object {
        private val SELECTED_WATCH_KEY = stringPreferencesKey("selected_watch")
        private val SELECTED_TIMEZONE_KEY = stringPreferencesKey("selected_timezone")
        private val SELECTED_WATCHES_KEY = stringSetPreferencesKey("selected_watches")
        private val USE_US_TIME_FORMAT_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("use_us_time_format")
        private val USE_DOUBLE_TAP_FOR_REMOVAL_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("use_double_tap_for_removal")

        // Key prefix for watch-specific timezone preferences
        private const val WATCH_TIMEZONE_PREFIX = "watch_timezone_"
    }

    // Get the currently selected watch name
    val selectedWatchName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_WATCH_KEY]
        }

    // Get the currently selected timezone ID
    val selectedTimeZoneId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_TIMEZONE_KEY]
        }

    // Get the time format preference (true for US format, false for International format)
    val useUsTimeFormat: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_US_TIME_FORMAT_KEY] ?: true // Default to US format
        }

    // Get the watch removal gesture preference (true for Double Tap, false for Long Press)
    val useDoubleTapForRemoval: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_DOUBLE_TAP_FOR_REMOVAL_KEY] ?: false // Default to Long Press
        }

    // Save the selected watch name
    suspend fun saveSelectedWatch(watchName: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_WATCH_KEY] = watchName
        }
    }

    // Save the selected timezone ID
    suspend fun saveSelectedTimeZone(timeZoneId: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_TIMEZONE_KEY] = timeZoneId
        }
    }

    // Save the time format preference
    suspend fun saveTimeFormat(useUsFormat: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_US_TIME_FORMAT_KEY] = useUsFormat
        }
    }

    // Save the watch removal gesture preference
    suspend fun saveWatchRemovalGesture(useDoubleTap: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_DOUBLE_TAP_FOR_REMOVAL_KEY] = useDoubleTap
        }
    }


    /*fun getWatchTimeZoneId(watchName: String): Flow<String> = context.dataStore.data
        .map { preferences ->
            val key = stringPreferencesKey("${WATCH_TIMEZONE_PREFIX}${watchName}")
            preferences[key] ?: ""
        } */

    // Get the timezone ID for a specific watch
    fun getWatchTimeZoneId(watchName: String, scope: CoroutineScope): SharedFlow<String?> = context.dataStore.data
        .map { preferences ->
            val key = stringPreferencesKey("${WATCH_TIMEZONE_PREFIX}${watchName}")
            preferences[key]
        }
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 0
        ) 



    fun getWatchTimeZoneIdBlocking(watchName: String): String? = runBlocking {
        context.dataStore.data
            .map { preferences ->
                val key = stringPreferencesKey("${WATCH_TIMEZONE_PREFIX}${watchName}")
                preferences[key]
            }.first()
    }

    // Save the timezone ID for a specific watch
    suspend fun saveWatchTimeZone(watchName: String, timeZoneId: String) {
        context.dataStore.edit { preferences ->
            val key = stringPreferencesKey("${WATCH_TIMEZONE_PREFIX}${watchName}")
            preferences[key] = timeZoneId
        }
    }

    // Clear the selected watch
    suspend fun clearSelectedWatch() {
        context.dataStore.edit { preferences ->
            preferences.remove(SELECTED_WATCH_KEY)
        }
    }

    // Clear the selected timezone
    suspend fun clearSelectedTimeZone() {
        context.dataStore.edit { preferences ->
            preferences.remove(SELECTED_TIMEZONE_KEY)
        }
    }

    // Clear the timezone for a specific watch
    suspend fun clearWatchTimeZone(watchName: String) {
        context.dataStore.edit { preferences ->
            val key = stringPreferencesKey("${WATCH_TIMEZONE_PREFIX}${watchName}")
            preferences.remove(key)
        }
    }

    // Get the list of selected watch names
    val selectedWatchNames: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_WATCHES_KEY] ?: emptySet()
        }

    // Save the list of selected watch names
    suspend fun saveSelectedWatches(watchNames: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_WATCHES_KEY] = watchNames
        }
    }

    // Add a watch to the list of selected watches
    suspend fun addSelectedWatch(watchName: String) {
        context.dataStore.edit { preferences ->
            val currentWatches = preferences[SELECTED_WATCHES_KEY] ?: emptySet()
            preferences[SELECTED_WATCHES_KEY] = currentWatches + watchName
        }
    }

    // Remove a watch from the list of selected watches
    suspend fun removeSelectedWatch(watchName: String) {
        context.dataStore.edit { preferences ->
            val currentWatches = preferences[SELECTED_WATCHES_KEY] ?: emptySet()
            preferences[SELECTED_WATCHES_KEY] = currentWatches - watchName
        }
    }

    // Clear all selected watches
    suspend fun clearAllSelectedWatches() {
        context.dataStore.edit { preferences ->
            preferences.remove(SELECTED_WATCHES_KEY)
        }
    }
}
