package com.coroutines.swisstime

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.DrawerDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.glance.appwidget.updateAll
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
//import com.coroutines.swisstime.data.TimeZoneRepository
//import com.coroutines.swisstime.data.TimeZoneService
//import com.coroutines.swisstime.data.WatchPreferencesRepository
import com.coroutines.swisstime.navigation.NavGraph
import com.coroutines.swisstime.navigation.SwissTimeNavigationBar
import com.coroutines.swisstime.navigation.TopLevelDestination
import com.coroutines.swisstime.ui.components.ModalDrawerContent
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.viewmodel.ThemeViewModel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.AventinusClassique
import com.coroutines.swisstime.watchfaces.Zeitwerk
import com.coroutines.swisstime.watchfaces.JurgsenZenithor
import com.coroutines.swisstime.watchfaces.EdgeOfSecond
import com.coroutines.swisstime.watchfaces.YamaNoToki
import com.coroutines.swisstime.watchfaces.LucernaRoma
import com.coroutines.swisstime.watchfaces.RomaMarina
import com.coroutines.swisstime.watchfaces.CenturioLuminor
import com.coroutines.swisstime.watchfaces.HorologiaRomanum
import com.coroutines.swisstime.watchfaces.Concordia
import com.coroutines.swisstime.watchfaces.KandinskyEvening
import com.coroutines.swisstime.watchfaces.PontifexChronometra
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.swisstime.watchfaces.ConstantinusAureusChronometer
import com.coroutines.swisstime.watchfaces.Knot
import com.coroutines.swisstime.watchfaces.LeonardAutomatic
import com.coroutines.swisstime.watchfaces.TemporisB
import com.coroutines.swisstime.watchfaces.Tokinoha

import com.coroutines.swisstime.watchfaces.Valentinianus
import com.coroutines.swisstime.watchfaces.ChantDuTemps
import com.coroutines.swisstime.watchfaces.VostokRussianMilitary
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import com.coroutines.swisstime.watchfaces.getWatches
import com.coroutines.swisstime.widget.WatchWidget
import kotlinx.coroutines.launch
import java.util.TimeZone
import com.coroutines.systemuicontroller.SystemUiController
import com.coroutines.systemuicontroller.rememberSystemUiController
import com.coroutines.worldclock.common.model.WatchInfo
import com.coroutines.worldclock.common.repository.ThemePreferencesRepository
import com.coroutines.worldclock.common.repository.TimeZoneRepository
import com.coroutines.worldclock.common.repository.WatchPreferencesRepository
import com.coroutines.worldclock.common.service.TimeZoneService

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WatchApp(watchPreferencesRepository: WatchPreferencesRepository) {
    // Get the current context
    val context = LocalContext.current

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Keep the splash screen selector code but make it inactive
    // As per requirements: "keep the code for the other implementation, but let it be completely inactive; only Splash Screen API"
    /*val splashScreenSelector = remember { SplashScreenSelector(context) }

    // Force the Splash Screen API to be active if it's not already
    if (!splashScreenSelector.isSplashScreenApiActive()) {
        splashScreenSelector.useSplashScreenApi()
    }*/

    // Create a ThemePreferencesRepository instance
    val themePreferencesRepository = remember { ThemePreferencesRepository(context) }

    // Create a ThemeViewModel instance
    val themeViewModel = viewModel<ThemeViewModel>(
        factory = ThemeViewModel.Factory(themePreferencesRepository)
    )

    // Collect theme preferences from the ThemeViewModel
    val themeMode by themeViewModel.themeMode.collectAsState()
    val darkMode by themeViewModel.darkMode.collectAsState()

    // Collect the currently selected watch name
    val selectedWatchName by watchPreferencesRepository.selectedWatchName.collectAsState(initial = null)

    // List of watches with their details
    val watches = getWatches()

    // Create a NavController
    val navController = rememberNavController()

    // Create TimeZoneService and TimeZoneRepository instances
    val timeZoneService = remember { TimeZoneService() }
    val timeZoneRepository = remember { TimeZoneRepository(timeZoneService, watchPreferencesRepository) }

    // Create a WatchViewModel instance
    val watchViewModel = viewModel<WatchViewModel>(
        factory = WatchViewModel.Factory(watchPreferencesRepository, timeZoneRepository, watches)
    )

    // Get the current back stack entry
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    // Create a drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val uiController = rememberSystemUiController()

    LaunchedEffect(drawerState.isOpen) {
        when (drawerState.isOpen) {
            true -> {
                uiController.setStatusBarColor(Color.Transparent)
            }

            false -> {
                uiController.setStatusBarColor(Color.Transparent)
            }
        }
    }

    // Function to select a watch for the widget
    val selectWatchForWidget = { watch: WatchInfo ->
        coroutineScope.launch {
            // Save the selected watch name
            watchPreferencesRepository.saveSelectedWatch(watch.name)

            // Update the widget
            WatchWidget().updateAll(context)

            // Show a confirmation toast
            Toast.makeText(
                context,
                "\"${watch.name}\" selected for widget",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Apply the theme based on the user's preferences
    SwissTimeTheme(
        themeMode = themeMode,
        dynamicColor = false
    ) {
        // Wrap the Scaffold with ModalNavigationDrawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet (
                    modifier = Modifier.fillMaxSize(),
                    drawerShape = DrawerDefaults.shape,
                    drawerContainerColor = Color.Transparent,
                    windowInsets = WindowInsets(0, 160, 0, 160),
                    ) {
                    ModalDrawerContent()
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    // No top app bar as per requirements
                },
                bottomBar = {
                    val configuration = LocalConfiguration.current
                    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    val showBottomBar = !isLandscape && TopLevelDestination.values().any { top ->
                        currentDestination?.hierarchy?.any { it.route == top.route } == true
                    }

                    if (showBottomBar) {
                        SwissTimeNavigationBar(
                            navController = navController,
                            currentDestination = currentDestination
                        )
                    }
                }
               /* bottomBar = {
                    // Get the current configuration to determine orientation
                    val configuration = LocalConfiguration.current
                    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                    // Only show the navigation bar if we're not on the WatchDetailScreen and not in landscape mode
                    val currentRoute = currentDestination?.route
                    if ((currentRoute == null || !currentRoute.startsWith("watchDetail")) && !isLandscape) {
                        SwissTimeNavigationBar(
                            navController = navController,
                            currentDestination = currentDestination
                        )
                    }
                }n*/
            ) { innerPadding ->
                // Use the Navigation 3 library
                // Apply the innerPadding to the content
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                    NavGraph(
                        navController = navController,
                        watches = watches,
                        selectedWatchName = selectedWatchName,
                        onSelectForWidget = { watch -> 
                            selectWatchForWidget(watch)
                            Unit // Return Unit to match the expected type
                        },
                        watchViewModel = watchViewModel,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}

