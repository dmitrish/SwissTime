package com.coroutines.swisstime.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.components.AppVersionSection
import com.coroutines.swisstime.ui.components.RateAppSection

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
                containerColor = MaterialTheme.colorScheme.surface
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
                    text = "World Timezone Clock is an elegant app that displays the time across different timezones with beautifully crafted mechanical watch faces. Each watch face is designed with attention to detail, mimicking the craftsmanship of real luxury timepieces.",
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
