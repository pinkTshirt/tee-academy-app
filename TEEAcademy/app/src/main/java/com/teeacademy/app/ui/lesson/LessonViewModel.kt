package com.teeacademy.app.ui.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.BookmarkEntityType
import com.teeacademy.app.domain.model.Figure
import com.teeacademy.app.domain.model.Lesson
import com.teeacademy.app.domain.model.Note
import com.teeacademy.app.domain.repository.BookmarkRepository
import com.teeacademy.app.domain.repository.FigureRepository
import com.teeacademy.app.domain.repository.LessonProgressRepository
import com.teeacademy.app.domain.repository.LessonRepository
import com.teeacademy.app.domain.repository.NoteRepository
import com.teeacademy.app.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LessonUiState {
    data object Loading : LessonUiState()
    data class Success(
        val lesson: Lesson,
        val figures: Map<String, Figure>,
        val isBookmarked: Boolean,
        val notes: List<Note>
    ) : LessonUiState()
}

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val lessonRepository: LessonRepository,
    private val figureRepository: FigureRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val noteRepository: NoteRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val sourceRepository: SourceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val lessonId: String = savedStateHandle.get<String>("lessonId") ?: ""

    val uiState: StateFlow<LessonUiState> = lessonRepository.getLessonById(lessonId)
        .flatMapLatest { lesson ->
            if (lesson == null) {
                flowOf(LessonUiState.Loading)
            } else {
                combine(
                    figureRepository.getFiguresForLesson(lessonId),
                    bookmarkRepository.isBookmarked(BookmarkEntityType.LESSON, lessonId),
                    noteRepository.getNotesForLesson(lessonId)
                ) { figures, isBookmarked, notes ->
                    LessonUiState.Success(
                        lesson = lesson,
                        figures = figures.associateBy { it.id },
                        isBookmarked = isBookmarked,
                        notes = notes
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LessonUiState.Loading)

    init {
        viewModelScope.launch {
            lessonProgressRepository.markOpened(lessonId)
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(BookmarkEntityType.LESSON, lessonId)
        }
    }

    fun markCompleted(completed: Boolean) {
        viewModelScope.launch {
            lessonProgressRepository.markCompleted(lessonId, completed)
        }
    }

    fun saveNote(text: String) {
        viewModelScope.launch {
            noteRepository.saveNote(
                Note(
                    lessonId = lessonId,
                    highlightId = null,
                    text = text,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun fullCitation(sourceCode: String): String =
        sourceRepository.getSourceByCode(sourceCode)?.fullCitation ?: sourceCode
}
