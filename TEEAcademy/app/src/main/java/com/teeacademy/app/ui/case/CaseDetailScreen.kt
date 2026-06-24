package com.teeacademy.app.ui.case

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.teeacademy.app.core.designsystem.components.CitationChip

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    caseId: String,
    onLessonRefClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CaseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val case = state.case ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(case.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SectionLabel("Presentation"); Text(case.presentation, style = MaterialTheme.typography.bodyLarge) }

            item {
                SectionLabel("Imaging Findings")
                if (!state.revealedFindings) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.revealFindings() }
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(24.dp)
                    ) {
                        Text("Tap to reveal imaging findings", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Try to interpret the case yourself first.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyRow {
                        items(state.figures, key = { it.id }) { figure ->
                            AsyncImage(
                                model = "file:///android_asset/${figure.localAssetPath}",
                                contentDescription = figure.altText,
                                error = androidx.compose.ui.res.painterResource(com.teeacademy.app.R.drawable.ic_image_placeholder),
                                modifier = Modifier
                                    .height(180.dp)
                                    .padding(end = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                }
            }

            if (state.revealedFindings) {
                item {
                    SectionLabel("Interpretation")
                    Text(case.interpretation, style = MaterialTheme.typography.bodyLarge)
                    case.relatedLessonIds.forEach { lessonId ->
                        CitationChip(sourceCode = "Review: $lessonId", onClick = { onLessonRefClick(lessonId) })
                    }
                }

                item {
                    SectionLabel("Decision Point")
                    Text(case.decisionQuestion, style = MaterialTheme.typography.titleMedium)
                    case.decisionOptions.forEachIndexed { index, option ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectDecision(index) }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = state.selectedDecisionIndex == index,
                                    onClick = { viewModel.selectDecision(index) }
                                )
                                Text(option.text)
                            }
                            if (state.selectedDecisionIndex == index) {
                                Text(
                                    option.rationale,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    SectionLabel("Teaching Pearl")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(case.teachingPearl, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}
