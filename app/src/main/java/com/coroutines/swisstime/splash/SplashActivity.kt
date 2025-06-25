package com.coroutines.swisstime.splash

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import com.coroutines.swisstime.MainActivity
import com.coroutines.swisstime.R

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the background color extend to status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Set content view first
        setContentView(R.layout.activity_splash)

        // Get the splash icon view
        val splashIcon = findViewById<ImageView>(R.id.splash_icon)

        // Use a pre-draw listener to ensure we catch the very first frame
        // This should help prevent any blank frames on Android 10
        val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Remove the listener to ensure it only runs once
                splashIcon.viewTreeObserver.removeOnPreDrawListener(this)

                // Get the width and height of the ImageView
                val width = splashIcon.width
                val height = splashIcon.height

                // Preload the animated drawable
                val avdResource = resources.getDrawable(R.drawable.avd_splash_screen, theme) as AnimatedVectorDrawable

                // Set explicit bounds on the drawable to match the ImageView dimensions
                avdResource.setBounds(0, 0, width, height)

                // Set the drawable with the correct bounds
                splashIcon.setImageDrawable(avdResource)

                // Start the animation immediately
                avdResource.start()

                // Return true to proceed with the drawing
                return true
            }
        }

        // Add the pre-draw listener
        splashIcon.viewTreeObserver.addOnPreDrawListener(preDrawListener)

        // Navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000) // 2 seconds delay
    }
}
