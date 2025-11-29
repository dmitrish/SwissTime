package com.coroutines.swisstime.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlin.math.absoluteValue


@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun SwissTimePager(
    pagerState: androidx.compose.foundation.pager.PagerState,
    pageCount: Int,
    isZoomed: Boolean,
    onToggleZoom: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    pageKey: (Int) -> String,
    pageContent: @Composable (index: Int) -> Unit
) {
    // Pager-only layout and effects. Caller owns data/state and surrounding UI.
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
       /* val pageWidth = 220.dp
        val viewportWidth = maxWidth
        val visibleSeparation = ((viewportWidth - pageWidth) / 2.7f)
        val pageSpacing = visibleSeparation - pageWidth // negative to overlap pages
        val sidePadding = (viewportWidth - pageWidth) / 2f */

       /* val pageWidth = 220.dp
        val viewportWidth = maxWidth

// What your current formula would produce
        val rawSpacing = ((viewportWidth - pageWidth) / 2.7f) - pageWidth

// Clamp: never allow big positive spacing
        val pageSpacing = rawSpacing.coerceAtMost((-24).dp) // <= -24.dp overlap (or 0.dp if you want just touching)

// Keep small constant padding so neighbors are visible on the sides
        val sidePadding = 16.dp */

        val pageWidth = 220.dp
        val viewportWidth = maxWidth

// Clamp spacing so it never becomes positive (which creates big gaps)
        val rawSpacing = ((viewportWidth - pageWidth) / 2.7f) - pageWidth
        val pageSpacing = rawSpacing - pageWidth// rawSpacing
            .coerceAtMost(0.dp)           // never allow positive spacing
            .coerceAtLeast((-48).dp)      // optional: cap overlap at -48.dp

// Center the current page in the viewport
        val sidePadding = ((viewportWidth - pageWidth) / 2f)
            .coerceAtLeast(0.dp)




        val customSnapAnimationSpec = tween<Float>(
            durationMillis = 300,
            easing = EaseOutCubic
        )

        val customFlingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapAnimationSpec = customSnapAnimationSpec
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fixed(pageWidth),
            pageSpacing = pageSpacing,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = sidePadding),
            userScrollEnabled = !isZoomed,
            flingBehavior = customFlingBehavior
        ) { page ->
            val current = pagerState.currentPage
            val fraction = pagerState.currentPageOffsetFraction
            val pageOffset = ((page - current) + fraction).absoluteValue
            val depthScale = lerp(0.7125f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
            val alpha = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

            val focused = pageOffset < 0.001f
            val targetZoom = if (focused && isZoomed) 1.6f else 1f
            val zoom by animateFloatAsState(targetValue = targetZoom, label = "zoom")
            val finalScale = depthScale * zoom

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f - pageOffset.coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center
            ) {
                val interaction = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val clickableModifier = if (focused) Modifier.clickable(
                    interactionSource = interaction,
                    indication = null
                ) { onToggleZoom() } else Modifier

                var mod = Modifier
                    .size(220.dp)
                    .then(clickableModifier)

                // Apply shared element transition first (if any), then apply clipping/scaling
                if (focused && sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        mod = mod.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = pageKey(page)),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                spring(
                                    stiffness = 400f,
                                    dampingRatio = 0.85f
                                )
                            }
                        )
                    }
                }

                // Apply clipping and scaling at the very end so the outermost layer is circular
                mod = mod.graphicsLayer {
                    shape = androidx.compose.foundation.shape.CircleShape
                    clip = true
                    shadowElevation = 0f
                    scaleX = finalScale
                    scaleY = finalScale
                    this.alpha = alpha
                }

                Box(modifier = mod, contentAlignment = Alignment.Center) {
                    pageContent(page)
                }
            }
        }
    }
}

@Preview
@Composable
fun previewSwissTimePager() {
    // Intentionally empty: pager requires external state and content
}
