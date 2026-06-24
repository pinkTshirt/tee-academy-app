package com.teeacademy.app.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.teeacademy.app.core.designsystem.components.CitationChip
import com.teeacademy.app.core.designsystem.components.KeyPointsBox
import com.teeacademy.app.core.designsystem.components.LessonLabelBadge
import com.teeacademy.app.core.designsystem.components.PitfallBox
import com.teeacademy.app.domain.model.Figure
import com.teeacademy.app.domain.model.Lesson
import com.teeacademy.app.domain.model.LessonBodyBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onFigureClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    onBack: () -> Unit,
    onNextLesson: (String) -> Unit,
    onPreviousLesson: (String) -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 840

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text((state as? LessonUiState.Success)?.lesson?.title ?: "Lesson") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    val s = state as? LessonUiState.Success
                    if (s != null) {
                        IconButton(onClick = { viewModel.toggleBookmark() }) {
                            Icon(
                                if (s.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is LessonUiState.Loading -> {}
            is LessonUiState.Success -> {
                if (isWideScreen) {
                    TwoColumnLessonBody(s, padding, onFigureClick, viewModel)
                } else {
                    SingleColumnLessonBody(s, padding, onFigureClick, viewModel)
                }
            }
        }
    }
}

@Composable
private fun SingleColumnLessonBody(
    s: LessonUiState.Success,
    padding: PaddingValues,
    onFigureClick: (String) -> Unit,
    viewModel: LessonViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { LessonHeader(s.lesson) }
        items(s.lesson.bodyBlocks) { block ->
            BodyBlockView(block, s.figures, onFigureClick)
        }
        item { CompletionRow(s.lesson.isCompleted, viewModel) }
    }
}

@Composable
private fun TwoColumnLessonBody(
    s: LessonUiState.Success,
    padding: PaddingValues,
    onFigureClick: (String) -> Unit,
    viewModel: LessonViewModel
) {
    // Per UX spec Section 3: text left (60%), pinned figure pane right (40%).
    // v1 pins the first referenced figure; a fuller implementation would
    // track scroll position to swap the pinned figure as the reader passes
    // each figureRef block — flagged as a v2 enhancement.
    val firstFigure = s.lesson.bodyBlocks
        .filterIsInstance<LessonBodyBlock.FigureRef>()
        .firstOrNull()?.let { s.figures[it.figureId] }

    Row(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier = Modifier.weight(0.6f).fillMaxHeight().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LessonHeader(s.lesson) }
            items(s.lesson.bodyBlocks) { block ->
                BodyBlockView(block, s.figures, onFigureClick)
            }
            item { CompletionRow(s.lesson.isCompleted, viewModel) }
        }
        Box(
            modifier = Modifier.weight(0.4f).fillMaxHeight().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (firstFigure != null) {
                AsyncImage(
                    model = "file:///android_asset/${firstFigure.localAssetPath}",
                    contentDescription = firstFigure.altText,
                    error = androidx.compose.ui.res.painterResource(com.teeacademy.app.R.drawable.ic_image_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .noIndicationClickable { onFigureClick(firstFigure.id) }
                )
            }
        }
    }
}

@Composable
private fun LessonHeader(lesson: Lesson) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            LessonLabelBadge(label = lesson.label)
            Text("${lesson.estimatedMinutes} min", style = MaterialTheme.typography.labelSmall)
            lesson.sourceCodes.forEach { code ->
                CitationChip(sourceCode = code, onClick = { /* expand citation card, v2 */ })
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                lesson.objective,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BodyBlockView(
    block: LessonBodyBlock,
    figures: Map<String, Figure>,
    onFigureClick: (String) -> Unit
) {
    when (block) {
        is LessonBodyBlock.Paragraph -> Text(block.text, style = MaterialTheme.typography.bodyLarge)
        is LessonBodyBlock.KeyPoints -> KeyPointsBox(points = block.items, modifier = Modifier.fillMaxWidth())
        is LessonBodyBlock.Pitfall -> PitfallBox(text = block.text, modifier = Modifier.fillMaxWidth())
        is LessonBodyBlock.FigureRef -> {
            val figure = figures[block.figureId]
            if (figure != null) {
                InlineFigure(figure = figure, onClick = { onFigureClick(figure.id) })
            }
        }
        is LessonBodyBlock.VideoRef -> {
            // Lightweight inline marker; full playback lives in VideoScreen.
            Text("\u25B6 Video: ${block.videoId}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun InlineFigure(figure: Figure, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().noIndicationClickable(onClick)) {
        AsyncImage(
            model = "file:///android_asset/${figure.localAssetPath}",
            contentDescription = figure.altText,
            error = androidx.compose.ui.res.painterResource(com.teeacademy.app.R.drawable.ic_image_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Text(
            figure.caption,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            figure.licenseString,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun CompletionRow(isCompleted: Boolean, viewModel: LessonViewModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isCompleted, onCheckedChange = { viewModel.markCompleted(it) })
        Text("Mark lesson as completed")
        if (isCompleted) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun Modifier.noIndicationClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
}
