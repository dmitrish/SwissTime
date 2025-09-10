package com.coroutines.livewallpaper.watches

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import com.coroutines.livewallpaper.common.BaseClock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A class that handles rendering the Zeitwerk watch face.
 */
class ZeitwerkClock(private val context: Context, private val handler: Handler) : BaseClock {

    // Color definitions for the Zeitwerk watch face
    private val clockFaceColor = Color.parseColor("#1A3A5A") // Deep Atlantic blue dial
    private val clockBorderColor = Color.parseColor("#D0D0D0") // Silver stainless steel border
    private val hourHandColor = Color.parseColor("#E0E0E0") // Silver hour hand
    private val minuteHandColor = Color.parseColor("#E0E0E0") // Silver minute hand
    private val secondHandColor = Color.parseColor("#E63946") // Red second hand
    private val markersColor = Color.parseColor("#FFFFFF") // White markers
    private val lumeColor = Color.parseColor("#90EE90") // Light green lume for hands and markers
    private val centerDotColor = Color.parseColor("#E0E0E0") // Silver center dot

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
        strokeWidth = 2f
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
        strokeWidth = 8f
    }

    private val markersPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val lumePaint = Paint().apply {
        color = lumeColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val centerDotPaint = Paint().apply {
        color = centerDotColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val logoPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val locationPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val modelPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val subModelPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val datePaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
        val logoTextSize = width * 0.12f
        val locationTextSize = width * 0.06f
        val modelTextSize = width * 0.08f
        val subModelTextSize = width * 0.06f
        val dateTextSize = width * 0.08f

        timePaint.textSize = timeTextSize
        logoPaint.textSize = logoTextSize
        locationPaint.textSize = locationTextSize
        modelPaint.textSize = modelTextSize
        subModelPaint.textSize = subModelTextSize
        datePaint.textSize = dateTextSize
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

        // Draw Zeitwerk watch face
        drawClockFace(canvas, centerX, centerY, radius)
        drawHourMarkersAndNumbers(canvas, centerX, centerY, radius)
        drawClockHands(canvas, centerX, centerY, radius)

        // Draw center dot
        canvas.drawCircle(centerX, centerY, radius * 0.03f, centerDotPaint)
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

    // Helper methods for drawing the Zeitwerk watch face

    private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw outer circle (border) - stainless steel case
        canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)

        // Draw inner circle (face) - Atlantic blue dial
        canvas.drawCircle(centerX, centerY, radius * 0.95f, clockFacePaint)

        // Draw "Zeitwerk" text
        canvas.drawText(
            "Zeitwerk",
            centerX,
            centerY - radius * 0.15f,
            logoPaint
        )

        // Draw "Alpenglühen" text
        canvas.drawText(
            "Alpenglühen",
            centerX,
            centerY - radius * 0.05f,
            locationPaint
        )

        // Draw "ZEIT" text
        canvas.drawText(
            "ZEIT",
            centerX,
            centerY + radius * 0.6f,
            modelPaint
        )

        // Draw "AUTOMATIC" text
        canvas.drawText(
            "AUTOMATIC",
            centerX,
            centerY + radius * 0.8f,
            subModelPaint
        )

        // Draw date window at 6 o'clock
        val dateAngle = Math.PI * 1.5 // 6 o'clock
        val dateX = centerX + cos(dateAngle).toFloat() * radius * 0.7f
        val dateY = centerY + sin(dateAngle).toFloat() * radius * 0.7f

        // Date window - rectangular with rounded corners
        val dateRect = RectF(
            dateX - radius * 0.08f,
            dateY - radius * 0.06f,
            dateX + radius * 0.08f,
            dateY + radius * 0.06f
        )

        val dateWindowPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        canvas.drawRoundRect(dateRect, radius * 0.01f, radius * 0.01f, dateWindowPaint)

        // Date text
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        canvas.drawText(
            day,
            dateX,
            dateY + radius * 0.035f,
            datePaint
        )
    }

    private fun drawHourMarkersAndNumbers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Zeitwerk uses simple line markers for hours
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i

            // Skip 6 o'clock where the date window is
            if (i == 6) continue

            val markerLength = if (i % 3 == 0) radius * 0.1f else radius * 0.05f // Longer at 12, 3, 9
            val markerWidth = if (i % 3 == 0) radius * 0.02f else radius * 0.01f // Thicker at 12, 3, 9

            val outerX = centerX + cos(angle).toFloat() * radius * 0.85f
            val outerY = centerY + sin(angle).toFloat() * radius * 0.85f
            val innerX = centerX + cos(angle).toFloat() * (radius * 0.85f - markerLength)
            val innerY = centerY + sin(angle).toFloat() * (radius * 0.85f - markerLength)

            // Draw hour marker
            markersPaint.strokeWidth = markerWidth
            canvas.drawLine(innerX, innerY, outerX, outerY, markersPaint)

            // Add lume dot at the end of the marker
            if (i % 3 == 0) {
                canvas.drawCircle(outerX, outerY, markerWidth * 0.8f, lumePaint)
            }
        }

        // Draw minute markers (smaller lines)
        for (i in 0 until 60) {
            // Skip positions where hour markers are
            if (i % 5 == 0) continue

            val angle = Math.PI * 2 * i / 60
            val markerLength = radius * 0.02f

            val outerX = centerX + cos(angle).toFloat() * radius * 0.85f
            val outerY = centerY + sin(angle).toFloat() * radius * 0.85f
            val innerX = centerX + cos(angle).toFloat() * (radius * 0.85f - markerLength)
            val innerY = centerY + sin(angle).toFloat() * (radius * 0.85f - markerLength)

            // Draw minute marker
            markersPaint.strokeWidth = radius * 0.005f
            canvas.drawLine(innerX, innerY, outerX, outerY, markersPaint)
        }
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Get current time with the current timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand - straight with lume
        val hourAngle = (hour * 30 + minute * 0.5f) * Math.PI / 180

        // Save canvas state
        canvas.save()

        // Rotate canvas for hour hand
        canvas.rotate(
            (hourAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )

        // Draw hour hand - straight and thin
        val hourHandRect = RectF(
            centerX - radius * 0.02f,
            centerY - radius * 0.5f,
            centerX + radius * 0.02f,
            centerY
        )
        canvas.drawRect(hourHandRect, hourHandPaint)

        // Lume on hour hand tip
        canvas.drawCircle(centerX, centerY - radius * 0.45f, radius * 0.03f, lumePaint)

        // Restore canvas state
        canvas.restore()

        // Minute hand - longer and thinner
        val minuteAngle = minute * 6f * Math.PI / 180

        // Save canvas state
        canvas.save()

        // Rotate canvas for minute hand
        canvas.rotate(
            (minuteAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )

        // Draw minute hand - straight and thin
        val minuteHandRect = RectF(
            centerX - radius * 0.015f,
            centerY - radius * 0.7f,
            centerX + radius * 0.015f,
            centerY
        )
        canvas.drawRect(minuteHandRect, minuteHandPaint)

        // Lume on minute hand tip
        canvas.drawCircle(centerX, centerY - radius * 0.65f, radius * 0.025f, lumePaint)

        // Restore canvas state
        canvas.restore()

        // Second hand - thin red with distinctive circle near tip
        val secondAngle = second * 6f * Math.PI / 180

        // Save canvas state
        canvas.save()

        // Rotate canvas for second hand
        canvas.rotate(
            (secondAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )

        // Draw main second hand
        canvas.drawLine(
            centerX,
            centerY + radius * 0.15f,
            centerX,
            centerY - radius * 0.75f,
            secondHandPaint
        )

        // Distinctive circle near tip
        canvas.drawCircle(centerX, centerY - radius * 0.65f, radius * 0.03f, secondHandFillPaint)

        // Counterbalance
        canvas.drawCircle(centerX, centerY + radius * 0.1f, radius * 0.02f, secondHandFillPaint)

        // Restore canvas state
        canvas.restore()
    }
}