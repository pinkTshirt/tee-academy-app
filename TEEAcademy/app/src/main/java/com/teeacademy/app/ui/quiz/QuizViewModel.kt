package com.teeacademy.app.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.QuizAttempt
import com.teeacademy.app.domain.model.QuizItem
import com.teeacademy.app.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizRunState(
    val items: List<QuizItem> = emptyList(),
    val currentIndex: Int = 0,
    val selectedIndexThisQuestion: Int? = null,
    val showFeedback: Boolean = false,
    val isPracticeMode: Boolean = true,
    val correctCount: Int = 0,
    val finished: Boolean = false,
    // Per-question correctness, used by QuizResultScreen for MK/ADV breakdown.
    val results: List<Boolean> = emptyList()
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _runState = MutableStateFlow(QuizRunState())
    val runState: StateFlow<QuizRunState> = _runState.asStateFlow()

    fun startQuiz(scopeType: String, scopeId: String, count: Int, practiceMode: Boolean = true) {
        viewModelScope.launch {
            val items = when (scopeType) {
                "module" -> quizRepository.getRandomQuizItemsForModule(scopeId, count)
                else -> quizRepository.getRandomQuizItems(count) // "mixed"
            }
            _runState.value = QuizRunState(items = items, isPracticeMode = practiceMode)
        }
    }

    fun selectAnswer(index: Int) {
        val s = _runState.value
        if (s.showFeedback && s.isPracticeMode) return // already locked in for this question
        _runState.value = s.copy(selectedIndexThisQuestion = index)
    }

    fun submitAnswer() {
        val s = _runState.value
        val selected = s.selectedIndexThisQuestion ?: return
        val currentItem = s.items[s.currentIndex]
        val isCorrect = selected == currentItem.correctIndex

        viewModelScope.launch {
            quizRepository.recordAttempt(
                QuizAttempt(
                    quizItemId = currentItem.id,
                    selectedIndex = selected,
                    isCorrect = isCorrect,
                    attemptedAt = System.currentTimeMillis()
                )
            )
        }

        val updated = s.copy(
            correctCount = s.correctCount + if (isCorrect) 1 else 0,
            results = s.results + isCorrect
        )

        if (s.isPracticeMode) {
            // Practice mode: surface feedback now; the user advances explicitly
            // via the "Next question" button once they've read the explanation.
            _runState.value = updated.copy(showFeedback = true)
        } else {
            // Exam mode: no per-question feedback. Advance immediately —
            // otherwise the screen has nothing left to act on (this was a
            // real bug in the v1 generated code: showFeedback never became
            // true in exam mode, but the "Next question" button only ever
            // rendered when showFeedback was true, so the quiz silently
            // froze on the first question for every exam-mode run).
            _runState.value = updated
            advanceOrFinish(updated)
        }
    }

    fun nextQuestion() {
        advanceOrFinish(_runState.value)
    }

    private fun advanceOrFinish(s: QuizRunState) {
        val nextIndex = s.currentIndex + 1
        _runState.value = if (nextIndex >= s.items.size) {
            s.copy(finished = true)
        } else {
            s.copy(
                currentIndex = nextIndex,
                selectedIndexThisQuestion = null,
                showFeedback = false
            )
        }
    }
}
