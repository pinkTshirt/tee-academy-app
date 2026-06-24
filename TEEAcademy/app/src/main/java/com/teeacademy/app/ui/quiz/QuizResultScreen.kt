package com.teeacademy.app.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * v1 shows the aggregate score only. A full MK/ADV and per-module
 * breakdown (per UX spec Section 6) requires persisting the per-question
 * result list across the nav boundary — flagged as a v2 enhancement once
 * a graph-scoped ViewModel or a dedicated QuizSessionRepository is added.
 */
@Composable
fun QuizResultScreen(
    correct: Int,
    total: Int,
    onRetake: () -> Unit
) {
    val percent = if (total == 0) 0 else (correct * 100) / total

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quiz Complete", style = MaterialTheme.typography.headlineSmall)
            Text(
                "$correct / $total correct ($percent%)",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = onRetake, modifier = Modifier.padding(top = 24.dp)) {
                Text("Take Another Quiz")
            }
        }
    }
}
