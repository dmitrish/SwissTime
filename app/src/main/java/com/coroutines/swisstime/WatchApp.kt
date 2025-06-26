package com.coroutines.swisstime

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.navigation.compose.rememberNavController
import com.coroutines.swisstime.data.WatchPreferencesRepository
import com.coroutines.swisstime.navigation.NavGraph
import com.coroutines.swisstime.ui.screens.WatchDetailScreen
import com.coroutines.swisstime.ui.screens.WatchListScreen
import androidx.compose.foundation.lazy.rememberLazyListState
import com.coroutines.swisstime.watchfaces.AhoiNeomatic38DateAtlantic
import com.coroutines.swisstime.watchfaces.AutobahnNeomatic41DateSportsGray
import com.coroutines.swisstime.watchfaces.BlancpainFiftyFathoms
import com.coroutines.swisstime.watchfaces.BreguetClassique
import com.coroutines.swisstime.watchfaces.BreitlingNavitimer
import com.coroutines.swisstime.watchfaces.CarlFBuchererManero
import com.coroutines.swisstime.watchfaces.ChopardLUC
import com.coroutines.swisstime.watchfaces.FranckMullerVanguard
import com.coroutines.swisstime.watchfaces.GirardPerregauxLaureato
import com.coroutines.swisstime.watchfaces.HMoserEndeavour
import com.coroutines.swisstime.watchfaces.IWCPortugieser
import com.coroutines.swisstime.watchfaces.JaegerLeCoultreReverso
import com.coroutines.swisstime.watchfaces.LonginesMasterCollection
import com.coroutines.swisstime.watchfaces.ParmigianiFTonda
import com.coroutines.swisstime.watchfaces.PiagetAltiplano
import com.coroutines.swisstime.watchfaces.TAGHeuerCarrera
import com.coroutines.swisstime.watchfaces.UlysseNardinMarineChronometer
import com.coroutines.swisstime.watchfaces.VacheronConstantinClock
import com.coroutines.swisstime.watchfaces.VacheronConstantinPatrimony
import com.coroutines.swisstime.watchfaces.ZenithElPrimero
import com.coroutines.swisstime.widget.WatchWidget
import kotlinx.coroutines.launch

@Composable
fun WatchApp(watchPreferencesRepository: WatchPreferencesRepository) {
    // Get the current context
    val context = LocalContext.current

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Keep the splash screen selector code but make it inactive
    // As per requirements: "keep the code for the other implementation, but let it be completely inactive; only Splash Screen API"
    /*val splashScreenSelector = remember { SplashScreenSelector(context) }

    // Force the Splash Screen API to be active if it's not already
    if (!splashScreenSelector.isSplashScreenApiActive()) {
        splashScreenSelector.useSplashScreenApi()
    }*/

    // Collect the currently selected watch name
    val selectedWatchName by watchPreferencesRepository.selectedWatchName.collectAsState(initial = null)

    // List of watches with their details
    val watches = getWatches()

    // Create a NavController
    val navController = rememberNavController()

    // Function to select a watch for the widget
    val selectWatchForWidget = { watch: WatchInfo ->
        coroutineScope.launch {
            // Save the selected watch name
            watchPreferencesRepository.saveSelectedWatch(watch.name)

            // Update the widget
            WatchWidget().updateAll(context)

            // Show a confirmation toast
            Toast.makeText(
                context,
                "\"${watch.name}\" selected for widget",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Simple app title bar (splash screen toggle removed as per requirements)
           /* Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // App title
                Text(
                    text = "Swiss Time",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            } */
        }
    ) { innerPadding ->
        // Use the Navigation 3 library
        // Apply the innerPadding to the content
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                watches = watches,
                selectedWatchName = selectedWatchName,
                onSelectForWidget = { watch -> 
                    selectWatchForWidget(watch)
                    Unit // Return Unit to match the expected type
                }
            )
        }
    }
}

