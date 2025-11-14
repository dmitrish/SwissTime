package com.coroutines.swisstime.ui.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.screens.TimeZoneAwareWatchFace2
import com.coroutines.swisstime.viewmodel.WallpaperViewmodel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.wallpaper.launchDigitalClockWallpaperPicker
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
            name = "Horologia Romanum",
            description = "The Horologia Romanum, first created in the 1930s, features a clean dial design inspired by precision marine chronometers. Known for its large case size and elegant simplicity, it represents HR's commitment to technical excellence and timeless design.",
            composable = { modifier, timeZone -> HorologiaRomanum(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
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

    Column(
        modifier = Modifier
        .fillMaxSize(),
        //.background(Color(0xFF000000).copy(alpha = 0.5f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f) // Square aspect ratio for watches
        ) { page ->

            val watch = watches[page]
            viewModel.saveWallpaperName(watch.name)
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

        // Page indicator
        Text(
            text = "${pagerState.currentPage + 1} ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}