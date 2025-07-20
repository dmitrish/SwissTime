package com.coroutines.swisstime.wearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.coroutines.swisstime.wearos.model.WatchFace
import com.coroutines.swisstime.wearos.repository.WatchFaceRepository
import com.coroutines.swisstime.wearos.service.TimeZoneService
import com.coroutines.swisstime.wearos.repository.TimeZoneRepository
import com.coroutines.swisstime.wearos.repository.TimeZoneInfo
import com.coroutines.swisstime.wearos.ui.CustomWorldMapWithDayNight
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the repositories
        val timeZoneService = TimeZoneService()
        val timeZoneRepository = TimeZoneRepository(timeZoneService)
        val watchFaceRepository = WatchFaceRepository(timeZoneRepository)

        setContent {
            WatchApp(watchFaceRepository)
        }
    }
}

@Composable
fun WatchApp(watchFaceRepository: WatchFaceRepository) {
    MaterialTheme {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "watch_list"
        ) {
            composable("watch_list") {
                WatchListScreen(
                    watchFaceRepository = watchFaceRepository,
                    onWatchSelected = { watchId ->
                        navController.navigate("watch_detail/$watchId")
                    }
                )
            }
            composable("watch_detail/{watchId}") { backStackEntry ->
                val watchId = backStackEntry.arguments?.getString("watchId")
                WatchDetailScreen(
                    watchFaceRepository = watchFaceRepository,
                    watchId = watchId ?: "1",
                    onSelectTimeZone = {
                        navController.navigate("timezone_selection")
                    }
                )
            }
            composable("timezone_selection") {
                TimeZoneSelectionScreen(
                    watchFaceRepository = watchFaceRepository,
                    onTimeZoneSelected = { timeZoneId ->
                        watchFaceRepository.saveSelectedTimeZone(timeZoneId)
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun WatchListScreen(
    watchFaceRepository: WatchFaceRepository,
    onWatchSelected: (String) -> Unit
) {
    val watchFaces = remember { watchFaceRepository.getWatchFaces() }

    Scaffold(
        timeText = { /* TimeText() */}
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            item {
                Text(
                    text = "Swiss Time Watch Faces",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            items(watchFaces) { watchFace ->
                WatchFaceItem(
                    watchFace = watchFace,
                    onClick = { onWatchSelected(watchFace.id) }
                )
            }
        }
    }
}

@Composable
fun WatchFaceItem(
    watchFace: WatchFace,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
           // .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
             //   .padding(8.dp)
        ) {
            Text(
                text = watchFace.name,
                style = MaterialTheme.typography.title2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = watchFace.description,
                style = MaterialTheme.typography.body2,
                maxLines = 2
            )
        }
    }
}

@Composable
fun WatchDetailScreen(
    watchFaceRepository: WatchFaceRepository,
    watchId: String,
    onSelectTimeZone: () -> Unit = {}
) {
    val watchFace = remember(watchId) { 
        watchFaceRepository.getWatchFaceById(watchId) ?: watchFaceRepository.getWatchFaces().firstOrNull() 
    }

    // Get the selected timezone
    val selectedTimeZoneId = watchFaceRepository.getSelectedTimeZoneId().collectAsState()
    val selectedTimeZone = remember(selectedTimeZoneId.value) {
        TimeZone.getTimeZone(selectedTimeZoneId.value)
    }

    // Make the watchface occupy the entire screen
    if (watchFace != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Display the watchface
            watchFace.composable(Modifier.fillMaxSize(), selectedTimeZone)

            // Add a clickable area at the top of the screen where the timezone name is displayed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Get the timezone display name using the selectedTimeZoneId state
                val timeZones = remember { watchFaceRepository.getAllTimeZones() }
                val timeZoneInfo = remember(selectedTimeZoneId.value) {
                    timeZones.find { it.id == selectedTimeZoneId.value } ?: TimeZoneInfo(
                        id = selectedTimeZoneId.value,
                        displayName = selectedTimeZoneId.value
                    )
                }

                // Create a clickable row with the timezone name and an icon
                Row(
                    modifier = Modifier
                        .clickable(onClick = onSelectTimeZone)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display the timezone name
                    Text(
                        text = timeZoneInfo.displayName,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Add an icon to indicate it's tappable
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Change Timezone",
                        tint = Color.White
                    )
                }
            }

            // Display the world map in the lower part of the screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding( 46.dp)
                    .aspectRatio(2f)  // Maintain the aspect ratio of the world map
            ) {
                //Text("hi")
                // Display the world map with day/night visualization
                CustomWorldMapWithDayNight(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Watch face not found",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TimeZoneSelectionScreen(
    watchFaceRepository: WatchFaceRepository,
    onTimeZoneSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val timeZones = remember { watchFaceRepository.getAllTimeZones() }

    Scaffold(
        timeText = { /* TimeText() */}
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            item {
                Text(
                    text = "Select Time Zone",
                    style = MaterialTheme.typography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            items(timeZones) { timeZoneInfo ->
                Card(
                    onClick = { onTimeZoneSelected(timeZoneInfo.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = timeZoneInfo.displayName,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WatchApp(WatchFaceRepository())
}
