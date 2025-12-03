package com.coroutines.swisstime.ui.screens

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.coroutines.swisstime.ui.theme.DarkNavy
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.coroutines.worldclock.common.components.getBitmap
import com.coroutines.worldclock.common.effects.shader.WaterEffectBitmapShader
import com.coroutines.worldclock.common.effects.viewmodel.WaterViewModel
import com.coroutines.swisstime.R

data class SunParams(
    val RA: Float,
    val Decl: Float,
    val GMST0: Float,
    val hourDecimal: Float
)

fun revNew(angle: Float): Float {
    return angle - kotlin.math.floor(angle / 360f) * 360f
}

fun calculateSunPositionNew(currentTime: Calendar): SunParams {
    val year = currentTime.get(Calendar.YEAR)
    val month = currentTime.get(Calendar.MONTH) + 1
    val day = currentTime.get(Calendar.DAY_OF_MONTH)
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)
    val second = currentTime.get(Calendar.SECOND)
    val hourDecimal = hour + minute / 60f + second / 3600f

    val daysToJ2000 = 367 * year - 7 * (year + (month + 9) / 12) / 4 + 275 * month / 9 + day - 730530 + hourDecimal / 24f

    val w = 282.9404f + 4.70935E-5f * daysToJ2000
    val e = 0.016709f - 1.151E-9f * daysToJ2000
    val M = revNew(356.0470f + 0.9856002585f * daysToJ2000)
    val oblecl = 23.4393f - 3.563E-7f * daysToJ2000
    val L = revNew(w + M)
    val E = M + (180f / PI.toFloat()) * e * sin(M * (PI / 180f).toFloat()) * (1 + e * cos(M * (PI / 180f).toFloat()))

    val x = cos(E * (PI / 180f).toFloat()) - e
    val y = sin(E * (PI / 180f).toFloat()) * sqrt(1 - e * e)
    val r = sqrt(x * x + y * y)
    val v = atan2(y, x) * (180f / PI.toFloat())
    val sunLongitude = revNew(v + w)

    val xeclip = r * cos(sunLongitude * (PI / 180f).toFloat())
    val yeclip = r * sin(sunLongitude * (PI / 180f).toFloat())
    val xequat = xeclip
    val yequat = yeclip * cos(oblecl * (PI / 180f).toFloat())
    val zequat = yeclip * sin(oblecl * (PI / 180f).toFloat())

    val RA = atan2(yequat, xequat) * (180f / PI.toFloat()) / 15f
    val Decl = asin(zequat / r) * (180f / PI.toFloat())
    val GMST0 = (L * (PI / 180f).toFloat() + PI.toFloat()) / 15f * (180f / PI.toFloat())

    return SunParams(RA, Decl, GMST0, hourDecimal)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CustomShaderWorldMapWithDayNight(
    modifier: Modifier = Modifier,
    nightOverlayColor: Color = Color(DarkNavy.toArgb()),
    updateIntervalMillis: Long = 6000
) {
    var currentTime by remember {
        mutableStateOf(Calendar.getInstance(TimeZone.getTimeZone("America/New_York")))
    }

    LaunchedEffect(true) {
        while(true) {
            currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
            delay(updateIntervalMillis)
        }
    }

    val sunParams: SunParams = remember(currentTime) {
        calculateSunPositionNew(currentTime)
    }

    // Create AGSL shader once
    val shader = remember {
        RuntimeShader(
            """
            uniform float2 resolution;
            uniform float ra;
            uniform float decl;
            uniform float gmst0;
            uniform float hourDecimal;
            uniform float blur;
            uniform float xOffsetPercent;
            
            const float PI = 3.14159265359;
            
            float rev(float angle) {
                float result = angle;
                while (result >= 360.0) result -= 360.0;
                while (result < 0.0) result += 360.0;
                return result;
            }
            
            half4 main(float2 fragCoord) {
                float x = fragCoord.x;
                float y = fragCoord.y;
                
                // Convert to longitude/latitude
                float xOffset = resolution.x * xOffsetPercent;
                float adjustedX = x + xOffset;
                float longitude = (adjustedX / resolution.x * 360.0) - 180.0;
                float latitude = 90.0 - (y / resolution.y * 180.0);
                
                // Calculate sun altitude at this point
                float latRad = latitude * (PI / 180.0);
                float SIDTIME = gmst0 + hourDecimal + longitude / 15.0;
                float HA = rev(SIDTIME - ra) * 15.0;
                float HArad = HA * (PI / 180.0);
                float declRad = decl * (PI / 180.0);
                
                float xval = cos(HArad) * cos(declRad);
                float yval = sin(HArad) * cos(declRad);
                float zval = sin(declRad);
                
                float xhor = xval * sin(latRad) - zval * cos(latRad);
                float yhor = yval;
                float zhor = xval * cos(latRad) + zval * sin(latRad);
                
                float altitude = atan(zhor, sqrt(xhor*xhor + yhor*yhor)) * (180.0 / PI);
                
                // Apply shading based on altitude
                if (altitude < -blur) {
                    // Night side
                    return half4(0.0, 0.0, 0.2, 0.13);
                } else if (altitude >= -blur && altitude <= blur) {
                    // Terminator region
                    float alpha = (altitude + blur) / (blur * 2.0);
                    if (alpha < 0.5) {
                        float nightAlpha = 0.13 * (1.0 - clamp(alpha, 0.0, 1.0));
                        return half4(0.0, 0.0, 0.2, nightAlpha);
                    }
                }
                
                // Day side (transparent)
                return half4(0.0, 0.0, 0.0, 0.0);
            }
        """.trimIndent()
        )
    }

    Box(
        modifier = modifier.background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .background(Color.Transparent)
        ) {
            // Your existing world map image/shader
            val bitmap = getBitmap(R.drawable.world)!!

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val waterViewModel = remember { WaterViewModel() }
                WaterEffectBitmapShader(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = bitmap.asImageBitmap(),
                    viewModel = waterViewModel,
                    shaderResId = R.raw.water_shader
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.world),
                    contentDescription = "Earth Map",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }

            // Day/night overlay using AGSL shader
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Update shader uniforms
                shader.setFloatUniform("resolution", size.width, size.height)
                shader.setFloatUniform("ra", sunParams.RA)
                shader.setFloatUniform("decl", sunParams.Decl)
                shader.setFloatUniform("gmst0", sunParams.GMST0)
                shader.setFloatUniform("hourDecimal", sunParams.hourDecimal)
                shader.setFloatUniform("blur", 4f)
                shader.setFloatUniform("xOffsetPercent", 0.16f)

                // Draw the shader
                drawRect(
                    brush = ShaderBrush(shader),
                    size = size
                )
            }
        }
    }
}