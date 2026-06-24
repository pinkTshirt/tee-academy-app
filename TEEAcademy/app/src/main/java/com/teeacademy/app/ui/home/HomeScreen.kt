package com.teeacademy.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.domain.model.Module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onModuleClick: (String) -> Unit,
    onResumeLesson: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onGlossaryClick: () -> Unit,
    onReferencesClick: () -> Unit,
    onCasesClick: () -> Unit,
    onQuizClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TEE Academy") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            is HomeUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            }
            is HomeUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    if (s.resumeLessonId != null) {
                        ResumeCard(lessonId = s.resumeLessonId, onClick = { onResumeLesson(s.resumeLessonId) })
                    }
                    QuickAccessRow(onGlossaryClick, onReferencesClick, onCasesClick, onQuizClick)
                    ModuleGrid(modules = s.modules, onModuleClick = onModuleClick)
                }
            }
        }
    }
}

@Composable
private fun ResumeCard(lessonId: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Continue where you left off", style = MaterialTheme.typography.titleMedium)
            Text(lessonId, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun QuickAccessRow(
    onGlossaryClick: () -> Unit,
    onReferencesClick: () -> Unit,
    onCasesClick: () -> Unit,
    onQuizClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickAccessItem("Cases", Icons.Filled.MenuBook, onCasesClick)
        QuickAccessItem("Quiz", Icons.Filled.MenuBook, onQuizClick)
        QuickAccessItem("Glossary", Icons.Filled.MenuBook, onGlossaryClick)
        QuickAccessItem("References", Icons.Filled.MenuBook, onReferencesClick)
    }
}

@Composable
private fun QuickAccessItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(onClick = onClick) { Icon(icon, contentDescription = label) }
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ModuleGrid(modules: List<Module>, onModuleClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 220.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(modules, key = { it.id }) { module ->
            ModuleCard(module = module, onClick = { onModuleClick(module.id) })
        }
    }
}

@Composable
private fun ModuleCard(module: Module, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(module.title, style = MaterialTheme.typography.titleMedium)
            Text(
                module.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            LinearProgressIndicator(
                progress = { module.completionPercent / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}
