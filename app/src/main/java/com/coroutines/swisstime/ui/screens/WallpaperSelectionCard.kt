package com.coroutines.swisstime.ui.screens

import android.app.WallpaperInfo
import android.app.WallpaperManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.navigation.Screen
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.darken
import com.coroutines.swisstime.wallpaper.wallpaperWatches
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository

@Composable
fun WallpaperSelectionCard(
    modifier: Modifier,
    wallpaperPreferenceRepository: WallpaperPreferenceRepository,
    onNavigationRequested: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Observe selected watch name
    val selectedName: String? by wallpaperPreferenceRepository.selectedWallpaperName.collectAsState(initial = null)

    // Check if the currently applied wallpaper is from this app (live wallpaper)
    val wm = WallpaperManager.getInstance(context)
    val activeFromThisApp: Boolean = try {
        val info: WallpaperInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) wm.wallpaperInfo else wm.wallpaperInfo
        info?.packageName == context.packageName
    } catch (e: Exception) {
        false
    }

    val watches = wallpaperWatches()
    val selectedWatch = watches.firstOrNull { it.name == selectedName }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkNavy.darken(0.3f)
        )
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(
                text = "Wallpaper",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // If our live wallpaper is active and we have a selected watch, show it
            if (activeFromThisApp && selectedWatch != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    selectedWatch.composable(Modifier.fillMaxWidth(), java.util.TimeZone.getDefault())
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show Remove button
                Button(
                    onClick = {
                        // Clear the wallpaper (both system and lock if possible)
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                wm.clear(WallpaperManager.FLAG_SYSTEM)
                                wm.clear(WallpaperManager.FLAG_LOCK)
                            } else {
                                wm.clear()
                            }
                        } catch (_: Exception) { }
                        // Also reset our stored selection so UI switches to default state immediately
                        scope.launch {
                            wallpaperPreferenceRepository.saveSelectedWallpaperName("")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Remove Wallpaper")
                }

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Default state: show Choose button that navigates to the picker
                Button(
                    onClick = { onNavigationRequested(Screen.Wallpaper.route) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Choose Wallpaper")
                }
            }
        }
    }
}

@Preview
@Composable
fun WallpaperSelectionCardPreview() {
    val context = LocalContext.current
    val repo = WallpaperPreferenceRepository(context)
    WallpaperSelectionCard(Modifier, repo) { }
}