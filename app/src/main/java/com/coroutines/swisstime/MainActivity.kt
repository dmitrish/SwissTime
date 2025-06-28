package com.coroutines.swisstime

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.coroutines.swisstime.data.WatchPreferencesRepository
import com.coroutines.swisstime.splash.SplashScreenSelector
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.widget.WatchWidget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.coroutines.swisstime.ui.screens.WatchDetailScreen
import com.coroutines.swisstime.ui.screens.WatchListScreen
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.watchfaces.AhoiNeomatic38DateAtlantic
import com.coroutines.swisstime.watchfaces.AutobahnNeomatic41DateSportsGray
import com.coroutines.swisstime.watchfaces.BlancpainFiftyFathoms
import com.coroutines.swisstime.watchfaces.BreguetClassique
import com.coroutines.swisstime.watchfaces.BreitlingNavitimer
import com.coroutines.swisstime.watchfaces.CarlFBuchererManero
import com.coroutines.swisstime.watchfaces.ChopardLUC
import com.coroutines.swisstime.watchfaces.FranckMullerVanguard
import com.coroutines.swisstime.watchfaces.GirardPerregauxLaureato
import com.coroutines.swisstime.watchfaces.HMoserEndeavour
import com.coroutines.swisstime.watchfaces.IWCPortugieser
import com.coroutines.swisstime.watchfaces.JaegerLeCoultreReverso
import com.coroutines.swisstime.watchfaces.LonginesMasterCollection
import com.coroutines.swisstime.watchfaces.ParmigianiFTonda
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import com.coroutines.swisstime.watchfaces.TAGHeuerCarrera
import com.coroutines.swisstime.watchfaces.UlysseNardinMarineChronometer
import com.coroutines.swisstime.watchfaces.VacheronConstantinClock
import com.coroutines.swisstime.watchfaces.VacheronConstantinPatrimony
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

// Data class to hold watch information
data class WatchInfo(
    val name: String,
    val description: String,
    val composable: @Composable (Modifier) -> Unit
)

// Function to get the watch face color based on the watch name
fun getWatchFaceColor(watchName: String): Color {
    return when {
        watchName.contains("Autobahn Neomatic 41") -> Color(0xFF4A4A4A) // Sports gray dial
        watchName.contains("Zenith El Primero") -> Color(0xFFF0F0F0) // Silver-white dial
        watchName.contains("Omega Seamaster") -> Color(0xFF0A4D8C) // Deep blue dial
        watchName.contains("Rolex Submariner") -> Color(0xFF000000) // Black dial
        watchName.contains("Patek Philippe") -> Color(0xFFF5F5DC) // Cream dial
        watchName.contains("Audemars Piguet") -> Color(0xFF00008B) // Dark blue dial
        watchName.contains("Vacheron Constantin") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Jaeger-LeCoultre") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Blancpain") -> Color(0xFF000000) // Black dial
        watchName.contains("IWC") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Breitling") -> Color(0xFF000080) // Navy blue dial
        watchName.contains("TAG Heuer") -> Color(0xFF000000) // Black dial
        watchName.contains("Longines") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Chopard") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Ulysse Nardin") -> Color(0xFF00008B) // Dark blue dial
        watchName.contains("Girard-Perregaux") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Oris") -> Color(0xFF000000) // Black dial
        watchName.contains("Tudor") -> Color(0xFF000000) // Black dial
        watchName.contains("Baume & Mercier") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Rado") -> Color(0xFF000000) // Black dial
        watchName.contains("Tissot") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Raymond Weil") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Frederique Constant") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Alpina") -> Color(0xFF000000) // Black dial
        watchName.contains("Mondaine") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Swatch") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Ahoi Neomatic") -> Color(0xFF1A3A5A) // Deep Atlantic blue dial
        else -> Color(0xFF2F4F4F) // Default to the current background color
    }
}

// Function to darken a color by a factor
fun Color.darken(factor: Float = 0.2f): Color {
    val alpha = this.alpha
    val red = this.red * (1 - factor)
    val green = this.green * (1 - factor)
    val blue = this.blue * (1 - factor)
    return Color(red, green, blue, alpha)
}

// Function to determine if a color is dark
fun Color.isDark(): Boolean {
    return this.luminance() < 0.5f
}

// Function to get appropriate text color for a background
fun getTextColorForBackground(backgroundColor: Color): Color {
    return if (backgroundColor.isDark()) Color.White else Color.Black
}

class MainActivity : ComponentActivity() {
    // Initialize the preferences repository
    private lateinit var watchPreferencesRepository: WatchPreferencesRepository

    // Flag to track whether content is ready to be shown
    private var isContentReady = false

    override fun onCreate(savedInstanceState: Bundle?) {


        // Install splash screen before calling super.onCreate()
        val splashScreen = installSplashScreen()


        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()))


        // Keep the splash screen visible until the app is fully loaded
       /* splashScreen.setKeepOnScreenCondition { !isContentReady }

        // Let the Splash Screen API handle the animation automatically
        // We only need to set up a custom exit animation for the fade out
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            // Get the splash screen view
            val splashScreenView = splashScreenViewProvider.view

            // Create a fade out animation with longer duration
            val fadeOut = android.animation.ObjectAnimator.ofFloat(
                splashScreenView,
                android.view.View.ALPHA,
                1f,
                0f
            )
            fadeOut.interpolator = android.view.animation.DecelerateInterpolator()
            fadeOut.duration = 1500L // 1.5 seconds for fade out to give animation more time

            // Start the animation and remove the splash screen once it's done
            fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // Remove the splash screen immediately after fade out
                    splashScreenViewProvider.remove()
                }
            })

            // Start the animation
            fadeOut.start()
        }


        */
        // Initialize the repository
        watchPreferencesRepository = WatchPreferencesRepository(this)

        setContent {
            SwissTimeTheme {

               WatchApp(watchPreferencesRepository)

                // Mark content as ready after a delay to give animation time to play
                // This will dismiss the splash screen
                SideEffect {
                    // Delay setting isContentReady to true to match the animation duration
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isContentReady = true
                    }, 2000) // 2 second delay to match the animation duration
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WatchAppPreview() {
    // Create a mock repository for preview
    val context = LocalContext.current
    val mockRepository = remember { WatchPreferencesRepository(context) }

    SwissTimeTheme {
       /* Box(
            Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            contentAlignment = Alignment.Center) {
            Text("hi")
        }*/
       WatchApp(mockRepository)
    }
}
