package com.coroutines.swisstime

import android.app.AlertDialog
import com.coroutines.swisstime.ui.adaptive.LocalWindowSizeClass
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.lifecycleScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.coroutines.swisstime.broadcast.ScreenUnlockReceiver
import com.coroutines.swisstime.update.AppUpdateManager
import com.coroutines.swisstime.utils.isDark
import com.coroutines.worldclock.common.repository.WatchPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch




fun getTextColorForBackground(backgroundColor: Color): Color {
    return if (backgroundColor.isDark()) Color.White else Color.Black
}

class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateState = MutableStateFlow<AppUpdateManager.UpdateStatus>(
        AppUpdateManager.UpdateStatus.Idle
    )
    private lateinit var screenUnlockReceiver: ScreenUnlockReceiver

    private lateinit var watchPreferencesRepository: WatchPreferencesRepository

    private var isContentReady = false

    private fun applyEdgeToEdge() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )
    }


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        applyEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { !isContentReady }
        setupScreenUnlockListener()
        setupPlayUpdates()

        setContent {
            SwissTimeTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    watchPreferencesRepository = WatchPreferencesRepository(this)

                    UpdateDialog()

                    WatchApp(watchPreferencesRepository)

                    SideEffect {
                        Handler(Looper.getMainLooper()).postDelayed({
                            isContentReady = true
                        }, 100)
                    }
                }
            }
        }
    }

    // CRITICAL: Add this method
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdateManager.onActivityResult(requestCode, resultCode)
    }



    private fun setupPlayUpdates() {
        appUpdateManager = AppUpdateManager(this, lifecycleScope)
        lifecycle.addObserver(appUpdateManager)

        // Use repeatOnLifecycle for lifecycle-safe collection
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appUpdateManager.updateStatus.collect { status ->
                    updateState.value = status
                }
            }
        }

        // Check for updates on startup
        appUpdateManager.checkForUpdate()
    }

    @Composable
    private fun UpdateDialog() {
        val updateStatus by updateState.collectAsState()

        when (val status = updateStatus) {
            is AppUpdateManager.UpdateStatus.Downloaded -> {
                AlertDialog(
                    onDismissRequest = { }, // Don't dismiss - user should make a choice
                    title = { Text("Update Ready") },
                    text = {
                        Text("A new version has been downloaded. Restart the app to install it.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            appUpdateManager.completeUpdate()
                        }) {
                            Text("RESTART")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            updateState.value = AppUpdateManager.UpdateStatus.Idle
                        }) {
                            Text("Later")
                        }
                    }
                )
            }

            is AppUpdateManager.UpdateStatus.Downloading -> {
                LinearProgressIndicator(
                    progress = { status.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            is AppUpdateManager.UpdateStatus.Failed -> {
                LaunchedEffect(status) {
                    // Log failures, don't necessarily show to user
                    Log.w("Updates", "Update check failed: ${status.reason}")
                    delay(3000)
                    updateState.value = AppUpdateManager.UpdateStatus.Idle
                }
            }

            else -> { /* No UI needed */ }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-apply edge-to-edge mode when activity resumes
        // This prevents content shifting when device is unlocked
        applyEdgeToEdge()
    }

    override fun onPause() {
        super.onPause()

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenUnlockReceiver)
        lifecycle.removeObserver(appUpdateManager)
    }

    private fun setupScreenUnlockListener() {
        screenUnlockReceiver = ScreenUnlockReceiver()

        val intentFilter = IntentFilter().apply {
            addAction("android.intent.action.USER_PRESENT")
        }

        registerReceiver(screenUnlockReceiver, intentFilter)
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
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
override fun onCreate(savedInstanceState: Bundle?) {

    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    applyEdgeToEdge()

    splashScreen.setKeepOnScreenCondition { !isContentReady }

    setupScreenUnlockListener()

    setUpPlayUpdates()

    setContent {
        SwissTimeTheme {
            val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(this)
            CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                watchPreferencesRepository = WatchPreferencesRepository(this)
                WatchApp(watchPreferencesRepository)
                SideEffect {
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isContentReady = true
                    }, 100)
                }
            }
        }
    }
}



private fun setUpPlayUpdates() {
    val activityScope = lifecycleScope

    appUpdateManager = AppUpdateManager(this, activityScope)

    lifecycle.addObserver(appUpdateManager)

    // Check for updates when the app starts
    appUpdateManager.checkForUpdate()

    // Set up a listener for update status changes
    lifecycleScope.launch {
        appUpdateManager.updateStatus.collect { status ->
            when (status) {
                is AppUpdateManager.UpdateStatus.Downloaded -> {
                    // Show a dialog when an update has been downloaded
                    AlertDialog.Builder(this@MainActivity)
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
                    Toast.makeText(
                        this@MainActivity,
                        "Update failed: ${status.reason}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    // No action needed for other states
                }
            }
        }
    }
}


 */
