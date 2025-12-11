package com.coroutines.swisstime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import kotlinx.coroutines.launch

class WallpaperViewmodel(private val wallpaperPreferenceRepository: WallpaperPreferenceRepository) :
  ViewModel() {

  /* val themeMode: StateFlow<String?> = wallpaperPreferenceRepository.selectedWallpaperName
  .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = null
  )*/

  fun saveWallpaperName(wallpaperName: String) {
    viewModelScope.launch { wallpaperPreferenceRepository.saveSelectedWallpaperName(wallpaperName) }
  }
}
