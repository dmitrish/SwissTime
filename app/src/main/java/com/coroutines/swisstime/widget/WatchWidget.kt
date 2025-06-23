package com.coroutines.swisstime.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.coroutines.swisstime.data.WatchPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Implementation of App Widget that shows the selected watch
 */
class WatchWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Get the selected watch name from preferences
        val repository = WatchPreferencesRepository(context)
        val selectedWatchName = runBlocking { repository.selectedWatchName.first() }

        provideContent {
            // Widget UI
            WatchWidgetContent(selectedWatchName)
        }
    }

    companion object {
        // Function to get watch face color based on the watch name
        fun getWatchFaceColor(watchName: String?): Color {
            return when {
                watchName == null -> Color.DarkGray
                watchName.contains("Zenith El Primero") -> Color.LightGray // Silver-white dial
                watchName.contains("Omega Seamaster") -> Color(0xFF0A4D8C) // Deep blue dial
                watchName.contains("Rolex Submariner") -> Color.Black // Black dial
                watchName.contains("Patek Philippe") -> Color(0xFFF5F5DC) // Cream dial
                watchName.contains("Audemars Piguet") -> Color(0xFF00008B) // Dark blue dial
                watchName.contains("Vacheron Constantin") -> Color.White // White dial
                watchName.contains("Jaeger-LeCoultre") -> Color.LightGray // Silver dial
                watchName.contains("Blancpain") -> Color.Black // Black dial
                watchName.contains("IWC") -> Color.White // White dial
                watchName.contains("Breitling") -> Color(0xFF000080) // Navy blue dial
                watchName.contains("TAG Heuer") -> Color.Black // Black dial
                watchName.contains("Longines") -> Color.LightGray // Silver dial
                watchName.contains("Chopard") -> Color.White // White dial
                watchName.contains("Ulysse Nardin") -> Color(0xFF00008B) // Dark blue dial
                watchName.contains("Girard-Perregaux") -> Color.LightGray // Silver dial
                watchName.contains("Franck Muller") -> Color.Black // Black dial
                watchName.contains("H. Moser") -> Color(0xFF1E5631) // Deep green dial
                watchName.contains("Parmigiani") -> Color(0xFF1A237E) // Deep blue dial
                watchName.contains("Carl F. Bucherer") -> Color(0xFFF5F5F5) // Silver-white dial
                watchName.contains("Piaget") -> Color(0xFF000080) // Deep blue dial
                watchName.contains("Oris") -> Color.Black // Black dial
                watchName.contains("Tudor") -> Color.Black // Black dial
                watchName.contains("Baume & Mercier") -> Color.White // White dial
                watchName.contains("Rado") -> Color.Black // Black dial
                watchName.contains("Tissot") -> Color.White // White dial
                else -> Color.DarkGray // Default color
            }
        }

        // Function to get appropriate text color for a background
        fun getTextColorForBackground(backgroundColor: Color): Color {
            // Simple check if the color is dark or light
            val darkness = 1 - (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
            return if (darkness > 0.5) Color.White else Color.Black
        }
    }
}

@Composable
private fun WatchWidgetContent(selectedWatchName: String?) {
    // Get current time
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Format time for digital display
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val currentTime = timeFormat.format(calendar.time)

    // Get watch face color
    val watchFaceColor = WatchWidget.getWatchFaceColor(selectedWatchName)
    val textColor = WatchWidget.getTextColorForBackground(watchFaceColor)

    Box(
        modifier = GlanceModifier.fillMaxSize().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedWatchName != null) {
            // Display the watch face
            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Watch name
                Text(
                    text = selectedWatchName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.padding(bottom = 8.dp)
                )

                // Watch face
                Box(
                    modifier = GlanceModifier
                        .size(160.dp)
                        .background(ColorProvider(watchFaceColor, watchFaceColor))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Digital time display
                    Text(
                        text = currentTime,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = ColorProvider(textColor, textColor)
                        )
                    )
                }
            }
        } else {
            // No watch selected
            Text(
                text = "No watch selected. Please select a watch from the app.",
                style = TextStyle(fontSize = 14.sp),
                modifier = GlanceModifier.padding(8.dp)
            )
        }
    }
}

/**
 * Receiver class for the widget
 */
class WatchWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WatchWidget()
}
