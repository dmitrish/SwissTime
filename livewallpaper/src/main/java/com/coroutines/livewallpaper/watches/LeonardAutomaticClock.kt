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
import java.util.Random
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** A class that handles rendering the Leonard Automatic watch face. */
class LeonardAutomaticClock(private val context: Context, private val handler: Handler) :
  BaseClock {

  // Color definitions for the Leonard Automatic watch face
  private val clockFaceColor = Color.parseColor("#F5F5F5") // Silver-white dial
  private val clockBorderColor = Color.parseColor("#8B4513") // Brown border (leather strap color)
  private val hourHandColor = Color.parseColor("#00008B") // Dark blue hour hand (blued steel)
  private val minuteHandColor = Color.parseColor("#00008B") // Dark blue minute hand
  private val secondHandColor = Color.parseColor("#00008B") // Dark blue second hand
  private val markersColor = Color.parseColor("#000000") // Black markers
  private val numbersColor = Color.parseColor("#000000") // Black numbers
  private val moonphaseColor = Color.parseColor("#000080") // Navy blue moonphase background
  private val moonColor = Color.parseColor("#FFFACD") // Light yellow moon
  private val centerDotColor = Color.parseColor("#00008B") // Dark blue center dot

  // Paint objects for the watch face
  private val hourHandPaint =
    Paint().apply {
      color = hourHandColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val minuteHandPaint =
    Paint().apply {
      color = minuteHandColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val secondHandPaint =
    Paint().apply {
      color = secondHandColor
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      strokeCap = Paint.Cap.ROUND
    }

  private val secondHandFillPaint =
    Paint().apply {
      color = secondHandColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val clockFacePaint =
    Paint().apply {
      color = clockFaceColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val clockBorderPaint =
    Paint().apply {
      color = clockBorderColor
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 6f
    }

  private val markersPaint =
    Paint().apply {
      color = markersColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val numbersPaint =
    Paint().apply {
      color = numbersColor
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }

  private val logoPaint =
    Paint().apply {
      color = Color.BLACK
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      isFakeBoldText = true
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

  private val automaticPaint =
    Paint().apply {
      color = Color.BLACK
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

  private val datePaint =
    Paint().apply {
      color = Color.BLACK
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      isFakeBoldText = true
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

  private val moonphasePaint =
    Paint().apply {
      color = moonphaseColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val moonPaint =
    Paint().apply {
      color = moonColor
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val moonCraterPaint =
    Paint().apply {
      color = moonColor
      isAntiAlias = true
      style = Paint.Style.FILL
      alpha = 179 // 0.7f * 255
    }

  private val moonphaseBorderPaint =
    Paint().apply {
      color = clockBorderColor
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 2f
    }

  private val starPaint =
    Paint().apply {
      color = Color.WHITE
      isAntiAlias = true
      style = Paint.Style.FILL
    }

  private val guillochePaint =
    Paint().apply {
      color = Color.BLACK
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 1f
      alpha = 8 // 0.03f * 255
    }

  private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
  private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())

  private val timeZoneReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        timeFormat.timeZone = TimeZone.getDefault()
        dateFormat.timeZone = TimeZone.getDefault()
      }
    }

  init {
    // Register to receive time zone changes
    val filter =
      IntentFilter().apply {
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
        addAction(Intent.ACTION_TIME_CHANGED)
      }
    context.registerReceiver(timeZoneReceiver, filter)
  }

  /** Updates the text sizes based on the surface dimensions. */
  override fun updateTextSizes(width: Int) {
    val radius = min(width, width) / 2 * 0.8f

    numbersPaint.textSize = radius * 0.12f
    logoPaint.textSize = radius * 0.12f
    automaticPaint.textSize = radius * 0.06f
    datePaint.textSize = radius * 0.08f
  }

  /** Draws the clock on the provided canvas. */
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

    // Draw Leonard Automatic watch face
    drawClockFace(canvas, centerX, centerY, radius)
    drawHourMarkersAndNumbers(canvas, centerX, centerY, radius)
    drawClockHands(canvas, centerX, centerY, radius)

    // Draw center dot
    canvas.drawCircle(
      centerX,
      centerY,
      radius * 0.03f,
      Paint().apply {
        color = centerDotColor
        isAntiAlias = true
        style = Paint.Style.FILL
      }
    )
  }

  /** Cleans up resources when the clock is no longer needed. */
  override fun destroy() {
    try {
      context.unregisterReceiver(timeZoneReceiver)
    } catch (e: IllegalArgumentException) {
      // Receiver not registered
    }
  }

  // Helper methods for drawing the Leonard Automatic watch face

  private fun drawClockFace(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
    // Draw outer circle (border)
    canvas.drawCircle(centerX, centerY, radius, clockBorderPaint)

    // Draw inner circle (face)
    canvas.drawCircle(centerX, centerY, radius - 3f, clockFacePaint)

    // Draw subtle guilloche pattern (concentric circles)
    for (i in 1..8) {
      canvas.drawCircle(centerX, centerY, radius * (0.9f - i * 0.1f), guillochePaint)
    }

    // Draw Léonard logo
    canvas.drawText("LÉONARD", centerX, centerY - radius * 0.3f, logoPaint)

    // Draw "AUTOMATIC" text
    canvas.drawText("AUTOMATIC", centerX, centerY - radius * 0.15f, automaticPaint)

    // Draw moonphase display at 6 o'clock
    val moonphaseY = centerY + radius * 0.4f
    val moonphaseWidth = radius * 0.4f
    val moonphaseHeight = radius * 0.2f

    // Moonphase background (night sky)
    canvas.drawRect(
      centerX - moonphaseWidth / 2,
      moonphaseY - moonphaseHeight / 2,
      centerX + moonphaseWidth / 2,
      moonphaseY + moonphaseHeight / 2,
      moonphasePaint
    )

    // Add stars to the night sky
    val random = Random(1234) // Fixed seed for consistent star pattern
    for (i in 0 until 20) {
      val starX = centerX - moonphaseWidth / 2 + random.nextFloat() * moonphaseWidth
      val starY = moonphaseY - moonphaseHeight / 2 + random.nextFloat() * moonphaseHeight
      val starSize = radius * 0.005f + random.nextFloat() * radius * 0.005f

      canvas.drawCircle(starX, starY, starSize, starPaint)
    }

    // Draw moon (position based on lunar phase)
    // For simplicity, we'll just draw a full moon
    val moonRadius = moonphaseHeight * 0.4f
    val moonX = centerX

    // Full moon
    canvas.drawCircle(moonX, moonphaseY, moonRadius, moonPaint)

    // Add some craters to the moon for detail
    val craters =
      listOf(Triple(0.3f, 0.2f, 0.1f), Triple(-0.2f, -0.3f, 0.15f), Triple(0.1f, -0.1f, 0.08f))

    for ((xOffset, yOffset, sizeRatio) in craters) {
      canvas.drawCircle(
        moonX + moonRadius * xOffset,
        moonphaseY + moonRadius * yOffset,
        moonRadius * sizeRatio,
        moonCraterPaint
      )
    }

    // Draw decorative frame around moonphase
    canvas.drawRect(
      centerX - moonphaseWidth / 2,
      moonphaseY - moonphaseHeight / 2,
      centerX + moonphaseWidth / 2,
      moonphaseY + moonphaseHeight / 2,
      moonphaseBorderPaint
    )
  }

  private fun drawHourMarkersAndNumbers(
    canvas: Canvas,
    centerX: Float,
    centerY: Float,
    radius: Float
  ) {
    // Longines Master Collection typically uses Roman numerals
    val romanNumerals =
      listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")

    // Draw Roman numerals
    for (i in 0 until 12) {
      // Skip VI (6 o'clock) where the moonphase is
      if (i == 5) continue

      val angle = Math.PI / 6 * i - Math.PI / 3
      val numberRadius = radius * 0.75f
      val numberX = centerX + cos(angle).toFloat() * numberRadius
      val numberY = centerY + sin(angle).toFloat() * numberRadius + numbersPaint.textSize / 3

      canvas.drawText(romanNumerals[i], numberX, numberY, numbersPaint)
    }

    // Draw minute markers (small dots)
    for (i in 0 until 60) {
      if (i % 5 == 0) continue // Skip where hour markers are

      val angle = Math.PI * 2 * i / 60
      val markerRadius = radius * 0.01f
      val markerX = centerX + cos(angle).toFloat() * radius * 0.85f
      val markerY = centerY + sin(angle).toFloat() * radius * 0.85f

      canvas.drawCircle(markerX, markerY, markerRadius, markersPaint)
    }

    // Draw date window at 3 o'clock
    val dateX = centerX + radius * 0.6f
    val dateY = centerY

    // Date window
    val dateWindowRect =
      RectF(
        dateX - radius * 0.08f,
        dateY - radius * 0.06f,
        dateX + radius * 0.08f,
        dateY + radius * 0.06f
      )

    // Draw white background
    val dateWindowBackgroundPaint =
      Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
      }
    canvas.drawRect(dateWindowRect, dateWindowBackgroundPaint)

    // Draw black border
    val dateWindowBorderPaint =
      Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 1f
      }
    canvas.drawRect(dateWindowRect, dateWindowBorderPaint)

    // Draw date text
    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    canvas.drawText(day, dateX, dateY + radius * 0.03f, datePaint)
  }

  private fun drawClockHands(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
    // Get current time with the current timezone
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Hour hand - elegant leaf shape (blued steel)
    val hourAngle = (hour * 30 + minute * 0.5f) * Math.PI / 180

    // Save canvas state
    canvas.save()

    // Rotate canvas for hour hand
    canvas.rotate((hourAngle * 180 / Math.PI).toFloat(), centerX, centerY)

    // Draw hour hand
    val hourHandPath = Path()
    hourHandPath.moveTo(centerX, centerY - radius * 0.5f) // Tip
    hourHandPath.quadTo(
      centerX + radius * 0.04f,
      centerY - radius * 0.25f, // Control point
      centerX + radius * 0.02f,
      centerY // End point
    )
    hourHandPath.quadTo(
      centerX,
      centerY + radius * 0.1f, // Control point
      centerX - radius * 0.02f,
      centerY // End point
    )
    hourHandPath.quadTo(
      centerX - radius * 0.04f,
      centerY - radius * 0.25f, // Control point
      centerX,
      centerY - radius * 0.5f // End point (back to start)
    )
    hourHandPath.close()

    canvas.drawPath(hourHandPath, hourHandPaint)

    // Restore canvas state
    canvas.restore()

    // Minute hand - longer leaf shape
    val minuteAngle = minute * 6f * Math.PI / 180

    // Save canvas state
    canvas.save()

    // Rotate canvas for minute hand
    canvas.rotate((minuteAngle * 180 / Math.PI).toFloat(), centerX, centerY)

    // Draw minute hand
    val minuteHandPath = Path()
    minuteHandPath.moveTo(centerX, centerY - radius * 0.7f) // Tip
    minuteHandPath.quadTo(
      centerX + radius * 0.03f,
      centerY - radius * 0.35f, // Control point
      centerX + radius * 0.015f,
      centerY // End point
    )
    minuteHandPath.quadTo(
      centerX,
      centerY + radius * 0.1f, // Control point
      centerX - radius * 0.015f,
      centerY // End point
    )
    minuteHandPath.quadTo(
      centerX - radius * 0.03f,
      centerY - radius * 0.35f, // Control point
      centerX,
      centerY - radius * 0.7f // End point (back to start)
    )
    minuteHandPath.close()

    canvas.drawPath(minuteHandPath, minuteHandPaint)

    // Restore canvas state
    canvas.restore()

    // Second hand - thin with small counterbalance
    val secondAngle = second * 6f * Math.PI / 180

    // Save canvas state
    canvas.save()

    // Rotate canvas for second hand
    canvas.rotate((secondAngle * 180 / Math.PI).toFloat(), centerX, centerY)

    // Main second hand
    canvas.drawLine(
      centerX,
      centerY + radius * 0.15f,
      centerX,
      centerY - radius * 0.75f,
      secondHandPaint
    )

    // Counterbalance
    canvas.drawCircle(centerX, centerY + radius * 0.1f, radius * 0.03f, secondHandFillPaint)

    // Restore canvas state
    canvas.restore()
  }
}
