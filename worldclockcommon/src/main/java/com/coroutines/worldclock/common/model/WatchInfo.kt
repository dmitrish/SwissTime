package com.coroutines.worldclock.common.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import java.util.TimeZone

@Stable
data class WatchInfo(
    val name: String,
    val description: String,
    val composable: @Composable (Modifier, TimeZone) -> Unit
)