package com.coroutines.swisstime.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.AventinusClassique
import com.coroutines.swisstime.watchfaces.CenturioLuminor
import com.coroutines.swisstime.watchfaces.ChantDuTemps
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.swisstime.watchfaces.Concordia
import com.coroutines.swisstime.watchfaces.ConstantinusAureusChronometer
import com.coroutines.swisstime.watchfaces.EdgeOfSecond
import com.coroutines.swisstime.watchfaces.HorologiaRomanum
import com.coroutines.swisstime.watchfaces.JurgsenZenithor
import com.coroutines.swisstime.watchfaces.KandinskyEvening
import com.coroutines.swisstime.watchfaces.LeonardAutomatic
import com.coroutines.swisstime.watchfaces.LucernaRoma
import com.coroutines.swisstime.watchfaces.PontifexChronometra
import com.coroutines.swisstime.watchfaces.RolexSubmarinerClock
import com.coroutines.swisstime.watchfaces.RomaMarina
import com.coroutines.swisstime.watchfaces.Tokinoha
import com.coroutines.swisstime.watchfaces.Valentinianus
import com.coroutines.swisstime.watchfaces.YamaNoToki
import com.coroutines.swisstime.watchfaces.Zeitwerk
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import com.coroutines.worldclock.common.model.WatchInfo
import java.util.TimeZone

// Map of watch name prefixes to their corresponding composable functions
// This avoids the expensive if-else chain and provides faster lookups
private val watchFaceMap =
  mapOf<String, @Composable (Modifier, TimeZone) -> Unit>(
    "Zenith El Primero" to { mod, tz -> ZenithElPrimero(modifier = mod, timeZone = tz) },
    "Chronomagus Regum" to { mod, tz -> ChronomagusRegum(modifier = mod, timeZone = tz) },
    "Jurgsen Zenithor" to { mod, tz -> JurgsenZenithor(modifier = mod, timeZone = tz) },
    "Roma Marina" to { mod, tz -> RomaMarina(modifier = mod, timeZone = tz) },
    "Leonard Automatic" to { mod, tz -> LeonardAutomatic(modifier = mod, timeZone = tz) },
    "Aventinus" to { mod, tz -> AventinusClassique(modifier = mod, timeZone = tz) },
    "Carl" to { mod, tz -> EdgeOfSecond(modifier = mod, timeZone = tz) },
    "YamaNoToki" to { mod, tz -> YamaNoToki(modifier = mod, timeZone = tz) },
    "Lucerna" to { mod, tz -> LucernaRoma(modifier = mod, timeZone = tz) },
    "Girard" to { mod, tz -> RomaMarina(modifier = mod, timeZone = tz) },
    "Centurio Luminor" to { mod, tz -> CenturioLuminor(modifier = mod, timeZone = tz) },
    "Horologia Romanum" to { mod, tz -> HorologiaRomanum(modifier = mod, timeZone = tz) },
    "Concordia" to { mod, tz -> Concordia(modifier = mod, timeZone = tz) },
    "Chant Du Temps" to { mod, tz -> ChantDuTemps(modifier = mod, timeZone = tz) },
    "Pontifex Chronometra" to { mod, tz -> PontifexChronometra(modifier = mod, timeZone = tz) },
    "Alpenglühen Zeitwerk" to { mod, tz -> Zeitwerk(modifier = mod, timeZone = tz) },
    "Rolex Submariner" to { mod, tz -> RolexSubmarinerClock(modifier = mod, timeZone = tz) },
    "Tokinoha" to { mod, tz -> Tokinoha(modifier = mod, timeZone = tz) },
    "Constantinus Aureus" to
      { mod, tz ->
        ConstantinusAureusChronometer(modifier = mod, timeZone = tz)
      },
    "Valentinianus" to { mod, tz -> Valentinianus(modifier = mod, timeZone = tz) },
    "Kandinsky Evening" to { mod, tz -> KandinskyEvening(modifier = mod, timeZone = tz) }
  )

@Composable
fun TimeZoneAwareWatchFace2(
  watchInfo: WatchInfo,
  viewModel: WatchViewModel,
  modifier: Modifier = Modifier
) {
  // Use the watch name as a stable key
  val watchName = watchInfo.name

  // Get the time zone for this watch directly without using flows for initial value
  // This is blocking on startup to ensure the watch starts with the correct timezone
  val initialTimeZone = remember(watchName) { viewModel.getTimeZoneDirect(watchName) }

  // Use Flow for immediate updates when timezone is changed in the dropdown
  val timeZoneFlow = viewModel.getWatchTimeZone(watchName)
  val timeZone by timeZoneFlow.collectAsState(initial = initialTimeZone)

  // Find the appropriate watch face composable based on the watch name
  // This is much faster than a large if-else chain
  val watchFaceComposable =
    remember(watchName) {
      // First try an exact match
      watchFaceMap[watchName]
        ?: run {
          // If no exact match, try to find a prefix match
          watchFaceMap.entries.find { (prefix, _) -> watchName.startsWith(prefix) }?.value
        }
    }

  // Render the watch face
  if (watchFaceComposable != null) {
    // Use the found composable
    watchFaceComposable(modifier, timeZone)
  } else {
    // Fallback to the default composable
    watchInfo.composable(modifier, timeZone)
  }
}
