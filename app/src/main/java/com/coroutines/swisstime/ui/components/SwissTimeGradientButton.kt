package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutines.swisstime.utils.darken
import com.coroutines.worldclock.common.theme.DarkNavy

@Composable
fun SwissTimeGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 28.dp
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = modifier
                .fillMaxHeight()
             //   .border(1.dp, Color.White, RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DarkNavy.darken(0.5f),
                         //   Color(0xFFE89F7F),
                            Color.Yellow.darken(0.4f),
                          //  DarkNavy.darken(0.1f),

                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
    }
}

// Usage example:
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // ... other UI elements ...

        SwissTimeGradientButton(
            text = "Log In",
            onClick = { /* Handle login */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SwissTimeGradientButton(
            text = "Sign Up",
            onClick = { /* Handle sign up */ }
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}