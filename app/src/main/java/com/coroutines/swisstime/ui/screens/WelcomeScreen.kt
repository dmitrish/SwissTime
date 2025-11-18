package com.coroutines.swisstime.ui.screens

import androidx.compose.runtime.Composable
import com.coroutines.swisstime.ui.components.SwissTimePager
import com.coroutines.swisstime.viewmodel.WatchViewModel

@Composable
fun WelcomeScreen(watchViewModel: WatchViewModel, onBackClick: () -> Unit) {
    SwissTimePager(watchViewModel, onBackClick)
}