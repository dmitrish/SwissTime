package com.coroutines.swisstime.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.BlancpainFiftyFathoms
import com.coroutines.swisstime.watchfaces.BreguetClassique
import com.coroutines.swisstime.watchfaces.BreitlingNavitimer
import com.coroutines.swisstime.watchfaces.CarlFBuchererManero
import com.coroutines.swisstime.watchfaces.ChopardLUC
import com.coroutines.swisstime.watchfaces.LucernaRoma
import com.coroutines.swisstime.watchfaces.GirardPerregauxLaureato
import com.coroutines.swisstime.watchfaces.HMoserEndeavour
import com.coroutines.swisstime.watchfaces.IWCPortugieser
import com.coroutines.swisstime.watchfaces.JaegerLeCoultreReverso
import com.coroutines.swisstime.watchfaces.LonginesMasterCollection
import com.coroutines.swisstime.watchfaces.ParmigianiFTonda
import com.coroutines.swisstime.watchfaces.PatekPhilippeClock
import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.swisstime.watchfaces.ConstantinusAureusChronometer
import com.coroutines.swisstime.watchfaces.RolexSubmarinerClock
import com.coroutines.swisstime.watchfaces.Tokinoha
import com.coroutines.swisstime.watchfaces.Valentinianus
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import com.coroutines.swisstime.watchfaces.KandinskyEvening
import java.util.TimeZone

// Map of watch name prefixes to their corresponding composable functions
// This avoids the expensive if-else chain and provides faster lookups
private val watchFaceMap = mapOf<String, @Composable (Modifier, TimeZone) -> Unit>(
    "Zenith El Primero" to { mod, tz -> ZenithElPrimero(modifier = mod, timeZone = tz) },
    "Chronomagus Regum" to { mod, tz -> ChronomagusRegum(modifier = mod, timeZone = tz) },
    "Blanc" to { mod, tz -> BlancpainFiftyFathoms(modifier = mod, timeZone = tz) },
    "Breitling" to { mod, tz -> BreitlingNavitimer(modifier = mod, timeZone = tz) },
    "Breguet" to { mod, tz -> BreguetClassique(modifier = mod, timeZone = tz) },
    "Carl" to { mod, tz -> CarlFBuchererManero(modifier = mod, timeZone = tz) },
    "Chopard" to { mod, tz -> ChopardLUC(modifier = mod, timeZone = tz) },
    "Lucerna" to { mod, tz -> LucernaRoma(modifier = mod, timeZone = tz) },
    "Girard" to { mod, tz -> GirardPerregauxLaureato(modifier = mod, timeZone = tz) },
    "H. Moser & Cie Endeavour" to { mod, tz -> HMoserEndeavour(modifier = mod, timeZone = tz) },
    "IWC" to { mod, tz -> IWCPortugieser(modifier = mod, timeZone = tz) },
    "Jaeger" to { mod, tz -> JaegerLeCoultreReverso(modifier = mod, timeZone = tz) },
    "Longines" to { mod, tz -> LonginesMasterCollection(modifier = mod, timeZone = tz) },
    "Parmigiani" to { mod, tz -> ParmigianiFTonda(modifier = mod, timeZone = tz) },
    "Patek" to { mod, tz -> PatekPhilippeClock(modifier = mod, timeZone = tz) },
    "Rolex Submariner" to { mod, tz -> RolexSubmarinerClock(modifier = mod, timeZone = tz) },
    "TAG" to { mod, tz -> Tokinoha(modifier = mod, timeZone = tz) },
    "Constantinus Aureus" to { mod, tz -> ConstantinusAureusChronometer(modifier = mod, timeZone = tz) },
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
    val initialTimeZone = remember(watchName) {
        viewModel.getTimeZoneDirect(watchName)
    }

    // Use Flow for immediate updates when timezone is changed in the dropdown
    val timeZoneFlow = viewModel.getWatchTimeZone(watchName)
    val timeZone by timeZoneFlow.collectAsState(initial = initialTimeZone)

    // Find the appropriate watch face composable based on the watch name
    // This is much faster than a large if-else chain
    val watchFaceComposable = remember(watchName) {
        // First try an exact match
        watchFaceMap[watchName] ?: run {
            // If no exact match, try to find a prefix match
            watchFaceMap.entries.find { (prefix, _) -> 
                watchName.startsWith(prefix)
            }?.value
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
