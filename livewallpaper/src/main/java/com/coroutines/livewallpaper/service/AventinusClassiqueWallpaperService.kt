package com.coroutines.livewallpaper.service

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.coroutines.livewallpaper.watches.AventinusClassiqueClock

/** A live wallpaper service that displays the Aventinus Classique watch face. */
class AventinusClassiqueWallpaperService : WallpaperService() {

  override fun onCreateEngine(): Engine {
    return ClockEngine()
  }

  /** The engine that handles rendering the Aventinus Classique watch face. */
  inner class ClockEngine : Engine() {
    private val handler = Handler(Looper.getMainLooper())
    private var visible = false
    private lateinit var clock: AventinusClassiqueClock

    private val timeUpdateRunnable =
      object : Runnable {
        override fun run() {
          drawFrame()
          // Schedule next update at the start of the next second
          val now = System.currentTimeMillis()
          val nextSecond = (now / 1000 + 1) * 1000
          handler.postDelayed(this, nextSecond - now)
        }
      }

    override fun onCreate(surfaceHolder: SurfaceHolder) {
      super.onCreate(surfaceHolder)
      clock = AventinusClassiqueClock(this@AventinusClassiqueWallpaperService, handler)
    }

    override fun onDestroy() {
      super.onDestroy()
      handler.removeCallbacks(timeUpdateRunnable)
      clock.destroy()
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
      clock.updateTextSizes(width)

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
          clock.draw(canvas)
        }
      } finally {
        if (canvas != null) {
          holder.unlockCanvasAndPost(canvas)
        }
      }
    }
  }
}
