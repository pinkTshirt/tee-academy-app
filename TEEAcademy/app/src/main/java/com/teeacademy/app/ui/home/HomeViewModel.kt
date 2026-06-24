package com.teeacademy.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.Bookmark
import com.teeacademy.app.domain.model.Module
import com.teeacademy.app.domain.repository.BookmarkRepository
import com.teeacademy.app.domain.repository.LessonProgressRepository
import com.teeacademy.app.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val modules: List<Module>,
        val resumeLessonId: String?,
        val recentBookmarks: List<Bookmark>
    ) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _resumeLessonId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        moduleRepository.getAllModules(),
        bookmarkRepository.getAllBookmarks(),
        _resumeLessonId
    ) { modules, bookmarks, resumeId ->
        HomeUiState.Success(
            modules = modules,
            resumeLessonId = resumeId,
            recentBookmarks = bookmarks.take(8)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    init {
        viewModelScope.launch {
            _resumeLessonId.value = lessonProgressRepository.getMostRecentLessonId()
        }
    }
}
