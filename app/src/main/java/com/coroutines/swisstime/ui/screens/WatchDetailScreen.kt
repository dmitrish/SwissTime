package com.coroutines.swisstime.ui.screens

import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.darken
import com.coroutines.swisstime.getTextColorForBackground
import com.coroutines.swisstime.getWatchFaceColor
import com.coroutines.swisstime.isDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WatchDetailScreen(
    watch: WatchInfo,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State to track whether the watch is expanded
    var isExpanded by remember { mutableStateOf(false) }

    // Get the current context to access the activity
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // Handle back button press
    BackHandler {
        if (isExpanded) {
            // If watch is expanded, collapse it first
            isExpanded = false
        } else {
            // Otherwise, go back to the list
            onBackClick()
        }
    }

    // Get the watch face color and darken it slightly
    val watchFaceColor = getWatchFaceColor(watch.name)
    val darkenedWatchFaceColor = watchFaceColor.darken(0.15f)

    // Get appropriate text color for the background
    val textColor = getTextColorForBackground(darkenedWatchFaceColor)

    // Handle system bars (status bar and navigation bar)
    DisposableEffect(activity) {
        val window = activity?.window
        if (window != null) {
            // Make the content draw behind the system bars
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Get the controller to manipulate system bars
            val controller = WindowInsetsControllerCompat(window, window.decorView)

            // Set status bar color (instead of hiding it)
            window.statusBarColor = darkenedWatchFaceColor.toArgb()

            // Set the appropriate appearance for the status bar
            if (darkenedWatchFaceColor.isDark()) {
                controller.isAppearanceLightStatusBars = false
            } else {
                controller.isAppearanceLightStatusBars = true
            }

            // Set navigation bar color
            window.navigationBarColor = darkenedWatchFaceColor.toArgb()

            // Set the appropriate appearance for the navigation bar
            if (darkenedWatchFaceColor.isDark()) {
                controller.isAppearanceLightNavigationBars = false
            } else {
                controller.isAppearanceLightNavigationBars = true
            }
        }

        // When leaving the screen, restore the original settings
        onDispose {
            // Restore the original system bar settings using the same approach as in MainActivity
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
                navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
            )
        }
    }


    // Use a Surface that fills the entire screen including the status bar area
    Surface(
        color = darkenedWatchFaceColor,
        // Don't apply any window insets padding to allow content to extend into status bar area
        contentColor = textColor,
        modifier = modifier.fillMaxSize()
    ) {
        if (isExpanded) {
            // Expanded view - only show the watch centered
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Apply padding for status bars to avoid content being hidden behind them
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .clickable { isExpanded = false }, // Click to collapse
                contentAlignment = Alignment.Center
            ) {
                // Make the watch as large as possible while maintaining aspect ratio
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Use padding only for horizontal edges and bottom, not for top to allow content to extend into status bar
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp, top = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    watch.composable(Modifier.fillMaxSize())
                }
            }
        } else {
            // Normal view - show all elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Use padding only for horizontal edges and bottom, not for top to allow content to extend into status bar
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Apply padding for status bars to avoid content being hidden behind them
                        .windowInsetsPadding(WindowInsets.statusBars),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "← Back to list",
                        modifier = Modifier
                            .clickable(onClick = onBackClick)
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Watch at the top - clickable to expand
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .padding(16.dp)
                        .clickable { isExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    watch.composable(Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name below
                Text(
                    text = watch.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description below name
                Text(
                    text = watch.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = textColor.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
