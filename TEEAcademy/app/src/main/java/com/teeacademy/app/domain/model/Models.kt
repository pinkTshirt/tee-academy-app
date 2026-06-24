package com.teeacademy.app.domain.model

enum class LessonLabel { MUST_KNOW, ADVANCED }

enum class FigureType { ORIGINAL_DIAGRAM, CC_LICENSED_ASSET, CASE_JOURNAL_FIGURE }

enum class VideoStatus { LIVE, PLACEHOLDER_PENDING_LICENSED_ASSET, LINKED_EXTERNAL }

data class Module(
    val id: String,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val iconRes: String,
    val completionPercent: Int = 0
)

data class Chapter(
    val id: String,
    val moduleId: String,
    val orderIndex: Int,
    val title: String
)

sealed class LessonBodyBlock {
    data class Paragraph(val text: String) : LessonBodyBlock()
    data class KeyPoints(val items: List<String>) : LessonBodyBlock()
    data class Pitfall(val text: String) : LessonBodyBlock()
    data class FigureRef(val figureId: String) : LessonBodyBlock()
    data class VideoRef(val videoId: String) : LessonBodyBlock()
}

data class Lesson(
    val id: String,
    val chapterId: String,
    val orderIndex: Int,
    val title: String,
    val objective: String,
    val label: LessonLabel,
    val estimatedMinutes: Int,
    val bodyBlocks: List<LessonBodyBlock>,
    val sourceCodes: List<String>,
    val relatedQuizItemIds: List<String>,
    val relatedGlossaryTermIds: List<String>,
    val isCompleted: Boolean = false
)

data class Figure(
    val id: String,
    val lessonId: String?,
    val caseId: String?,
    val type: FigureType,
    val localAssetPath: String,
    val caption: String,
    val altText: String,
    val sourceCode: String,
    val licenseString: String,
    val orderInLesson: Int
)

data class Video(
    val id: String,
    val lessonId: String?,
    val caseId: String?,
    val localAssetPath: String?,
    val remoteUrl: String?,
    val caption: String,
    val transcript: String?,
    val durationSec: Int,
    val sourceCode: String?,
    val licenseString: String?,
    val status: VideoStatus
)

data class GlossaryTerm(
    val id: String,
    val term: String,
    val definition: String,
    val relatedLessonIds: List<String>
)

data class QuizItem(
    val id: String,
    val lessonId: String?,
    val label: LessonLabel,
    val stem: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val sourceCode: String
)

data class QuizAttempt(
    val id: Long = 0,
    val quizItemId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long
)

data class DecisionOption(val text: String, val rationale: String)

data class Case(
    val id: String,
    val title: String,
    val presentation: String,
    val imagingFigureIds: List<String>,
    val imagingVideoIds: List<String>,
    val interpretation: String,
    val decisionQuestion: String,
    val decisionOptions: List<DecisionOption>,
    val teachingPearl: String,
    val sourceCode: String,
    val relatedLessonIds: List<String>
)

data class Source(
    val code: String,
    val fullCitation: String,
    val url: String?,
    val reliabilityTier: Int,
    val licenseStatus: String,
    val reuseCleared: Boolean,
    val reuseConstraints: String
)

enum class BookmarkEntityType { LESSON, FIGURE, VIDEO, CASE, QUIZ_ITEM }

data class Bookmark(
    val id: Long = 0,
    val entityType: BookmarkEntityType,
    val entityId: String,
    val createdAt: Long
)

data class Highlight(
    val id: Long = 0,
    val lessonId: String,
    val startOffset: Int,
    val endOffset: Int,
    val createdAt: Long
)

data class Note(
    val id: Long = 0,
    val lessonId: String,
    val highlightId: Long?,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long
)

/** Unified result item for global search (UX spec Section 8). */
data class SearchResult(
    val entityType: String, // "lesson" | "figure" | "glossary" | "quiz_item" | "case"
    val entityId: String,
    val title: String,
    val breadcrumb: String,
    val label: LessonLabel?
)
