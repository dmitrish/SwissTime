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
import kotlin.math.sqrt

/**
 * A class that handles rendering the Roma Marina watch face.
 */
class RomaMarinaClock(private val context: Context, private val handler: Handler) : BaseClock {

    // Color definitions for the Roma Marina watch face
    private val clockFaceColor = Color.parseColor("#2F4F4F") // Dark slate gray dial (hobnail pattern)
    private val clockBorderColor = Color.parseColor("#C0C0C0") // Silver border
    private val hourHandColor = Color.parseColor("#E0E0E0") // Light silver hour hand
    private val minuteHandColor = Color.parseColor("#E0E0E0") // Light silver minute hand
    private val secondHandColor = Color.parseColor("#FF4500") // Orange-red second hand
    private val markersColor = Color.parseColor("#E0E0E0") // Light silver markers
    private val numbersColor = Color.parseColor("#E0E0E0") // Light silver numbers
    private val centerDotColor = Color.parseColor("#E0E0E0") // Light silver center dot

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
        style = Paint.Style.FILL
    }

    private val markersPaint = Paint().apply {
        color = markersColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val markerHighlightPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val brandTextPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val modelTextPaint = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
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
        val dateTextSize = width * 0.08f
        val brandTextSize = width * 0.1f
        val modelTextSize = width * 0.07f

        timePaint.textSize = timeTextSize
        datePaint.textSize = dateTextSize
        brandTextPaint.textSize = brandTextSize
        modelTextPaint.textSize = modelTextSize
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
        val centerY = height / 1.9f
        val radius = min(width, height) / 2 * 0.8f

        // Draw Roma Marina watch face
        drawClockFace(canvas, centerX, centerY, radius)
        drawHourMarkersAndNumbers(canvas, centerX, centerY, radius)
        drawDateWindow(canvas, centerX, centerY, radius)
        drawClockHands(canvas, centerX, centerY, radius)

        // Draw center dot
        canvas.drawCircle(centerX, centerY, radius * 0.03f, Paint().apply {
            color = centerDotColor
            isAntiAlias = true
            style = Paint.Style.FILL
        })
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

    // Helper methods for drawing the Roma Marina watch face

    private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw octagonal bezel
        val octagonPath = Path()
        for (i in 0 until 8) {
            val angle = Math.PI / 4 * i
            val x = centerX + cos(angle).toFloat() * radius
            val y = centerY + sin(angle).toFloat() * radius

            if (i == 0) {
                octagonPath.moveTo(x, y)
            } else {
                octagonPath.lineTo(x, y)
            }
        }
        octagonPath.close()
        canvas.drawPath(octagonPath, clockBorderPaint)

        // Draw inner octagonal face
        val innerOctagonPath = Path()
        val innerRadius = radius * 0.92f
        for (i in 0 until 8) {
            val angle = Math.PI / 4 * i
            val x = centerX + cos(angle).toFloat() * innerRadius
            val y = centerY + sin(angle).toFloat() * innerRadius

            if (i == 0) {
                innerOctagonPath.moveTo(x, y)
            } else {
                innerOctagonPath.lineTo(x, y)
            }
        }
        innerOctagonPath.close()
        canvas.drawPath(innerOctagonPath, clockFacePaint)

        // Draw hobnail pattern
        val patternRadius = radius * 0.8f
        val gridSize = 12 // Number of squares in each direction
        val squareSize = patternRadius * 2 / gridSize

        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val x = centerX - patternRadius + i * squareSize
                val y = centerY - patternRadius + j * squareSize

                // Skip squares outside the circle
                val distanceFromCenter = sqrt(
                    (x + squareSize / 2 - centerX).pow(2) +
                            (y + squareSize / 2 - centerY).pow(2)
                )
                if (distanceFromCenter > patternRadius) continue

                // Draw raised pyramid effect for hobnail pattern
                val pyramidPath = Path()
                // Base square
                pyramidPath.moveTo(x, y)
                pyramidPath.lineTo(x + squareSize, y)
                pyramidPath.lineTo(x + squareSize, y + squareSize)
                pyramidPath.lineTo(x, y + squareSize)
                pyramidPath.close()

                // Draw with slight highlight to create 3D effect
                canvas.drawPath(pyramidPath, Paint().apply {
                    color = clockFaceColor
                    alpha = 204 // 0.8f * 255
                    isAntiAlias = true
                    style = Paint.Style.FILL
                })

                // Draw highlight on top-left
                val highlightPath = Path()
                highlightPath.moveTo(x, y)
                highlightPath.lineTo(x + squareSize, y)
                highlightPath.lineTo(x + squareSize/2, y + squareSize/2)
                highlightPath.close()

                canvas.drawPath(highlightPath, Paint().apply {
                    color = clockFaceColor
                    alpha = 153 // 0.6f * 255
                    isAntiAlias = true
                    style = Paint.Style.FILL
                })

                // Draw shadow on bottom-right
                val shadowPath = Path()
                shadowPath.moveTo(x + squareSize, y)
                shadowPath.lineTo(x + squareSize, y + squareSize)
                shadowPath.lineTo(x + squareSize/2, y + squareSize/2)
                shadowPath.close()

                canvas.drawPath(shadowPath, Paint().apply {
                    color = clockFaceColor
                    alpha = 255 // 1.0f * 255
                    isAntiAlias = true
                    style = Paint.Style.FILL
                })
            }
        }

        // Draw brand text
        brandTextPaint.textSize = radius * 0.1f
        canvas.drawText(
            "ROMA-MARINA",
            centerX,
            centerY - radius * 0.3f,
            brandTextPaint
        )

        // Draw model text
        modelTextPaint.textSize = radius * 0.07f
        canvas.drawText(
            "MILITARE",
            centerX,
            centerY - radius * 0.15f,
            modelTextPaint
        )
    }

    private fun drawHourMarkersAndNumbers(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        for (i in 0 until 12) {
            // Skip 3 o'clock where the date window is
            if (i == 3) continue

            val angle = Math.PI / 6 * i
            val markerLength = radius * 0.1f
            val markerWidth = radius * 0.02f

            val markerX = centerX + cos(angle).toFloat() * radius * 0.7f
            val markerY = centerY + sin(angle).toFloat() * radius * 0.7f

            // Save canvas state
            canvas.save()

            // Rotate canvas to draw rectangular marker
            canvas.rotate(
                i * 30f,
                markerX,
                markerY
            )

            // Draw rectangular marker with 3D effect
            // Main marker
            canvas.drawRect(
                markerX - markerWidth / 2,
                markerY - markerLength / 2,
                markerX + markerWidth / 2,
                markerY + markerLength / 2,
                markersPaint
            )

            // Highlight for 3D effect
            canvas.drawRect(
                markerX - markerWidth / 2 + 1f,
                markerY - markerLength / 2 + 1f,
                markerX,
                markerY + markerLength / 2 - 1f,
                markerHighlightPaint
            )

            // Restore canvas state
            canvas.restore()
        }

        // Special double marker at 12 o'clock
        val angle12 = Math.PI * 1.5 // 12 o'clock
        val marker12X = centerX + cos(angle12).toFloat() * radius * 0.7f
        val marker12Y = centerY + sin(angle12).toFloat() * radius * 0.7f
        val markerWidth = radius * 0.02f
        val markerLength = radius * 0.1f
        val markerGap = radius * 0.01f

        // Left marker at 12
        canvas.drawRect(
            marker12X - markerWidth * 1.5f - markerGap/2,
            marker12Y - markerLength / 2,
            marker12X - markerGap/2,
            marker12Y + markerLength / 2,
            markersPaint
        )

        // Right marker at 12
        canvas.drawRect(
            marker12X + markerGap/2,
            marker12Y - markerLength / 2,
            marker12X + markerWidth + markerGap/2,
            marker12Y + markerLength / 2,
            markersPaint
        )
    }

    private fun drawDateWindow(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Draw date window at 3 o'clock
        val dateAngle = Math.PI / 2
        val dateX = centerX + cos(dateAngle).toFloat() * radius * 0.6f
        val dateY = centerY + sin(dateAngle).toFloat() * radius * 0.6f

        // Date window background
        val dateWindowRect = RectF(
            dateX - radius * 0.08f,
            dateY - radius * 0.06f,
            dateX + radius * 0.08f,
            dateY + radius * 0.06f
        )

        // Draw white background
        val dateWindowBackgroundPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        canvas.drawRect(dateWindowRect, dateWindowBackgroundPaint)

        // Draw black border
        val dateWindowBorderPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRect(dateWindowRect, dateWindowBorderPaint)

        // Draw date text
        datePaint.textSize = radius * 0.08f

        // Get current date
        val calendar = Calendar.getInstance()
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        canvas.drawText(
            date.toString(),
            dateX,
            dateY + radius * 0.03f,
            datePaint
        )
    }

    private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        // Get current time with the current timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hour hand - sword-shaped with center groove
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
        hourHandPath.lineTo(centerX + radius * 0.04f, centerY - radius * 0.2f) // Right shoulder
        hourHandPath.lineTo(centerX + radius * 0.02f, centerY) // Right base
        hourHandPath.lineTo(centerX - radius * 0.02f, centerY) // Left base
        hourHandPath.lineTo(centerX - radius * 0.04f, centerY - radius * 0.2f) // Left shoulder
        hourHandPath.close()

        canvas.drawPath(hourHandPath, hourHandPaint)

        // Draw center groove
        canvas.drawLine(
            centerX,
            centerY - radius * 0.45f,
            centerX,
            centerY - radius * 0.05f,
            Paint().apply {
                color = clockFaceColor
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
        )

        // Restore canvas state
        canvas.restore()

        // Minute hand - longer sword-shaped with center groove
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
        minuteHandPath.lineTo(centerX + radius * 0.03f, centerY - radius * 0.2f) // Right shoulder
        minuteHandPath.lineTo(centerX + radius * 0.015f, centerY) // Right base
        minuteHandPath.lineTo(centerX - radius * 0.015f, centerY) // Left base
        minuteHandPath.lineTo(centerX - radius * 0.03f, centerY - radius * 0.2f) // Left shoulder
        minuteHandPath.close()

        canvas.drawPath(minuteHandPath, minuteHandPaint)

        // Draw center groove
        canvas.drawLine(
            centerX,
            centerY - radius * 0.65f,
            centerX,
            centerY - radius * 0.05f,
            Paint().apply {
                color = clockFaceColor
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
        )

        // Restore canvas state
        canvas.restore()

        // Second hand - thin with distinctive arrow tip
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

        // Draw arrow tip
        val arrowSize = radius * 0.04f
        val arrowPath = Path()
        arrowPath.moveTo(centerX, centerY - radius * 0.75f - arrowSize) // Tip
        arrowPath.lineTo(centerX + arrowSize / 2, centerY - radius * 0.75f) // Right corner
        arrowPath.lineTo(centerX - arrowSize / 2, centerY - radius * 0.75f) // Left corner
        arrowPath.close()

        canvas.drawPath(arrowPath, secondHandFillPaint)

        // Draw counterbalance
        canvas.drawCircle(
            centerX,
            centerY + radius * 0.1f,
            radius * 0.03f,
            secondHandFillPaint
        )

        // Restore canvas state
        canvas.restore()
    }

    // Extension function to calculate power of 2
    private fun Float.pow(exponent: Int): Float {
        var result = 1f
        repeat(exponent) { result *= this }
        return result
    }
}