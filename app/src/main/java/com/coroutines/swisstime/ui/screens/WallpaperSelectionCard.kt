package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coroutines.swisstime.navigation.Screen
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.darken

@Composable
fun WallpaperSelectionCard(modifier: Modifier, onNavigationRequested: (String) -> Unit) {
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
                text = "Wallpaper",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Button(onClick = {
                        onNavigationRequested(Screen.Wallpaper.route)
                    /*navController.navigate(Screen.Wallpaper.route)*/
                    }) {
                        Text("Choose Wallpaper")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WallpaperSelectionCardPreview() {
    WallpaperSelectionCard(Modifier, {})
}