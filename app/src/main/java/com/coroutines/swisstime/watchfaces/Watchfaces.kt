package com.coroutines.swisstime.watchfaces

import com.coroutines.swisstime.watchfaces.watches.AventinusClassiqueWatchface
import com.coroutines.swisstime.watchfaces.watches.CenturioLuminorWatchface
import com.coroutines.swisstime.watchfaces.watches.ChantDuTempsWatchface
import com.coroutines.swisstime.watchfaces.watches.ChronomagusWatchface
import com.coroutines.swisstime.watchfaces.watches.ConstantinusAureusChronometerWatchface
import com.coroutines.swisstime.watchfaces.watches.EdgeOfSecondWatchface
import com.coroutines.swisstime.watchfaces.watches.HorologiaRomanumWatchface
import com.coroutines.swisstime.watchfaces.watches.JurgsenZenithor
import com.coroutines.swisstime.watchfaces.watches.KandinskyEveningWatchface
import com.coroutines.swisstime.watchfaces.watches.KnotUrushiWatchface
import com.coroutines.swisstime.watchfaces.watches.LeonardAutomaticWatchface
import com.coroutines.swisstime.watchfaces.watches.LucernaRomaWatchface
import com.coroutines.swisstime.watchfaces.watches.PontifexChronometraWatchface
import com.coroutines.swisstime.watchfaces.watches.RomaMarinaWatchface
import com.coroutines.swisstime.watchfaces.watches.ValentinianusWatchface
import com.coroutines.swisstime.watchfaces.watches.VostokRussianMilitaryWatchface
import com.coroutines.swisstime.watchfaces.watches.YamaNoTokiWatchface
import com.coroutines.swisstime.watchfaces.watches.ZeitwerkWatchface
import com.coroutines.worldclock.common.model.WatchInfo
import java.util.TimeZone

