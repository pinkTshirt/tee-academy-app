package com.teeacademy.app.data.seed

import kotlinx.serialization.Serializable

/**
 * These @Serializable classes mirror the JSON schema defined in Phase 5
 * (Content Ingestion Plan, Section 2) exactly. SeedLoader parses
 * the *.json files under assets/seed into these, then maps to Room entities for insertion.
 */

@Serializable
data class SeedModule(
    val id: String,
    val orderIndex: Int,
    val title: String,
    val description: String,
    val iconRes: String
)

@Serializable
data class SeedChapter(
    val id: String,
    val moduleId: String,
    val orderIndex: Int,
    val title: String
)

@Serializable
data class SeedBodyBlock(
    val type: String, // "paragraph" | "keyPoints" | "pitfall" | "figureRef" | "videoRef"
    val text: String? = null,
    val items: List<String>? = null,
    val figureId: String? = null,
    val videoId: String? = null
)

@Serializable
data class SeedLesson(
    val id: String,
    val chapterId: String,
    val orderIndex: Int,
    val title: String,
    val objective: String,
    val label: String,
    val estimatedMinutes: Int,
    val bodyBlocks: List<SeedBodyBlock>,
    val sourceCodes: List<String>,
    val relatedQuizItemIds: List<String> = emptyList(),
    val relatedGlossaryTermIds: List<String> = emptyList()
)

@Serializable
data class SeedFigure(
    val id: String,
    val lessonId: String? = null,
    val caseId: String? = null,
    val type: String,
    val localAssetPath: String,
    val caption: String,
    val altText: String,
    val sourceCode: String,
    val licenseString: String,
    val reuseCleared: Boolean,
    val orderInLesson: Int = 0
)

@Serializable
data class SeedVideo(
    val id: String,
    val lessonId: String? = null,
    val caseId: String? = null,
    val localAssetPath: String? = null,
    val remoteUrl: String? = null,
    val caption: String,
    val transcript: String? = null,
    val durationSec: Int = 0,
    val sourceCode: String? = null,
    val licenseString: String? = null,
    val status: String
)

@Serializable
data class SeedGlossaryTerm(
    val id: String,
    val term: String,
    val definition: String,
    val relatedLessonIds: List<String> = emptyList()
)

@Serializable
data class SeedQuizItem(
    val id: String,
    val lessonId: String? = null,
    val label: String,
    val stem: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val sourceCode: String
)

@Serializable
data class SeedDecisionOption(
    val text: String,
    val rationale: String
)

@Serializable
data class SeedDecisionPoint(
    val question: String,
    val options: List<String>,
    val rationaleByOption: List<String>
)

@Serializable
data class SeedCase(
    val id: String,
    val title: String,
    val presentation: String,
    val imagingFigureIds: List<String> = emptyList(),
    val imagingVideoIds: List<String> = emptyList(),
    val interpretation: String,
    val decisionPoint: SeedDecisionPoint,
    val teachingPearl: String,
    val sourceCode: String,
    val relatedLessonIds: List<String> = emptyList()
)

@Serializable
data class SeedSource(
    val code: String,
    val fullCitation: String,
    val url: String? = null,
    val reliabilityTier: Int,
    val licenseStatus: String,
    val reuseCleared: Boolean,
    val reuseConstraints: String
)
