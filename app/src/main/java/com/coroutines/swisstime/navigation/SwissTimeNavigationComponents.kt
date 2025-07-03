package com.coroutines.swisstime.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.coroutines.swisstime.ui.theme.DarkNavy

/**
 * Top level destinations to be used in the BottomBar
 */
enum class TopLevelDestination(
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
    val iconText: @Composable () -> Unit,
    val route: String
) {
    TIME(
        selectedIcon = { Icon(Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.White) },
        unselectedIcon = { Icon(Icons.Outlined.AccessTime, contentDescription = "Time", tint = Color.White.copy(alpha = 0.7f)) },
        iconText = { Text(text = "Time", color = Color.White) },
        route = Screen.Time.route
    ),
    WATCH_LIST(
        selectedIcon = { Icon(Icons.Filled.Watch, contentDescription = "Watches", tint = Color.White) },
        unselectedIcon = { Icon(Icons.Outlined.Watch, contentDescription = "Watches", tint = Color.White.copy(alpha = 0.7f)) },
        iconText = { Text(text = "Watches", color = Color.White) },
        route = Screen.WatchList.route
    ),
    SETTINGS(
        selectedIcon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White) },
        unselectedIcon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.7f)) },
        iconText = { Text(text = "Settings", color = Color.White) },
        route = Screen.Settings.route
    )
}

/**
 * SwissTime navigation bar with icon buttons
 */
@Composable
fun SwissTimeNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentDestination: NavDestination?
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        TopLevelDestination.values().forEach { destination ->
            val selected = currentDestination?.hierarchy?.any { 
                it.route == destination.route 
            } == true

            SwissTimeNavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(destination.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (selected) {
                        destination.selectedIcon()
                    } else {
                        destination.unselectedIcon()
                    }
                },
                label = destination.iconText
            )
        }
    }
}

/**
 * Navigation bar item with icon and label content slots
 */
@Composable
fun RowScope.SwissTimeNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    // Create a darker shade of DarkNavy for the selected state
    val darkerNavy = Color(0xFF20373A) // ~30% darker than DarkNavy (0xFF2F4F4F)

    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        modifier = modifier,
        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            indicatorColor = darkerNavy, // Use darker navy as the selection indicator color
            unselectedIconColor = Color.White.copy(alpha = 0.7f),
            unselectedTextColor = Color.White.copy(alpha = 0.7f)
        )
    )
}