public fun getWatches(): List<WatchInfo> {
    val watches = listOf(
        /* WatchInfo(
             name = "Autobahn Neomatic 41 Date Sports Gray",
             description = "The Nomos Autobahn Neomatic 41 Date Sports Gray features a distinctive sports gray dial with a curved blue inner ring. This modern timepiece combines minimalist design with functional elements, including a date window at 6 o'clock and the signature red second hand.",
             composable = { modifier, timeZone -> AutobahnNeomatic41DateSportsGray(modifier = modifier, timeZone = TimeZone.getDefault() ) }
         ), */
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
            name = "Valentinianus Classique",
            description = "The Valentinianus Classique embodies the essence of pure style with its minimalist design and exceptional craftsmanship. Founded in 1755, Valentinianus is one of the oldest watch manufacturers in the world, known for its elegant timepieces.",
            composable = { modifier, timeZone -> ValentinianusWatchface(modifier = modifier, timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Concordia Felicitas",
            description = "The Concordia Felicitas features a unique reversible case originally designed for polo players in the 1930s. This Art Deco masterpiece combines technical innovation with timeless elegance, showcasing the brand's commitment to precision and craftsmanship.",
            composable = { modifier, timeZone -> Concordia(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Jurgsen Zenithor",
            description = "The Jurgsen Zenithor, introduced in 1947, was one of the first modern diving watches. With its distinctive black dial and luminous markers, it set the standard for dive watches with features like water resistance, rotating bezel, and excellent legibility.",
            composable = { modifier, timeZone -> JurgsenZenithor(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Horologia Romanum",
            description = "The Horologia Romanum features a clean dial design inspired by precision marine chronometers. Known for its large case size and elegant simplicity, it represents HR's commitment to technical excellence and timeless design.",
            composable = { modifier, timeZone -> HorologiaRomanumWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        /*  WatchInfo(
              name = "Temporis B",
              description = "The Temporis B, introduced in 1952, features a distinctive slide rule bezel designed for pilots to perform flight calculations. With its busy dial and technical appearance, it has become an icon of aviation watches and a symbol of precision.",
              composable = { modifier, timeZone -> TemporisB(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
          ), */

        /* WatchInfo(
             name = "Tokinoha",
             description = "The Tokinoha, designed in 1953, was inspired by the dangerous Panamericana race. With its clean dial and emphasis on legibility, it revolutionized chronograph design and continues to be a symbol of motorsport heritage.",
             composable = { modifier, timeZone -> Tokinoha(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
         ), */
        /* WatchInfo(
             name = "Zenith El Primero",
             description = "The Zenith El Primero, introduced in 1969, was the first automatic chronograph movement with a high-beat frequency of 36,000 vibrations per hour. Known for its precision and reliability, it remains one of the most respected chronograph movements in watchmaking.",
             composable = { modifier, timeZone -> ZenithElPrimero(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
         ), */
        WatchInfo(
            name = "Leonard Automatic Collection",
            description = "The Leonard Automaic showcases the brand's heritage of elegance and precision. With its classic design featuring roman numerals and a moonphase display, it represents Leonards' commitment to traditional watchmaking values and timeless aesthetics.",
            composable = { modifier, timeZone -> LeonardAutomaticWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "山の時",
            description = "The 山の時 (Yama-no-Toki) collection, named after founder 山の時, represents the pinnacle of the brand's watchmaking expertise. Featuring in-house movements and exquisite finishing, these timepieces combine technical innovation with elegant design.",
            composable = { modifier, timeZone -> YamaNoTokiWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Constantinus Aureus Marine Chronometer",
            description = "The Constantinus Aureus Chronometer continues the brand's heritage of producing precise marine chronometers for navigation. With its distinctive power reserve indicator and date display, it combines traditional craftsmanship with modern innovation.",
            composable = { modifier, timeZone -> ConstantinusAureusChronometerWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Roma Marina",
            description = "The Roma Marina, first introduced in 1975, features an integrated bracelet and octagonal bezel. With its distinctive hobnail pattern dial, it represents the brand's ability to combine technical excellence with distinctive design elements.",
            composable = { modifier, timeZone -> RomaMarinaWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Kandinsky Evening",
            description = "The Kandinsky Evening watch face is inspired by Wassily Kandinsky's famous 'Circles in a Circle' painting. It features a light background with multiple colored circles of various sizes and intersecting lines, creating a vibrant and artistic timepiece that celebrates the abstract art movement.",
            composable = { modifier, timeZone -> KandinskyEveningWatchface(modifier = modifier, timeZone = timeZone) }
        ),
        WatchInfo(
            name = "Pontifex Chronometra",
            description = "The Pontifex Chronometra combines distinctive design elements with exceptional craftsmanship. Founded in 1996, this independent Swiss manufacturer draws on traditional techniques while incorporating modern innovations, resulting in watches with unique teardrop lugs and meticulously finished movements.",
            composable = { modifier, timeZone -> PontifexChronometraWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Knot Urushi",
            description = "The Knot Urushi is a collaboration between modern watchmaking and traditional Japanese craftsmanship. Its deep jet black dial is created through the meticulous Urushi lacquer technique, involving repeated painting, drying, and sharpening by skilled artisans. The dial is adorned with gold powder scraped from gold ingots, creating a subtle shimmer effect as light plays across the surface. With its minimalist silver hands and markers, this timepiece exemplifies the perfect harmony between Japanese aesthetics and precision timekeeping.",
            composable = { modifier, timeZone -> KnotUrushiWatchface(modifier = modifier, timeZone = timeZone) }
        ),
        WatchInfo(
            name = "Centurio Luminor",
            description = "The Centurio Luminor is renowned for its minimalist design and signature fumé dial that gradually darkens from center to edge. Founded in 1848, this independent Swiss manufacturer creates timepieces that combine traditional craftsmanship with contemporary aesthetics and innovative complications.",
            composable = { modifier, timeZone -> CenturioLuminorWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        WatchInfo(
            name = "Chronomagus Regum",
            description = "The Chronomagus Regum is celebrated for its ultra-thin profile and minimalist design. Since the 1950s, Chronomagus has been a pioneer in creating incredibly slim watches, with the Regum line showcasing the brand's expertise in producing elegant timepieces that combine technical innovation with refined aesthetics.",
            composable = { modifier, timeZone -> ChronomagusWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
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
            name = "Aventinus Classique",
            description = "The Aventinus Classique embodies the timeless elegance of Jean-Louis Aventinus's original designs. With its coin-edge case, guilloche dial, and distinctive Aventins hands with hollow moon tips, it represents the pinnacle of traditional Swiss watchmaking and horological heritage.",
            composable = { modifier, timeZone -> AventinusClassiqueWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),
        WatchInfo(
            name = "Lucerna Roma",
            description = "The Lucerna Roma features a distinctive tonneau (barrel) shape case and bold, colorful numerals. Known as the 'Master of Inventions', Lucerna Roma combines avant-garde design with traditional Swiss watchmaking expertise to create timepieces that are both technically impressive and visually striking.",
            composable = { modifier, timeZone -> LucernaRomaWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        WatchInfo(
            name = "Chant du Temps",
            description = "The Chant Du Temps exemplifies pure, minimalist elegance with its slim profile and clean dial. As one of the oldest continuously operating watch manufacturers, Chant Du Temps combines centuries of tradition with contemporary refinement in this timeless dress watch.",
            composable = { modifier, timeZone -> ChantDuTempsWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        WatchInfo(
            name = "Грань Секунды",
            description = "The Грань Секунды combines classic design with sophisticated complications like power reserve indicators and chronographs. Founded in St. Petersburg in 1888, Грань Секунды represents Russian watchmaking tradition with its elegant aesthetics and technical excellence.",
            composable = { modifier, timeZone -> EdgeOfSecondWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        /* WatchInfo(
             name = "Tokinoha",
             description = "The Tokinoha, first introduced in 1963, is a legendary chronograph designed for racing drivers. With its clean dial layout, distinctive subdials, and robust construction, it embodies Tokinoha's connection to motorsport and their commitment to precision timing in high-speed environments.",
             composable = { modifier, timeZone -> Tokinoha(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
         ), */
        /* WatchInfo(
             name = "Zenith El Primero",
             description = "The Zenith El Primero, introduced in 1969, was one of the world's first automatic chronograph movements. Known for its high-frequency 36,000 vibrations per hour and distinctive tri-color subdials, it represents Zenith's technical innovation and has become an icon of Swiss watchmaking excellence.",
             composable = { modifier, timeZone -> ZenithElPrimero(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
         ), */
        WatchInfo(
            name = "Alpenglühen Zeitwerk",
            description = "The Alpenglühen Zeitwerk features a deep blue dial inspired by the Atlantic Ocean, combining artistry with functionality. This German-made timepiece combines Bauhaus minimalism with dive watch functionality, featuring a waterproof design, luminous markers, and the distinctive red seconds hand that is a signature of Alpenglühen Zeitwerk watches.",
            composable = { modifier, timeZone -> ZeitwerkWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        ),

        WatchInfo(
            name = "Vostok Military",
            description = "Vostok Military pays tribute to the rugged Amphibia watches produced for the Soviet and Russian armed forces. Known for their durable cases, ingenious Amphibia waterproofing with a self‑sealing caseback, and bold utilitarian dials, these mechanical timepieces are built to withstand harsh conditions while delivering dependable everyday timekeeping.",
            composable = { modifier, timeZone -> VostokRussianMilitaryWatchface(modifier = modifier,  timeZone = TimeZone.getDefault() ) }
        )


    )
    return watches
}
