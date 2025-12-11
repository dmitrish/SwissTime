package com.coroutines.livewallpaper.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A horizontal pager for use in WallpaperService that supports multiple pages with smooth
 * animations and touch gestures.
 */
class WallpaperHorizontalPager(
  private val context: Context,
  private val pageCount: Int,
  private val onPageChanged: (Int) -> Unit = {}
) {
  // Current page index (0-based)
  var currentPage = 0
    private set

  // Current offset for animation (0.0 to 1.0)
  private var currentOffset = 0f

  // Width of the pager (set during drawing)
  private var pagerWidth = 0

  // Animation properties
  private val animator =
    ValueAnimator().apply {
      interpolator = DecelerateInterpolator()
      duration = 300 // Animation duration in ms
      addUpdateListener { animation ->
        currentOffset = animation.animatedValue as Float
        // Request redraw
        onPageOffsetChanged(currentOffset)
      }
    }

  // Gesture detector for handling touch events
  private val gestureDetector =
    GestureDetector(
      context,
      object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
          return true
        }

        override fun onFling(
          e1: MotionEvent?,
          e2: MotionEvent,
          velocityX: Float,
          velocityY: Float
        ): Boolean {
          if (abs(velocityX) > abs(velocityY)) {
            if (velocityX < 0 && currentPage < pageCount - 1) {
              // Fling to next page
              animateToPage(currentPage + 1)
              return true
            } else if (velocityX > 0 && currentPage > 0) {
              // Fling to previous page
              animateToPage(currentPage - 1)
              return true
            }
          }
          return false
        }

        override fun onScroll(
          e1: MotionEvent?,
          e2: MotionEvent,
          distanceX: Float,
          distanceY: Float
        ): Boolean {
          if (e1 == null) return false

          // Calculate drag distance as a percentage of screen width
          val dragOffset = (e1.x - e2.x) / pagerWidth

          // Limit offset to valid range
          val newOffset =
            when {
              currentPage == 0 && dragOffset < 0 ->
                max(0f, dragOffset * 0.3f) // Resistance when at first page
              currentPage == pageCount - 1 && dragOffset > 0 ->
                min(0f, dragOffset * 0.3f) // Resistance when at last page
              else -> dragOffset
            }

          // Update offset and request redraw
          currentOffset = newOffset
          onPageOffsetChanged(currentOffset)
          return true
        }
      }
    )

  // Callback for when page offset changes (for redrawing)
  private var onPageOffsetChanged: (Float) -> Unit = {}

  /**
   * Set a callback to be invoked when the page offset changes. This should trigger a redraw of the
   * wallpaper.
   */
  fun setOnPageOffsetChangedListener(listener: (Float) -> Unit) {
    onPageOffsetChanged = listener
  }

  /** Handle touch events from the WallpaperService.Engine */
  fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL -> {
        // When touch is released, snap to the nearest page
        if (abs(currentOffset) > 0.1f) {
          if (currentOffset > 0 && currentPage < pageCount - 1) {
            // Snap to next page
            animateToPage(currentPage + 1)
          } else if (currentOffset < 0 && currentPage > 0) {
            // Snap to previous page
            animateToPage(currentPage - 1)
          } else {
            // Snap back to current page
            animateToPage(currentPage)
          }
        } else {
          // Small movement, snap back
          animateToPage(currentPage)
        }
      }
    }

    return gestureDetector.onTouchEvent(event)
  }

  /** Draw the current page and adjacent pages if they're visible */
  fun draw(canvas: Canvas, drawPage: (Canvas, Int, Float) -> Unit) {
    pagerWidth = canvas.width

    // Save canvas state
    canvas.save()

    // Calculate visible pages based on current offset
    val visiblePages = mutableListOf(currentPage)

    // Add adjacent pages if they're partially visible
    if (currentOffset > 0 && currentPage < pageCount - 1) {
      visiblePages.add(currentPage + 1)
    } else if (currentOffset < 0 && currentPage > 0) {
      visiblePages.add(currentPage - 1)
    }

    // Draw each visible page with appropriate translation
    for (page in visiblePages) {
      val pageOffset =
        when (page) {
          currentPage -> -currentOffset * pagerWidth
          currentPage + 1 -> (1 - currentOffset) * pagerWidth
          currentPage - 1 -> (-1 - currentOffset) * pagerWidth
          else -> 0f
        }

      // Translate canvas for this page
      canvas.save()
      canvas.translate(pageOffset, 0f)

      // Calculate page offset for animations (0.0 to 1.0)
      val pageTransitionOffset =
        when (page) {
          currentPage -> abs(currentOffset)
          currentPage + 1 -> 1 - currentOffset
          currentPage - 1 -> 1 + currentOffset
          else -> 0f
        }

      // Draw the page content
      drawPage(canvas, page, pageTransitionOffset)

      // Restore canvas for next page
      canvas.restore()
    }

    // Restore original canvas state
    canvas.restore()
  }

  /** Animate to the specified page */
  fun animateToPage(page: Int) {
    if (page < 0 || page >= pageCount || page == currentPage && currentOffset == 0f) {
      return
    }

    animator.cancel()

    if (page == currentPage) {
      // Animate back to current page
      animator.setFloatValues(currentOffset, 0f)
    } else {
      // Animate to new page
      val targetOffset = if (page > currentPage) 1f else -1f
      animator.setFloatValues(currentOffset, targetOffset)
    }

    animator.start()

    // If we're actually changing pages
    if (page != currentPage) {
      animator.addListener(
        object : android.animation.AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: android.animation.Animator) {
            // Update current page after animation completes
            currentPage = page
            currentOffset = 0f
            onPageChanged(currentPage)
            animator.removeAllListeners()
          }
        }
      )
    }
  }
}
