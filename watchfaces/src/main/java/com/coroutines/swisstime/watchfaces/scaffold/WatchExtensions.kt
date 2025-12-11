package com.coroutines.swisstime.watchfaces.scaffold

import java.util.Calendar

/** Helper extension to extract WatchTime from Calendar. */
fun Calendar.toWatchTime(): WatchTime = WatchTime.from(this)
