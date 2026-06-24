package com.teeacademy.app.ui.library

import androidx.compose.foundation.clickable
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
import com.teeacademy.app.domain.model.Bookmark
import com.teeacademy.app.domain.model.BookmarkEntityType
import com.teeacademy.app.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    bookmarkRepository: BookmarkRepository
) : ViewModel() {
    val bookmarks: StateFlow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onLessonClick: (String) -> Unit,
    onFigureClick: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Library") }) }) { padding ->
        if (bookmarks.isEmpty()) {
            Text(
                "No bookmarks yet. Tap the bookmark icon on any lesson, figure, video, or case to save it here.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            return@Scaffold
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(bookmarks, key = { it.id }) { bookmark ->
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (bookmark.entityType) {
                                BookmarkEntityType.LESSON -> onLessonClick(bookmark.entityId)
                                BookmarkEntityType.FIGURE -> onFigureClick(bookmark.entityId)
                                else -> {}
                            }
                        }
                        .padding(16.dp)
                ) {
                    Text(bookmark.entityType.name, style = MaterialTheme.typography.labelSmall)
                    Text(bookmark.entityId, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
