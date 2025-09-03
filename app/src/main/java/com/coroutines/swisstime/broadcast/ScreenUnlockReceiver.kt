package com.coroutines.swisstime.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.coroutines.swisstime.eventbus.Event
import com.coroutines.swisstime.eventbus.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenUnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val extras = intent.extras

        // Create event based on the broadcast
        val event = when (action) {
            "android.intent.action.USER_PRESENT" -> {
                val data = intent.getStringExtra("data") ?: ""
                Event.NotificationReceived(data)
            }
            else -> Event.BroadcastReceived(action ?: "", extras)
        }

        // Publish event using coroutine scope
        CoroutineScope(Dispatchers.IO).launch {
            EventBus.publish(event)
        }

        // Alternative: Use tryPublish if you can't use coroutines
        // EventBus.tryPublish(event)
    }
}