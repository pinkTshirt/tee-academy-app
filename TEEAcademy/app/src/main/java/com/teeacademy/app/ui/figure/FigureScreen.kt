package com.teeacademy.app.ui.figure

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.teeacademy.app.core.designsystem.imageMatteColor

@Composable
fun FigureScreen(
    figureId: String,
    onClose: () -> Unit,
    viewModel: FigureViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val figure = state.figure

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxSize().background(imageMatteColor())
    ) {
        if (figure != null) {
            AsyncImage(
                model = "file:///android_asset/${figure.localAssetPath}",
                contentDescription = figure.altText,
                error = androidx.compose.ui.res.painterResource(com.teeacademy.app.R.drawable.ic_image_placeholder),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan.x
                        }
                    }
                    .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset)
            )
        }

        // Top bar: close + bookmark.
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
            IconButton(onClick = { viewModel.toggleBookmark() }) {
                Icon(
                    if (state.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = Color.White
                )
            }
        }

        // Bottom sheet: caption + mandatory attribution string.
        if (figure != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                Text(figure.caption, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(
                    figure.licenseString,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
