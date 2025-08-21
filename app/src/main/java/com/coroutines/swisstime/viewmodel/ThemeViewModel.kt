package com.coroutines.swisstime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coroutines.worldclock.common.repository.ThemePreferencesRepository
import com.coroutines.worldclock.common.theme.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing theme preferences
 */
class ThemeViewModel(
    private val themePreferencesRepository: ThemePreferencesRepository
) : ViewModel() {

    // Get the theme mode as a StateFlow
    val themeMode: StateFlow<ThemeMode> = themePreferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    // Get the dark mode preference as a StateFlow
    val darkMode: StateFlow<Boolean> = themePreferencesRepository.darkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // Save the theme mode
    fun saveThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            themePreferencesRepository.saveThemeMode(themeMode)
        }
    }

    // Save the dark mode preference
    fun saveDarkMode(darkMode: Boolean) {
        viewModelScope.launch {
            themePreferencesRepository.saveDarkMode(darkMode)
        }
    }

    // Reset to default theme preferences
    fun resetThemePreferences() {
        viewModelScope.launch {
            themePreferencesRepository.resetThemePreferences()
        }
    }

    /**
     * Factory for creating ThemeViewModel instances
     */
    class Factory(
        private val themePreferencesRepository: ThemePreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(themePreferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}