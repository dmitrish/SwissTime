package com.coroutines.swisstime.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import com.coroutines.swisstime.ui.components.SwissTimePager
import com.coroutines.swisstime.viewmodel.WatchViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WelcomeScreen(
    watchViewModel: WatchViewModel,
    onBackClick: () -> Unit,
    sharedTransitionScope: androidx.compose.animation.SharedTransitionScope? = null,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope? = null,
) {
    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        SwissTimePager(
            watchViewModel = watchViewModel,
            onBackClick = onBackClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    } else {
        SwissTimePager(
            watchViewModel = watchViewModel,
            onBackClick = onBackClick,
            sharedTransitionScope = null,
            animatedVisibilityScope = null
        )
    }
}