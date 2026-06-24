package com.teeacademy.app.ui.case

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun CaseListScreen(
    onCaseClick: (String) -> Unit,
    viewModel: CaseListViewModel = hiltViewModel()
) {
    val cases by viewModel.cases.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Case Library") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            items(cases, key = { it.id }) { case ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    onClick = { onCaseClick(case.id) }
                ) {
                    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
                        Text(case.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            case.presentation,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
