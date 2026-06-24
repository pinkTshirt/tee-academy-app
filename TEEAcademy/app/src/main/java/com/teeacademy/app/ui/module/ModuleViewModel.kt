package com.teeacademy.app.ui.module

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.Chapter
import com.teeacademy.app.domain.model.Lesson
import com.teeacademy.app.domain.model.Module
import com.teeacademy.app.domain.repository.LessonRepository
import com.teeacademy.app.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ChapterWithLessons(val chapter: Chapter, val lessons: List<Lesson>)

sealed class ModuleUiState {
    data object Loading : ModuleUiState()
    data class Success(val module: Module, val chapters: List<ChapterWithLessons>) : ModuleUiState()
}

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val lessonRepository: LessonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val moduleId: String = savedStateHandle.get<String>("moduleId") ?: ""

    val uiState: StateFlow<ModuleUiState> = moduleRepository.getModuleById(moduleId)
        .flatMapLatest { module ->
            if (module == null) {
                kotlinx.coroutines.flow.flowOf(ModuleUiState.Loading)
            } else {
                moduleRepository.getChaptersForModule(moduleId).flatMapLatest { chapters ->
                    if (chapters.isEmpty()) {
                        kotlinx.coroutines.flow.flowOf(ModuleUiState.Success(module, emptyList()))
                    } else {
                        combine(chapters.map { chapter ->
                            lessonRepository.getLessonsForChapter(chapter.id).combine(
                                kotlinx.coroutines.flow.flowOf(chapter)
                            ) { lessons, ch -> ChapterWithLessons(ch, lessons) }
                        }) { array -> ModuleUiState.Success(module, array.toList()) }
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ModuleUiState.Loading)
}
