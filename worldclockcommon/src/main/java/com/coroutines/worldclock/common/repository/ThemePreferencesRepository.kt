package com.coroutines.worldclock.common.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coroutines.worldclock.common.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance at the top level
private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * Repository for managing theme preferences
 */
class ThemePreferencesRepository(private val context: Context) {

    // Define preference keys
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Get the currently selected theme mode
    val themeMode: Flow<ThemeMode> = context.themeDataStore.data
        .map { preferences ->
            val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }

    // Get the dark mode preference
    val darkMode: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    // Save the theme mode
    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    // Save the dark mode preference
    suspend fun saveDarkMode(darkMode: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = darkMode
        }
    }

    // Reset to default theme preferences
    suspend fun resetThemePreferences() {
        context.themeDataStore.edit { preferences ->
            preferences.remove(THEME_MODE_KEY)
            preferences.remove(DARK_MODE_KEY)
        }
    }
}