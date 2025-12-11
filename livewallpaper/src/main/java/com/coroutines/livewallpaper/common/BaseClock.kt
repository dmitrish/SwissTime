package com.coroutines.livewallpaper.common

import android.graphics.Canvas

interface BaseClock {
  fun updateTextSizes(width: Int)

  fun draw(canvas: Canvas)

  fun destroy()
}
