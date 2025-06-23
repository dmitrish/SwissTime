package com.coroutines.swisstime.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance at the top level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "watch_preferences")

/**
 * Repository for managing watch preferences
 */
class WatchPreferencesRepository(private val context: Context) {

    // Define preference keys
    companion object {
        private val SELECTED_WATCH_KEY = stringPreferencesKey("selected_watch")
    }

    // Get the currently selected watch name
    val selectedWatchName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_WATCH_KEY]
        }

    // Save the selected watch name
    suspend fun saveSelectedWatch(watchName: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_WATCH_KEY] = watchName
        }
    }

    // Clear the selected watch
    suspend fun clearSelectedWatch() {
        context.dataStore.edit { preferences ->
            preferences.remove(SELECTED_WATCH_KEY)
        }
    }
}