package com.coroutines.swisstime.wallpaper

import com.coroutines.swisstime.watchfaces.ChronomagusRegum
import com.coroutines.swisstime.watchfaces.Knot
import com.coroutines.swisstime.watchfaces.RomaMarina
import com.coroutines.swisstime.watchfaces.Zeitwerk
import com.coroutines.worldclock.common.model.WatchInfo
import java.util.TimeZone

fun wallpaperWatches(): List<WatchInfo> {
  val watches =
    listOf(
      WatchInfo(
        name = "Roma Marina",
        description =
          "The Roma Marina, first introduced in 1975, features an integrated bracelet and octagonal bezel. With its distinctive hobnail pattern dial, it represents the brand's ability to combine technical excellence with distinctive design elements.",
        composable = { modifier, timeZone ->
          RomaMarina(modifier = modifier, timeZone = TimeZone.getDefault())
        }
      ),
      WatchInfo(
        name = "Chronomagus Regum",
        description =
          "The Chronomagus Regum is celebrated for its ultra-thin profile and minimalist design. Since the 1950s, Chronomagus has been a pioneer in creating incredibly slim watches, with the Regum line showcasing the brand's expertise in producing elegant timepieces that combine technical innovation with refined aesthetics.",
        composable = { modifier, timeZone ->
          ChronomagusRegum(modifier = modifier, timeZone = TimeZone.getDefault())
        }
      ),
      WatchInfo(
        name = "Alpenglühen Zeitwerk",
        description =
          "The Alpenglühen Zeitwerk features a deep blue dial inspired by the Atlantic Ocean. This German-made timepiece combines Bauhaus minimalism with dive watch functionality, featuring a waterproof design, luminous markers, and the distinctive red seconds hand that is a signature of Alpenglühen Zeitwerk watches.",
        composable = { modifier, timeZone ->
          Zeitwerk(modifier = modifier, timeZone = TimeZone.getDefault())
        }
      ),
      WatchInfo(
        name = "Knot Urushi",
        description =
          "The Knot Urushi is a collaboration between modern watchmaking and traditional Japanese craftsmanship. Its deep jet black dial is created through the meticulous Urushi lacquer technique, involving repeated painting, drying, and sharpening by skilled artisans. The dial is adorned with gold powder scraped from gold ingots, creating a subtle shimmer effect as light plays across the surface. With its minimalist silver hands and markers, this timepiece exemplifies the perfect harmony between Japanese aesthetics and precision timekeeping.",
        composable = { modifier, timeZone -> Knot(modifier = modifier, timeZone = timeZone) }
      )
    )
  return watches
}
