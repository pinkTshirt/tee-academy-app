package com.teeacademy.app.data.seed

import android.content.Context
import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.data.local.entity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
* Loads the *.json files under assets/seed into Room on first launch or when the bundled
 * seed version (CURRENT_SEED_VERSION) is newer than what's stored in
 * app_meta. Content tables are cleared and replaced; user-generated
 * tables (bookmarks/notes/highlights/quiz_attempts/lesson_progress) are
 * NEVER touched here, per Phase 5 ingestion plan Section "Content
 * Seeding Strategy".
 */
@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moduleDao: ModuleDao,
    private val chapterDao: ChapterDao,
    private val lessonDao: LessonDao,
    private val figureDao: FigureDao,
    private val videoDao: VideoDao,
    private val glossaryDao: GlossaryDao,
    private val quizItemDao: QuizItemDao,
    private val caseDao: CaseDao,
    private val sourceDao: SourceDao,
    private val appMetaDao: AppMetaDao
) {
    companion object {
        const val CURRENT_SEED_VERSION = "1"
        const val SEED_VERSION_KEY = "seed_version"
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun seedIfNeeded() {
        val storedVersion = appMetaDao.getValue(SEED_VERSION_KEY)
        if (storedVersion == CURRENT_SEED_VERSION) return // already up to date

        val modules = readAsset<List<SeedModule>>("seed/modules.json")
        val chapters = readAsset<List<SeedChapter>>("seed/chapters.json")
        val lessons = readAsset<List<SeedLesson>>("seed/lessons.json")
        val figures = readAsset<List<SeedFigure>>("seed/figures.json")
        val videos = readAsset<List<SeedVideo>>("seed/videos.json")
        val glossary = readAsset<List<SeedGlossaryTerm>>("seed/glossary.json")
        val quizItems = readAsset<List<SeedQuizItem>>("seed/quiz_items.json")
        val cases = readAsset<List<SeedCase>>("seed/cases.json")
        val sources = readAsset<List<SeedSource>>("seed/sources.json")

        // Clear + insert content tables only.
        moduleDao.clearAll(); moduleDao.insertAll(modules.map { it.toEntity() })
        chapterDao.clearAll(); chapterDao.insertAll(chapters.map { it.toEntity() })
        lessonDao.clearAll(); lessonDao.insertAll(lessons.map { it.toEntity() })
        figureDao.clearAll(); figureDao.insertAll(figures.map { it.toEntity() })
        videoDao.clearAll(); videoDao.insertAll(videos.map { it.toEntity() })
        glossaryDao.clearAll(); glossaryDao.insertAll(glossary.map { it.toEntity() })
        quizItemDao.clearAll(); quizItemDao.insertAll(quizItems.map { it.toEntity() })
        caseDao.clearAll(); caseDao.insertAll(cases.map { it.toEntity() })
        sourceDao.clearAll(); sourceDao.insertAll(sources.map { it.toEntity() })

        appMetaDao.upsert(AppMetaEntity(SEED_VERSION_KEY, CURRENT_SEED_VERSION))
    }

    private inline fun <reified T> readAsset(path: String): T {
        val text = context.assets.open(path).bufferedReader().use { it.readText() }
        return json.decodeFromString(text)
    }

    private fun SeedModule.toEntity() = ModuleEntity(id, orderIndex, title, description, iconRes)

    private fun SeedChapter.toEntity() = ChapterEntity(id, moduleId, orderIndex, title)

    private fun SeedLesson.toEntity() = LessonEntity(
        id = id,
        chapterId = chapterId,
        orderIndex = orderIndex,
        title = title,
        objective = objective,
        bodyBlocksJson = Json.encodeToString(bodyBlocks),
        label = label,
        estimatedMinutes = estimatedMinutes,
        sourceCodesCsv = sourceCodes.joinToString(","),
        relatedQuizItemIdsCsv = relatedQuizItemIds.joinToString(","),
        relatedGlossaryTermIdsCsv = relatedGlossaryTermIds.joinToString(",")
    )

    private fun SeedFigure.toEntity() = FigureEntity(
        id, lessonId, caseId, type, localAssetPath, caption, altText,
        sourceCode, licenseString, orderInLesson
    )

    private fun SeedVideo.toEntity() = VideoEntity(
        id, lessonId, caseId, localAssetPath, remoteUrl, caption, transcript,
        durationSec, sourceCode, licenseString, status
    )

    private fun SeedGlossaryTerm.toEntity() = GlossaryTermEntity(
        id, term, definition, relatedLessonIds.joinToString(",")
    )

    private fun SeedQuizItem.toEntity() = QuizItemEntity(
        id, lessonId, label, stem, Json.encodeToString(options), correctIndex, explanation, sourceCode
    )

    private fun SeedCase.toEntity() = CaseEntity(
        id = id,
        title = title,
        presentation = presentation,
        imagingFigureIdsCsv = imagingFigureIds.joinToString(","),
        imagingVideoIdsCsv = imagingVideoIds.joinToString(","),
        interpretation = interpretation,
        decisionQuestion = decisionPoint.question,
        decisionOptionsJson = Json.encodeToString(
            decisionPoint.options.zip(decisionPoint.rationaleByOption)
                .map { SeedDecisionOption(it.first, it.second) }
        ),
        teachingPearl = teachingPearl,
        sourceCode = sourceCode,
        relatedLessonIdsCsv = relatedLessonIds.joinToString(",")
    )

    private fun SeedSource.toEntity() = SourceEntity(
        code, fullCitation, url, reliabilityTier, licenseStatus, reuseCleared, reuseConstraints
    )
}
