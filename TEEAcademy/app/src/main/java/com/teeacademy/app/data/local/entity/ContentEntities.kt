package com.teeacademy.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val iconRes: String
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val moduleId: String,
    val orderIndex: Int,
    val title: String
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val orderIndex: Int,
    val title: String,
    val objective: String,
    /** Serialized JSON array of LessonBodyBlock — see SeedModels.kt for the wire format. */
    val bodyBlocksJson: String,
    val label: String, // "MK" | "ADV"
    val estimatedMinutes: Int,
    val sourceCodesCsv: String,
    val relatedQuizItemIdsCsv: String,
    val relatedGlossaryTermIdsCsv: String
)

@Entity(tableName = "figures")
data class FigureEntity(
    @PrimaryKey val id: String,
    val lessonId: String?,
    val caseId: String?,
    val type: String, // FigureType name
    val localAssetPath: String,
    val caption: String,
    val altText: String,
    val sourceCode: String,
    val licenseString: String,
    val orderInLesson: Int
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val lessonId: String?,
    val caseId: String?,
    val localAssetPath: String?,
    val remoteUrl: String?,
    val caption: String,
    val transcript: String?,
    val durationSec: Int,
    val sourceCode: String?,
    val licenseString: String?,
    val status: String // VideoStatus name
)

@Entity(tableName = "glossary_terms")
data class GlossaryTermEntity(
    @PrimaryKey val id: String,
    val term: String,
    val definition: String,
    val relatedLessonIdsCsv: String
)

@Entity(tableName = "quiz_items")
data class QuizItemEntity(
    @PrimaryKey val id: String,
    val lessonId: String?,
    val label: String,
    val stem: String,
    /** Serialized JSON array of String options. */
    val optionsJson: String,
    val correctIndex: Int,
    val explanation: String,
    val sourceCode: String
)

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val presentation: String,
    val imagingFigureIdsCsv: String,
    val imagingVideoIdsCsv: String,
    val interpretation: String,
    val decisionQuestion: String,
    /** Serialized JSON array of {text, rationale}. */
    val decisionOptionsJson: String,
    val teachingPearl: String,
    val sourceCode: String,
    val relatedLessonIdsCsv: String
)

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val code: String,
    val fullCitation: String,
    val url: String?,
    val reliabilityTier: Int,
    val licenseStatus: String,
    val reuseCleared: Boolean,
    val reuseConstraints: String
)
