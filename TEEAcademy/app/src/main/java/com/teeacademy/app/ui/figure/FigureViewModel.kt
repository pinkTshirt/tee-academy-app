package com.teeacademy.app.ui.figure

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.BookmarkEntityType
import com.teeacademy.app.domain.model.Figure
import com.teeacademy.app.domain.repository.BookmarkRepository
import com.teeacademy.app.domain.repository.FigureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FigureUiState(
    val figure: Figure? = null,
    val isBookmarked: Boolean = false
)

@HiltViewModel
class FigureViewModel @Inject constructor(
    private val figureRepository: FigureRepository,
    private val bookmarkRepository: BookmarkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val figureId: String = savedStateHandle.get<String>("figureId") ?: ""

    val uiState: StateFlow<FigureUiState> = combine(
        figureRepository.getFigureById(figureId),
        bookmarkRepository.isBookmarked(BookmarkEntityType.FIGURE, figureId)
    ) { figure, bookmarked -> FigureUiState(figure, bookmarked) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FigureUiState())

    fun toggleBookmark() {
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(BookmarkEntityType.FIGURE, figureId)
        }
    }
}
