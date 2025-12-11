package com.coroutines.swisstime.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandLogosScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val brandLogos = getBrandLogos(context)

  Scaffold(
    containerColor = Color.Black,
    contentColor = Color.White,
    topBar = {
      TopAppBar(
        title = { Text("Watch Brand Logos", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
          )
      )
    }
  ) { paddingValues ->
    LazyVerticalStaggeredGrid(
      columns = StaggeredGridCells.Fixed(2),
      modifier =
        Modifier.fillMaxSize().background(Color.Black).padding(paddingValues).padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalItemSpacing = 8.dp
    ) {
      items(brandLogos) { brandLogo ->
        BrandLogoItem(brandLogo = brandLogo, onClick = { /* Optional: Handle click on logo */})
      }
    }
  }
}

@Composable
fun BrandLogoItem(brandLogo: BrandLogo, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Black)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Logo image
      Image(
        painter = painterResource(id = brandLogo.resourceId),
        contentDescription = brandLogo.name,
        modifier = Modifier.fillMaxWidth().height(100.dp).padding(8.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Brand name
      Text(
        text = brandLogo.name,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

data class BrandLogo(val name: String, val resourceId: Int)

fun getBrandLogos(context: Context): List<BrandLogo> {
  val packageName = context.packageName
  val logos = mutableListOf<BrandLogo>()

  // Get all drawable resources that start with "logo_"
  val drawableClass = Class.forName("$packageName.R\$drawable")

  for (field in drawableClass.fields) {
    val fieldName = field.name
    if (fieldName.startsWith("logo_")) {
      val resourceId = field.getInt(null)
      // Format the name by removing "logo_" prefix and replacing underscores with spaces
      val name =
        fieldName
          .removePrefix("logo_")
          .replace("_logo_white", "")
          .replace("_", " ")
          .split(" ")
          .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

      logos.add(BrandLogo(name, resourceId))
    }
  }

  return logos
}
