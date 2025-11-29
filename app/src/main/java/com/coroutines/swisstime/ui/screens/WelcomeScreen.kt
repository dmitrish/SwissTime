package com.coroutines.swisstime.ui.screens

import com.coroutines.swisstime.ui.adaptive.LocalWindowSizeClass
import com.coroutines.swisstime.ui.adaptive.*
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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

    val windowSizeClass = LocalWindowSizeClass.current
    val widthClass = windowSizeClass.widthSizeClass
    val heightClass = windowSizeClass.heightSizeClass


    // Data and state owned by WelcomeScreen
    val watches = com.coroutines.swisstime.watchfaces.getWatches()
    val middle = if (watches.isNotEmpty()) watches.size / 2 else 0
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = middle, pageCount = { watches.size })

    var isZoomed by remember { mutableStateOf(false) }

    // Reset zoom when page changes
    LaunchedEffect(pagerState.currentPage) {
        isZoomed = false
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxHeight = this.maxHeight

        val topMargin = when (isLandscape()) {
            false -> when (heightClass) {
                WindowHeightSizeClass.Compact -> maxHeight * 0.08f
                WindowHeightSizeClass.Medium  -> maxHeight * 0.10f
                WindowHeightSizeClass.Expanded -> maxHeight * 0.20f
                else -> maxHeight * 0.15f
            }
            true -> when (heightClass) {
                WindowHeightSizeClass.Compact -> maxHeight * 0.03f
                WindowHeightSizeClass.Medium  -> maxHeight * 0.20f
                WindowHeightSizeClass.Expanded -> maxHeight * 0.08f
                else -> maxHeight * 0.15f
            }
        }


        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (title, chooseText, descText, pager, button) = createRefs()

            val isLandscape = isLandscape() // will use in non compposable scope, so must compute here


            // Create a horizontal chain when in landscape
            if (isLandscape) {
                createHorizontalChain(
                    title, chooseText,
                    chainStyle = ChainStyle.Packed // Keeps them together, centered
                )
            }

            Text(
                text = "Let's get started!",
                modifier = Modifier
                    .constrainAs(title) {
                        if (isLandscape) {
                            top.linkTo(parent.top, margin = topMargin)
                            // Chain will handle horizontal positioning
                        } else {
                            top.linkTo(parent.top, margin = topMargin)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    }
                    .padding(horizontal = if (isLandscape)  2.dp else 24.dp),
                textAlign = if (isLandscape) TextAlign.Start else TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Choose your first watch",
                modifier = Modifier
                    .constrainAs(chooseText) {
                        if (isLandscape) {
                            top.linkTo(title.top) // Align to same baseline/top
                            bottom.linkTo(title.bottom)
                            // Chain will handle horizontal positioning
                        } else {
                            top.linkTo(title.bottom, margin = 10.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    }
                    .padding(horizontal = if (isLandscape)  2.dp else 24.dp),
                textAlign = if (isLandscape) TextAlign.Start else TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )


            /* Text(
                 text = "Let's get started!",
                 modifier = Modifier
                     .constrainAs(title) {
                         top.linkTo(parent.top, margin = topMargin)
                         if (isLandscape) {
                             start.linkTo(parent.start)
                             // Don't link end - this left-aligns it
                         } else {
                             start.linkTo(parent.start)
                             end.linkTo(parent.end)
                             // Both linked - this centers it
                         }
                     }
                     .padding(horizontal = 24.dp),
                // textAlign = TextAlign.Center,
                 textAlign = if (isLandscape) TextAlign.End else TextAlign.Center,
                 style = MaterialTheme.typography.headlineLarge
             )



             Text(
                 text = "Choose your first watch",
                 modifier = Modifier
                     .constrainAs(chooseText) {
                        // top.linkTo(title.bottom, margin = 10.dp)
                         top.linkTo(
                             when (isLandscape) {
                                 false -> title.bottom
                                 true -> parent.top
                             },
                             margin = 10.dp
                         )
                         start.linkTo(
                             when (isLandscape) {
                                 false -> parent.start
                                 true -> title.end
                             },
                         )
                         end.linkTo(parent.end)
                     }
                     .padding(horizontal = 24.dp),
                 textAlign = TextAlign.Center,
                 style = MaterialTheme.typography.headlineMedium
             ) */

            if (isLandscape() && heightClass == WindowHeightSizeClass.Compact) {
                Text(
                    text = "",
                    modifier = Modifier
                        .constrainAs(descText) {
                            top.linkTo(chooseText.bottom, margin = 5.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(horizontal = 24.dp),

                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else{
                Text(
                    text = firstSentence(watches.getOrNull(pagerState.currentPage)?.description),
                    modifier = Modifier
                        .constrainAs(descText) {
                            top.linkTo(chooseText.bottom, margin = 50.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .constrainAs(pager) {
                        top.linkTo(descText.bottom, margin = 16.dp)
                        bottom.linkTo(button.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
                    .aspectRatio(1f)
            ) {
                SwissTimePager(
                    pagerState = pagerState,
                    pageCount = watches.size,
                    isZoomed = isZoomed,
                    onToggleZoom = { isZoomed = !isZoomed },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    pageKey = { index -> "watch-${watches[index].name}" },
                    pageContent = { index ->
                        val watch = watches[index]
                        watch.composable(Modifier.fillMaxSize(), java.util.TimeZone.getDefault())
                    }
                )
            }

            // Constant-height bottom container to avoid vertical nudge when toggling zoom
            val bottomHeight = 56.dp
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp)
                    .height(bottomHeight)
                    .fillMaxWidth()
            ) {
                if (isZoomed) {
                    Button(
                        onClick = {
                            watchViewModel.saveSelectedWatch(watch = watches[pagerState.currentPage])
                            onBackClick()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "Select this watch")
                    }
                } else {
                    Text(
                        text = "Tap to zoom",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun firstSentence(description: String?): String {
    val text = description?.trim().orEmpty()
    if (text.isEmpty()) return ""
    val parts = text.split(Regex("(?<=[.!?])\\s+"))
    return parts.firstOrNull().orEmpty()
}