package com.coroutines.worldclock.common.effects

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

/**
 * A composable that applies a frosted glass effect to its content using AGSL (Android Graphics
 * Shading Language). The effect is triggered by pinch zoom gestures.
 *
 * @param modifier The modifier to be applied to the layout
 * @param content The content to apply the frosted glass effect to
 */
@Composable
fun FrostedGlassAGSLEffect(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  // State for tracking pinch zoom
  var scale by remember { mutableFloatStateOf(1f) }
  var isEffectActive by remember { mutableStateOf(false) }

  // Calculate blur radius based on scale (1.0 to 2.5)
  val blurRadius = ((scale - 1f) * 25f).coerceIn(0f, 25f)

  // Calculate overlay opacity based on scale
  val overlayOpacity = ((scale - 1f) * 0.15f).coerceIn(0f, 0.15f)

  // Box to contain both the original content and the frosted glass effect
  Box(modifier = modifier) {
    // Original content
    content()

    // Frosted glass overlay (only visible when effect is active)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isEffectActive) {
      // Create and apply AGSL shader effect for Android 13+ (API 33+)
      Box(
        modifier =
          Modifier.fillMaxSize()
            .alpha(1f) // Make fully visible when active
            .graphicsLayer {
              // Apply AGSL shader effect
              renderEffect = createAGSLEffect(blurRadius)
              // Apply transparency and scale
              this.alpha = 0.85f // Slight transparency
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
      // For devices that don't support AGSL, still detect gestures
      Box(
        modifier =
          Modifier.fillMaxSize()
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

/**
 * Creates an AGSL shader effect for the frosted glass effect. This function requires Android 13
 * (API level 33) or higher.
 *
 * @param blurRadius The radius of the blur effect
 * @return A RenderEffect that can be applied to a Compose UI element
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun createAGSLEffect(blurRadius: Float): androidx.compose.ui.graphics.RenderEffect {
  // AGSL shader for frosted glass effect
  val shaderSource =
    """
        uniform shader content;
        uniform float blurRadius;

        half4 main(float2 coord) {
            // Initialize color accumulator
            half4 color = half4(0.0);
            float totalWeight = 0.0;

            // Gaussian blur parameters
            float sigma = blurRadius * 0.5;
            float sigmaSquared = sigma * sigma;

            // Number of samples based on blur radius
            int samples = int(min(max(blurRadius * 0.3, 5.0), 15.0));

            // Perform Gaussian blur
            for (int x = -samples; x <= samples; x++) {
                for (int y = -samples; y <= samples; y++) {
                    float2 offset = float2(float(x), float(y));
                    float weight = exp(-(offset.x * offset.x + offset.y * offset.y) / (2.0 * sigmaSquared));
                    color += content.eval(coord + offset * blurRadius * 0.5) * weight;
                    totalWeight += weight;
                }
            }

            // Normalize color
            color /= totalWeight;

            // Add slight desaturation and brightness adjustment
            float luminance = dot(color.rgb, half3(0.299, 0.587, 0.114));
            half3 desaturated = mix(color.rgb, half3(luminance), 0.2);
            half3 brightened = desaturated * 1.1;

            return half4(brightened, color.a);
        }
    """

  // Create AGSL shader
  val agslShader = RuntimeShader(shaderSource)
  agslShader.setFloatUniform("blurRadius", blurRadius)

  // Create RenderEffect from the shader
  return RenderEffect.createRuntimeShaderEffect(agslShader, "content").asComposeRenderEffect()
}
