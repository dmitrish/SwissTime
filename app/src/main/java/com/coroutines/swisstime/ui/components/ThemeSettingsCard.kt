package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.darken
import com.coroutines.worldclock.common.theme.ThemeMode


/**
* A card component that displays theme settings including theme selection and dark mode toggle.
*
* @param themeMode The current theme mode (DAY, NIGHT, or SYSTEM)
* @param darkMode Whether dark mode is enabled
* @param onThemeClick Callback for when the theme selection row is clicked
* @param onDarkModeChange Callback for when the dark mode toggle is changed
* @param modifier Optional modifier for the card
*/
@Composable
fun ThemeSettingsCard(
    themeMode: ThemeMode,
    darkMode: Boolean,
    onThemeClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkNavy.darken(0.3f)// MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Theme selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeClick() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when(themeMode) {
                            ThemeMode.DAY -> "Day Theme"
                            ThemeMode.NIGHT -> "Night Theme"
                            ThemeMode.SYSTEM -> "System Default"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Theme preview
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when(themeMode) {
                                ThemeMode.DAY -> Color(0xFF2F4F4F) // LightNavy
                                ThemeMode.NIGHT -> Color.Black
                                ThemeMode.SYSTEM -> if (darkMode) Color(0xFF2F4F4F) else Color(0xFF2F4F4F)
                            }
                        )
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Dark mode toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (darkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = "Dark Mode",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Switch(
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange
                )
            }
        }
    }
}
