package com.coroutines.swisstime.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.coroutines.worldclock.common.theme.ThemeMode


private val DarkColorScheme = darkColorScheme(
    primary = DarkGold,
    secondary = DarkBronze,
    tertiary = DarkSilver,
    background = DarkNavy,
    surface = DarkAccent,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = DarkSilver,
    onSurface = DarkSilver
)

private val LightColorScheme = lightColorScheme(
    primary = LightGold,
    secondary = LightBronze,
    tertiary = LightSilver,
    background = LightNavy,
    surface = LightAccent,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = LightSilver,
    onSurface = LightSilver
)

// Night theme color scheme with black background
private val NightColorScheme = lightColorScheme(
    primary = NightGold,
    secondary = NightBronze,
    tertiary = NightSilver,
    background = NightNavy, // Black background
    surface = NightAccent,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = NightSilver,
    onSurface = NightSilver
)

@Composable
fun SwissTimeTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is disabled by default to maintain the luxury watch aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DAY -> false
        ThemeMode.NIGHT -> false // Night theme is not the same as dark theme
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == ThemeMode.NIGHT -> NightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
