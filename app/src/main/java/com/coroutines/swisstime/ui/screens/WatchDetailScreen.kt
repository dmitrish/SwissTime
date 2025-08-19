package com.coroutines.swisstime.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.getTextColorForBackground
import com.coroutines.swisstime.getWatchFaceColor
import com.coroutines.swisstime.utils.darken
import com.coroutines.worldclock.common.model.WatchInfo
import java.util.TimeZone

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

    val originalColor = MaterialTheme.colorScheme.background.toArgb()

    // Handle back button press
    BackHandler {
        if (isExpanded) {
            // If watch is expanded, collapse it first
            isExpanded = false
        } else {
            // Otherwise, go back to the list
          //  activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            onBackClick()
        }
    }

    // Get the watch face color and darken it slightly
    val watchFaceColor = getWatchFaceColor(watch.name)
    val darkenedWatchFaceColor = watchFaceColor.darken(0.15f)

    // Get appropriate text color for the background
    val textColor = getTextColorForBackground(darkenedWatchFaceColor)

    // System bars (status bar and navigation bar) are kept transparent (set in MainActivity)
    // and the screen's background color extends under them
    // and it DOES NOT WORK
    // need additional logic to handle system bars colors
    //here it is

    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    activity?.window?.setStatusBarColor(darkenedWatchFaceColor.toArgb());
    activity?.window?.setNavigationBarColor(darkenedWatchFaceColor.toArgb());


    // val activity = LocalView.current.context as? Activity
    DisposableEffect(key1 = activity) {


   // activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
   // activity?.window?.setStatusBarColor(darkenedWatchFaceColor.toArgb());
   // activity?.window?.setNavigationBarColor(darkenedWatchFaceColor.toArgb());

        onDispose {
           // activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
          //  activity?.window?.setStatusBarColor(originalColor);
          //  activity?.window?.setNavigationBarColor(Color.Transparent.toArgb());

        }
   }

    // Animate the scale of the watch
    val watchScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "watchScale"
    )

    // Animate the alpha of the details (name, description)
    val detailsAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "detailsAlpha"
    )

    // Animate the position of the watch
    val watchPositionFactor by animateFloatAsState(
        targetValue = if (isExpanded) 0.5f else 0.25f, // 0.5 = center, 0.25 = top quarter
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "watchPosition"
    )



    // Use a Surface that fills the entire screen including the status bar and navigation bar areas
    Surface(
        color = darkenedWatchFaceColor,
        // Don't apply any window insets padding to allow content to extend into system bars
        contentColor = textColor,
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = isExpanded) { isExpanded = false } // Click to collapse when expanded
        ) {
            // Normal view - show all elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Use padding only for horizontal edges, not for top or bottom to allow content to extend into system bars
                    .padding(start = 16.dp, end = 16.dp, bottom = 0.dp, top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(detailsAlpha) // Fade out when expanded
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

                // Watch - positioned based on animation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(watchPositionFactor * 2) // Animate position
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Use different shape for Jaeger-LeCoultre Reverso which is rectangular
                    val clipShape = if (watch.name.contains("Jaeger-LeCoultre Reverso")) {
                        RoundedCornerShape(20.dp) // Rounded rectangle for Reverso
                    } else {
                        CircleShape // Circle for all other watches
                    }

                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .scale(watchScale) // Animate scale first
                            .clip(clipShape) // Then clip to appropriate shape
                            .clickable { isExpanded = !isExpanded }, // Toggle expanded state on click
                        contentAlignment = Alignment.Center
                    ) {
                        watch.composable(Modifier.fillMaxSize(),  TimeZone.getDefault() )
                    }
                }

                // Details section - fades out when expanded
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(detailsAlpha), // Fade out when expanded
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    // Description below name - scrollable with visible scrollbar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Take remaining space
                            .padding(horizontal = 16.dp)
                    ) {
                        val scrollState = rememberScrollState()

                        // Main text content
                        Text(
                            text = watch.description,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = textColor.copy(alpha = 0.9f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                                .verticalScroll(scrollState)
                        )

                        // Custom scrollbar
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(8.dp)
                                .padding(1.dp)
                        ) {
                            // Background track
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(6.dp)
                                    .background(
                                        color = textColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )

                            // Scrollbar thumb - only visible when content is scrollable
                            if (scrollState.maxValue > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(scrollState.value.toFloat() / scrollState.maxValue.toFloat())
                                        .width(6.dp)
                                        .background(
                                            color = textColor.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(3.dp)
                                        )
                                        .align(Alignment.TopStart)
                                )
                            }
                        }
                    }
                }

                // Add a spacer that matches the navigation bar height
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    }
}
