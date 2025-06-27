package com.coroutines.swisstime

import android.util.Log

/**
 * Utility class for logging performance timing information.
 * Used to measure the time between tapping on the Piaget watch and the OptimizedWorldMapScreen being completely drawn.
 */
object TimingLogger {
    // Static variable to store the start time
    var startTime: Long = 0
    
    // Tag for logging
    private const val TAG = "PerformanceLog"
    
    /**
     * Log the end time and calculate the duration since the start time.
     */
    fun logEndTime() {
        if (startTime == 0L) {
            Log.w(TAG, "End time logged but start time was not set")
            return
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        Log.d(TAG, "OptimizedWorldMapScreen completely drawn at $endTime ms")
        Log.d(TAG, "Time from tap to complete drawing: $duration ms")
    }
}