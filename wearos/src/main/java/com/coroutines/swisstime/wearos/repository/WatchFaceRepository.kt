package com.coroutines.swisstime.wearos.repository

import androidx.compose.ui.Modifier
import com.coroutines.swisstime.wearos.model.WatchFace
import com.coroutines.swisstime.wearos.service.TimeZoneService
import com.coroutines.swisstime.wearos.watchfaces.AventinusClassique
import com.coroutines.swisstime.wearos.watchfaces.CenturioLuminor
import com.coroutines.swisstime.wearos.watchfaces.Chronomagus
import com.coroutines.swisstime.wearos.watchfaces.Concordia
import com.coroutines.swisstime.wearos.watchfaces.HMoserEndeavour
import com.coroutines.swisstime.wearos.watchfaces.HorologiaRomanum
import com.coroutines.swisstime.wearos.watchfaces.JurgsenZenithor
import com.coroutines.swisstime.wearos.watchfaces.LucernaRoma
import com.coroutines.swisstime.wearos.watchfaces.PontifexChronometra
import com.coroutines.swisstime.wearos.watchfaces.Valentinianus
import com.coroutines.swisstime.wearos.watchfaces.Zeitwerk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.TimeZone

/**
 * Repository for watch faces
 */
class WatchFaceRepository(
    private val timeZoneRepository: TimeZoneRepository = TimeZoneRepository(TimeZoneService())
) {

    // List of available watch faces
    private val watchFaces = listOf(
        WatchFace(
            id = "9",
            name = "Lucerna Roma",
            description = "The Lucerna Roma features a distinctive tonneau (barrel) shape case and bold, colorful numerals. Known as the 'Master of Inventions', Lucerna Roma combines avant-garde design with traditional Swiss watchmaking expertise to create timepieces that are both technically impressive and visually striking.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                LucernaRoma(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "1",
            name = "Valentinianus Classique",
            description = "The Valentinianus Classique embodies the essence of pure style with its minimalist design and exceptional craftsmanship. Founded in 1755, Valentinianus is one of the oldest watch manufacturers in the world, known for its elegant timepieces.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                Valentinianus(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "2",
            name = "Centurio Luminor",
            description = "The Centurio Luminor features a distinctive green dial with luminous hands and hour markers. Established in 1728, Centurio is renowned for its robust and reliable timepieces that combine traditional craftsmanship with modern technology.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                CenturioLuminor(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "3",
            name = "Chronomagus Regium",
            description = "The Chronomagus Regium features an elegant deep blue dial with ultra-thin profile and minimalist design. Known for its exceptional craftsmanship and precision, this timepiece represents the pinnacle of horological artistry.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                Chronomagus(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "4",
            name = "H. Moser Endeavour",
            description = "The H. Moser Endeavour features a stunning fumé green dial with elegant leaf-shaped hands. Established in 1828, H. Moser & Cie is renowned for its minimalist designs and exceptional craftsmanship, creating timepieces that are both innovative and timeless.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                HMoserEndeavour(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "5",
            name = "Zeitwerk Alpenglühen",
            description = "The Zeitwerk Alpenglühen features a deep Atlantic blue dial with a distinctive date window at 6 o'clock and luminous hands and markers. With its unique rectangular date display and red second hand with a distinctive circle near the tip, this timepiece combines technical excellence with elegant design.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                Zeitwerk(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "6",
            name = "Aventinus Classique",
            description = "The Aventinus Classique features an elegant off-white dial with a fluted gold bezel and distinctive blue hands. With its exquisite guilloche pattern and Roman numerals, this timepiece exemplifies traditional watchmaking artistry with a secret signature marking its exclusivity.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                AventinusClassique(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "7",
            name = "Pontifex Chronometra",
            description = "The Pontifex Chronometra showcases a deep blue dial with gold accents and a distinctive date window at 3 o'clock. Its delta-shaped hands and teardrop hour markers are complemented by an intricate wave guilloche pattern, creating a timepiece of exceptional elegance and precision.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                PontifexChronometra(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "8",
            name = "Horologia Romanum",
            description = "The Horologia Romanum features a classic white dial with bold Arabic numerals and blued steel hands. Its distinctive seconds subdial at 6 o'clock adds a layer of sophistication to this elegant timepiece, inspired by traditional pocket watch designs with a modern interpretation.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                HorologiaRomanum(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "10",
            name = "Concordia Felicitas",
            description = "The Concordia Felicitas features a unique rectangular case with Art Deco styling and a beige/cream dial. With its distinctive blue hands and decorative corner lines, this timepiece combines elegant design with technical precision, showcasing the brand's commitment to traditional watchmaking values.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                Concordia(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        ),
        WatchFace(
            id = "11",
            name = "Jurgsen Zenithor",
            description = "The Jurgsen Zenithor features a deep black dial with a navy blue bezel and luminous markers. This distinctive dive watch combines robust functionality with elegant design, showcasing precise timekeeping with its white hands and orange-red second hand with a distinctive circle near the tip.",
            composable = { modifier, timeZone, onSelectTimeZone -> 
                JurgsenZenithor(
                    modifier = modifier, 
                    timeZone = timeZone,
                    watchFaceRepository = this,
                    onSelectTimeZone = onSelectTimeZone
                ) 
            }
        )
        // More watch faces will be added here
    )

    // Selected watch face ID
    private val _selectedWatchFaceId = MutableStateFlow<String?>(null)
    val selectedWatchFaceId: StateFlow<String?> = _selectedWatchFaceId.asStateFlow()

    /**
     * Get all available watch faces
     * @param isScreenRound Whether the screen is round or rectangular
     */
    fun getWatchFaces(isScreenRound: Boolean = true): List<WatchFace> {
        return if (isScreenRound) {
            // For round screens, return all watch faces except rectangular ones (Lucerna Roma and Concordia)
            watchFaces.filter { it.id != "9" && it.id != "10" }
        } else {
            // For rectangular screens, return only rectangular watches (Lucerna Roma and Concordia)
            watchFaces.filter { it.id == "9" || it.id == "10" }
        }
    }

    /**
     * Get a watch face by ID
     * @param id The ID of the watch face to get
     * @param isScreenRound Whether the screen is round or rectangular
     */
    fun getWatchFaceById(id: String, isScreenRound: Boolean = true): WatchFace? {
        // Get available watch faces based on screen shape
        val availableWatchFaces = getWatchFaces(isScreenRound)

        // Find the watch face with the given ID among the available watch faces
        return availableWatchFaces.find { it.id == id }
    }

    /**
     * Select a watch face by ID
     */
    fun selectWatchFace(id: String) {
        _selectedWatchFaceId.value = id
    }

    /**
     * Get the currently selected watch face
     * @param isScreenRound Whether the screen is round or rectangular
     */
    fun getSelectedWatchFace(isScreenRound: Boolean = true): WatchFace? {
        val id = _selectedWatchFaceId.value

        // Get available watch faces based on screen shape
        val availableWatchFaces = getWatchFaces(isScreenRound)

        // If no watch face is selected or the selected watch face is not available for this screen shape,
        // return the first available watch face
        if (id == null || availableWatchFaces.none { it.id == id }) {
            return availableWatchFaces.firstOrNull()
        }

        // Return the selected watch face
        return getWatchFaceById(id, isScreenRound)
    }

    /**
     * Get all available time zones
     */
    fun getAllTimeZones(): List<TimeZoneInfo> {
        return timeZoneRepository.getAllTimeZones()
    }

    /**
     * Get the selected time zone
     */
    fun getSelectedTimeZone(): TimeZone {
        return timeZoneRepository.getSelectedTimeZone()
    }

    /**
     * Get the selected time zone info
     */
    fun getSelectedTimeZoneInfo(): TimeZoneInfo {
        return timeZoneRepository.getSelectedTimeZoneInfo()
    }

    /**
     * Get the selected time zone ID as a StateFlow
     */
    fun getSelectedTimeZoneId(): StateFlow<String> {
        return timeZoneRepository.selectedTimeZoneId
    }

    /**
     * Save the selected time zone
     */
    fun saveSelectedTimeZone(timeZoneId: String) {
        timeZoneRepository.saveSelectedTimeZone(timeZoneId)
    }
}
