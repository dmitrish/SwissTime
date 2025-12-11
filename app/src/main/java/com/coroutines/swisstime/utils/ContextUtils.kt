package com.coroutines.swisstime.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable

@Composable
public fun getApplicationVersionInfo(context: Context): Pair<String?, Int> {
  // Get version information from PackageManager
  val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
  val versionName = packageInfo.versionName
  val versionCode =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      packageInfo.longVersionCode.toInt()
    } else {
      @Suppress("DEPRECATION") packageInfo.versionCode
    }
  return Pair(versionName, versionCode)
}
