package com.coroutines.swisstime.wallpaper

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroutines.livewallpaper.DigitalClockWallpaperService
import com.coroutines.livewallpaper.common.BaseClock
import com.coroutines.livewallpaper.service.ChronomagusRegumWallpaperService
import com.coroutines.livewallpaper.service.PagerWallpaperService
import com.coroutines.livewallpaper.service.RomaMarinaWallpaperService
import com.coroutines.livewallpaper.service.ZeitwerkWallpaperService
import com.coroutines.livewallpaper.watches.ChronomagusRegumClock
import com.coroutines.livewallpaper.watches.KnotClock
import com.coroutines.livewallpaper.watches.PontifexChronometraClock
import com.coroutines.livewallpaper.watches.RomaMarinaClock
import com.coroutines.livewallpaper.watches.ZeitwerkClock
import com.coroutines.swisstime.utils.darken
import com.coroutines.worldclock.common.theme.DarkNavy

/**
 * Launches the live wallpaper picker for the digital clock wallpaper
 */
fun launchDigitalClockWallpaperPicker(context: Context, name: String) {

    val className = when(name){
        "Pontifex" -> PagerWallpaperService::class.java.name
        "Chronomagus" -> ChronomagusRegumWallpaperService::class.java.name
        "RomaMarina" -> RomaMarinaWallpaperService::class.java.name
        "Zeitwerk" -> ZeitwerkWallpaperService::class.java.name
        else -> PagerWallpaperService::class.java.name
    }

    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
    intent.putExtra(
        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
        ComponentName(context.packageName, className)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


@Composable
fun WallPaperWatchesHorizontalPager(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    
    // Create instances of all available watches
    val watches = remember {
        listOf(
            "Pontifex" to PontifexChronometraClock(context, handler),
            "RomaMarina" to RomaMarinaClock(context, handler),
            "Chronomagus" to ChronomagusRegumClock(context, handler),
            "Knot" to KnotClock(context, handler),
            "Zeitwerk" to ZeitwerkClock(context, handler)
        )
    }
    
    // Create a pager state
    val pagerState = rememberPagerState(pageCount = { watches.size })

    Column(
        modifier = modifier
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

            val watch = watches[page].second
            val name = watches[page].first

            Box (
                modifier = Modifier.fillMaxSize().
                  // .background(DarkNavy.darken(0.3f)).
                clickable{
                    launchDigitalClockWallpaperPicker(context, name)
                },
                contentAlignment = Alignment.Center
            ) {
                WatchCanvas(watch = watch)
            }
        }
        
        // Page indicator
        Text(
            text = "${pagerState.currentPage + 1} / ${watches.size}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
    
    // Clean up resources when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            watches.forEach { it.second.destroy() }
        }
    }
}


@Preview
@Composable
fun WallPaperWatchesHorizontalPagerPreview() {
    WallPaperWatchesHorizontalPager()
}

/**
 * A composable that renders a watch on a Canvas
 */
@Composable
private fun WatchCanvas(watch: BaseClock) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Get the size of the canvas
        val canvasWidth = size.width.toInt()
        val canvasHeight = size.height.toInt()
        
        // Update text sizes based on canvas width
        watch.updateTextSizes(canvasWidth)
        
        // Draw the watch on the canvas
        drawIntoCanvas { canvas ->
            watch.draw(canvas.nativeCanvas)
        }
    }
}
/**
 * A composable that displays a card with information about the digital clock wallpaper
 * and a button to set it as the device wallpaper.
 */
@Composable
fun DigitalClockWallpaperCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Digital Clock Live Wallpaper",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Display the current time on your home screen with this simple digital clock live wallpaper.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {  }
            ) {
                Text("Set as Wallpaper")
            }
        }
    }
}