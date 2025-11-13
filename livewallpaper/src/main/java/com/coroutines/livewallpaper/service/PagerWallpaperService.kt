package com.coroutines.livewallpaper.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.coroutines.livewallpaper.watches.ChronomagusRegumClock
import com.coroutines.livewallpaper.watches.KnotClock
import com.coroutines.livewallpaper.watches.PontifexChronometraClock
import com.coroutines.livewallpaper.watches.RomaMarinaClock
import com.coroutines.livewallpaper.watches.ZeitwerkClock
import com.coroutines.livewallpaper.common.BaseClock
import com.coroutines.livewallpaper.components.WallpaperHorizontalPager
import com.coroutines.worldclock.common.repository.WallpaperPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//import com.coroutines.livewallpaper.watches.AventinusClassiqueClock
//import com.coroutines.livewallpaper.watches.LeonardAutomaticClock

class PagerWallpaperService : WallpaperService() {
    lateinit var appContext: Context

    override fun onCreateEngine(): Engine {
        appContext = applicationContext
        return PagerEngine()
    }

    inner class PagerEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private val engineJob = SupervisorJob()
        private val engineScope = CoroutineScope(Dispatchers.Main.immediate + engineJob)

        // List of clocks/pages to display
        private val clocks = mutableListOf<BaseClock>()

        // The horizontal pager
        private lateinit var pager: WallpaperHorizontalPager

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

            // Collect the initial selected wallpaper name in a coroutine
            engineScope.launch {
               // val selected = wallpaperPreferenceRepository.selectedWallpaperName.first()
                // TODO: Use `selected` to configure initial page or clock selection if needed

                val selected = "romamarina"

                when (selected) {
                    "romamarina" -> clocks.add(RomaMarinaClock(this@PagerWallpaperService, handler))
                    "zeitwerk" -> clocks.add(ZeitwerkClock(this@PagerWallpaperService, handler))
                    else -> clocks.add(KnotClock(this@PagerWallpaperService, handler))
                }


                pager = WallpaperHorizontalPager(
                    context = this@PagerWallpaperService,
                    pageCount = clocks.size,
                    onPageChanged = { page ->
                        // Optional: Handle page change events
                    }
                )

                // Set up redraw listener
                pager.setOnPageOffsetChangedListener { _ ->
                    drawFrame()
                }
            }


            // Initialize clocks
          /*  clocks.add(PontifexChronometraClock(this@PagerWallpaperService, handler))
            clocks.add(RomaMarinaClock(this@PagerWallpaperService, handler))
            clocks.add(ChronomagusRegumClock(this@PagerWallpaperService, handler))
            clocks.add(KnotClock(this@PagerWallpaperService, handler))
            clocks.add(ZeitwerkClock(this@PagerWallpaperService, handler)) */
           // clocks.add(AventinusClassiqueClock(this@PagerWallpaperService, handler))
          //  clocks.add(LeonardAutomaticClock(this@PagerWallpaperService, handler))
            // Add more clocks as needed

            // Initialize pager

        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(timeUpdateRunnable)
            clocks.forEach { it.destroy() }
            // Cancel any running coroutines to avoid leaks
            engineJob.cancel()
        }

        override fun onTouchEvent(event: MotionEvent) {
            // Pass touch events to the pager
            pager.onTouchEvent(event)
            return super.onTouchEvent(event)
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

            // Update all clocks
            clocks.forEach { it.updateTextSizes(width) }

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
                    // Clear background
                    canvas.drawColor(Color.BLACK)

                    // Draw pager with all pages
                    pager.draw(canvas) { pageCanvas, pageIndex, pageOffset ->
                        // Draw the clock for this page
                        clocks[pageIndex].draw(pageCanvas)
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}