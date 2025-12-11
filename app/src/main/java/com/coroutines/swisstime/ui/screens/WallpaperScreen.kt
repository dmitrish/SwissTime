package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.components.WatchPager

/**
 * A full-screen composable for selecting wallpapers.
 *
 * This screen displays the WallPaperWatchesHorizontalPager in a full-screen layout with a TopAppBar
 * that includes a back button to return to the previous screen.
 *
 * @param onBackClick Function to call when the back button is clicked
 * @param modifier Optional modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) {

  val context = androidx.compose.ui.platform.LocalContext.current
  Column(
    modifier =
      modifier
        .fillMaxSize()
        //  .background(MaterialTheme.colorScheme.background)
        .padding(16.dp),
    // .verticalScroll(scrollState),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    WatchPager(context = context)
  }
}

@Preview
@Composable
fun WallpaperScreenPreview() {
  WallpaperScreen(onBackClick = {})
}
