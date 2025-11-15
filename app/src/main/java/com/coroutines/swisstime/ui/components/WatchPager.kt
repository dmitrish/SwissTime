package com.coroutines.swisstime.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.screens.TimeZoneAwareWatchFace2
import com.coroutines.swisstime.viewmodel.WallpaperViewmodel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.wallpaper.launchDigitalClockWallpaperPicker
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.swisstime.watchfaces.HorologiaRomanum
import com.coroutines.swisstime.watchfaces.JurgsenZenithor
import com.coroutines.swisstime.watchfaces.Knot
import com.coroutines.swisstime.watchfaces.RomaMarina
import com.coroutines.swisstime.watchfaces.Zeitwerk
import com.coroutines.worldclock.common.model.WatchInfo
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import java.util.TimeZone


@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun WatchPager (context: Context){

    val appContext = context.applicationContext

    val viewModel = WallpaperViewmodel(WallpaperPreferenceRepository(appContext))

    val watches = listOf(
        WatchInfo(
            name = "Roma Marina",
            description = "The Roma Marina, first introduced in 1975, features an integrated bracelet and octagonal bezel. With its distinctive hobnail pattern dial, it represents the brand's ability to combine technical excellence with distinctive design elements.",
            composable = { modifier, timeZone -> RomaMarina(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Chronomagus Regum",
            description = "The Chronomagus Regum is celebrated for its ultra-thin profile and minimalist design. Since the 1950s, Chronomagus has been a pioneer in creating incredibly slim watches, with the Regum line showcasing the brand's expertise in producing elegant timepieces that combine technical innovation with refined aesthetics.",
            composable = { modifier, timeZone -> ChronomagusRegum(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Alpenglühen Zeitwerk",
            description = "The Alpenglühen Zeitwerk features a deep blue dial inspired by the Atlantic Ocean. This German-made timepiece combines Bauhaus minimalism with dive watch functionality, featuring a waterproof design, luminous markers, and the distinctive red seconds hand that is a signature of Alpenglühen Zeitwerk watches.",
            composable = { modifier, timeZone -> Zeitwerk(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
    WatchInfo(
        name = "Knot Urushi",
        description = "The Knot Urushi is a collaboration between modern watchmaking and traditional Japanese craftsmanship. Its deep jet black dial is created through the meticulous Urushi lacquer technique, involving repeated painting, drying, and sharpening by skilled artisans. The dial is adorned with gold powder scraped from gold ingots, creating a subtle shimmer effect as light plays across the surface. With its minimalist silver hands and markers, this timepiece exemplifies the perfect harmony between Japanese aesthetics and precision timekeeping.",
        composable = { modifier, timeZone -> Knot(modifier = modifier, timeZone = timeZone) }
    ))
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
        modifier = Modifier
            .fillMaxSize(),
        //.background(Color(0xFF000000).copy(alpha = 0.5f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Square based on width to leave space for indicator and button
        ) { page ->

            val watch = watches[page]
          //  val watchInfo = WatchInfo("name here", "America/Los_Angeles")

          //  val name = watches[page].first

            Box (
                modifier = Modifier.fillMaxSize().
                    // .background(DarkNavy.darken(0.3f)).
                clickable{
                   // viewModel.saveWallpaperName(watch.name)
                    launchDigitalClockWallpaperPicker(context, watch.name)
                },
                contentAlignment = Alignment.Center
            ) {
                watch.composable(Modifier.fillMaxSize(), TimeZone.getDefault())
              //  TimeZoneAwareWatchFace2(watch, viewModel = WatchViewModel())
               // WatchCanvas(watch = watch)
            }
        }

        // Dots page indicator
        Row(
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

        // Choose button
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
    }
}