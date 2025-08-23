package com.coroutines.swisstime

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
//import com.coroutines.swisstime.data.WatchPreferencesRepository
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.coroutines.swisstime.update.AppUpdateManager
import com.coroutines.swisstime.utils.isDark
import com.coroutines.worldclock.common.repository.WatchPreferencesRepository
import kotlinx.coroutines.launch
import java.util.TimeZone

// Data class to hold watch information


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
        watchName.contains("Tokinoha") -> Color(0xFF000000) // Black dial
        watchName.contains("Longines") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Chopard") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Constantinus") -> Color(0xFF00008B) // Dark blue dial
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



// Function to get appropriate text color for a background
fun getTextColorForBackground(backgroundColor: Color): Color {
    return if (backgroundColor.isDark()) Color.White else Color.Black
}

class MainActivity : ComponentActivity() {

    private lateinit var watchPreferencesRepository: WatchPreferencesRepository

    private lateinit var appUpdateManager: AppUpdateManager

    private var isContentReady = false

    private fun applyEdgeToEdge() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        // Apply edge-to-edge mode
        applyEdgeToEdge()
        // Keep the splash screen visible until the app is fully loaded
        splashScreen.setKeepOnScreenCondition { !isContentReady }

        watchPreferencesRepository = WatchPreferencesRepository(this)

        // Create a coroutine scope for the activity
        val activityScope = lifecycleScope

        // Initialize the app update manager
        appUpdateManager = AppUpdateManager(this, activityScope)

        // Add the lifecycle observer to handle update checks on resume
        lifecycle.addObserver(appUpdateManager)

        // Check for updates when the app starts
        appUpdateManager.checkForUpdate()

        // Set up a listener for update status changes
        lifecycleScope.launch {
            appUpdateManager.updateStatus.collect { status ->
                when (status) {
                    is AppUpdateManager.UpdateStatus.Downloaded -> {
                        // Show a dialog when an update has been downloaded
                        android.app.AlertDialog.Builder(this@MainActivity)
                            .setTitle("Update Downloaded")
                            .setMessage("An update has been downloaded. Install now?")
                            .setPositiveButton("RESTART") { _, _ ->
                                appUpdateManager.completeUpdate()
                            }
                            .setNegativeButton("Later", null)
                            .show()
                    }
                    is AppUpdateManager.UpdateStatus.Failed -> {
                        // Show a toast when an update has failed
                        android.widget.Toast.makeText(
                            this@MainActivity,
                            "Update failed: ${status.reason}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        // No action needed for other states
                    }
                }
            }
        }

        setContent {
            SwissTimeTheme {
               WatchApp(watchPreferencesRepository)
                SideEffect {
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isContentReady = true
                    }, 500)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-apply edge-to-edge mode when activity resumes
        // This prevents content shifting when device is unlocked
        applyEdgeToEdge()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        appUpdateManager.onActivityResult(requestCode, resultCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(appUpdateManager)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WatchAppPreview() {

    val context = LocalContext.current
    val mockRepository = remember { WatchPreferencesRepository(context) }

    SwissTimeTheme {
       WatchApp(mockRepository)
    }
}


/*
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