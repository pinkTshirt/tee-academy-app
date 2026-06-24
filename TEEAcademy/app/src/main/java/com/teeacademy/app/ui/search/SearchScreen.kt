package com.teeacademy.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.domain.model.SearchResult

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onLessonClick: (String) -> Unit,
    onCaseClick: (String) -> Unit,
    onClose: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::onQueryChange,
                        placeholder = { Text("Search lessons, glossary, cases...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                }
            )
        }
    ) { padding ->
        if (query.isBlank()) {
            return@Scaffold
        }
        if (results.isEmpty()) {
            Text(
                "No results — try broader terms.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            return@Scaffold
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(results, key = { "${it.entityType}_${it.entityId}" }) { result ->
                SearchResultRow(result) {
                    when (result.entityType) {
                        "lesson" -> onLessonClick(result.entityId)
                        "case" -> onCaseClick(result.entityId)
                        else -> { /* glossary results shown inline, no navigation target in v1 */ }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(result: SearchResult, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)
    ) {
        Text(result.breadcrumb, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(result.title, style = MaterialTheme.typography.titleMedium)
    }
}
