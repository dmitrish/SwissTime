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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coroutines.livewallpaper.DigitalClockWallpaperService
import com.coroutines.livewallpaper.common.BaseClock
import com.coroutines.livewallpaper.service.PagerWallpaperService
import com.coroutines.livewallpaper.watches.ChronomagusRegumClock
import com.coroutines.livewallpaper.watches.KnotClock
import com.coroutines.livewallpaper.watches.PontifexChronometraClock
import com.coroutines.livewallpaper.watches.RomaMarinaClock
import com.coroutines.livewallpaper.watches.ZeitwerkClock

/**
 * Launches the live wallpaper picker for the digital clock wallpaper
 */
fun launchDigitalClockWallpaperPicker(context: Context) {
    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
    intent.putExtra(
        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
        ComponentName(context.packageName, PagerWallpaperService::class.java.name)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


@Composable
fun WallPaperWatchesHorizontalPager() {
    val context = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    
    // Create instances of all available watches
    val watches = remember {
        listOf(
            PontifexChronometraClock(context, handler),
            RomaMarinaClock(context, handler),
            ChronomagusRegumClock(context, handler),
            KnotClock(context, handler),
            ZeitwerkClock(context, handler)
        )
    }
    
    // Create a pager state
    val pagerState = rememberPagerState(pageCount = { watches.size })
    
    // Column to hold the pager and page indicator
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Horizontal pager for watches
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Square aspect ratio for watches
        ) { page ->
            // Get the watch for this page
            val watch = watches[page]
            
            // Box to center the watch
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Canvas to draw the watch
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
            watches.forEach { it.destroy() }
        }
    }
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
                onClick = { launchDigitalClockWallpaperPicker(context) }
            ) {
                Text("Set as Wallpaper")
            }
        }
    }
}