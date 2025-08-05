package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coroutines.swisstime.ui.screens.BrandLogo

// Composable for the horizontal row of brand logos
@Composable
fun BrandLogosRow(
    brandLogos: List<BrandLogo>,
    onClick: (BrandLogo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(brandLogos) { brandLogo ->
            BrandLogoRowItem(
                brandLogo = brandLogo,
                onClick = { onClick(brandLogo) }
            )
        }
    }
}
