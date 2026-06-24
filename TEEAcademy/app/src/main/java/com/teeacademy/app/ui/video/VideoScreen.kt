package com.teeacademy.app.ui.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.teeacademy.app.domain.model.VideoStatus

private const val FRAME_STEP_MS = 33L // ~1 frame at 30fps; coarse but adequate for v1 review use

@Composable
fun VideoScreen(
    videoId: String,
    onClose: () -> Unit,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val video by viewModel.video.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when {
        video == null -> {}
        video!!.status != VideoStatus.LIVE -> {
            EmptyVideoState(onClose = onClose, isExternal = video!!.status == VideoStatus.LINKED_EXTERNAL)
        }
        else -> {
            val mediaUri = video!!.localAssetPath?.let { "asset:///$it" } ?: video!!.remoteUrl.orEmpty()
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(mediaUri))
                    prepare()
                }
            }
            var isLooping by remember { mutableStateOf(true) }
            exoPlayer.repeatMode = if (isLooping) androidx.media3.common.Player.REPEAT_MODE_ONE
                else androidx.media3.common.Player.REPEAT_MODE_OFF

            DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                    IconButton(onClick = { isLooping = !isLooping }) {
                        Icon(
                            Icons.Filled.Loop,
                            contentDescription = "Loop",
                            tint = if (isLooping) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                AndroidView(
                    factory = { PlayerView(it).apply { player = exoPlayer } },
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )

                // Frame-step controls — important for valve-motion review,
                // not just the standard ±10s skip most players default to.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { exoPlayer.seekTo(exoPlayer.currentPosition - FRAME_STEP_MS) }) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Step back one frame")
                    }
                    IconButton(onClick = { exoPlayer.seekTo(exoPlayer.currentPosition + FRAME_STEP_MS) }) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Step forward one frame")
                    }
                }

                Column(Modifier.padding(16.dp)) {
                    Text(video!!.caption, style = MaterialTheme.typography.bodyMedium)
                    video!!.licenseString?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyVideoState(onClose: () -> Unit, isExternal: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
        }
        Text(
            if (isExternal) "This topic is covered by an external simulator — see the linked resource in References."
            else "Video coming soon — pending a licensed asset. See Phase 5 ingestion plan for status.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
