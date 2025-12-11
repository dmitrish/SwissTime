package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.coroutines.worldclock.common.model.WatchInfo
import java.util.TimeZone

@Composable
fun WatchListItem(
  watch: WatchInfo,
  onClick: () -> Unit,
  onTitleClick: (WatchInfo) -> Unit,
  isSelectedForWidget: Boolean = false,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable(onClick = onClick).padding(8.dp),
    colors =
      androidx.compose.material3.CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.background
      )
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      verticalAlignment = Alignment.Top // Align tops of image and content
    ) {
      // Watch face on the left
      Box(
        modifier =
          Modifier.size(80.dp) // Reduced from 120.dp to 80.dp
            .padding(end = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        watch.composable(Modifier.fillMaxSize(), TimeZone.getDefault())
      }

      // Column for title and description on the right
      Column(modifier = Modifier.weight(1f)) {
        // Title row
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
            modifier = Modifier.weight(1f).clickable(onClick = { onClick() })
          )

          // Widget selection icon
          IconButton(onClick = { onTitleClick(watch) }, modifier = Modifier.size(40.dp)) {
            Icon(
              imageVector =
                if (isSelectedForWidget) Icons.Filled.Check else Icons.Outlined.AddToHomeScreen,
              contentDescription =
                if (isSelectedForWidget) "Selected for widget" else "Add to widget",
              tint =
                if (isSelectedForWidget) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Description below the title
        Text(
          text = watch.description,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 5,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
      }
    }
  }
}
