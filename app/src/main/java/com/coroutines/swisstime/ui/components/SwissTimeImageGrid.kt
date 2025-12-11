package com.coroutines.swisstime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.watchfaces.getWatches

@Composable
fun SwissTimeJourneyScreen(backgroundColor: Color = Color.White) {
  // Drag and drop shared state
  val dragUrl =
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
  val dragPos =
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Offset?>(null) }
  val isDragging =
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
  val droppedUrl =
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
  val dropZoneRect =
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Rect?>(null) }

  val density = LocalDensity.current
  val previewSizeDp = 96.dp
  val previewSizePx = with(density) { previewSizeDp.toPx() }

  Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(modifier = Modifier.fillMaxSize().weight(1f)) {
        Column(
          modifier =
            Modifier.fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 24.dp)
              .padding(top = 24.dp, bottom = 416.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Logo
          Logo()

          Spacer(modifier = Modifier.height(24.dp))

          // Welcome Text
          Text(
            text = "Welcome to",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Main Heading
          Text(
            text = "World Clock",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1a1a1a),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Subtitle
          Text(
            text = "Eternal Timepieces",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Drop zone between logo/texts and the bottom grid
          DropZone(
            droppedUrl = droppedUrl.value,
            onBounds = { rect -> dropZoneRect.value = rect },
            modifier = Modifier.fillMaxWidth().height(140.dp).padding(top = 8.dp)
          )

          Spacer(modifier = Modifier.height(32.dp))

          // Image Grid moved out of scroll to be bottom-aligned
          Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom-aligned overlay: ImageGrid with SlideUpIndicator drawn on top (transparent
        // background)
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
          Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            // Grid as the background layer
            ImageGridX(
              onDragStart = { url, startAbsPx, _ ->
                dragUrl.value = url
                dragPos.value = startAbsPx
                isDragging.value = true
              },
              onDrag = { deltaPx -> dragPos.value = dragPos.value?.plus(deltaPx) },
              onDragEnd = {
                val pos = dragPos.value
                val zone = dropZoneRect.value
                if (pos != null && zone != null && zone.contains(pos)) {
                  droppedUrl.value = dragUrl.value
                }
                dragUrl.value = null
                dragPos.value = null
                isDragging.value = false
              }
            )
            // SlideUpIndicator overlaid so the grid remains visible underneath
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)) {
              SlideUpIndicator()
            }
          }
        }
      }
    }

    // Floating drag preview overlay
    val currentUrl = dragUrl.value
    val currentPos = dragPos.value
    if (isDragging.value && currentUrl != null && currentPos != null) {
      Box(
        modifier =
          Modifier.offset {
              IntOffset(
                (currentPos.x - previewSizePx / 2f).toInt(),
                (currentPos.y - previewSizePx / 2f).toInt()
              )
            }
            .size(previewSizeDp)
      ) {
        AsyncImage(
          model = currentUrl,
          contentDescription = "Dragging image",
          modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}

@Composable
fun Logo() {
  Box(
    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF2D3561)),
    contentAlignment = Alignment.Center
  ) {
    Text(text = "C", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
  }
}

@Composable
fun ImageGrid() {
  val watches = getWatches()

  val imageUrls =
    listOf(
      "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1515377905703-c4788e51af15?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1560750588-73207b1ef5b8?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1616683693504-3ea7e9ad6fec?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=400&h=400&fit=crop"
    )

  BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(360.dp)) {
    val interColumnSpacing = 8.dp
    val totalInterSpacing = 32.dp // 4 gaps between 5 columns

    // Capture viewport width from BoxWithConstraints to avoid scope issues in nested lambdas
    val viewportWidth = maxWidth
    // Natural column width so that 5 columns + 4 equal gaps exactly fill the parent width
    val columnWidth = (viewportWidth - totalInterSpacing) / 5
    val overhang = columnWidth / 3

    // A viewport-clipped container hosting a Row of 5 equal-width wrappers.
    // Each wrapper has equal spacing (8.dp) between them. The first and last
    // inner columns are wider by overhang and aligned to spill outward so that
    // only 2/3 of their width remains visible, keeping equal visible spacing.
    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
      Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(interColumnSpacing),
        verticalAlignment = Alignment.Top
      ) {
        val topOffsets = listOf(0.dp, 25.dp, 50.dp, 0.dp, 25.dp)
        repeat(5) { columnIndex ->
          Box(
            modifier =
              Modifier.width(columnWidth) // visible width per column
                .fillMaxHeight()
                .clipToBounds()
          ) {
            val innerWidth =
              if (columnIndex == 0 || columnIndex == 4) columnWidth + overhang else columnWidth
            val innerAlignment =
              when (columnIndex) {
                0 -> Alignment.TopEnd // overflow to the left
                4 -> Alignment.TopStart // overflow to the right
                else -> Alignment.TopStart
              }
            Column(
              modifier =
                Modifier.width(innerWidth)
                  .align(innerAlignment)
                  .padding(top = topOffsets[columnIndex]),
              verticalArrangement = Arrangement.spacedBy(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              imageUrls.forEach { imageUrl ->
                AsyncImage(
                  model = imageUrl,
                  contentDescription = "Grid image",
                  modifier =
                    Modifier.aspectRatio(1f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFE0E0E0)),
                  contentScale = ContentScale.Crop,
                  placeholder =
                    androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFFE0E0E0)),
                  error = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFFBDBDBD))
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SlideUpIndicator() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Icon(
      imageVector = Icons.Default.KeyboardArrowUp,
      contentDescription = "Slide up",
      modifier = Modifier.size(24.dp),
      tint = Color.Gray
    )
    Text(text = "Slide up to explore", fontSize = 14.sp, color = Color.Gray)
  }
}

@Composable
fun DropZone(droppedUrl: String?, onBounds: (Rect) -> Unit, modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color.White.copy(alpha = 0.06f))
        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        .onGloballyPositioned { coords -> onBounds(coords.boundsInRoot()) },
    contentAlignment = Alignment.Center
  ) {
    if (droppedUrl == null) {
      Text(text = "Drop image here", color = Color.Gray, fontSize = 14.sp)
    } else {
      AsyncImage(
        model = droppedUrl,
        contentDescription = "Dropped image",
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
      )
    }
  }
}

