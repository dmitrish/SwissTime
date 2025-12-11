package com.coroutines.swisstime.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.viewmodel.WallpaperViewmodel
import com.coroutines.swisstime.wallpaper.launchDigitalClockWallpaperPicker
import com.coroutines.swisstime.wallpaper.wallpaperWatches
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import java.util.TimeZone
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun WatchPager(context: Context) {

  val appContext = context.applicationContext
  val viewModel = WallpaperViewmodel(WallpaperPreferenceRepository(appContext))
  val watches = wallpaperWatches()
  val pagerState = rememberPagerState(pageCount = { watches.size })

  // Persist the selected watch name when the current page changes
  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }
      .distinctUntilChanged()
      .collectLatest { page ->
        val name = watches.getOrNull(page)?.name ?: return@collectLatest
        viewModel.saveWallpaperName(name)
      }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    // .background(Color(0xFF000000).copy(alpha = 0.5f)),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    HorizontalPager(
      state = pagerState,
      modifier =
        Modifier.fillMaxWidth()
          .aspectRatio(1f) // Square based on width to leave space for indicator and button
    ) { page ->
      val watch = watches[page]

      Box(
        modifier =
          Modifier.fillMaxSize().clickable {
            launchDigitalClockWallpaperPicker(context, watch.name)
          },
        contentAlignment = Alignment.Center
      ) {
        watch.composable(Modifier.fillMaxSize(), TimeZone.getDefault())
      }
    }

    // Dots page indicator
    Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
      repeat(watches.size) { index ->
        Box(
          modifier =
            Modifier.size(8.dp)
              .background(
                color =
                  if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = CircleShape
              )
        )
        if (index < watches.size - 1) {
          Spacer(modifier = Modifier.width(8.dp))
        }
      }
    }

    // Choose button
    /*Button(
        onClick = {
            val current = watches.getOrNull(pagerState.currentPage)
            if (current != null) {
                launchDigitalClockWallpaperPicker(context, current.name)
            }
        },
        modifier = Modifier.padding(top = 50.dp)
    ) {
        Text(text = "Choose as Wallpaper")
    } */

    Spacer(modifier = Modifier.height(50.dp))

    Button(
      onClick = {
        val current = watches.getOrNull(pagerState.currentPage)
        if (current != null) {
          launchDigitalClockWallpaperPicker(context, current.name)
        }
      },
      modifier = Modifier.padding(top = 50.dp)
    ) {
      Text(text = "Choose as Wallpaper")
    }

    /*  SwissTimeGradientButton("Choose as Wallpaper", onClick = {
        val current = watches.getOrNull(pagerState.currentPage)
        if (current != null) {
            launchDigitalClockWallpaperPicker(context, current.name)
        }
    })*/
  }
}