private fun getWatches(): List<WatchInfo> {
    val watches = listOf(
        WatchInfo(
            name = "Autobahn Neomatic 41 Date Sports Gray",
            description = "The Nomos Autobahn Neomatic 41 Date Sports Gray features a distinctive sports gray dial with a curved blue inner ring. This modern timepiece combines minimalist design with functional elements, including a date window at 6 o'clock and the signature red second hand.",
            composable = { modifier -> AutobahnNeomatic41DateSportsGray(modifier = modifier) }
        ),
        /* WatchInfo(
             name = "Patek Philippe Perpetual Calendar",
             description = "The Patek Philippe Perpetual Calendar London Edition features a light cream dial with gold accents and a sophisticated perpetual calendar complication. This timepiece represents the pinnacle of Swiss watchmaking with its elegant design and mechanical precision.",
             composable = { modifier -> AnalogClock(modifier = modifier) }
         ),
         WatchInfo(
             name = "Patek Philippe Grand Complications",
             description = "The Patek Philippe Grand Complications 5208r-001 showcases a black dial with rose gold elements. This exceptional timepiece combines multiple complications in a single watch, demonstrating Patek Philippe's mastery of haute horlogerie.",
             composable = { modifier -> PatekPhilippeClock(modifier = modifier) }
         ),
         WatchInfo(
             name = "Audemars Piguet Royal Oak",
             description = "The Audemars Piguet Royal Oak features a distinctive octagonal bezel with exposed screws and an integrated bracelet. Its deep blue dial with the signature 'Grande Tapisserie' pattern makes it one of the most recognizable luxury sports watches in the world.",
             composable = { modifier -> AudemarsPiguetClock(modifier = modifier) }
         ),
         WatchInfo(
             name = "Omega Seamaster 300m",
             description = "The Omega Seamaster 300m is characterized by its wave-patterned blue dial, ceramic bezel, and helium escape valve. As a professional diving watch, it combines robust functionality with elegant design, making it suitable for both underwater adventures and formal occasions.",
             composable = { modifier -> OmegaSeamasterClock(modifier = modifier) }
         ),
         WatchInfo(
             name = "Rolex Submariner",
             description = "The Rolex Submariner is an iconic luxury dive watch with a black dial, gold accents, and a distinctive blue rotating bezel. Known for its Mercedes-style hands and date window at 3 o'clock, it combines timeless elegance with professional diving capabilities.",
             composable = { modifier -> RolexSubmarinerClock(modifier = modifier) }
         ),*/
        // Additional Swiss watch brands
        WatchInfo(
            name = "Vacheron Constantin Patrimony",
            description = "The Vacheron Constantin Patrimony embodies the essence of pure style with its minimalist design and exceptional craftsmanship. Founded in 1755, Vacheron Constantin is one of the oldest watch manufacturers in the world, known for its elegant timepieces.",
            composable = { modifier -> VacheronConstantinClock(modifier = modifier) }
        ),
        WatchInfo(
            name = "Jaeger-LeCoultre Reverso",
            description = "The Jaeger-LeCoultre Reverso features a unique reversible case originally designed for polo players in the 1930s. This Art Deco masterpiece combines technical innovation with timeless elegance, showcasing the brand's commitment to precision and craftsmanship.",
            composable = { modifier -> JaegerLeCoultreReverso(modifier = modifier) }
        ),
        WatchInfo(
            name = "Blancpain Fifty Fathoms",
            description = "The Blancpain Fifty Fathoms, introduced in 1953, was one of the first modern diving watches. With its distinctive black dial and luminous markers, it set the standard for dive watches with features like water resistance, rotating bezel, and excellent legibility.",
            composable = { modifier -> BlancpainFiftyFathoms(modifier = modifier) }
        ),
        WatchInfo(
            name = "IWC Portugieser",
            description = "The IWC Portugieser, first created in the 1930s, features a clean dial design inspired by precision marine chronometers. Known for its large case size and elegant simplicity, it represents IWC's commitment to technical excellence and timeless design.",
            composable = { modifier -> IWCPortugieser(modifier = modifier) }
        ),
        WatchInfo(
            name = "Breitling Navitimer",
            description = "The Breitling Navitimer, introduced in 1952, features a distinctive slide rule bezel designed for pilots to perform flight calculations. With its busy dial and technical appearance, it has become an icon of aviation watches and a symbol of precision.",
            composable = { modifier -> BreitlingNavitimer(modifier = modifier) }
        ),
        WatchInfo(
            name = "TAG Heuer Carrera",
            description = "The TAG Heuer Carrera, designed in 1963, was inspired by the dangerous Carrera Panamericana race. With its clean dial and emphasis on legibility, it revolutionized chronograph design and continues to be a symbol of motorsport heritage.",
            composable = { modifier -> TAGHeuerCarrera(modifier = modifier) }
        ),
        WatchInfo(
            name = "Zenith El Primero",
            description = "The Zenith El Primero, introduced in 1969, was the first automatic chronograph movement with a high-beat frequency of 36,000 vibrations per hour. Known for its precision and reliability, it remains one of the most respected chronograph movements in watchmaking.",
            composable = { modifier -> ZenithElPrimero(modifier = modifier) }
        ),
        WatchInfo(
            name = "Longines Master Collection",
            description = "The Longines Master Collection showcases the brand's heritage of elegance and precision. With its classic design featuring roman numerals and a moonphase display, it represents Longines' commitment to traditional watchmaking values and timeless aesthetics.",
            composable = { modifier -> LonginesMasterCollection(modifier = modifier) }
        ),
        WatchInfo(
            name = "Chopard L.U.C",
            description = "The Chopard L.U.C collection, named after founder Louis-Ulysse Chopard, represents the pinnacle of the brand's watchmaking expertise. Featuring in-house movements and exquisite finishing, these timepieces combine technical innovation with elegant design.",
            composable = { modifier -> ChopardLUC(modifier = modifier) }
        ),
        WatchInfo(
            name = "Ulysse Nardin Marine Chronometer",
            description = "The Ulysse Nardin Marine Chronometer continues the brand's heritage of producing precise marine chronometers for navigation. With its distinctive power reserve indicator and date display, it combines traditional craftsmanship with modern innovation.",
            composable = { modifier -> UlysseNardinMarineChronometer(modifier = modifier) }
        ),
        WatchInfo(
            name = "Girard-Perregaux Laureato",
            description = "The Girard-Perregaux Laureato, first introduced in 1975, features an integrated bracelet and octagonal bezel. With its distinctive hobnail pattern dial, it represents the brand's ability to combine technical excellence with distinctive design elements.",
            composable = { modifier -> GirardPerregauxLaureato(modifier = modifier) }
        ),
        /*  WatchInfo(
              name = "Oris Aquis",
              description = "The Oris Aquis is a professional dive watch known for its robust construction and distinctive design. With its ceramic bezel insert and high water resistance, it offers exceptional performance at a more accessible price point than many Swiss luxury watches.",
              composable = { modifier -> RolexSubmarinerClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Tudor Black Bay",
              description = "The Tudor Black Bay draws inspiration from the brand's historic dive watches. With its domed crystal, prominent crown, and snowflake hands, it combines vintage aesthetics with modern reliability, representing Tudor's heritage as Rolex's sister brand.",
              composable = { modifier -> RolexSubmarinerClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Baume & Mercier Clifton",
              description = "The Baume & Mercier Clifton collection embodies urban elegance with its clean lines and balanced proportions. Drawing inspiration from the brand's historic models from the 1950s, it represents accessible luxury with a focus on timeless design.",
              composable = { modifier -> AnalogClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Rado Captain Cook",
              description = "The Rado Captain Cook is a modern reinterpretation of the brand's 1960s dive watch. Known for its distinctive rotating anchor logo and high-tech ceramic materials, it combines Rado's innovative approach to materials with vintage-inspired design.",
              composable = { modifier -> OmegaSeamasterClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Tissot Le Locle",
              description = "The Tissot Le Locle, named after the brand's hometown, features a classic design with roman numerals and a guilloche pattern dial. It represents Tissot's heritage of traditional Swiss watchmaking at an accessible price point.",
              composable = { modifier -> AnalogClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Raymond Weil Freelancer",
              description = "The Raymond Weil Freelancer collection offers a contemporary interpretation of classic watchmaking. With its open-heart design revealing the balance wheel, it demonstrates the brand's commitment to mechanical watchmaking and musical inspiration.",
              composable = { modifier -> PatekPhilippeClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Frederique Constant Slimline",
              description = "The Frederique Constant Slimline collection embodies elegant simplicity with its thin case and minimalist dial. Known for offering in-house movements at accessible prices, it represents the brand's mission to democratize Swiss luxury watchmaking.",
              composable = { modifier -> AnalogClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Alpina Startimer Pilot",
              description = "The Alpina Startimer Pilot collection draws inspiration from the brand's history of producing aviation watches. With its oversized crown and highly legible dial, it continues Alpina's tradition of creating robust timepieces for extreme conditions.",
              composable = { modifier -> AudemarsPiguetClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Mondaine Swiss Railways",
              description = "The Mondaine Swiss Railways watch is based on the iconic clock design found in Swiss train stations. With its minimalist red seconds hand and clean dial, it has become a symbol of Swiss design excellence and functional simplicity.",
              composable = { modifier -> AnalogClock(modifier = modifier) }
          ),
          WatchInfo(
              name = "Swatch Sistem51",
              description = "The Swatch Sistem51 revolutionized Swiss watchmaking with its fully automated assembly process and 51-component automatic movement. Combining innovative production techniques with playful design, it represents Swatch's role in revitalizing the Swiss watch industry.",
              composable = { modifier -> AudemarsPiguetClock(modifier = modifier) }
          ),*/
        // New Swiss watches
        WatchInfo(
            name = "Franck Muller Vanguard",
            description = "The Franck Muller Vanguard features a distinctive tonneau (barrel) shape case and bold, colorful numerals. Known as the 'Master of Complications', Franck Muller combines avant-garde design with traditional Swiss watchmaking expertise to create timepieces that are both technically impressive and visually striking.",
            composable = { modifier -> FranckMullerVanguard(modifier = modifier) }
        ),
        WatchInfo(
            name = "H. Moser & Cie Endeavour",
            description = "The H. Moser & Cie Endeavour is renowned for its minimalist design and signature fumé dial that gradually darkens from center to edge. Founded in 1828, this independent Swiss manufacturer creates timepieces that combine traditional craftsmanship with contemporary aesthetics and innovative complications.",
            composable = { modifier -> HMoserEndeavour(modifier = modifier) }
        ),
        WatchInfo(
            name = "Breguet Classique",
            description = "The Breguet Classique embodies the timeless elegance of Abraham-Louis Breguet's original designs. With its coin-edge case, guilloche dial, and distinctive Breguet hands with hollow moon tips, it represents the pinnacle of traditional Swiss watchmaking and horological heritage.",
            composable = { modifier -> BreguetClassique(modifier = modifier) }
        ),
        WatchInfo(
            name = "Vacheron Constantin Patrimony",
            description = "The Vacheron Constantin Patrimony exemplifies pure, minimalist elegance with its slim profile and clean dial. As one of the oldest continuously operating watch manufacturers, Vacheron Constantin combines centuries of tradition with contemporary refinement in this timeless dress watch.",
            composable = { modifier -> VacheronConstantinPatrimony(modifier = modifier) }
        ),
        WatchInfo(
            name = "Parmigiani Fleurier Tonda",
            description = "The Parmigiani Fleurier Tonda combines distinctive design elements with exceptional craftsmanship. Founded in 1996, this independent Swiss manufacturer draws on traditional techniques while incorporating modern innovations, resulting in watches with unique teardrop lugs and meticulously finished movements.",
            composable = { modifier -> ParmigianiFTonda(modifier = modifier) }
        ),
        WatchInfo(
            name = "Carl F. Bucherer Manero",
            description = "The Carl F. Bucherer Manero combines classic design with sophisticated complications like power reserve indicators and chronographs. Founded in Lucerne in 1888, Carl F. Bucherer represents Swiss watchmaking tradition with its elegant aesthetics and technical excellence.",
            composable = { modifier -> CarlFBuchererManero(modifier = modifier) }
        ),
        WatchInfo(
            name = "Piaget Altiplano",
            description = "The Piaget Altiplano is celebrated for its ultra-thin profile and minimalist design. Since the 1950s, Piaget has been a pioneer in creating incredibly slim watches, with the Altiplano line showcasing the brand's expertise in producing elegant timepieces that combine technical innovation with refined aesthetics.",
            composable = { modifier -> PiagetAltiplano(modifier = modifier) }
        ),
        WatchInfo(
            name = "TAG Heuer Carrera",
            description = "The TAG Heuer Carrera, first introduced in 1963, is a legendary chronograph designed for racing drivers. With its clean dial layout, distinctive subdials, and robust construction, it embodies TAG Heuer's connection to motorsport and their commitment to precision timing in high-speed environments.",
            composable = { modifier -> TAGHeuerCarrera(modifier = modifier) }
        ),
        WatchInfo(
            name = "Zenith El Primero",
            description = "The Zenith El Primero, introduced in 1969, was one of the world's first automatic chronograph movements. Known for its high-frequency 36,000 vibrations per hour and distinctive tri-color subdials, it represents Zenith's technical innovation and has become an icon of Swiss watchmaking excellence.",
            composable = { modifier -> ZenithElPrimero(modifier = modifier) }
        ),
        WatchInfo(
            name = "Nomos Ahoi Neomatic 38 date Atlantic",
            description = "The Nomos Ahoi Neomatic 38 date Atlantic features a deep blue dial inspired by the Atlantic Ocean. This German-made timepiece combines Bauhaus minimalism with dive watch functionality, featuring a waterproof design, luminous markers, and the distinctive red seconds hand that is a signature of Nomos Glashütte watches.",
            composable = { modifier -> AhoiNeomatic38DateAtlantic(modifier = modifier) }
        )
    )
    return watches
}