@Composable
private fun DraggableImage(
  url: String,
  onDragStart: (String, Offset, IntSize) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
) {
  var itemRect by
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Rect?>(null) }
  var itemSize by
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<IntSize?>(null) }

  Box(
    modifier =
      Modifier.onGloballyPositioned { coords ->
          itemRect = coords.boundsInRoot()
          itemSize = coords.size
        }
        .pointerInput(url) {
          detectDragGestures(
            onDragStart = {
              val rect = itemRect
              val size = itemSize
              if (rect != null && size != null) {
                onDragStart(url, rect.center, size)
              }
            },
            onDrag = { change, dragAmount ->
              change.consumeAllChanges()
              onDrag(Offset(dragAmount.x, dragAmount.y))
            },
            onDragCancel = { onDragEnd() },
            onDragEnd = { onDragEnd() }
          )
        }
  ) {
    AsyncImage(
      model = url,
      contentDescription = "Grid image",
      modifier =
        Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0E0E0)),
      contentScale = ContentScale.Crop
    )
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF2F4F4F, showSystemUi = false)
@Composable
fun SwissTimeJourneyPreview() {
  SwissTimeTheme {
    Surface(Modifier.background(DarkNavy)) { SwissTimeJourneyScreen(backgroundColor = DarkNavy) }
  }
}

@Preview()
@Composable
fun SwissTimeJourneyPreviewWhite() {
  SwissTimeTheme { Surface() { SwissTimeJourneyScreen(backgroundColor = Color.White) } }
}

/**
 * A grid layout that allows the first and last columns to "peek" outside the viewport.
 *
 * @param columns Number of columns in the grid
 * @param spacing Spacing between items (both horizontal and vertical)
 * @param peekFraction Fraction of outer columns that extend outside viewport (e.g., 0.33f for 1/3)
 * @param modifier Modifier to apply to the layout
 * @param content Composable content containing the grid items
 */
@Composable
fun PeekingGrid(
  columns: Int,
  spacing: Dp,
  peekFraction: Float = 1f / 3f,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Layout(content = content, modifier = modifier.graphicsLayer(clip = false)) {
    measurables,
    constraints ->
    val screenWidth = constraints.maxWidth
    val spacingPx = spacing.roundToPx()
    val totalSpacing = spacingPx * (columns - 1)

    // Calculate column width based on peek fraction
    // Visible columns = (1 - peekFraction) + (columns - 2) + (1 - peekFraction)
    //                 = columns - 2*peekFraction
    // Formula: columnWidth * (columns - 2*peekFraction) + (columns - 1) * spacing = screenWidth
    val visibleColumns = columns - 2 * peekFraction
    val columnWidth = ((screenWidth - totalSpacing) / visibleColumns).toInt()

    // Offset by peekFraction of column width to the left
    val offsetX = -(columnWidth * peekFraction).toInt()

    // Measure all placeables with the calculated column size
    val placeables =
      measurables.map { measurable ->
        measurable.measure(
          constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = columnWidth,
            maxHeight = columnWidth
          )
        )
      }

    // Calculate total height needed
    val rows = (placeables.size + columns - 1) / columns
    val totalHeight = rows * columnWidth + (rows - 1) * spacingPx

    layout(screenWidth, totalHeight) {
      placeables.forEachIndexed { index, placeable ->
        val column = index % columns
        val row = index / columns

        val x = offsetX + column * (columnWidth + spacingPx)
        val y = row * (columnWidth + spacingPx)

        placeable.place(x, y)
      }
    }
  }
}

