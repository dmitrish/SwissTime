package com.coroutines.swisstime.splash

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        setContentView(R.layout.activity_splash)

        // Start the animation
        val splashIcon = findViewById<ImageView>(R.id.splash_icon)
        val avd = splashIcon.drawable as? AnimatedVectorDrawable
        avd?.start()

        // Navigate to MainActivity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000) // 2 seconds delay
    }
}