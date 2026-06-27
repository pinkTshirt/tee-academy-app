package com.teeacademy.app.ui.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.Case
import com.teeacademy.app.domain.model.Figure
import com.teeacademy.app.domain.repository.CaseRepository
import com.teeacademy.app.domain.repository.FigureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CaseListViewModel @Inject constructor(
    caseRepository: CaseRepository
) : ViewModel() {
    val cases: StateFlow<List<Case>> = caseRepository.getAllCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

data class CaseDetailUiState(
    val case: Case? = null,
    val figures: List<Figure> = emptyList(),
    val revealedFindings: Boolean = false,
    val selectedDecisionIndex: Int? = null
)

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    private val caseRepository: CaseRepository,
    private val figureRepository: FigureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val caseId: String = savedStateHandle.get<String>("caseId") ?: ""

    private val revealState = kotlinx.coroutines.flow.MutableStateFlow(false)
    private val decisionState = kotlinx.coroutines.flow.MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CaseDetailUiState> = caseRepository.getCaseById(caseId)
        .flatMapLatest { case ->
            if (case == null) {
                flowOf(CaseDetailUiState())
            } else {
                combine(
                    figureRepository.getFiguresForCase(caseId),
                    revealState,
                    decisionState
                ) { figures, revealed, decisionIndex ->
                    CaseDetailUiState(case, figures, revealed, decisionIndex)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CaseDetailUiState())

    fun revealFindings() { revealState.value = true }
    fun selectDecision(index: Int) { decisionState.value = index }
}
