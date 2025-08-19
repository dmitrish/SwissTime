package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.screens.BrandLogo
import com.coroutines.swisstime.utils.darken

// Composable for a single brand logo item in the horizontal row
@Composable
fun BrandLogoRowItem(
    brandLogo: BrandLogo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier

            .background(MaterialTheme.colorScheme.background.darken(0.25f), MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo image
        Image(
            painter = painterResource(id = brandLogo.resourceId),
            contentDescription = brandLogo.name,
            modifier = Modifier
                .width(175.dp)
                .height(110.dp)
                .padding(16.dp)
        )
    }
}