package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.components.WatchListItem
import com.coroutines.worldclock.common.model.WatchInfo

@Composable
fun WatchListScreen(
  watches: List<WatchInfo>,
  onWatchClick: (WatchInfo) -> Unit,
  onTitleClick: (WatchInfo) -> Unit,
  selectedWatches: List<WatchInfo>,
  modifier: Modifier = Modifier,
  listState: LazyListState
) {

  val backgroundColor = MaterialTheme.colorScheme.background
  val context = LocalContext.current

  // Use a Surface that fills the entire screen including the status bar area
  Surface(
    color = backgroundColor,
    // Don't apply any window insets padding to allow content to extend into status bar area
    modifier = modifier.fillMaxSize()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Add a spacer that matches the status bar height
      Spacer(modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars))

      LazyColumn(
        state = listState,
        modifier =
          Modifier.fillMaxSize().testTag("watchList").semantics { testTagsAsResourceId = true },
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp)
      ) {
        items(watches) { watch ->
          WatchListItem(
            watch = watch,
            onClick = { onWatchClick(watch) },
            onTitleClick = onTitleClick,
            isSelectedForWidget = selectedWatches.any { it.name == watch.name },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
