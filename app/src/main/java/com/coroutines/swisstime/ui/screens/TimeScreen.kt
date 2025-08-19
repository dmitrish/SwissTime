package com.coroutines.swisstime.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.viewmodel.WatchViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.coroutines.swisstime.util.PerformanceMetrics
import com.coroutines.worldclock.common.components.CustomWorldMapWithDayNight
import com.coroutines.worldclock.common.model.WatchInfo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter


/**
 * A composable that manages the time for all watches.
 * It updates all watch times in the WatchTimeStore periodically.
 * This ensures that all watches keep ticking, even when not visible in the pager.
 */
@Composable
fun WatchTimeManager(watchViewModel: WatchViewModel, selectedWatches: List<WatchInfo>) {
    // Create a unique instance ID for this WatchTimeManager
    val instanceId = remember { java.util.UUID.randomUUID().toString() }
    println("[DEBUG_LOG] Creating WatchTimeManager instance $instanceId for ${selectedWatches.size} watches")

    // Track which watches have been initialized
    val initializedWatches = remember { mutableSetOf<String>() }

    // Initialize the watch times and timezones with priority for the first watch
    androidx.compose.runtime.LaunchedEffect(selectedWatches) {
        println("[DEBUG_LOG] Initializing watches in WatchTimeManager $instanceId")

        // First, initialize only the first watch for better first page performance
        if (selectedWatches.isNotEmpty()) {
            val firstWatch = selectedWatches[0]

            // Only initialize if not already initialized
            if (!initializedWatches.contains(firstWatch.name)) {
                // Get the timezone for this watch
                val timeZoneFlow = watchViewModel.getWatchTimeZone(firstWatch.name)
                val timeZone = timeZoneFlow.value

                // Register the watch and its timezone in the WatchTimeStore
                println("[DEBUG_LOG] Registering first watch ${firstWatch.name} with timezone ${timeZone.id}")
                WatchTimeStore.watchTimeZoneMap[firstWatch.name] = timeZone

                // Initialize the time
                WatchTimeStore.updateTime(firstWatch.name, timeZone)
                initializedWatches.add(firstWatch.name)
                println("[DEBUG_LOG] Initialized first watch ${firstWatch.name}")
            }
        }

        // Then initialize the rest of the watches
        // Skip the first watch as it's already initialized
        val remainingWatches = if (selectedWatches.isNotEmpty()) selectedWatches.drop(1) else emptyList()

        remainingWatches.forEach { watch ->
            // Only initialize if not already initialized
            if (!initializedWatches.contains(watch.name)) {
                // Get the timezone for this watch
                val timeZoneFlow = watchViewModel.getWatchTimeZone(watch.name)
                val timeZone = timeZoneFlow.value

                // Register the watch and its timezone in the WatchTimeStore
                WatchTimeStore.watchTimeZoneMap[watch.name] = timeZone

                // Initialize the time
                WatchTimeStore.updateTime(watch.name, timeZone)
                initializedWatches.add(watch.name)
                println("[DEBUG_LOG] Initialized watch ${watch.name}")
            }
        }
    }

    // Update all watch times more frequently (every 100ms)
    // Use the instanceId as part of the key to ensure this effect is tied to this specific instance
    // Also use the selectedWatches.size as a key to ensure the effect is re-executed when watches are added or removed
    androidx.compose.runtime.LaunchedEffect(instanceId, selectedWatches.size) {
        println("[DEBUG_LOG] Starting time update loop in WatchTimeManager $instanceId for ${selectedWatches.size} watches")

        // Create a set of watch names for faster lookup
        val watchNames = selectedWatches.map { it.name }.toSet()

        // Log all watches and their timezones for debugging
        watchNames.forEach { watchName ->
            val timeZone = WatchTimeStore.watchTimeZoneMap[watchName]
            println("[DEBUG_LOG] Watch $watchName has timezone ${timeZone?.id ?: "null"} in WatchTimeManager $instanceId")
        }

        while (true) {
            // Get the current time to ensure all watches are updated with the same reference time
            val now = System.currentTimeMillis()

            // Only update watches that have been initialized
            // This ensures we don't waste time updating watches that aren't ready yet
            val watchesToUpdate = watchNames.filter { initializedWatches.contains(it) }

            // Update all initialized watches
            // This ensures that all watches keep ticking, even when not visible in the pager
            watchesToUpdate.forEach { watchName ->
                val timeZone = WatchTimeStore.watchTimeZoneMap[watchName]
                if (timeZone != null) {
                    // Update the watch time
                    WatchTimeStore.updateTime(watchName, timeZone)
                }
            }

            // Wait for next update (100ms for smoother updates)
            kotlinx.coroutines.delay(100)
        }
    }

    // Clean up when this composable is disposed
    DisposableEffect(instanceId) {
        onDispose {
            println("[DEBUG_LOG] Disposing WatchTimeManager instance $instanceId")
        }
    }
}

