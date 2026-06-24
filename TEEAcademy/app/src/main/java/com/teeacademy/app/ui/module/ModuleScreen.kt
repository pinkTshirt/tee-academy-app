package com.teeacademy.app.ui.module

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.core.designsystem.components.LessonLabelBadge
import com.teeacademy.app.domain.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(
    moduleId: String,
    onLessonClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text((state as? ModuleUiState.Success)?.module?.title ?: "Module")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is ModuleUiState.Loading -> {}
            is ModuleUiState.Success -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    LinearProgressIndicator(
                        progress = { s.module.completionPercent / 100f },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                    LazyColumn {
                        items(s.chapters, key = { it.chapter.id }) { chapterWithLessons ->
                            ChapterAccordion(
                                title = chapterWithLessons.chapter.title,
                                lessons = chapterWithLessons.lessons,
                                onLessonClick = onLessonClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterAccordion(
    title: String,
    lessons: List<Lesson>,
    onLessonClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
        if (expanded) {
            lessons.forEach { lesson ->
                LessonRow(lesson = lesson, onClick = { onLessonClick(lesson.id) })
            }
        }
    }
}

@Composable
private fun LessonRow(lesson: Lesson, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(lesson.title, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LessonLabelBadge(label = lesson.label)
                Text(
                    "${lesson.estimatedMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            if (lesson.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = if (lesson.isCompleted) "Completed" else "Not completed",
            tint = if (lesson.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    }
}
