package com.coroutines.livewallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * A live wallpaper service that displays the current time in a simple digital format.
 */
class DigitalClockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return ClockEngine()
    }

    /**
     * The engine that handles rendering the digital clock.
     */
    inner class ClockEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        
        private val timePaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        
        private val datePaint = Paint().apply {
            color = Color.LTGRAY
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        
        private val timeUpdateRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                // Schedule next update at the start of the next second
                val now = System.currentTimeMillis()
                val nextSecond = (now / 1000 + 1) * 1000
                handler.postDelayed(this, nextSecond - now)
            }
        }
        
        private val timeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                timeFormat.timeZone = TimeZone.getDefault()
                dateFormat.timeZone = TimeZone.getDefault()
                drawFrame()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            
            // Register to receive time zone changes
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_TIME_CHANGED)
            }
            registerReceiver(timeZoneReceiver, filter)
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(timeUpdateRunnable)
            try {
                unregisterReceiver(timeZoneReceiver)
            } catch (e: IllegalArgumentException) {
                // Receiver not registered
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                drawFrame()
                handler.post(timeUpdateRunnable)
            } else {
                handler.removeCallbacks(timeUpdateRunnable)
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            
            // Adjust text size based on surface dimensions
            val timeTextSize = width * 0.15f
            val dateTextSize = width * 0.05f
            
            timePaint.textSize = timeTextSize
            datePaint.textSize = dateTextSize
            
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(timeUpdateRunnable)
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // Draw background
                    canvas.drawColor(Color.BLACK)
                    
                    // Get current time and date
                    val now = Date()
                    val timeText = timeFormat.format(now)
                    val dateText = dateFormat.format(now)
                    
                    // Get canvas dimensions
                    val width = canvas.width
                    val height = canvas.height
                    
                    // Draw time
                    canvas.drawText(
                        timeText,
                        width / 2f,
                        height / 2f,
                        timePaint
                    )
                    
                    // Draw date below time
                    canvas.drawText(
                        dateText,
                        width / 2f,
                        height / 2f + timePaint.textSize,
                        datePaint
                    )
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}