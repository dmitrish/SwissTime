package com.coroutines.livewallpaper.service

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.coroutines.livewallpaper.common.BaseClock
import com.coroutines.livewallpaper.watches.KnotClock
import com.coroutines.livewallpaper.watches.RomaMarinaClock
import com.coroutines.livewallpaper.watches.ZeitwerkClock
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A live wallpaper service that displays the Roma Marina watch face.
 */
class RomaMarinaWallpaperService : WallpaperService() {
    lateinit var appContext: Context


    override fun onCreateEngine(): Engine {
        appContext = applicationContext
        return ClockEngine()
    }

    /**
     * The engine that handles rendering the Roma Marina watch face.
     */
    inner class ClockEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private lateinit var clock: BaseClock

        private val engineJob = SupervisorJob()
        private val engineScope = CoroutineScope(Dispatchers.Main.immediate + engineJob)

        private val timeUpdateRunnable = object : Runnable {
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
            val wallpaperPreferenceRepository = WallpaperPreferenceRepository(appContext)

            runBlocking {
                // val selected = "Alpenglühen Zeitwerk"
                // TODO: Use `selected` to configure initial page or clock selection if needed

                try {
                    val selected = wallpaperPreferenceRepository.selectedWallpaperName.first()
                    Log.d("PagerWallpaperService", "selected=$selected")
                    when (selected) {
                        "Roma Marina" -> clock = RomaMarinaClock(this@RomaMarinaWallpaperService, handler)
                        "Alpenglühen Zeitwerk" -> clock = ZeitwerkClock(this@RomaMarinaWallpaperService, handler)
                        else -> clock = KnotClock(this@RomaMarinaWallpaperService, handler)
                    }

                    // build clocks then pager
                } catch (t: Throwable) {
                    Log.e("PagerWallpaperService", "Failed to read selected wallpaper", t)
                }
            }

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