package com.coroutines.swisstime.ui.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// CompositionLocal to share the maximum width of Settings screen action buttons
// across different cards (e.g., AppVersionSection, RateAppSection), so they can
// align to the same width equal to the longest one.
val LocalSettingsButtonsMaxWidth =
  compositionLocalOf<MutableState<Dp>> {
    // Provide a safe default so screens that don't set this provider won't crash.
    // 0.dp means "no shared width yet" and callers should treat it as wrap-content.
    mutableStateOf(0.dp)
  }
