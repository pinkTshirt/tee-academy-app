package com.teeacademy.app.data.repository

import com.teeacademy.app.data.local.entity.*
import com.teeacademy.app.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import com.teeacademy.app.data.seed.SeedBodyBlock
import com.teeacademy.app.data.seed.SeedDecisionOption

private val json = Json { ignoreUnknownKeys = true }

private fun csvToList(csv: String): List<String> =
    if (csv.isBlank()) emptyList() else csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }

fun ModuleEntity.toDomain(completionPercent: Int = 0) =
    Module(id, orderIndex, title, description, iconRes, completionPercent)

fun ChapterEntity.toDomain() = Chapter(id, moduleId, orderIndex, title)

private fun SeedBodyBlock.toDomain(): LessonBodyBlock = when (type) {
    "paragraph" -> LessonBodyBlock.Paragraph(text.orEmpty())
    "keyPoints" -> LessonBodyBlock.KeyPoints(items.orEmpty())
    "pitfall" -> LessonBodyBlock.Pitfall(text.orEmpty())
    "figureRef" -> LessonBodyBlock.FigureRef(figureId.orEmpty())
    "videoRef" -> LessonBodyBlock.VideoRef(videoId.orEmpty())
    else -> LessonBodyBlock.Paragraph(text.orEmpty())
}

fun LessonEntity.toDomain(isCompleted: Boolean = false): Lesson {
    val blocks: List<SeedBodyBlock> = json.decodeFromString(bodyBlocksJson)
    return Lesson(
        id = id,
        chapterId = chapterId,
        orderIndex = orderIndex,
        title = title,
        objective = objective,
        label = if (label == "MK") LessonLabel.MUST_KNOW else LessonLabel.ADVANCED,
        estimatedMinutes = estimatedMinutes,
        bodyBlocks = blocks.map { it.toDomain() },
        sourceCodes = csvToList(sourceCodesCsv),
        relatedQuizItemIds = csvToList(relatedQuizItemIdsCsv),
        relatedGlossaryTermIds = csvToList(relatedGlossaryTermIdsCsv),
        isCompleted = isCompleted
    )
}

fun FigureEntity.toDomain() = Figure(
    id, lessonId, caseId,
    type = runCatching { FigureType.valueOf(type) }.getOrDefault(FigureType.ORIGINAL_DIAGRAM),
    localAssetPath, caption, altText, sourceCode, licenseString, orderInLesson
)

fun VideoEntity.toDomain() = Video(
    id, lessonId, caseId, localAssetPath, remoteUrl, caption, transcript, durationSec,
    sourceCode, licenseString,
    status = runCatching { VideoStatus.valueOf(status) }.getOrDefault(VideoStatus.PLACEHOLDER_PENDING_LICENSED_ASSET)
)

fun GlossaryTermEntity.toDomain() = GlossaryTerm(id, term, definition, csvToList(relatedLessonIdsCsv))

fun QuizItemEntity.toDomain(): QuizItem {
    val options: List<String> = json.decodeFromString(optionsJson)
    return QuizItem(
        id, lessonId,
        label = if (label == "MK") LessonLabel.MUST_KNOW else LessonLabel.ADVANCED,
        stem = stem, options = options, correctIndex = correctIndex,
        explanation = explanation, sourceCode = sourceCode
    )
}

fun CaseEntity.toDomain(): Case {
    val decisionOptions: List<SeedDecisionOption> = json.decodeFromString(decisionOptionsJson)
    return Case(
        id = id,
        title = title,
        presentation = presentation,
        imagingFigureIds = csvToList(imagingFigureIdsCsv),
        imagingVideoIds = csvToList(imagingVideoIdsCsv),
        interpretation = interpretation,
        decisionQuestion = decisionQuestion,
        decisionOptions = decisionOptions.map { DecisionOption(it.text, it.rationale) },
        teachingPearl = teachingPearl,
        sourceCode = sourceCode,
        relatedLessonIds = csvToList(relatedLessonIdsCsv)
    )
}

fun SourceEntity.toDomain() = Source(code, fullCitation, url, reliabilityTier, licenseStatus, reuseCleared, reuseConstraints)

fun BookmarkEntity.toDomain() = Bookmark(
    id, runCatching { BookmarkEntityType.valueOf(entityType.uppercase()) }.getOrDefault(BookmarkEntityType.LESSON),
    entityId, createdAt
)

fun NoteEntity.toDomain() = Note(id, lessonId, highlightId, text, createdAt, updatedAt)

fun QuizAttemptEntity.toDomain() = QuizAttempt(id, quizItemId, selectedIndex, isCorrect, attemptedAt)
