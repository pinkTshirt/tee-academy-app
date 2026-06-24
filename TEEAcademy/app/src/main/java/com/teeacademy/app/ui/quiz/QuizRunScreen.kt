package com.teeacademy.app.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.core.designsystem.components.CitationChip
import com.teeacademy.app.core.designsystem.components.LessonLabelBadge

@Composable
fun QuizRunScreen(
    scopeType: String,
    scopeId: String,
    count: Int,
    practiceMode: Boolean,
    onFinished: (correct: Int, total: Int) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.runState.collectAsStateWithLifecycle()

    LaunchedEffect(scopeType, scopeId, count, practiceMode) {
        viewModel.startQuiz(scopeType, scopeId, count, practiceMode)
    }

    LaunchedEffect(state.finished) {
        if (state.finished) onFinished(state.correctCount, state.items.size)
    }

    // Exam-mode integrity rule (UX spec Section 6): no back-navigation once
    // an exam run has started, so a question already answered can't be
    // revisited and changed after seeing later questions. This was
    // documented in the original spec but never actually enforced in the
    // generated code — this BackHandler closes that gap. Practice mode is
    // intentionally exempt: free navigation there is correct per spec.
    if (!practiceMode && state.items.isNotEmpty() && !state.finished) {
        BackHandler(enabled = true) { /* consume — exam mode blocks back press */ }
    }

    if (state.items.isEmpty()) return
    val currentItem = state.items[state.currentIndex]

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1f) / state.items.size },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Question ${state.currentIndex + 1} of ${state.items.size}",
                style = MaterialTheme.typography.labelSmall
            )
            LessonLabelBadge(label = currentItem.label)
            Text(currentItem.stem, style = MaterialTheme.typography.titleLarge)

            currentItem.options.forEachIndexed { index, option ->
                AnswerOption(
                    text = option,
                    index = index,
                    selected = state.selectedIndexThisQuestion == index,
                    isCorrect = if (state.showFeedback) index == currentItem.correctIndex else null,
                    isWrongSelected = state.showFeedback && state.selectedIndexThisQuestion == index && index != currentItem.correctIndex,
                    onClick = { viewModel.selectAnswer(index) }
                )
            }

            if (state.showFeedback) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(currentItem.explanation, style = MaterialTheme.typography.bodyMedium)
                    CitationChip(sourceCode = currentItem.sourceCode, onClick = {})
                }
                Button(onClick = { viewModel.nextQuestion() }) { Text("Next question") }
            } else {
                Button(
                    onClick = { viewModel.submitAnswer() },
                    enabled = state.selectedIndexThisQuestion != null
                ) { Text("Submit") }
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    index: Int,
    selected: Boolean,
    isCorrect: Boolean?,
    isWrongSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isCorrect == true -> MaterialTheme.colorScheme.primaryContainer
        isWrongSelected -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
