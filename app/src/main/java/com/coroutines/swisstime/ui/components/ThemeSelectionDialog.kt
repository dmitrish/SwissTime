package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.ui.theme.ThemeMode
import com.coroutines.swisstime.utils.darken

/**
 * A dialog that allows the user to select a theme mode.
 *
 * @param themeMode The current theme mode (DAY, NIGHT, or SYSTEM)
 * @param onThemeModeChange Callback for when a theme mode is selected
 * @param onDismiss Callback for when the dialog is dismissed
 */
@Composable
fun ThemeSelectionDialog(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkNavy.darken(0.3f)// MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Theme",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Day Theme option
                ThemeOption(
                    title = "Day Theme",
                    description = "Default theme with navy background",
                    selected = themeMode == ThemeMode.DAY,
                    onClick = {
                        onThemeModeChange(ThemeMode.DAY)
                        onDismiss()
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Night Theme option
                ThemeOption(
                    title = "Night Theme",
                    description = "Dark theme with black background",
                    selected = themeMode == ThemeMode.NIGHT,
                    onClick = {
                        onThemeModeChange(ThemeMode.NIGHT)
                        onDismiss()
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // System Default option
                ThemeOption(
                    title = "System Default",
                    description = "Follow system dark mode settings",
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = {
                        onThemeModeChange(ThemeMode.SYSTEM)
                        onDismiss()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
