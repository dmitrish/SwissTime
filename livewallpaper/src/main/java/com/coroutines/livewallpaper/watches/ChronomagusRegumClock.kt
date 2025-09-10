package com.coroutines.livewallpaper.watches

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import com.coroutines.livewallpaper.common.BaseClock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A class that handles rendering the Chronomagus Regum watch face.
 */
class ChronomagusRegumClock(private val context: Context, private val handler: Handler) : BaseClock {

    // Color definitions for the Chronomagus Regum watch face
    private val clockFaceColor = Color.parseColor("#000080") // Deep blue dial
    private val clockBorderColor = Color.parseColor("#FFFFFF") // White gold case
    private val hourHandColor = Color.parseColor("#FFFFFF") // White hour hand
    private val minuteHandColor = Color.parseColor("#FFFFFF") // White minute hand
    private val secondHandColor = Color.parseColor("#FFFFFF") // White second hand
    private val markersColor = Color.parseColor("#FFFFFF") // White markers
    private val logoColor = Color.parseColor("#FFFFFF") // White logo

    // Paint objects for the watch face
    private val hourHandPaint = Paint().apply {
        color = hourHandColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
    }

    private val minuteHandPaint = Paint().apply {
        color = minuteHandColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        strokeCap = Paint.Cap.ROUND
    }

    private val secondHandPaint = Paint().apply {
        color = secondHandColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
        strokeCap = Paint.Cap.ROUND
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
        strokeWidth = 4f
    }

    private val markersPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val markerDotPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val logoPaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val modelPaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val swissMadePaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val ultraThinPaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface= Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    // For backward compatibility
    private val timePaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

    private val timeZoneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            timeFormat.timeZone = TimeZone.getDefault()
            dateFormat.timeZone = TimeZone.getDefault()
        }
    }

    init {
        // Register to receive time zone changes
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
        }
        context.registerReceiver(timeZoneReceiver, filter)
    }

    /**
     * Updates the text sizes based on the surface dimensions.
     */
    override fun updateTextSizes(width: Int) {
        val timeTextSize = width * 0.15f
        val logoTextSize = width * 0.08f
        val modelTextSize = width * 0.06f
        val swissMadeTextSize = width * 0.04f
        val ultraThinTextSize = width * 0.05f

        timePaint.textSize = timeTextSize
        logoPaint.textSize = logoTextSize
        modelPaint.textSize = modelTextSize
        swissMadePaint.textSize = swissMadeTextSize
        ultraThinPaint.textSize = ultraThinTextSize
    }

    /**
     * Draws the clock on the provided canvas.
     */
    override fun draw(canvas: Canvas) {
        // Draw background
        canvas.drawColor(Color.BLACK)

        // Get canvas dimensions
        val width = canvas.width
        val height = canvas.height

        // Calculate center and radius
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2 * 0.8f

        // Draw Chronomagus Regum watch face
        drawClockFace(canvas, centerX, centerY, radius)
        drawHourMarkers(canvas, centerX, centerY, radius)
        drawClockHands(canvas, centerX, centerY, radius)

        // Draw center dot
        canvas.drawCircle(centerX, centerY, radius * 0.01f, markerDotPaint)

        // Draw logo
        drawLogo(canvas, centerX, centerY, radius)
    }

    /**
     * Cleans up resources when the clock is no longer needed.
     */
    override fun destroy() {
        try {
            context.unregisterReceiver(timeZoneReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
    }

    // Helper methods for drawing the Chronomagus Regum watch face

    private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw outer circle (case) - very thin to represent the ultra-thin profile
        canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)

        // Draw inner circle (face)
        canvas.drawCircle(centerX, centerY, radius - 2f, clockFacePaint)
    }

    private fun drawHourMarkers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Chronomagus Regum typically has very minimalist hour markers
        // Often just simple thin lines or small dots

        for (i in 0 until 12) {
            val angle = PI / 6 * i

            // For 3, 6, 9, and 12 o'clock, use slightly longer markers
            val markerLength = if (i % 3 == 0) radius * 0.05f else radius * 0.03f
            val markerWidth = if (i % 3 == 0) 1.5f else 1f

            val startX = centerX + cos(angle).toFloat() * (radius * 0.85f)
            val startY = centerY + sin(angle).toFloat() * (radius * 0.85f)
            val endX = centerX + cos(angle).toFloat() * (radius * 0.85f - markerLength)
            val endY = centerY + sin(angle).toFloat() * (radius * 0.85f - markerLength)

            // Draw minimalist markers
            markersPaint.strokeWidth = markerWidth
            canvas.drawLine(startX, startY, endX, endY, markersPaint)
        }

        // Add small dots at each hour position for a more refined look
        for (i in 0 until 12) {
            val angle = PI / 6 * i
            val dotRadius = if (i % 3 == 0) 1.5f else 1f

            val dotX = centerX + cos(angle).toFloat() * (radius * 0.9f)
            val dotY = centerY + sin(angle).toFloat() * (radius * 0.9f)

            canvas.drawCircle(dotX, dotY, dotRadius, markerDotPaint)
        }
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Get current time with the current timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand - very thin and elegant
        val hourAngle = (hour * 30 + minute * 0.5f) * Math.PI / 180
        val hourEndX = centerX + sin(hourAngle).toFloat() * radius * 0.5f
        val hourEndY = centerY - cos(hourAngle).toFloat() * radius * 0.5f
        canvas.drawLine(centerX, centerY, hourEndX, hourEndY, hourHandPaint)

        // Minute hand - longer and equally thin
        val minuteAngle = minute * 6f * Math.PI / 180
        val minuteEndX = centerX + sin(minuteAngle).toFloat() * radius * 0.7f
        val minuteEndY = centerY - cos(minuteAngle).toFloat() * radius * 0.7f
        canvas.drawLine(centerX, centerY, minuteEndX, minuteEndY, minuteHandPaint)

        // Second hand - extremely thin
        val secondAngle = second * 6f * Math.PI / 180
        val secondEndX = centerX + sin(secondAngle).toFloat() * radius * 0.8f
        val secondEndY = centerY - cos(secondAngle).toFloat() * radius * 0.8f
        canvas.drawLine(centerX, centerY, secondEndX, secondEndY, secondHandPaint)
    }

    private fun drawLogo(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw "CHRONOMAGUS" text
        canvas.drawText(
            "CHRONOMAGUS",
            centerX,
            centerY - radius * 0.3f,
            logoPaint
        )

        // Draw "REGIUM" text
        canvas.drawText(
            "REGIUM",
            centerX,
            centerY - radius * 0.2f,
            modelPaint
        )

        // Draw "Fabricatum Romae" text
        canvas.drawText(
            "Fabricatum Romae",
            centerX,
            centerY + radius * 0.5f,
            swissMadePaint
        )

        // Draw "ULTRA-THIN" text
        canvas.drawText(
            "ULTRA-THIN",
            centerX,
            centerY + radius * 0.2f,
            ultraThinPaint
        )
    }
}