/**
 * A screen that displays a horizontal pager of watches, allowing the user to swipe between them.
 * Each watch is associated with its own timezone.
 */
@Composable
fun TimeScreen(
    watchViewModel: WatchViewModel,
    onBackClick: () -> Unit = {}
) {
    val TAG = "Performance:TimeScreen"
    // Get the selected watches
    val selectedWatches by watchViewModel.selectedWatches.collectAsState()

    // Report performance metrics periodically
    LaunchedEffect(Unit) {
        // Wait for some metrics to be collected before reporting
        kotlinx.coroutines.delay(10000) // Wait 10 seconds

        // Report metrics every 30 seconds
        while(true) {
            // Get average page transition time
            val avgTransitionTime = PerformanceMetrics.getAverageDuration(PerformanceMetrics.Categories.PAGE_TRANSITION)
            Log.d(TAG, "Average page transition time: $avgTransitionTime ms")

            // Get average page rendering time
            val avgRenderingTime = PerformanceMetrics.getAverageDuration(PerformanceMetrics.Categories.PAGE_RENDERING)
            Log.d(TAG, "Average page rendering time: $avgRenderingTime ms")

            // Get all transition metrics for detailed analysis
            val transitionMetrics = PerformanceMetrics.getMetrics(PerformanceMetrics.Categories.PAGE_TRANSITION)
            Log.d(TAG, "Collected ${transitionMetrics.size} page transition metrics")

            // Get all rendering metrics for detailed analysis
            val renderingMetrics = PerformanceMetrics.getMetrics(PerformanceMetrics.Categories.PAGE_RENDERING)
            Log.d(TAG, "Collected ${renderingMetrics.size} page rendering metrics")

            // Wait before next report
            kotlinx.coroutines.delay(30000) // 30 seconds
        }
    }

    // Start the watch time manager to keep all watches ticking
    // This ensures that watch times are initialized before the user navigates to them
    if (selectedWatches.isNotEmpty()) {
        // Use remember to ensure WatchTimeManager is only created once
        val watchTimeManagerCreated = remember { mutableStateOf(false) }

        if (!watchTimeManagerCreated.value) {
            // Pre-initialize all watches to improve page transition performance
            LaunchedEffect(Unit) {
                // Mark as created to prevent multiple instances
                watchTimeManagerCreated.value = true

                // Log start of pre-initialization
                Log.d(TAG, "Starting pre-initialization of all watches")
                val startTime = System.currentTimeMillis()

                // Use a background dispatcher for ALL heavy initialization work
                // This ensures the UI thread remains responsive during initialization
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                    // Pre-initialize the first watch's time zone to improve first page performance
                    if (selectedWatches.isNotEmpty()) {
                        val firstWatch = selectedWatches[0]
                        // Use direct methods for better performance
                        val timeZone = watchViewModel.getTimeZoneDirect(firstWatch.name)
                        WatchTimeStore.watchTimeZoneMap[firstWatch.name] = timeZone
                        WatchTimeStore.updateTime(firstWatch.name, timeZone)
                        println("[DEBUG_LOG] Pre-initialized first watch ${firstWatch.name}")

                        // Pre-load time zone info for the first watch to avoid doing it during page rendering
                        val timeZoneId = watchViewModel.getWatchTimeZoneIdDirect(firstWatch.name)
                        if (timeZoneId != null) {
                            watchViewModel.getCachedTimeZoneInfo(timeZoneId)
                        }
                    }

                    // Pre-initialize time zone data for all watches to improve page transition performance
                    // This ensures that when transitioning to a new page, the time zone data is already loaded
                    selectedWatches.forEachIndexed { index, watch ->
                        // Skip the first watch as it's already initialized
                        if (index > 0) {
                            // Pre-load time zone for this watch using direct methods
                            val timeZone = watchViewModel.getTimeZoneDirect(watch.name)
                            WatchTimeStore.watchTimeZoneMap[watch.name] = timeZone

                            // Pre-load time zone info for this watch
                            val timeZoneId = watchViewModel.getWatchTimeZoneIdDirect(watch.name)
                            if (timeZoneId != null) {
                                watchViewModel.getCachedTimeZoneInfo(timeZoneId)
                            }

                            // Log progress
                            println("[DEBUG_LOG] Pre-initialized watch ${watch.name} (${index+1}/${selectedWatches.size})")
                        }
                    }

                    // Pre-initialize the watch face for page 1 to further improve first transition performance
                    // Now doing this on the background thread to avoid blocking the UI thread
                    if (selectedWatches.size > 1) {
                        val page1Watch = selectedWatches[1]
                        Log.d(TAG, "Pre-initializing watch face for page 1 (${page1Watch.name})")

                        // Get the time zone directly for page 1's watch - much more efficient
                        val timeZone = watchViewModel.getTimeZoneDirect(page1Watch.name)

                        // Force creation of the time zone info for page 1's watch
                        val timeZoneId = watchViewModel.getWatchTimeZoneIdDirect(page1Watch.name)
                        if (timeZoneId != null) {
                            val timeZoneInfo = watchViewModel.getCachedTimeZoneInfo(timeZoneId)
                            Log.d(TAG, "Pre-initialized watch face for page 1 with time zone ${timeZoneInfo.id}")

                            // Force creation of ZoneId for the watch if it uses java.time
                            try {
                                java.time.ZoneId.of(timeZoneId)

                                // Create a Calendar instance with the correct timezone
                                val calendar = java.util.Calendar.getInstance(timeZone)

                                // Create a Date instance
                                val date = java.util.Date()

                                // Check if it's daylight time
                                timeZone.inDaylightTime(date)

                                // Force update the watch time with the correct timezone
                                WatchTimeStore.updateTime(page1Watch.name, timeZone)

                                Log.d(TAG, "Pre-created objects for page 1 watch face")
                            } catch (e: Exception) {
                                // Ignore exceptions, this is just to pre-create the object
                                Log.e(TAG, "Error pre-creating objects: ${e.message}")
                            }
                        }

                        Log.d(TAG, "Completed pre-initialization for page 1")
                    }
                }

                // Log completion of pre-initialization
                val endTime = System.currentTimeMillis()
                Log.d(TAG, "Completed pre-initialization of all watches in ${endTime - startTime} ms")
            }

            // Start the full WatchTimeManager after pre-initializing all watches
           // WatchTimeManager(watchViewModel, selectedWatches)
        }
    }

    if (selectedWatches.isNotEmpty()) {
        // Create a pager state with beyondBoundsPageCount to keep all pages in memory
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { selectedWatches.size }
        )

        // Track the current page for the indicator
        val currentPage = pagerState.currentPage

        // Variables to track page transition metrics
        var transitionStartTime by remember { mutableStateOf(0L) }
        var lastPage by remember { mutableStateOf(0) }
        var isTransitioning by remember { mutableStateOf(false) }

        // Track page transition metrics
        LaunchedEffect(pagerState) {
            // Track when scrolling starts (transition begins)
            snapshotFlow { pagerState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { scrolling ->
                    if (scrolling && !isTransitioning) {
                        // Transition started
                        isTransitioning = true
                        transitionStartTime = System.currentTimeMillis()
                        lastPage = pagerState.currentPage
                        Log.d(TAG, "Page transition started from page $lastPage at $transitionStartTime")
                    } else if (!scrolling && isTransitioning) {
                        // Transition completed
                        val endTime = System.currentTimeMillis()
                        val transitionTime = endTime - transitionStartTime
                        val currentPageNow = pagerState.currentPage

                        // Log transition metrics with more detailed information
                        Log.d(TAG, "Page transition completed to page $currentPageNow at $endTime")
                        Log.d(TAG, "Page transition from $lastPage to $currentPageNow took $transitionTime ms")

                        // Add more detailed logging for specific transitions
                        if (lastPage == 0 && currentPageNow == 1) {
                            Log.d(TAG, "PERFORMANCE METRIC: First transition from page 0 to 1 took $transitionTime ms")
                        } else if (lastPage == 1 && currentPageNow == 0) {
                            Log.d(TAG, "PERFORMANCE METRIC: Transition from page 1 to 0 took $transitionTime ms")
                        } else if (lastPage == 1 && currentPageNow == 2) {
                            Log.d(TAG, "PERFORMANCE METRIC: Transition from page 1 to 2 took $transitionTime ms")
                        }

                        // Record metric using PerformanceMetrics utility
                        val transitionName = "Page${lastPage}to${currentPageNow}"
                        val metadata = mapOf(
                            "fromPage" to lastPage,
                            "toPage" to currentPageNow,
                            "startTime" to transitionStartTime,
                            "endTime" to endTime
                        )
                        PerformanceMetrics.recordMetric(
                            category = PerformanceMetrics.Categories.PAGE_TRANSITION,
                            name = transitionName,
                            durationMs = transitionTime,
                            metadata = metadata
                        )

                        isTransitioning = false
                    }
                }
        }

        // Ensure the pager state is updated when the selected watches change
        LaunchedEffect(selectedWatches.size) {
            if (pagerState.currentPage >= selectedWatches.size && selectedWatches.isNotEmpty()) {
                pagerState.animateScrollToPage(0)
            }
        }

        // Get the current configuration to determine orientation
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Landscape layout - pager on the left, map on the right
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Left side - Pager with watches
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Horizontal pager for watches
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        key = { page -> "watch_${selectedWatches[page].name}_$page" },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
                    ) { page ->
                        // Log the page index and measure rendering time
                        val startTime = System.currentTimeMillis()
                        Log.d(TAG, "Page $page rendering started at $startTime")

                        // Display the selected watch screen for the current page
                        val watch = selectedWatches[page]

                        androidx.compose.runtime.key(watch.name) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Use LaunchedEffect to measure and log rendering time
                                LaunchedEffect(page) {
                                    val endTime = System.currentTimeMillis()
                                    val renderTime = endTime - startTime

                                    // Log rendering metrics
                                    Log.d(TAG, "Page $page rendering completed at $endTime")
                                    Log.d(TAG, "Page $page rendering took $renderTime ms")
                                    Log.d(TAG, "Page $page contains watch: ${watch.name}")

                                    // Record metric using PerformanceMetrics utility
                                    val renderingName = "Page${page}Rendering"
                                    val metadata = mapOf(
                                        "page" to page,
                                        "watchName" to watch.name,
                                        "startTime" to startTime,
                                        "endTime" to endTime
                                    )
                                    PerformanceMetrics.recordMetric(
                                        category = PerformanceMetrics.Categories.PAGE_RENDERING,
                                        name = renderingName,
                                        durationMs = renderTime,
                                        metadata = metadata
                                    )
                                }

                                // Use the optimized SelectedWatchScreen2 component
                                SelectedWatchScreen2(
                                    onBackClick = onBackClick,
                                    selectedWatch = watch,
                                    watchViewModel = watchViewModel,
                                    isPageTransitioning = pagerState.isScrollInProgress
                                )
                            }
                        }
                    }

                    // Pager indicator
                    Text(
                        text = "${currentPage + 1} / ${selectedWatches.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = 25.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                // Right side - World Map
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomWorldMapWithDayNight(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f)
                    )
                }
            }
        } else {
            // Portrait layout - original vertical layout
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Horizontal pager for watches
                HorizontalPager(
                    state = pagerState,
                    // Reduce the number of preloaded pages to minimize memory usage and initial rendering cost
                    // This should improve the first page transition performance
                    beyondViewportPageCount = 1, // Only load the current page, previous, and next page
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    // Set a large key to ensure pages are not recycled
                    // Include the page index and the total number of watches to ensure proper keying
                    key = { page -> "watch_${selectedWatches[page].name}_$page" },
                    // Use a custom content padding to reduce the amount of content that needs to be rendered
                    // This improves performance by reducing the number of composables that need to be created
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)
                ) { page ->
                    // Log the page index and measure rendering time
                    val startTime = System.currentTimeMillis()
                    Log.d(TAG, "Page $page rendering started at $startTime")

                    // Display the selected watch screen for the current page
                    val watch = selectedWatches[page]

                    // Use a key that includes the watch name to ensure proper recomposition
                    // This helps avoid unnecessary recompositions during page transitions
                    androidx.compose.runtime.key(watch.name) {
                        // Wrap in a Box to improve performance by reducing the number of measure/layout passes
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Use LaunchedEffect to measure and log rendering time
                            LaunchedEffect(page) {
                                val endTime = System.currentTimeMillis()
                                val renderTime = endTime - startTime

                                // Log rendering metrics
                                Log.d(TAG, "Page $page rendering completed at $endTime")
                                Log.d(TAG, "Page $page rendering took $renderTime ms")
                                Log.d(TAG, "Page $page contains watch: ${watch.name}")

                                // Record metric using PerformanceMetrics utility
                                val renderingName = "Page${page}Rendering"
                                val metadata = mapOf(
                                    "page" to page,
                                    "watchName" to watch.name,
                                    "startTime" to startTime,
                                    "endTime" to endTime
                                )
                                PerformanceMetrics.recordMetric(
                                    category = PerformanceMetrics.Categories.PAGE_RENDERING,
                                    name = renderingName,
                                    durationMs = renderTime,
                                    metadata = metadata
                                )
                            }

                            // Use the optimized SelectedWatchScreen2 component
                            // Pass isPageTransitioning parameter to prevent heavy rendering during transitions
                            SelectedWatchScreen2(
                                onBackClick = onBackClick,
                                selectedWatch = watch,
                                watchViewModel = watchViewModel,
                                isPageTransitioning = pagerState.isScrollInProgress
                            )
                        }
                    }
                }

                // Simplified pager indicator - just show the current page number out of total
                Text(
                    text = "${currentPage + 1} / ${selectedWatches.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 25.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // World map
                CustomWorldMapWithDayNight( )
            }
        }
    } else {
        // If no watches are selected, display a message prompting the user to select watches
        // with persistence.jpg image as background
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background image
          /*  Image(
                painter = painterResource(id = R.drawable.persistence),
                contentDescription = "Persistence background",
                modifier = Modifier.fillMaxSize().alpha(0.4f),
                contentScale = ContentScale.Crop
            )*/

           /* val bitmap = getBitmap(R.drawable.meltingwatch)!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                WaterEffectBitmapShader(
                    modifier = Modifier
                        //fillMaxWidth(0.8f).fillMaxHeight(0.5f)
                        .clip(ShapeDefaults.ExtraLarge).alpha(0.8f),
                    bitmap = bitmap.asImageBitmap(),
                    viewModel = WaterViewModel(),
                    shaderResId = R.raw.water_shader
                )
            }
            else{
                Image(
                    painter = painterResource(id = R.drawable.meltingwatch),
                    contentDescription = "Persistence of Time",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }

            */

            // Text on top of the image
            Text(
                text = "No watches selected. Please select watches from the Watch List.",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
