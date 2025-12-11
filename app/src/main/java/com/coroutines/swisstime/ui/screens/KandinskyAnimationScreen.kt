package com.coroutines.swisstime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.coroutines.swisstime.R
import com.coroutines.swisstime.ui.theme.SwissTimeTheme

/**
 * A screen that displays the Kandinsky Circles animation. The animation is implemented as an
 * animated vector drawable.
 */
@Composable
fun KandinskyAnimationScreen(modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Load and display the animated vector drawable
    // Note: AnimatedVectorDrawables are automatically animated when displayed in Compose
    Image(
      painter = painterResource(id = R.drawable.avd_kandinsky_circles),
      contentDescription = "Kandinsky Circles Animation",
      modifier = Modifier.fillMaxSize(0.8f)
    )
  }
}

@Preview(showBackground = true)
@Composable
fun KandinskyAnimationScreenPreview() {
  SwissTimeTheme { KandinskyAnimationScreen() }
}
