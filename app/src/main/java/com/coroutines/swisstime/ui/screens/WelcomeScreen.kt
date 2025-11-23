package com.coroutines.swisstime.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (title, chooseText, descText, pager, button) = createRefs()

            Text(
                text = "Let's get started!",
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top, margin = maxHeight * 0.20f)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Choose your first watch",
                modifier = Modifier
                    .constrainAs(chooseText) {
                        top.linkTo(title.bottom, margin = 10.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )

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

            if (isZoomed) {
                Button(
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
                    Text(text = "Select this watch")
                }
            } else {
                Text(
                    text = "Tap to zoom",
                    modifier = Modifier
                        .constrainAs(button) {
                            bottom.linkTo(parent.bottom, margin = 40.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
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