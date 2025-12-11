package com.coroutines.swisstime.splash

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast

/** Helper class to toggle between different splash screen implementations. */
class SplashScreenSelector(private val context: Context) {

  companion object {
    // Class names as registered in AndroidManifest.xml
    private const val SPLASH_ACTIVITY = "com.coroutines.swisstime.splash.SplashActivity"
    private const val MAIN_ACTIVITY_WITH_SPLASH_API =
      "com.coroutines.swisstime.MainActivityWithSplashScreenAPI"
  }

  /**
   * Enable the Splash Screen API implementation and disable the original SplashActivity. This
   * requires an app restart to take effect.
   */
  fun useSplashScreenApi() {
    val packageManager = context.packageManager

    // Disable the original SplashActivity
    packageManager.setComponentEnabledSetting(
      ComponentName(context, SPLASH_ACTIVITY),
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )

    // Enable the MainActivity with Splash Screen API
    packageManager.setComponentEnabledSetting(
      ComponentName(context, MAIN_ACTIVITY_WITH_SPLASH_API),
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )

    Toast.makeText(
        context,
        "Splash Screen API enabled. Please restart the app for changes to take effect.",
        Toast.LENGTH_LONG
      )
      .show()
  }

  /**
   * Enable the original SplashActivity implementation and disable the Splash Screen API. This
   * requires an app restart to take effect.
   */
  fun useOriginalSplashScreen() {
    val packageManager = context.packageManager

    // Enable the original SplashActivity
    packageManager.setComponentEnabledSetting(
      ComponentName(context, SPLASH_ACTIVITY),
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )

    // Disable the MainActivity with Splash Screen API
    packageManager.setComponentEnabledSetting(
      ComponentName(context, MAIN_ACTIVITY_WITH_SPLASH_API),
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )

    Toast.makeText(
        context,
        "Original splash screen enabled. Please restart the app for changes to take effect.",
        Toast.LENGTH_LONG
      )
      .show()
  }

  /** Check if the Splash Screen API implementation is currently active. */
  fun isSplashScreenApiActive(): Boolean {
    try {
      val packageManager = context.packageManager
      val splashApiState =
        packageManager.getComponentEnabledSetting(
          ComponentName(context, MAIN_ACTIVITY_WITH_SPLASH_API)
        )

      // Check if the original splash activity is disabled
      val originalSplashState =
        packageManager.getComponentEnabledSetting(ComponentName(context, SPLASH_ACTIVITY))

      // The Splash Screen API is active if the MainActivityWithSplashScreenAPI is enabled
      // and the original SplashActivity is disabled
      return splashApiState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED &&
        originalSplashState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    } catch (e: Exception) {
      // If there's any error, default to true since we want to use the Splash Screen API
      return true
    }
  }
}
