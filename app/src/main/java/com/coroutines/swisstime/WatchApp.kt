package com.coroutines.swisstime

// import com.coroutines.swisstime.data.TimeZoneRepository
// import com.coroutines.swisstime.data.TimeZoneService
// import com.coroutines.swisstime.data.WatchPreferencesRepository
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.coroutines.swisstime.navigation.NavGraph
import com.coroutines.swisstime.navigation.SwissTimeNavigationBar
import com.coroutines.swisstime.navigation.TopLevelDestination
import com.coroutines.swisstime.ui.components.ModalDrawerContent
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.viewmodel.ThemeViewModel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.getWatches
import com.coroutines.swisstime.widget.WatchWidget
import com.coroutines.systemuicontroller.rememberSystemUiController
import com.coroutines.worldclock.common.model.WatchInfo
import com.coroutines.worldclock.common.repository.ThemePreferencesRepository
import com.coroutines.worldclock.common.repository.TimeZoneRepository
import com.coroutines.worldclock.common.repository.WatchPreferencesRepository
import com.coroutines.worldclock.common.service.TimeZoneService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WatchApp(watchPreferencesRepository: WatchPreferencesRepository) {
  val context = LocalContext.current

  val coroutineScope = rememberCoroutineScope()

  val themePreferencesRepository = remember { ThemePreferencesRepository(context) }

  val themeViewModel =
    viewModel<ThemeViewModel>(factory = ThemeViewModel.Factory(themePreferencesRepository))

  val themeMode by themeViewModel.themeMode.collectAsState()
  val darkMode by themeViewModel.darkMode.collectAsState()

  val selectedWatchName by
    watchPreferencesRepository.selectedWatchName.collectAsState(initial = null)

  val watches = getWatches()

  val navController = rememberNavController()

  val timeZoneService = remember { TimeZoneService() }
  val timeZoneRepository = remember {
    TimeZoneRepository(timeZoneService, watchPreferencesRepository)
  }

  val watchViewModel =
    viewModel<WatchViewModel>(
      factory = WatchViewModel.Factory(watchPreferencesRepository, timeZoneRepository, watches)
    )

  val navBackStackEntry = navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry.value?.destination

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

  val selectWatchForWidget = { watch: WatchInfo ->
    coroutineScope.launch {
      watchPreferencesRepository.saveSelectedWatch(watch.name)

      WatchWidget().updateAll(context)

      Toast.makeText(context, "\"${watch.name}\" selected for widget", Toast.LENGTH_SHORT).show()
    }
  }

  SwissTimeTheme(themeMode = themeMode, dynamicColor = false) {
    ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        ModalDrawerSheet(
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
          // No top app bar
        },
        bottomBar = {
          val configuration = LocalConfiguration.current
          val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

          val showBottomBar =
            !isLandscape &&
              TopLevelDestination.values().any { top ->
                currentDestination?.hierarchy?.any { it.route == top.route } == true
              }

          if (showBottomBar) {
            SwissTimeNavigationBar(
              navController = navController,
              currentDestination = currentDestination
            )
          }
        }
      ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
          NavGraph(
            navController = navController,
            watches = watches,
            selectedWatchName = selectedWatchName,
            onSelectForWidget = { watch ->
              selectWatchForWidget(watch)
              Unit
            },
            watchViewModel = watchViewModel,
            themeViewModel = themeViewModel
          )
        }
      }
    }
  }
}
