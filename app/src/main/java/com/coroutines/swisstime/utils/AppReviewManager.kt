package com.coroutines.swisstime.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory


class AppReviewManager(private val context: Context) {

    fun requestReview() {
        try {
            // Create a ReviewManager instance
            val manager = ReviewManagerFactory.create(context)

            // Request a ReviewInfo object
            val request = manager.requestReviewFlow()

            // Add a listener to handle the result
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result

                    // Get the current activity
                    val activity = context as? Activity
                    if (activity != null) {
                        // Launch the review flow
                        val flow = manager.launchReviewFlow(activity, reviewInfo)

                        flow.addOnCompleteListener {
                            // The flow has finished
                            Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Couldn't get the activity, fallback to Play Store
                        openPlayStore()
                    }
                } else {
                    // There was some problem, fallback to the Play Store
                    openPlayStore()
                }
            }
        } catch (e: Exception) {
            // Fallback to the Play Store if there's any exception
            openPlayStore()
        }
    }

    /**
     * Open the Play Store app or website for rating.
     */
    private fun openPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // If Play Store app is not available, open in browser
            val webIntent = Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
            context.startActivity(webIntent)
        }
    }
}
