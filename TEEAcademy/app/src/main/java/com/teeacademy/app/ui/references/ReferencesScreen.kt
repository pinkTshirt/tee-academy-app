package com.teeacademy.app.ui.references

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.domain.model.Source
import com.teeacademy.app.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReferencesViewModel @Inject constructor(
    sourceRepository: SourceRepository
) : ViewModel() {
    val sources: StateFlow<List<Source>> = sourceRepository.getAllSources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

private fun tierLabel(tier: Int): String = when (tier) {
    1 -> "Tier 1 — Guideline / Consensus"
    2 -> "Tier 2 — Review / Society Practical Guideline"
    else -> "Supplementary / Web-Sourced"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferencesScreen(viewModel: ReferencesViewModel = hiltViewModel()) {
    val sources by viewModel.sources.collectAsStateWithLifecycle()
    val grouped = sources.groupBy { it.reliabilityTier }.toSortedMap()

    Scaffold(topBar = { TopAppBar(title = { Text("References") }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            grouped.forEach { (tier, tierSources) ->
                item {
                    Text(
                        tierLabel(tier),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                items(tierSources, key = { it.code }) { source ->
                    androidx.compose.foundation.layout.Column(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Text("[${source.code}]", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(source.fullCitation, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${source.licenseStatus}${if (source.reuseCleared) " — figures usable per constraints" else " — text reference only"}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
