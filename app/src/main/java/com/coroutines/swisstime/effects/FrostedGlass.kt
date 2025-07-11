package com.coroutines.swisstime.effects

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

/**
 * A composable that applies a frosted glass effect to its content.
 * The effect is triggered by pinch zoom gestures.
 *
 * @param modifier The modifier to be applied to the layout
 * @param content The content to apply the frosted glass effect to
 */
@Composable
fun FrostedGlassEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // State for tracking pinch zoom
    var scale by remember { mutableFloatStateOf(1f) }
    var isEffectActive by remember { mutableStateOf(false) }

    // Calculate blur radius based on scale (1.0 to 2.5)
    val blurRadius = ((scale - 1f) * 25f).coerceIn(0f, 25f)

    // Calculate saturation based on scale
    val saturation = 1f - ((scale - 1f) * 0.3f).coerceIn(0f, 0.5f)

    // Calculate brightness based on scale
    val brightness = 1f + ((scale - 1f) * 0.1f).coerceIn(0f, 0.2f)

    // Calculate overlay opacity based on scale
    val overlayOpacity = ((scale - 1f) * 0.15f).coerceIn(0f, 0.15f)

    // Box to contain both the original content and the frosted glass effect
    Box(modifier = modifier) {
        // Original content
        content()

        // Frosted glass overlay (only visible when effect is active)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isEffectActive) {
            // Create blur effect
            val blurEffect = RenderEffect
                .createBlurEffect(
                    blurRadius, 
                    blurRadius,
                    Shader.TileMode.DECAL
                )
                .asComposeRenderEffect()

            // Apply frosted glass effect with blur and white overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(1f) // Make fully visible when active
                    .graphicsLayer {
                        // Apply blur effect
                        renderEffect = blurEffect
                        // Apply saturation and brightness
                        this.alpha = 0.85f  // Slight transparency
                        this.scaleX = scale
                        this.scaleY = scale
                    }
                    .background(Color.White.copy(alpha = overlayOpacity))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            // Update scale with zoom factor
                            scale = (scale * zoom).coerceIn(1f, 2.5f)

                            // Activate effect when scale is greater than threshold
                            isEffectActive = scale > 1.05f
                        }
                    }
            ) {
                // Render the same content for the blur effect
                content()
            }
        } else {
            // For devices that don't support RenderEffect, still detect gestures
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f) // Invisible overlay just for gesture detection
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            // Update scale with zoom factor
                            scale = (scale * zoom).coerceIn(1f, 2.5f)

                            // Activate effect when scale is greater than threshold
                            isEffectActive = scale > 1.05f
                        }
                    }
            )
        }
    }
}
