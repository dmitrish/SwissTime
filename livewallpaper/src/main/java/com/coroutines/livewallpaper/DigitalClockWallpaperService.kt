package com.coroutines.livewallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A live wallpaper service that displays the Pontifex Chronometra watch face.
 */
class DigitalClockWallpaperService : WallpaperService() {

    // Color definitions for the Pontifex Chronometra watch face
    private val clockFaceColor = Color.parseColor("#1E2C4A") // Deep blue dial
    private val clockBorderColor = Color.parseColor("#D4AF37") // Gold border
    private val hourHandColor = Color.parseColor("#E0E0E0") // Silver hour hand
    private val minuteHandColor = Color.parseColor("#E0E0E0") // Silver minute hand
    private val secondHandColor = Color.parseColor("#D4AF37") // Gold second hand
    private val markersColor = Color.parseColor("#E0E0E0") // Silver markers
    private val numbersColor = Color.parseColor("#E0E0E0") // Silver numbers
    private val accentColor = Color.parseColor("#D4AF37") // Gold accent
    private val guillocheColor = Color.parseColor("#2A3C5A") // Darker blue for guilloche pattern

    override fun onCreateEngine(): Engine {
        return ClockEngine()
    }

    /**
     * The engine that handles rendering the Pontifex Chronometra watch face.
     */
    inner class ClockEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        
        // Paint objects for the watch face
        private val hourHandPaint = Paint().apply {
            color = hourHandColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        private val minuteHandPaint = Paint().apply {
            color = minuteHandColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        private val secondHandPaint = Paint().apply {
            color = secondHandColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            strokeCap = Paint.Cap.ROUND
        }
        
        private val secondHandFillPaint = Paint().apply {
            color = secondHandColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        private val clockFacePaint = Paint().apply {
            color = clockFaceColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        
        private val clockBorderPaint = Paint().apply {
            color = clockBorderColor
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        
        private val markersPaint = Paint().apply {
            color = markersColor
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }
        
        private val guillochePaint = Paint().apply {
            color = guillocheColor
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }
        
        private val brandTextPaint = Paint().apply {
            color = markersColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }
        
        private val datePaint = Paint().apply {
            color = markersColor
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        // For backward compatibility
        private val timePaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
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
        
        // Helper methods for drawing the Pontifex Chronometra watch face
        
        private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            // Draw the outer border
            clockBorderPaint.strokeWidth = radius * 0.03f
            canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)
            
            // Draw the main face
            canvas.drawCircle(centerX, centerY, radius * 0.97f, clockFacePaint)
            
            // Draw a subtle inner ring
            clockBorderPaint.strokeWidth = radius * 0.005f
            canvas.drawCircle(centerX, centerY, radius * 0.9f, clockBorderPaint)
        }
        
        private fun drawHourMarkers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            for (i in 0 until 12) {
                val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
                val markerRadius = radius * 0.85f
                
                val markerX = centerX + cos(angle).toFloat() * markerRadius
                val markerY = centerY + sin(angle).toFloat() * markerRadius
                
                // Draw applied markers
                if (i % 3 == 0) {
                    // Double marker for 12, 3, 6, 9
                    canvas.drawLine(
                        markerX - cos(angle + Math.PI/2).toFloat() * radius * 0.04f,
                        markerY - sin(angle + Math.PI/2).toFloat() * radius * 0.04f,
                        markerX + cos(angle + Math.PI/2).toFloat() * radius * 0.04f,
                        markerY + sin(angle + Math.PI/2).toFloat() * radius * 0.04f,
                        markersPaint.apply { strokeWidth = radius * 0.02f }
                    )
                } else {
                    // Teardrop markers for other hours (distinctive Parmigiani style)
                    val path = Path()
                    path.moveTo(
                        markerX + cos(angle).toFloat() * radius * 0.03f,
                        markerY + sin(angle).toFloat() * radius * 0.03f
                    )
                    
                    // Create teardrop shape pointing toward center
                    val angleToCenter = Math.atan2(
                        (centerY - markerY).toDouble(),
                        (centerX - markerX).toDouble()
                    ).toFloat()
                    
                    // Control points for the teardrop curve
                    val controlX1 = markerX + cos(angleToCenter + 0.5f).toFloat() * radius * 0.02f
                    val controlY1 = markerY + sin(angleToCenter + 0.5f).toFloat() * radius * 0.02f
                    val controlX2 = markerX + cos(angleToCenter - 0.5f).toFloat() * radius * 0.02f
                    val controlY2 = markerY + sin(angleToCenter - 0.5f).toFloat() * radius * 0.02f
                    
                    // Draw the teardrop
                    path.quadTo(
                        controlX1,
                        controlY1,
                        markerX,
                        markerY
                    )
                    
                    path.quadTo(
                        controlX2,
                        controlY2,
                        markerX + cos(angle).toFloat() * radius * 0.03f,
                        markerY + sin(angle).toFloat() * radius * 0.03f
                    )
                    
                    path.close()
                    canvas.drawPath(path, markersPaint)
                }
            }
        }
        
        private fun drawGuilloche(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            val patternRadius = radius * 0.7f
            
            // Draw a wave pattern (simplified guilloche)
            for (angle in 0 until 360 step 5) {
                val radians = angle * Math.PI / 180
                
                // Create a wave effect
                val waveAmplitude = radius * 0.02f
                val waveFrequency = 8f
                
                val path = Path()
                val startX = centerX + cos(radians).toFloat() * (radius * 0.2f)
                val startY = centerY + sin(radians).toFloat() * (radius * 0.2f)
                
                path.moveTo(startX, startY)
                
                for (i in 0..100) {
                    val t = i / 100f
                    val distance = radius * 0.2f + t * (patternRadius - radius * 0.2f)
                    val waveOffset = sin(t * waveFrequency * Math.PI).toFloat() * waveAmplitude
                    
                    val x = centerX + cos(radians).toFloat() * distance + 
                            cos(radians + Math.PI/2).toFloat() * waveOffset
                    val y = centerY + sin(radians).toFloat() * distance +
                            sin(radians + Math.PI/2).toFloat() * waveOffset
                    
                    path.lineTo(x, y)
                }
                
                canvas.drawPath(path, guillochePaint)
            }
        }
        
        private fun drawDateWindow(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            val dateWindowX = centerX + radius * 0.6f
            val dateWindowY = centerY
            val dateWindowWidth = radius * 0.15f
            val dateWindowHeight = radius * 0.1f
            
            // Draw date window background
            val dateWindowRect = RectF(
                dateWindowX - dateWindowWidth / 2,
                dateWindowY - dateWindowHeight / 2,
                dateWindowX + dateWindowWidth / 2,
                dateWindowY + dateWindowHeight / 2
            )
            
            val dateWindowBackgroundPaint = Paint().apply {
                color = Color.parseColor("#0A1525")
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            
            canvas.drawRect(dateWindowRect, dateWindowBackgroundPaint)
            
            // Draw date window border
            val dateWindowBorderPaint = Paint().apply {
                color = accentColor
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            
            canvas.drawRect(dateWindowRect, dateWindowBorderPaint)
            
            // Draw date text
            datePaint.textSize = radius * 0.06f
            
            // Get current date
            val calendar = Calendar.getInstance()
            val date = calendar.get(Calendar.DAY_OF_MONTH)
            
            canvas.drawText(
                date.toString(),
                dateWindowX,
                dateWindowY + radius * 0.02f,
                datePaint
            )
        }
        
        private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            // Get current time with the current timezone
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            
            // Hour hand - Delta-shaped (Parmigiani style)
            val hourAngle = (hour * 30 + minute * 0.5f) * Math.PI / 180
            
            val hourHandPath = Path()
            hourHandPath.moveTo(
                centerX + sin(hourAngle).toFloat() * radius * 0.45f,
                centerY - cos(hourAngle).toFloat() * radius * 0.45f
            ) // Tip
            hourHandPath.lineTo(
                centerX + sin(hourAngle + Math.PI/2).toFloat() * radius * 0.04f + sin(hourAngle).toFloat() * radius * 0.2f,
                centerY - cos(hourAngle + Math.PI/2).toFloat() * radius * 0.04f - cos(hourAngle).toFloat() * radius * 0.2f
            ) // Right shoulder
            hourHandPath.lineTo(
                centerX + sin(hourAngle + Math.PI/2).toFloat() * radius * 0.015f,
                centerY - cos(hourAngle + Math.PI/2).toFloat() * radius * 0.015f
            ) // Right base
            hourHandPath.lineTo(
                centerX - sin(hourAngle + Math.PI/2).toFloat() * radius * 0.015f,
                centerY + cos(hourAngle + Math.PI/2).toFloat() * radius * 0.015f
            ) // Left base
            hourHandPath.lineTo(
                centerX - sin(hourAngle + Math.PI/2).toFloat() * radius * 0.04f + sin(hourAngle).toFloat() * radius * 0.2f,
                centerY + cos(hourAngle + Math.PI/2).toFloat() * radius * 0.04f - cos(hourAngle).toFloat() * radius * 0.2f
            ) // Left shoulder
            hourHandPath.close()
            
            canvas.drawPath(hourHandPath, hourHandPaint)
            
            // Minute hand
            val minuteAngle = minute * 6f * Math.PI / 180
            
            val minuteHandPath = Path()
            minuteHandPath.moveTo(
                centerX + sin(minuteAngle).toFloat() * radius * 0.65f,
                centerY - cos(minuteAngle).toFloat() * radius * 0.65f
            ) // Tip
            minuteHandPath.lineTo(
                centerX + sin(minuteAngle + Math.PI/2).toFloat() * radius * 0.03f + sin(minuteAngle).toFloat() * radius * 0.3f,
                centerY - cos(minuteAngle + Math.PI/2).toFloat() * radius * 0.03f - cos(minuteAngle).toFloat() * radius * 0.3f
            ) // Right shoulder
            minuteHandPath.lineTo(
                centerX + sin(minuteAngle + Math.PI/2).toFloat() * radius * 0.01f,
                centerY - cos(minuteAngle + Math.PI/2).toFloat() * radius * 0.01f
            ) // Right base
            minuteHandPath.lineTo(
                centerX - sin(minuteAngle + Math.PI/2).toFloat() * radius * 0.01f,
                centerY + cos(minuteAngle + Math.PI/2).toFloat() * radius * 0.01f
            ) // Left base
            minuteHandPath.lineTo(
                centerX - sin(minuteAngle + Math.PI/2).toFloat() * radius * 0.03f + sin(minuteAngle).toFloat() * radius * 0.3f,
                centerY + cos(minuteAngle + Math.PI/2).toFloat() * radius * 0.03f - cos(minuteAngle).toFloat() * radius * 0.3f
            ) // Left shoulder
            minuteHandPath.close()
            
            canvas.drawPath(minuteHandPath, minuteHandPaint)
            
            // Second hand
            val secondAngle = second * 6f * Math.PI / 180
            
            // Main hand
            canvas.drawLine(
                centerX + sin(secondAngle).toFloat() * radius * 0.2f,
                centerY - cos(secondAngle).toFloat() * radius * 0.2f,
                centerX + sin(secondAngle).toFloat() * radius * 0.75f,
                centerY - cos(secondAngle).toFloat() * radius * 0.75f,
                secondHandPaint
            )
            
            // Oval counterweight
            val ovalPath = Path()
            val ovalWidth = radius * 0.05f
            val ovalHeight = radius * 0.1f
            
            val ovalCenterX = centerX - sin(secondAngle).toFloat() * radius * 0.1f
            val ovalCenterY = centerY + cos(secondAngle).toFloat() * radius * 0.1f
            
            val ovalRect = RectF(
                ovalCenterX - ovalWidth,
                ovalCenterY - ovalHeight / 2,
                ovalCenterX + ovalWidth,
                ovalCenterY + ovalHeight / 2
            )
            
            // Rotate the canvas to draw the oval
            canvas.save()
            canvas.rotate(
                (secondAngle * 180 / Math.PI).toFloat(),
                ovalCenterX,
                ovalCenterY
            )
            
            canvas.drawOval(ovalRect, secondHandFillPaint)
            
            canvas.restore()
        }
        
        private fun drawLogo(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
            // Draw "PONTIFEX" text
            brandTextPaint.textSize = radius * 0.07f
            canvas.drawText(
                "PONTIFEX",
                centerX,
                centerY - radius * 0.25f,
                brandTextPaint
            )
            
            // Draw "CHRONOMETRA" text
            brandTextPaint.textSize = radius * 0.05f
            canvas.drawText(
                "CHRONOMETRA",
                centerX,
                centerY - radius * 0.15f,
                brandTextPaint
            )
            
            // Draw "SWISS MADE" text
            brandTextPaint.textSize = radius * 0.04f
            canvas.drawText(
                "SWISS MADE",
                centerX,
                centerY + radius * 0.5f,
                brandTextPaint
            )
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
                    
                    // Get canvas dimensions
                    val width = canvas.width
                    val height = canvas.height
                    
                    // Calculate center and radius
                    val centerX = width / 2f
                    val centerY = height / 2f
                    val radius = min(width, height) / 2 * 0.8f
                    
                    // Draw Pontifex Chronometra watch face
                    drawClockFace(canvas, centerX, centerY, radius)
                    drawHourMarkers(canvas, centerX, centerY, radius)
                    drawGuilloche(canvas, centerX, centerY, radius)
                    drawDateWindow(canvas, centerX, centerY, radius)
                    drawClockHands(canvas, centerX, centerY, radius)
                    
                    // Draw center dot
                    val centerDotPaint = Paint().apply {
                        color = accentColor
                        isAntiAlias = true
                        style = Paint.Style.FILL
                    }
                    canvas.drawCircle(centerX, centerY, radius * 0.02f, centerDotPaint)
                    
                    // Draw logo
                    drawLogo(canvas, centerX, centerY, radius)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}