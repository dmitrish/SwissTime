// Complete imports for AnimatedCardList and AnimatedCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.coroutines.swisstime.wearos.model.WatchFace
import kotlinx.coroutines.delay

// Data class for card items
data class CardData(
    val title: String,
    val description: String
)

// Sample data function
fun getCardData(): List<CardData> {
    return listOf(
        CardData("Workout", "Start your daily exercise"),
        CardData("Heart Rate", "Check your current BPM"),
        CardData("Steps", "Today's step count"),
        CardData("Sleep", "Last night's sleep data"),
        CardData("Weather", "Current conditions"),
        CardData("Timer", "Set workout timer"),
        CardData("Music", "Control your playlist"),
        CardData("Notifications", "Recent messages")
    )
}

@Composable
fun AnimatedCardList(watchFaces: List<WatchFace>,   onWatchSelected: (String) -> Unit)  {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        scalingParams = ScalingLazyColumnDefaults.scalingParams(
            edgeScale = 0.7f,
            edgeAlpha = 0.7f,
            minElementHeight = 32f,
            maxElementHeight = 46f,
            minTransitionArea = 0.2f,
            maxTransitionArea = 0.6f,
            scaleInterpolator = CubicBezierEasing(0.25f, 0.00f, 0.75f, 1.00f),
          //  snapOffset = 0f
        )
    ) {
        // Header with fade-in animation
        item {
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800),
                label = "header_alpha"
            )

            Text(
                text = "World Clock Watch Faces",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .alpha(alpha),
                color = MaterialTheme.colors.primary
            )
        }

        // Animated cards
        itemsIndexed(watchFaces) { index, cardItem ->
            val animationDelay = index * 100
            AnimatedCard(
                title = "hello",
                description = cardItem.description,
                animationDelay = animationDelay,
                onClick = {
                     onWatchSelected(cardItem.id)
                    // Handle card click here
                   // println("Clicked on ${cardItem.title}")
                }
            )
        }

        // Footer spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnimatedCard(
    title: String,
    description: String,
    animationDelay: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.title3,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

