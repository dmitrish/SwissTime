package com.coroutines.swisstime.wearos.repository

import androidx.compose.ui.Modifier
import com.coroutines.swisstime.wearos.model.WatchFace
import com.coroutines.swisstime.wearos.service.TimeZoneService
import com.coroutines.swisstime.wearos.watchfaces.CenturioLuminor
import com.coroutines.swisstime.wearos.watchfaces.Chronomagus
import com.coroutines.swisstime.wearos.watchfaces.Valentinianus
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
        )
        // More watch faces will be added here
    )

    // Selected watch face ID
    private val _selectedWatchFaceId = MutableStateFlow<String?>(null)
    val selectedWatchFaceId: StateFlow<String?> = _selectedWatchFaceId.asStateFlow()

    /**
     * Get all available watch faces
     */
    fun getWatchFaces(): List<WatchFace> {
        return watchFaces
    }

    /**
     * Get a watch face by ID
     */
    fun getWatchFaceById(id: String): WatchFace? {
        return watchFaces.find { it.id == id }
    }

    /**
     * Select a watch face by ID
     */
    fun selectWatchFace(id: String) {
        _selectedWatchFaceId.value = id
    }

    /**
     * Get the currently selected watch face
     */
    fun getSelectedWatchFace(): WatchFace? {
        val id = _selectedWatchFaceId.value ?: return watchFaces.firstOrNull()
        return getWatchFaceById(id)
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
