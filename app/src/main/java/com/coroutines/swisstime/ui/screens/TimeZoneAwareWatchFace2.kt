package com.coroutines.swisstime.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.coroutines.swisstime.WatchInfo
import com.coroutines.swisstime.viewmodel.WatchViewModel
import com.coroutines.swisstime.watchfaces.BlancpainFiftyFathoms
import com.coroutines.swisstime.watchfaces.BreguetClassique
import com.coroutines.swisstime.watchfaces.BreitlingNavitimer
import com.coroutines.swisstime.watchfaces.CarlFBuchererManero
import com.coroutines.swisstime.watchfaces.ChopardLUC
import com.coroutines.swisstime.watchfaces.FranckMullerVanguard
import com.coroutines.swisstime.watchfaces.HMoserEndeavour
import com.coroutines.swisstime.watchfaces.IWCPortugieser
import com.coroutines.swisstime.watchfaces.JaegerLeCoultreReverso
import com.coroutines.swisstime.watchfaces.LonginesMasterCollection
import com.coroutines.swisstime.watchfaces.ParmigianiFTonda
import com.coroutines.swisstime.watchfaces.PatekPhilippeClock
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import com.coroutines.swisstime.watchfaces.RolexSubmarinerClock
import com.coroutines.swisstime.watchfaces.TAGHeuerCarrera
import com.coroutines.swisstime.watchfaces.UlysseNardinMarineChronometer
import com.coroutines.swisstime.watchfaces.VacheronConstantinClock
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

@Composable
fun TimeZoneAwareWatchFace2(
    watchInfo: WatchInfo,
    timeZone: TimeZone,
    viewModel: WatchViewModel,
    modifier: Modifier = Modifier
) {
    // Use the watch name as a stable key
    val watchName = watchInfo.name


    // Special handling for watch faces that accept a timezone parameter directly
    if (watchName == "Zenith El Primero") {
        // For ZenithElPrimero, pass the timezone directly
        ZenithElPrimero(
            modifier = modifier,
            timeZone = timeZone
        )
    } else if (watchName == "Piaget Altiplano") {
        // For PiagetAltiplano, pass the timezone directly
        PiagetAltiplano(
            modifier = modifier,
            timeZone = timeZone
        )
    } else if (watchName.startsWith("Blanc")) {
        // For PiagetAltiplano, pass the timezone directly
        BlancpainFiftyFathoms(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Breitling")) {
        // For PiagetAltiplano, pass the timezone directly
        BreitlingNavitimer(
            modifier = modifier,
            timeZone = timeZone
        )
    }
 else if (watchName.startsWith("Breguet")) {
    // For PiagetAltiplano, pass the timezone directly
        BreguetClassique(
            modifier = modifier,
            timeZone = timeZone
        )
}
    else if (watchName.startsWith("Carl")) {
        // For PiagetAltiplano, pass the timezone directly
        CarlFBuchererManero(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Chopard")) {
        // For PiagetAltiplano, pass the timezone directly
        ChopardLUC(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Franck")) {
        // For PiagetAltiplano, pass the timezone directly
        FranckMullerVanguard(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Moser")) {
        // For PiagetAltiplano, pass the timezone directly
        HMoserEndeavour(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("IWC")) {
        // For PiagetAltiplano, pass the timezone directly
        IWCPortugieser(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Jaeger")) {
        // For PiagetAltiplano, pass the timezone directly
        JaegerLeCoultreReverso(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Longines")) {
        // For PiagetAltiplano, pass the timezone directly
        LonginesMasterCollection(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Parmigiani")) {
        // For PiagetAltiplano, pass the timezone directly
        ParmigianiFTonda(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Patek")) {
        // For PiagetAltiplano, pass the timezone directly
        PatekPhilippeClock(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Rolex Submariner")) {
        // For PiagetAltiplano, pass the timezone directly
        RolexSubmarinerClock(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("TAG")) {
        // For PiagetAltiplano, pass the timezone directly
        TAGHeuerCarrera(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Ulysee Nardin")) {
        // For PiagetAltiplano, pass the timezone directly
        UlysseNardinMarineChronometer(
            modifier = modifier,
            timeZone = timeZone
        )
    }
    else if (watchName.startsWith("Vacherone Constantin")) {
        // For PiagetAltiplano, pass the timezone directly
        VacheronConstantinClock(
            modifier = modifier,
            timeZone = timeZone
        )
    }
 else {
        // For other watches, just call the composable directly
        // The default timezone has already been set, so they will use the correct timezone
          watchInfo.composable(modifier, timeZone)
    }
}
