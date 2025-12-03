package com.coroutines.swisstime.navigation

import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
//import com.coroutines.swisstime.getWatchFaceColor
import com.coroutines.swisstime.ui.screens.AboutScreen
import com.coroutines.swisstime.ui.screens.BrandLogosScreen
import com.coroutines.worldclock.common.components.CustomWorldMapScreen
import com.coroutines.swisstime.ui.screens.OptimizedWorldMapScreen
import com.coroutines.swisstime.ui.screens.SettingsScreen
import com.coroutines.swisstime.ui.screens.TimeScreen
import com.coroutines.swisstime.ui.screens.WallpaperScreen
import com.coroutines.swisstime.ui.screens.WatchDetailScreen
import com.coroutines.swisstime.ui.screens.WatchListScreen
import com.coroutines.swisstime.ui.screens.WelcomeScreen
import com.coroutines.swisstime.utils.darken
import com.coroutines.swisstime.utils.getWatchFaceColor
import com.coroutines.swisstime.viewmodel.ThemeViewModel
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.worldclock.common.model.WatchInfo

// Define the routes for the app
sealed class Screen(val route: String) {
    object Time : Screen("time")
    object WatchList : Screen("watchList")
    object WatchDetail : Screen("watchDetail/{watchIndex}") {
        fun createRoute(watchIndex: Int): String = "watchDetail/$watchIndex"
    }
    object BrandLogos : Screen("brandLogos")
    object WorldMap : Screen("worldMap")
    object CustomWorldMap : Screen("customWorldMap")
    object Settings : Screen("settings")
    object About : Screen("about")
    object Wallpaper : Screen("wallpaper")

    object Welcome : Screen("welcome")
}

// Animation duration for transitions - extremely short duration for immediate UI updates
private const val ANIMATION_DURATION = 25

@Composable
@androidx.compose.animation.ExperimentalSharedTransitionApi
fun NavGraph(
    navController: NavHostController,
    watches: List<WatchInfo>,
    selectedWatchName: String?,
    onSelectForWidget: (WatchInfo) -> Unit,
    watchViewModel: WatchViewModel,
    themeViewModel: ThemeViewModel
) {

    // Determine the start destination based on whether a watch is already selected
   // val hasSelectedWatch = watchViewModel.selectedWatches.collectAsState().value.isNotEmpty()
   // val startDestinationRoute = if (hasSelectedWatch) Screen.Time.route else Screen.Welcome.route


    
    androidx.compose.animation.SharedTransitionLayout {
        val sharedScope = this
        NavHost(
            navController = navController,
            startDestination = "decide",
            // Set enter/exit animations for the entire NavHost
            enterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(ANIMATION_DURATION))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(ANIMATION_DURATION))
        }
    ) {
        composable("decide") {
            val selected = watchViewModel.selectedWatches.collectAsState().value
            val loaded = watchViewModel.selectedWatchesLoaded.collectAsState().value
            LaunchedEffect(loaded, selected) {
                if (!loaded) return@LaunchedEffect
                val target = if (selected.isNotEmpty()) Screen.Time.route else Screen.Welcome.route
                navController.navigate(target) { popUpTo("decide") { inclusive = true } }
            }
        }

        composable(
            route = Screen.Time.route,
            enterTransition = {
                // Disable fade when navigating from Welcome -> Time to let shared element drive the motion
                if (initialState.destination.route == Screen.Welcome.route) null
                else fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the TimeScreen
            TimeScreen(
                watchViewModel = watchViewModel,
                sharedTransitionScope = sharedScope,
                animatedVisibilityScope = this
            )
        }


        composable(
            route = Screen.WatchList.route,
            // Add specific animations for this route if needed
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Create a LazyListState to remember scroll position
            val listState = rememberLazyListState()

            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the WatchListScreen directly without SystemUiController
            WatchListScreen(
                watches = watches,
                onWatchClick = { watch ->
                    // Find the index of the watch in the list
                    val index = watches.indexOf(watch)
                        navController.navigate(Screen.WatchDetail.createRoute(index))
               },
                onTitleClick = { watch ->
                    // Toggle the watch's selection status using the new method
                    val wasAdded = watchViewModel.toggleWatchSelection(watch)

                    // Show appropriate toast message
                    Toast.makeText(
                        context,
                        if (wasAdded) "\"${watch.name}\" added to selected watches" 
                        else "\"${watch.name}\" removed from selected watches",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                selectedWatches = watchViewModel.selectedWatches.collectAsState().value,
                listState = listState
            )
        }

        composable(
            route = Screen.WatchDetail.route,
            arguments = listOf(
                navArgument("watchIndex") {
                    type = NavType.IntType
                }
            ),
            // Add specific animations for this route if needed
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            // Get the watch index from the arguments
            val watchIndex = backStackEntry.arguments?.getInt("watchIndex") ?: 0
            // Get the watch from the list
            val watch = watches[watchIndex]

            val watchFaceColor = getWatchFaceColor(watch.name)
            val darkenedWatchFaceColor = watchFaceColor.darken(0.15f)

            val context = LocalContext.current
            val activity = context as? ComponentActivity


            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity?.window?.setStatusBarColor(darkenedWatchFaceColor.toArgb());
            activity?.window?.setNavigationBarColor(darkenedWatchFaceColor.toArgb());


            // Render the WatchDetailScreen directly without SystemUiController
            WatchDetailScreen(
                watch = watch,
                onBackClick = {
                    // Navigate back to the list screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.BrandLogos.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the BrandLogosScreen
            BrandLogosScreen(
                onBackClick = {
                    // Navigate back to the list screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.WorldMap.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the WorldMapScreen
            OptimizedWorldMapScreen(
                onBackClick = {
                    // Navigate back to the list screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CustomWorldMap.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the CustomWorldMapScreen
            CustomWorldMapScreen(
                onBackClick = {
                    // Navigate back to the list screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the SettingsScreen
            SettingsScreen(
                themeViewModel = themeViewModel,
                watchViewModel = watchViewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.About.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the AboutScreen
            AboutScreen()
        }

        composable(
            route = Screen.Wallpaper.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            // Get the current context to access the activity
            val context = LocalContext.current
            val activity = context as? ComponentActivity

            val originalColor = MaterialTheme.colorScheme.background.toArgb()

            activity?.window?.setStatusBarColor(originalColor);
            activity?.window?.setNavigationBarColor(originalColor);

            // Render the WallpaperScreen
            WallpaperScreen(
                onBackClick = {
                    // Navigate back to the previous screen
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Welcome.route,
            enterTransition = {
                fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                // Disable fade when navigating from Welcome -> Time to let shared element drive the motion
                if (targetState.destination.route == Screen.Time.route) null
                else fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            WelcomeScreen(
                watchViewModel = watchViewModel,
                onBackClick = {
                    navController.navigate(Screen.Time.route)
                },
                sharedTransitionScope = sharedScope,
                animatedVisibilityScope = this
            )
        }
    }
}
}
