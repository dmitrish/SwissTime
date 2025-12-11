package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.AppReviewManager
import com.coroutines.swisstime.utils.darken

@Composable
fun RateAppSection(modifier: Modifier = Modifier) {
  val context = LocalContext.current

  Card(
    modifier = modifier.fillMaxWidth().testTag("rate_app_section_card"),
    colors =
      CardDefaults.cardColors(
        containerColor = DarkNavy.darken(0.3f) // MaterialTheme.colorScheme.surface
      )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Rate the App",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Enjoying World Timezone Clock App? Let us know what you think!",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.testTag("rate_app_description")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Rate the app button
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val buttonsMaxWidthState =
          com.coroutines.swisstime.ui.components.LocalSettingsButtonsMaxWidth.current
        val density = LocalDensity.current
        val widthModifier =
          if (buttonsMaxWidthState.value > 0.dp) Modifier.width(buttonsMaxWidthState.value)
          else Modifier
        /*  SwissTimeGradientButton(
            text = "Rate the App",
            onClick = {
                val reviewManager = AppReviewManager(context)
                reviewManager.requestReview()
            },
            modifier = widthModifier.then(
                Modifier.onGloballyPositioned { coords ->
                    val measured = with(density) { coords.size.width.toDp() }
                    if (measured > buttonsMaxWidthState.value) {
                        buttonsMaxWidthState.value = measured
                    }
                }
            ).testTag("rate_app_button_text")
        ) */
        Button(
          onClick = {
            // Use the AppReviewManager to request a review
            // This will use the Google Play In-App Review API in production
            val reviewManager = AppReviewManager(context)
            reviewManager.requestReview()
          },
          modifier = Modifier.fillMaxWidth(0.6f).testTag("rate_app_button")
        ) {
          Text(text = "Rate the App")
        }
      }
    }
  }
}
