package com.coroutines.swisstime.ui.research

import androidx.compose.ui.tooling.preview.Preview



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YachtDetailsScreen(
    onBackClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onLaterClick: () -> Unit = {},
    onRentNowClick: () -> Unit = {}
) {
    var isBookmarked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar with Image Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                // Main Yacht Image (placeholder with gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2C3E5A),
                                    Color(0xFF1A2738)
                                )
                            )
                        )
                )

                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Title
                    Text(
                        text = "Yacht Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    // Bookmark Button
                    IconButton(
                        onClick = {
                            isBookmarked = !isBookmarked
                            onBookmarkClick()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Color(0xFF4FC3F7) else Color.White
                        )
                    }
                }
            }

            // Thumbnail Images Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-40).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(4) { index ->
                    ThumbnailImage(
                        isSelected = index == 1,
                        onClick = { /* Handle thumbnail click */ }
                    )
                }
            }

            // Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A3444)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Title and Price Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SeaLux Cruiser 78",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Customer Review (4.9 / 5)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFB0B8C1),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Price
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Rent Price",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB0B8C1),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$3.7K",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/day",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB0B8C1),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureChip("10-12 Seats")
                        FeatureChip("3 Cabins")
                        FeatureChip("1 Kitchen")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Experience pure luxury aboard the 78ft SeaLux Cruiser, featuring 3 stylish cabins and spacious lounging areas for 10-12 guests. Perfect for private charters, sunsets...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB0B8C1),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Later Button
                        OutlinedButton(
                            onClick = onLaterClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFF4A5568)
                            )
                        ) {
                            Text(
                                text = "Later",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Rent Now Button
                        Button(
                            onClick = onRentNowClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF1A1F2E)
                            )
                        ) {
                            Text(
                                text = "Rent Now",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ThumbnailImage(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3A4A5C),
                        Color(0xFF2C3A4A)
                    )
                )
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    )
}

@Composable
fun FeatureChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF3A4556),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE0E6ED),
                fontSize = 13.sp
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun YachtDetailsScreenPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        YachtDetailsScreen()
    }
}