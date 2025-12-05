package com.coroutines.swisstime.watchfaces.scaffold

import java.util.Calendar

data class WatchTime(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val hourAngle: Float,
    val minuteAngle: Float,
    val secondAngle: Float
) {
    companion object {
        fun from(calendar: Calendar): WatchTime {
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            return WatchTime(
                hour = hour,
                minute = minute,
                second = second,
                hourAngle = hour * 30f + minute * 0.5f,
                minuteAngle = minute * 6f,
                secondAngle = second * 6f
            )
        }
    }
}
