package com.teeacademy.app.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizLaunchScreen(
    onStartQuiz: (scopeType: String, scopeId: String, count: Int, practiceMode: Boolean) -> Unit
) {
    var count by remember { mutableIntStateOf(10) }
    var practiceMode by remember { mutableStateOf(true) }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Start a Quiz", style = MaterialTheme.typography.headlineSmall)

            Text("Scope", style = MaterialTheme.typography.titleMedium)
            Text(
                "Mixed (all modules) — module-scoped quizzes are available from each Module screen.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text("Question count", style = MaterialTheme.typography.titleMedium)
            Column {
                listOf(10, 20).forEach { option ->
                    CountOptionRow(option, count == option) { count = option }
                }
            }

            Text("Mode", style = MaterialTheme.typography.titleMedium)
            Column {
                ModeRow("Practice (feedback after each question)", practiceMode) { practiceMode = true }
                ModeRow("Exam (feedback at the end)", !practiceMode) { practiceMode = false }
            }

            Button(onClick = { onStartQuiz("mixed", "", count, practiceMode) }) {
                Text("Start Quiz")
            }
        }
    }
}

@Composable
private fun CountOptionRow(count: Int, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text("$count questions")
    }
}

@Composable
private fun ModeRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label)
    }
}
