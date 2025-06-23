package com.coroutines.swisstime

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.coroutines.swisstime.data.WatchPreferencesRepository
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.widget.WatchWidget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import kotlinx.coroutines.launch
import kotlin.math.max

// Data class to hold watch information
data class WatchInfo(
    val name: String,
    val description: String,
    val composable: @Composable (Modifier) -> Unit
)

// Function to get the watch face color based on the watch name
fun getWatchFaceColor(watchName: String): Color {
    return when {
        watchName.contains("Zenith El Primero") -> Color(0xFFF0F0F0) // Silver-white dial
        watchName.contains("Omega Seamaster") -> Color(0xFF0A4D8C) // Deep blue dial
        watchName.contains("Rolex Submariner") -> Color(0xFF000000) // Black dial
        watchName.contains("Patek Philippe") -> Color(0xFFF5F5DC) // Cream dial
        watchName.contains("Audemars Piguet") -> Color(0xFF00008B) // Dark blue dial
        watchName.contains("Vacheron Constantin") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Jaeger-LeCoultre") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Blancpain") -> Color(0xFF000000) // Black dial
        watchName.contains("IWC") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Breitling") -> Color(0xFF000080) // Navy blue dial
        watchName.contains("TAG Heuer") -> Color(0xFF000000) // Black dial
        watchName.contains("Longines") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Chopard") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Ulysse Nardin") -> Color(0xFF00008B) // Dark blue dial
        watchName.contains("Girard-Perregaux") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Oris") -> Color(0xFF000000) // Black dial
        watchName.contains("Tudor") -> Color(0xFF000000) // Black dial
        watchName.contains("Baume & Mercier") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Rado") -> Color(0xFF000000) // Black dial
        watchName.contains("Tissot") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Raymond Weil") -> Color(0xFFF5F5F5) // Silver dial
        watchName.contains("Frederique Constant") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Alpina") -> Color(0xFF000000) // Black dial
        watchName.contains("Mondaine") -> Color(0xFFFFFFFF) // White dial
        watchName.contains("Swatch") -> Color(0xFFFFFFFF) // White dial
        else -> Color(0xFF2F4F4F) // Default to the current background color
    }
}

// Function to darken a color by a factor
fun Color.darken(factor: Float = 0.2f): Color {
    val alpha = this.alpha
    val red = this.red * (1 - factor)
    val green = this.green * (1 - factor)
    val blue = this.blue * (1 - factor)
    return Color(red, green, blue, alpha)
}

// Function to determine if a color is dark
fun Color.isDark(): Boolean {
    return this.luminance() < 0.5f
}

// Function to get appropriate text color for a background
fun getTextColorForBackground(backgroundColor: Color): Color {
    return if (backgroundColor.isDark()) Color.White else Color.Black
}

class MainActivity : ComponentActivity() {
    // Initialize the preferences repository
    private lateinit var watchPreferencesRepository: WatchPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the repository
        watchPreferencesRepository = WatchPreferencesRepository(this)

        setContent {
            SwissTimeTheme {
                WatchApp(watchPreferencesRepository)
            }
        }
    }
}

@Composable
fun WatchApp(watchPreferencesRepository: WatchPreferencesRepository) {
    // Get the current context
    val context = LocalContext.current

    // Create a coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Collect the currently selected watch name
    val selectedWatchName by watchPreferencesRepository.selectedWatchName.collectAsState(initial = null)

    // List of watches with their details
    val watches = listOf(
        WatchInfo(
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
        ),
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
        )
    )

    // State to track the currently selected watch for detail view
    var selectedWatch by remember { mutableStateOf<WatchInfo?>(null) }

    // Remember scroll position
    val listState = rememberLazyListState()

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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (selectedWatch == null) {
            // List view
            WatchListScreen(
                watches = watches,
                onWatchClick = { selectedWatch = it },
                selectedWatchName = selectedWatchName,
                onSelectForWidget = selectWatchForWidget,
                modifier = Modifier.padding(innerPadding),
                listState = listState
            )
        } else {
            // Detail view
            WatchDetailScreen(
                watch = selectedWatch!!,
                onBackClick = { selectedWatch = null },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun WatchListScreen(
    watches: List<WatchInfo>,
    onWatchClick: (WatchInfo) -> Unit,
    selectedWatchName: String?,
    onSelectForWidget: (WatchInfo) -> Any,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    // Capture the background color
    val backgroundColor = MaterialTheme.colorScheme.background

    // Set status and navigation bar colors to match the main screen background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = backgroundColor.toArgb()
            window.navigationBarColor = backgroundColor.toArgb()
        }
    }

    Surface(
        color = backgroundColor,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(watches) { watch ->
                WatchListItem(
                    watch = watch,
                    onClick = { onWatchClick(watch) },
                    isSelectedForWidget = selectedWatchName == watch.name,
                    onSelectForWidget = { onSelectForWidget(watch) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun WatchListItem(
    watch: WatchInfo,
    onClick: () -> Unit,
    isSelectedForWidget: Boolean = false,
    onSelectForWidget: () -> Any,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Watch face on the left
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(0.dp, 8.dp, 8.dp, 8.dp),
                contentAlignment = Alignment.Center
            ) {
                watch.composable(Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Description on the right
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = watch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // Widget selection icon
                    IconButton(
                        onClick = { onSelectForWidget() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelectedForWidget) Icons.Filled.Check else Icons.Outlined.AddToHomeScreen,
                            contentDescription = if (isSelectedForWidget) "Selected for widget" else "Add to widget",
                            tint = if (isSelectedForWidget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = watch.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun WatchDetailScreen(
    watch: WatchInfo,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State to track whether the watch is expanded
    var isExpanded by remember { mutableStateOf(false) }

    // Handle back button press
    BackHandler {
        if (isExpanded) {
            // If watch is expanded, collapse it first
            isExpanded = false
        } else {
            // Otherwise, go back to the list
            onBackClick()
        }
    }

    // Get the watch face color and darken it slightly
    val watchFaceColor = getWatchFaceColor(watch.name)
    val darkenedWatchFaceColor = watchFaceColor.darken(0.15f)

    // Get appropriate text color for the background
    val textColor = getTextColorForBackground(darkenedWatchFaceColor)

    // Set status and navigation bar colors to match the detail screen background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = darkenedWatchFaceColor.toArgb()
            window.navigationBarColor = darkenedWatchFaceColor.toArgb()
        }
    }

    Surface(
        color = darkenedWatchFaceColor,
        modifier = modifier.fillMaxSize()
    ) {
        if (isExpanded) {
            // Expanded view - only show the watch centered
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isExpanded = false }, // Click to collapse
                contentAlignment = Alignment.Center
            ) {
                // Make the watch as large as possible while maintaining aspect ratio
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    watch.composable(Modifier.fillMaxSize())
                }
            }
        } else {
            // Normal view - show all elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "← Back to list",
                        modifier = Modifier
                            .clickable(onClick = onBackClick)
                            .padding(8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Watch at the top - clickable to expand
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .padding(16.dp)
                        .clickable { isExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    watch.composable(Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name below
                Text(
                    text = watch.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description below name
                Text(
                    text = watch.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = textColor.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchAppPreview() {
    // Create a mock repository for preview
    val context = LocalContext.current
    val mockRepository = remember { WatchPreferencesRepository(context) }

    SwissTimeTheme {
        WatchApp(mockRepository)
    }
}
