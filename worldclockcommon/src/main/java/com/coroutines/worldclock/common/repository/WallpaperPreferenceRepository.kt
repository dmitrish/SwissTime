package com.coroutines.worldclock.common.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wallpaper_preferences")

class WallpaperPreferenceRepository (private val context: Context) {

    companion object {
        private val SELECTED_WALLPAPER_KEY = stringPreferencesKey("selected_wallpaper")
    }

    val selectedWallpaperName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_WALLPAPER_KEY] ?: "Roma Marina"
        }

    suspend fun saveSelectedWallpaperName(wallpaperName: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_WALLPAPER_KEY] = wallpaperName
        }
    }


}