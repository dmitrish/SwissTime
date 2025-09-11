package com.coroutines.livewallpaper.watches

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
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
import kotlin.random.Random

/**
 * A class that handles rendering the Knot watch face with Urushi lacquer dial.
 */
class KnotClock(private val context: Context, private val handler: Handler) : BaseClock {

    // Color definitions for the Knot watch face
    private val urushiBlackColor = Color.parseColor("#000000") // Deep jet black for the Urushi lacquer dial
    private val goldPowderColor = Color.parseColor("#D4AF37") // Gold powder accent
    private val goldPowderHighlightColor = Color.parseColor("#FFD700") // Brighter gold for highlights
    private val silverCaseColor = Color.parseColor("#E0E0E0") // Silver polished case
    private val silverHandsColor = Color.parseColor("#E0E0E0") // Silver hands
    private val markersColor = Color.parseColor("#F5F5F5") // Bright silver hour markers for better contrast
    private val logoColor = Color.parseColor("#E0E0E0") // Silver for the logo

    // Paint objects for the watch face
    private val hourHandPaint = Paint().apply {
        color = silverHandsColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val minuteHandPaint = Paint().apply {
        color = silverHandsColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val secondHandPaint = Paint().apply {
        color = silverHandsColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1f
        strokeCap = Paint.Cap.ROUND
    }

    private val secondHandFillPaint = Paint().apply {
        color = silverHandsColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val clockFacePaint = Paint().apply {
        color = urushiBlackColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val clockBorderPaint = Paint().apply {
        color = silverCaseColor
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private val markersPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val markerHighlightPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val markerShadowPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        alpha = 128 // 50% opacity
    }

    private val dateBgPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val dateBorderPaint = Paint().apply {
        color = silverCaseColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val datePaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val logoPaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val subtitlePaint = Paint().apply {
        color = logoColor
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
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

    // Pre-generate gold powder particles for consistent pattern
    private val goldParticles = generateGoldParticles(500)

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
        val timeTextSize = width * 0.12f
        val logoTextSize = width * 0.06f
        val subtitleTextSize = width * 0.03f
        val dateTextSize = width * 0.04f

        timePaint.textSize = timeTextSize
        logoPaint.textSize = logoTextSize
        subtitlePaint.textSize = subtitleTextSize
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

        // Update stroke widths based on radius
        clockBorderPaint.strokeWidth = radius * 0.05f

        // Draw Knot watch face
        drawClockFace(canvas, centerX, centerY, radius)
        drawHourMarkers(canvas, centerX, centerY, radius)
        drawUrushiTexture(canvas, centerX, centerY, radius)
        drawClockHands(canvas, centerX, centerY, radius)

        // Draw center dot
        canvas.drawCircle(centerX, centerY, radius * 0.02f, secondHandFillPaint)

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

    // Helper methods for drawing the Knot watch face

    private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw the outer silver case
        canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)

        // Draw the main face with deep black Urushi lacquer
        canvas.drawCircle(centerX, centerY, radius * 0.95f, clockFacePaint)
    }

    private fun drawHourMarkers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Knot watches typically have applied hour markers that are more substantial
        for (i in 0 until 12) {
            val angle = Math.PI / 6 * i - Math.PI / 2 // Start at 12 o'clock

            // Position for the marker
            val markerRadius = radius * 0.85f
            val markerX = centerX + cos(angle).toFloat() * markerRadius
            val markerY = centerY + sin(angle).toFloat() * markerRadius

            // Save canvas state
            canvas.save()

            // Rotate canvas for marker
            canvas.rotate(
                (angle * 180 / Math.PI).toFloat() + 90,
                markerX,
                markerY
            )

            if (i % 3 == 0) {
                // Major markers (12, 3, 6, 9) - larger rectangular markers
                val markerWidth = radius * 0.05f
                val markerHeight = radius * 0.14f

                // Skip 3 o'clock where the date window is
                if (i != 3) {
                    // Draw main marker
                    val markerRect = RectF(
                        markerX - markerWidth / 2,
                        markerY - markerHeight / 2,
                        markerX + markerWidth / 2,
                        markerY + markerHeight / 2
                    )
                    canvas.drawRect(markerRect, markersPaint)

                    // Add highlight on top/left edge
                    canvas.drawLine(
                        markerX - markerWidth / 2,
                        markerY - markerHeight / 2,
                        markerX + markerWidth / 2,
                        markerY - markerHeight / 2,
                        markerHighlightPaint
                    )
                    canvas.drawLine(
                        markerX - markerWidth / 2,
                        markerY - markerHeight / 2,
                        markerX - markerWidth / 2,
                        markerY + markerHeight / 2,
                        markerHighlightPaint
                    )

                    // Add shadow on bottom/right edge
                    canvas.drawLine(
                        markerX - markerWidth / 2,
                        markerY + markerHeight / 2,
                        markerX + markerWidth / 2,
                        markerY + markerHeight / 2,
                        markerShadowPaint
                    )
                    canvas.drawLine(
                        markerX + markerWidth / 2,
                        markerY - markerHeight / 2,
                        markerX + markerWidth / 2,
                        markerY + markerHeight / 2,
                        markerShadowPaint
                    )
                }
            } else {
                // Minor markers - smaller rectangular markers
                val markerWidth = radius * 0.04f
                val markerHeight = radius * 0.1f

                // Draw main marker
                val markerRect = RectF(
                    markerX - markerWidth / 2,
                    markerY - markerHeight / 2,
                    markerX + markerWidth / 2,
                    markerY + markerHeight / 2
                )
                canvas.drawRect(markerRect, markersPaint)

                // Add highlight on top/left edge
                canvas.drawLine(
                    markerX - markerWidth / 2,
                    markerY - markerHeight / 2,
                    markerX + markerWidth / 2,
                    markerY - markerHeight / 2,
                    markerHighlightPaint
                )
                canvas.drawLine(
                    markerX - markerWidth / 2,
                    markerY - markerHeight / 2,
                    markerX - markerWidth / 2,
                    markerY + markerHeight / 2,
                    markerHighlightPaint
                )

                // Add shadow on bottom/right edge
                canvas.drawLine(
                    markerX - markerWidth / 2,
                    markerY + markerHeight / 2,
                    markerX + markerWidth / 2,
                    markerY + markerHeight / 2,
                    markerShadowPaint
                )
                canvas.drawLine(
                    markerX + markerWidth / 2,
                    markerY - markerHeight / 2,
                    markerX + markerWidth / 2,
                    markerY + markerHeight / 2,
                    markerShadowPaint
                )
            }

            // Restore canvas state
            canvas.restore()
        }

        // Draw date window at 3 o'clock position
        val dateX = centerX + radius * 0.6f
        val dateY = centerY

        // Date window with white background
        val dateRect = RectF(
            dateX - radius * 0.08f,
            dateY - radius * 0.06f,
            dateX + radius * 0.08f,
            dateY + radius * 0.06f
        )
        canvas.drawRect(dateRect, dateBgPaint)

        // Add a thin silver border around the date window
        canvas.drawRect(dateRect, dateBorderPaint)

        // Date text
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        canvas.drawText(
            day,
            dateX,
            dateY + radius * 0.03f,
            datePaint
        )
    }

    private fun drawUrushiTexture(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw pre-generated gold powder particles
        for (particle in goldParticles) {
            val x = centerX + particle.x * radius
            val y = centerY + particle.y * radius

            val particlePaint = Paint().apply {
                color = particle.color
                isAntiAlias = true
                style = Paint.Style.FILL
                alpha = particle.alpha
            }

            canvas.drawCircle(x, y, particle.size, particlePaint)
        }

        // Add subtle radial gradient to simulate the depth of Urushi lacquer
        val gradientPaint = Paint().apply {
            shader = RadialGradient(
                centerX, centerY, radius * 0.95f,
                Color.parseColor("#050505"), urushiBlackColor,
                Shader.TileMode.CLAMP
            )
            isAntiAlias = true
            style = Paint.Style.FILL
            alpha = 178 // 70% opacity
        }

        canvas.drawCircle(centerX, centerY, radius * 0.95f, gradientPaint)
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Get current time with the current timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand - simple silver hand with slight taper
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
        hourHandPath.moveTo(centerX, centerY - radius * 0.45f) // Tip
        hourHandPath.lineTo(centerX + radius * 0.03f, centerY - radius * 0.1f) // Right edge
        hourHandPath.lineTo(centerX + radius * 0.015f, centerY) // Right base
        hourHandPath.lineTo(centerX - radius * 0.015f, centerY) // Left base
        hourHandPath.lineTo(centerX - radius * 0.03f, centerY - radius * 0.1f) // Left edge
        hourHandPath.close()

        canvas.drawPath(hourHandPath, hourHandPaint)

        // Restore canvas state
        canvas.restore()

        // Minute hand - simple silver hand with slight taper
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
        minuteHandPath.lineTo(centerX + radius * 0.02f, centerY - radius * 0.1f) // Right edge
        minuteHandPath.lineTo(centerX + radius * 0.01f, centerY) // Right base
        minuteHandPath.lineTo(centerX - radius * 0.01f, centerY) // Left base
        minuteHandPath.lineTo(centerX - radius * 0.02f, centerY - radius * 0.1f) // Left edge
        minuteHandPath.close()

        canvas.drawPath(minuteHandPath, minuteHandPaint)

        // Restore canvas state
        canvas.restore()

        // Second hand - thin silver with counterbalance
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
            centerY + radius * 0.2f,
            centerX,
            centerY - radius * 0.75f,
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
        // Draw "KNOT" text
        canvas.drawText(
            "KNOT",
            centerX,
            centerY - radius * 0.3f,
            logoPaint
        )

        // Draw "URUSHI" text
        canvas.drawText(
            "URUSHI",
            centerX,
            centerY + radius * 0.4f,
            subtitlePaint
        )
    }

    // Helper class for gold powder particles
    private data class GoldParticle(
        val x: Float,
        val y: Float,
        val size: Float,
        val color: Int,
        val alpha: Int
    )

    // Generate gold powder particles with a fixed random seed for consistency
    private fun generateGoldParticles(count: Int): List<GoldParticle> {
        val random = Random(0) // Fixed seed for consistent pattern
        val particles = mutableListOf<GoldParticle>()

        for (i in 0 until count) {
            // Random position within the dial
            val angle = random.nextDouble(0.0, 2 * Math.PI)
            val distance = random.nextDouble(0.0, 0.85)

            val x = (cos(angle) * distance).toFloat()
            val y = (sin(angle) * distance).toFloat()

            // Random size for gold particles
            val particleSize = random.nextFloat() * 1.5f + 0.5f

            // Random gold shade
            val goldShade = if (random.nextFloat() > 0.7f) {
                goldPowderHighlightColor
            } else {
                goldPowderColor
            }

            // Random alpha
            val alpha = (random.nextFloat() * 128 + 25).toInt() // 10-60% opacity

            particles.add(GoldParticle(x, y, particleSize, goldShade, alpha))
        }

        return particles
    }
}