package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.WatchInfo


@Composable
fun WatchListScreen(
    watches: List<WatchInfo>,
    onWatchClick: (WatchInfo) -> Unit,
    onTitleClick: () -> Unit,
    selectedWatchName: String?,
    onSelectForWidget: (WatchInfo) -> Any,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    // Capture the background color
    val backgroundColor = MaterialTheme.colorScheme.background

    // Use a Surface that fills the entire screen including the status bar area
    Surface(
        color = backgroundColor,
        // Don't apply any window insets padding to allow content to extend into status bar area
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Add a spacer that matches the status bar height
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
            )

            // Main content
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(watches) { watch ->
                    WatchListItem(
                        watch = watch,
                        onClick = { onWatchClick(watch) },
                        onTitleClick = onTitleClick,
                        isSelectedForWidget = selectedWatchName == watch.name,
                        onSelectForWidget = { onSelectForWidget(watch) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun WatchListItem(
    watch: WatchInfo,
    onClick: () -> Unit,
    onTitleClick: () -> Unit,
    isSelectedForWidget: Boolean = false,
    onSelectForWidget: () -> Any,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Watch face on the left
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(0.dp, 8.dp, 8.dp, 8.dp),
                contentAlignment = Alignment.Center
            ) {
                watch.composable(Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Description on the right
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = watch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onTitleClick)
                    )

                    // Widget selection icon
                    IconButton(
                        onClick = { onSelectForWidget() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelectedForWidget) Icons.Filled.Check else Icons.Outlined.AddToHomeScreen,
                            contentDescription = if (isSelectedForWidget) "Selected for widget" else "Add to widget",
                            tint = if (isSelectedForWidget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = watch.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
