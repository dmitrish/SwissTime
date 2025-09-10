package com.coroutines.livewallpaper.watches

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
import com.coroutines.livewallpaper.common.BaseClock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A class that handles rendering the Aventinus Classique watch face.
 */
class AventinusClassiqueClock(private val context: Context, private val handler: Handler) : BaseClock {

    // Color definitions for the Aventinus Classique watch face
    private val clockFaceColor = Color.parseColor("#F5F5F0") // Off-white dial
    private val clockBorderColor = Color.parseColor("#D4AF37") // Gold border
    private val hourHandColor = Color.parseColor("#000080") // Blue hour hand (blued steel)
    private val minuteHandColor = Color.parseColor("#000080") // Blue minute hand
    private val secondHandColor = Color.parseColor("#000080") // Blue second hand
    private val markersColor = Color.parseColor("#000000") // Black markers
    private val numbersColor = Color.parseColor("#000000") // Black roman numerals
    private val accentColor = Color.parseColor("#D4AF37") // Gold accent
    private val guillocheColor = Color.parseColor("#EEEEE0") // Light cream for guilloche pattern

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
        strokeWidth = 1f
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

    private val flutePaint = Paint().apply {
        color = clockBorderColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val markersPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val numbersPaint = Paint().apply {
        color = numbersColor
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }

    private val logoPaint = Paint().apply {
        color = numbersColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
    }

    private val signaturePaint = Paint().apply {
        color = numbersColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
    }

    private val centerDotPaint = Paint().apply {
        color = accentColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val guillochePaint = Paint().apply {
        color = guillocheColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
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
        val radius = min(width, width) / 2 * 0.8f
        
        numbersPaint.textSize = radius * 0.15f
        logoPaint.textSize = radius * 0.1f
        signaturePaint.textSize = radius * 0.06f
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

        // Draw Aventinus Classique watch face
        drawClockFace(canvas, centerX, centerY, radius)
        drawHourMarkersAndNumbers(canvas, centerX, centerY, radius)
        drawGuillochePattern(canvas, centerX, centerY, radius)
        drawClockHands(canvas, centerX, centerY, radius)
        drawLogo(canvas, centerX, centerY, radius)

        // Draw center dot
        canvas.drawCircle(centerX, centerY, radius * 0.02f, centerDotPaint)
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

    // Helper methods for drawing the Aventinus Classique watch face

    private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw the outer border (fluted bezel)
        canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)
        
        // Draw the inner fluted pattern
        val flutesCount = 60
        for (i in 0 until flutesCount) {
            val angle = (i * 360f / flutesCount) * (Math.PI / 180f)
            val outerRadius = radius * 0.96f
            val innerRadius = radius * 0.92f
            
            val startX = centerX + cos(angle).toFloat() * innerRadius
            val startY = centerY + sin(angle).toFloat() * innerRadius
            val endX = centerX + cos(angle).toFloat() * outerRadius
            val endY = centerY + sin(angle).toFloat() * outerRadius
            
            canvas.drawLine(startX, startY, endX, endY, flutePaint)
        }
        
        // Draw the main face
        canvas.drawCircle(centerX, centerY, radius * 0.9f, clockFacePaint)
    }

    private fun drawHourMarkersAndNumbers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // AVENTINUS is known for its elegant roman numerals
        val romanNumerals = listOf("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")
        
        // Draw Roman numerals
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock
            val numberRadius = radius * 0.75f
            
            val numberX = centerX + cos(angle).toFloat() * numberRadius
            val numberY = centerY + sin(angle).toFloat() * numberRadius + numbersPaint.textSize / 3
            
            // Draw roman numerals
            canvas.drawText(
                romanNumerals[i],
                numberX,
                numberY,
                numbersPaint
            )
            
            // Draw minute markers
            for (j in 0 until 5) {
                val minuteAngle = Math.PI / 30 * (i * 5 + j) - Math.PI / 2
                val innerRadius = radius * 0.85f
                val outerRadius = radius * 0.88f
                
                val startX = centerX + cos(minuteAngle).toFloat() * innerRadius
                val startY = centerY + sin(minuteAngle).toFloat() * innerRadius
                val endX = centerX + cos(minuteAngle).toFloat() * outerRadius
                val endY = centerY + sin(minuteAngle).toFloat() * outerRadius
                
                canvas.drawLine(startX, startY, endX, endY, markersPaint)
            }
        }
    }

    private fun drawGuillochePattern(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // AVENTINUS is famous for its guilloche patterns
        val guillocheRadius = radius * 0.6f
        val circleCount = 15
        val circleSpacing = guillocheRadius / circleCount
        
        for (i in 1..circleCount) {
            canvas.drawCircle(
                centerX,
                centerY,
                guillocheRadius - (i * circleSpacing),
                guillochePaint
            )
        }
        
        // Add cross-hatching
        for (angle in 0 until 360 step 10) {
            val radians = angle * Math.PI / 180
            val startX = centerX + cos(radians).toFloat() * (radius * 0.1f)
            val startY = centerY + sin(radians).toFloat() * (radius * 0.1f)
            val endX = centerX + cos(radians).toFloat() * guillocheRadius
            val endY = centerY + sin(radians).toFloat() * guillocheRadius
            
            canvas.drawLine(startX, startY, endX, endY, guillochePaint)
        }
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Get current time with the current timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand - AVENTINUS-style with hollow moon tip
        val hourAngle = (hour * 30 + minute * 0.5f) * Math.PI / 180
        
        // Save canvas state
        canvas.save()
        
        // Rotate canvas for hour hand
        canvas.rotate(
            (hourAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )
        
        // Draw hour hand
        val hourHandPath = Path()
        hourHandPath.moveTo(centerX, centerY - radius * 0.5f) // Tip
        hourHandPath.quadTo(
            centerX + radius * 0.03f, centerY - radius * 0.48f,
            centerX + radius * 0.02f, centerY - radius * 0.45f
        )
        hourHandPath.lineTo(centerX + radius * 0.02f, centerY)
        hourHandPath.lineTo(centerX - radius * 0.02f, centerY)
        hourHandPath.lineTo(centerX - radius * 0.02f, centerY - radius * 0.45f)
        hourHandPath.quadTo(
            centerX - radius * 0.03f, centerY - radius * 0.48f,
            centerX, centerY - radius * 0.5f
        )
        hourHandPath.close()
        
        canvas.drawPath(hourHandPath, hourHandPaint)
        
        // Restore canvas state
        canvas.restore()

        // Minute hand - AVENTINUS-style with hollow moon tip
        val minuteAngle = minute * 6f * Math.PI / 180
        
        // Save canvas state
        canvas.save()
        
        // Rotate canvas for minute hand
        canvas.rotate(
            (minuteAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )
        
        // Draw minute hand
        val minuteHandPath = Path()
        minuteHandPath.moveTo(centerX, centerY - radius * 0.7f) // Tip
        minuteHandPath.quadTo(
            centerX + radius * 0.025f, centerY - radius * 0.68f,
            centerX + radius * 0.015f, centerY - radius * 0.65f
        )
        minuteHandPath.lineTo(centerX + radius * 0.015f, centerY)
        minuteHandPath.lineTo(centerX - radius * 0.015f, centerY)
        minuteHandPath.lineTo(centerX - radius * 0.015f, centerY - radius * 0.65f)
        minuteHandPath.quadTo(
            centerX - radius * 0.025f, centerY - radius * 0.68f,
            centerX, centerY - radius * 0.7f
        )
        minuteHandPath.close()
        
        canvas.drawPath(minuteHandPath, minuteHandPaint)
        
        // Restore canvas state
        canvas.restore()

        // Second hand - thin with counterbalance
        val secondAngle = second * 6f * Math.PI / 180
        
        // Save canvas state
        canvas.save()
        
        // Rotate canvas for second hand
        canvas.rotate(
            (secondAngle * 180 / Math.PI).toFloat(),
            centerX,
            centerY
        )
        
        // Main hand
        canvas.drawLine(
            centerX,
            centerY + radius * 0.2f,
            centerX,
            centerY - radius * 0.8f,
            secondHandPaint
        )
        
        // Counterbalance
        canvas.drawCircle(
            centerX,
            centerY + radius * 0.15f,
            radius * 0.03f,
            secondHandFillPaint
        )
        
        // Restore canvas state
        canvas.restore()
    }

    private fun drawLogo(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw "AVENTINUS" text
        canvas.drawText(
            "AVENTINUS",
            centerX,
            centerY - radius * 0.3f,
            logoPaint
        )
        
        // Draw signature (Aventinus watches often have a secret signature)
        canvas.drawText(
            "No. 1947",
            centerX,
            centerY + radius * 0.4f,
            signaturePaint
        )
    }
}