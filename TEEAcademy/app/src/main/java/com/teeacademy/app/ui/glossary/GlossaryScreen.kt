package com.teeacademy.app.ui.glossary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teeacademy.app.domain.model.GlossaryTerm
import com.teeacademy.app.domain.repository.GlossaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GlossaryViewModel @Inject constructor(
    private val glossaryRepository: GlossaryRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val terms: StateFlow<List<GlossaryTerm>> = _query
        .flatMapLatest { q -> if (q.isBlank()) glossaryRepository.getAllTerms() else glossaryRepository.search(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(q: String) { _query.value = q }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(viewModel: GlossaryViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val terms by viewModel.terms.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Glossary") }) }) { padding ->
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("Filter terms...") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            LazyColumn {
                items(terms, key = { it.id }) { term ->
                    androidx.compose.foundation.layout.Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(term.term, style = MaterialTheme.typography.titleMedium)
                        Text(term.definition, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
