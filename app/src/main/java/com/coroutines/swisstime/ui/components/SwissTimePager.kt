package com.coroutines.swisstime.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.coroutines.swisstime.viewmodel.WallpaperViewmodel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.wallpaper.launchDigitalClockWallpaperPicker
import com.coroutines.swisstime.wallpaper.wallpaperWatches
import com.coroutines.swisstime.watchfaces.getWatches
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.TimeZone

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun SwissTimePager (watchViewModel: WatchViewModel, onBackClick: () -> Unit){

    val watches = getWatches()
    val middle = if (watches.isNotEmpty()) watches.size / 2 else 0
    val pagerState = rememberPagerState(initialPage = middle, pageCount = { watches.size })

    // Tap-to-zoom state (applies to the currently focused page only)
    var isZoomed by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    // Track if the user has enlarged and then returned the focused watch for the current page
    var hasZoomedOnce by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var hasEnlargedAndReturned by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // Reset zoom and progression flags when the focused page changes
    LaunchedEffect(pagerState.currentPage) {
        isZoomed = false
        hasZoomedOnce = false
        hasEnlargedAndReturned = false
    }

    // Observe zoom changes to mark completion when user returns from zoom
    LaunchedEffect(isZoomed) {
        if (isZoomed) {
            hasZoomedOnce = true
        } else {
            // isZoomed changed to false: if it was previously zoomed at least once, mark completed
            if (hasZoomedOnce) {
                hasEnlargedAndReturned = true
            }
        }
    }

    // Ensure we stay centered on middle when list first appears
    LaunchedEffect(watches.size) {
        if (watches.isNotEmpty()) {
            pagerState.scrollToPage(middle)
        }
    }

    // Persist the selected watch name when the current page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collectLatest { page ->
                val name = watches.getOrNull(page)?.name ?: return@collectLatest
            }
    }

    // We need the screen height to place the pager at 1/6 from the bottom
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxHeight = maxHeight
        // Single parent ConstraintLayout to anchor top texts and bottom button
        androidx.constraintlayout.compose.ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (title, chooseText, descText, pager, button) = createRefs()

            // Title near the top
            androidx.compose.material3.Text(
                text = "Let's get started!",
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top, margin = maxHeight * 0.22f)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp),
                   // .padding(bottom = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )

            // "Choose your first watch" below the title
            androidx.compose.material3.Text(
                text = "Choose your first watch",
                modifier = Modifier
                    .constrainAs(chooseText) {
                        top.linkTo(title.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp),
                   // .padding(bottom = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            // Description anchored 10.dp below the above text
            androidx.compose.material3.Text(
                text = firstSentence(watches.getOrNull(pagerState.currentPage)?.description),
                modifier = Modifier
                    .constrainAs(descText) {
                        top.linkTo(chooseText.bottom, margin = 50.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )

            // Pager fills the space between description and button
            BoxWithConstraints(
                modifier = Modifier
                    .constrainAs(pager) {
                        top.linkTo(descText.bottom, margin = 16.dp)
                        bottom.linkTo(button.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                    }
                    .aspectRatio(1f)
            ) {
                // We want three pages visible on each side. Keep base page width 200+ dp and
                // create overlap by using a negative pageSpacing so adjacent pages are only ~24.dp apart.
                val pageWidth = 220.dp
                // Compute separation so that 7 pages (center +/- 3) fit within the viewport width.
                val viewportWidth = maxWidth
                val visibleSeparation = ((viewportWidth - pageWidth) / 2.7f)
                val pageSpacing = visibleSeparation - pageWidth // negative to overlap pages
                // Add symmetric side padding so the focused page is geometrically centered in the viewport
                val sidePadding = (viewportWidth - pageWidth) / 2f

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSize = PageSize.Fixed(pageWidth),
                    pageSpacing = pageSpacing,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = sidePadding)
                ) { page ->

                    val current = pagerState.currentPage
                    val fraction = pagerState.currentPageOffsetFraction
                    val pageOffset = ((page - current) + fraction).absoluteValue
                    val depthScale = lerp(0.7125f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                    val alpha = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                    // Additional zoom only when this page is focused and tapped
                    val focused = pageOffset < 0.001f
                    val targetZoom = if (focused && isZoomed) 1.6f else 1f
                    val zoom by animateFloatAsState(targetValue = targetZoom, label = "zoom")
                    val finalScale = depthScale * zoom

                    val watch = watches[page]

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(1f - pageOffset.coerceIn(0f, 1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val clickableModifier = if (focused) Modifier.clickable { isZoomed = !isZoomed } else Modifier
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .then(clickableModifier)
                                .graphicsLayer {
                                    scaleX = finalScale
                                    scaleY = finalScale
                                    this.alpha = alpha
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            watch.composable(Modifier.fillMaxSize(), TimeZone.getDefault())
                        }
                    }
                }
            }

            // Bottom-anchored action button
            androidx.compose.material3.Button(
                onClick = {
                    watchViewModel.saveSelectedWatch(watch = watches[pagerState.currentPage])
                    onBackClick()
                },
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                androidx.compose.material3.Text(
                    text = if (hasEnlargedAndReturned) "Select this watch" else "Tap the watch to enlarge"
                )
            }
        }
    }
}

@Preview
@Composable
fun previewSwissTimePager() {
   // SwissTimePager()
}

// Dots page indicator
/* Row(
     modifier = Modifier.padding(top = 8.dp),
     verticalAlignment = Alignment.CenterVertically
 ) {
     repeat(watches.size) { index ->
         Box(
             modifier = Modifier
                 .size(8.dp)
                 .background(
                     color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                     shape = CircleShape
                 )
         )
         if (index < watches.size - 1) {
             Spacer(modifier = Modifier.width(8.dp))
         }
     }
 }

 Spacer(modifier = Modifier.height(50.dp))

 SwissTimeGradientButton("Choose as Wallpaper", onClick = {
     val current = watches.getOrNull(pagerState.currentPage)
     if (current != null) {
     }
 })*/

// Helper to extract the first sentence from a description
private fun firstSentence(description: String?): String {
    val text = description?.trim().orEmpty()
    if (text.isEmpty()) return ""
    // Split on sentence-ending punctuation followed by whitespace
    val parts = text.split(Regex("(?<=[.!?])\\s+"))
    return parts.firstOrNull().orEmpty()
}
