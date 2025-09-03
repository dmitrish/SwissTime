package com.coroutines.swisstime.eventbus

import android.os.Bundle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Event>(
        replay = 0, // Don't replay events to new subscribers
        extraBufferCapacity = 1 // Buffer one event if no active collectors
    )

    val events: SharedFlow<Event> = _events.asSharedFlow()

    suspend fun publish(event: Event) {
        _events.emit(event)
    }

    // Non-suspending version for cases where you can't use suspend functions
    fun tryPublish(event: Event) {
        _events.tryEmit(event)
    }
}

// Define your event types
sealed class Event {
    data class NotificationReceived(val data: String) : Event()
    data class BroadcastReceived(val action: String, val extras: Bundle?) : Event()
    // Add more event types as needed
}