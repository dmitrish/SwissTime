package com.coroutines.swisstime.ui.screens

import AboutAppText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.components.AppVersionSection
import com.coroutines.swisstime.ui.components.RateAppSection
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.darken

/**
 * AboutScreen displays information about the app, including a description,
 * a section for rating the app, and a section showing the app version.
 *
 * This component has been refactored to accept RateAppSection and AppVersionSection
 * as parameters to make it more flexible and testable. The refactoring uses function types
 * as parameters with default values that use the existing RateAppSection and AppVersionSection
 * components. This approach allows the AboutScreen to be more flexible and testable while
 * maintaining backward compatibility.
 *
 * The refactoring also required updating the tests to work with the new implementation:
 * 
 * 1. AboutScreenTest: We created custom implementations of RateAppSection and AppVersionSection
 *    for testing, with test tags for all elements. We updated the test to use these custom
 *    implementations and to find elements using test tags instead of text content.
 *    
 * 2. AboutScreenThemeTest: This test was already using test tags to find UI elements, so it
 *    continued to work with the refactored implementation without changes.
 *    
 * 3. AboutScreenScreenshotTests: These tests use screenshot comparison, so they continued to
 *    work with the refactored implementation without changes.
 *
 * This refactoring approach allows for greater flexibility in testing and reuse of the
 * AboutScreen component, while maintaining compatibility with existing tests and ensuring
 * the visual appearance is preserved.
 */
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    rateAppSection: @Composable () -> Unit = { RateAppSection() },
    appVersionSection: @Composable () -> Unit = { AppVersionSection() }
) {
    val scrollState = rememberScrollState()


    // Provide a shared max width so the action buttons in RateAppSection and AppVersionSection
    // can match the width of the longer one.
    val buttonsMaxWidth = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0.dp) }
    androidx.compose.runtime.CompositionLocalProvider(
        com.coroutines.swisstime.ui.components.LocalSettingsButtonsMaxWidth provides buttonsMaxWidth
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Text(
            text = "About",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Divider()

        // About section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("about_section_card"),
            colors = CardDefaults.cardColors(
                containerColor = DarkNavy.darken(0.3f)// MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
               /* Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp)) */

                Text(
                    text = "World Timezone Clock with Fun Mechanical Watchfaces",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = AboutAppText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Rate the App section
        rateAppSection()

        // App Version section
        appVersionSection()
        }
    }
}