@Composable
fun ImageGridX(
  onDragStart: (url: String, startAbsPx: Offset, itemSizePx: IntSize) -> Unit = { _, _, _ -> },
  onDrag: (deltaPx: Offset) -> Unit = {},
  onDragEnd: () -> Unit = {}
) {
  val imageUrls =
    listOf(
      "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEg2TV7oOK294sKPqg0YCmyvvEqU3AAImAnriKzBlI2v_ud66W9mkq_-PwK8pwzp-8_varipotBaSco02bQQAkfuLaajQdWzG3GiFJYZUQppYwAWsOhPOO6cwzdOrpI_xre4-E2T/s400/express+bus+009.jpg",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&h=400&fit=crop",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEg2TV7oOK294sKPqg0YCmyvvEqU3AAImAnriKzBlI2v_ud66W9mkq_-PwK8pwzp-8_varipotBaSco02bQQAkfuLaajQdWzG3GiFJYZUQppYwAWsOhPOO6cwzdOrpI_xre4-E2T/s400/express+bus+009.jpg",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&h=400&fit=crop",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1515377905703-c4788e51af15?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1560750588-73207b1ef5b8?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1616683693504-3ea7e9ad6fec?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1521119989659-a83eee488004?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=400&h=400&fit=crop"
    )

  PeekingGrid(
    columns = 5,
    spacing = 8.dp,
    peekFraction = 1f / 3f, // 1/3 of outer columns extend outside viewport
    modifier = Modifier.fillMaxWidth()
  ) {
    imageUrls.forEach { imageUrl ->
      DraggableImage(
        url = imageUrl,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd
      )
    }
  }
}

@Composable
fun ImageGridC() {
  val imageUrls =
    listOf(
      "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEg2TV7oOK294sKPqg0YCmyvvEqU3AAImAnriKzBlI2v_ud66W9mkq_-PwK8pwzp-8_varipotBaSco02bQQAkfuLaajQdWzG3GiFJYZUQppYwAWsOhPOO6cwzdOrpI_xre4-E2T/s400/express+bus+009.jpg",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400&h=400&fit=crop",
      "https://live.staticflickr.com/3895/14938267498_50d9d5d454_b.jpg",
      "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1616683693504-3ea7e9ad6fec?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1521119989659-a83eee488004?w=400&h=400&fit=crop",
      "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=400&h=400&fit=crop"
    )

  val columnSpacing = 8.dp

  Layout(
    content = {
      imageUrls.forEach { imageUrl ->
        AsyncImage(
          model = imageUrl,
          contentDescription = "Skin progress photo",
          modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE0E0E0)),
          contentScale = ContentScale.Crop
        )
      }
    },
    modifier = Modifier.fillMaxWidth().graphicsLayer(clip = false)
  ) { measurables, constraints ->
    val screenWidth = constraints.maxWidth
    val spacing = columnSpacing.roundToPx()
    val totalSpacing = spacing * 4

    // Calculate column width: 3 × (screenWidth - 4 × spacing) / 13
    val columnWidth = (screenWidth - totalSpacing) * 3 / 13

    // Offset by 1/3 of column width to the left
    val offsetX = -columnWidth / 3

    // Measure all placeables with the calculated column size
    val placeables =
      measurables.map { measurable ->
        measurable.measure(
          constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = columnWidth,
            maxHeight = columnWidth
          )
        )
      }

    // Calculate total height needed (3 rows)
    val rows = (placeables.size + 4) / 5
    val totalHeight = rows * columnWidth + (rows - 1) * spacing

    layout(screenWidth, totalHeight) {
      placeables.forEachIndexed { index, placeable ->
        val column = index % 5
        val row = index / 5

        val x = offsetX + column * (columnWidth + spacing)
        val y = row * (columnWidth + spacing)

        placeable.place(x, y)
      }
    }
  }
}
