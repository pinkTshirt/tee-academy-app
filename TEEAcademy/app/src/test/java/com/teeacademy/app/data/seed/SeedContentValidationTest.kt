package com.teeacademy.app.data.seed

import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * Content-integrity checks per Phase 4 Section 13 ("Content integrity" row
 * of the testing strategy table). Run on every content update, not just
 * code changes — catches the highest-risk error category in this app:
 * broken citation cross-references and malformed quiz answer keys.
 *
 * NOTE: this test reads seed JSON directly from the source tree rather
 * than via Android assets (no Android runtime in a pure JVM unit test),
 * so the path below assumes the standard Gradle module layout.
 */
class SeedContentValidationTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val seedDir = File("src/main/assets/seed")

    private fun <T> load(file: String, deserialize: (String) -> T): T =
        deserialize(File(seedDir, file).readText())

    @Test
    fun `every lesson sourceCode exists in sources registry`() {
        val sources = load("sources.json") { json.decodeFromString<List<SeedSource>>(it) }
        val sourceCodes = sources.map { it.code }.toSet()
        val lessons = load("lessons.json") { json.decodeFromString<List<SeedLesson>>(it) }

        lessons.forEach { lesson ->
            lesson.sourceCodes.forEach { code ->
                assertTrue(
                    "Lesson ${lesson.id} references unknown source code: $code",
                    code in sourceCodes
                )
            }
        }
    }

    @Test
    fun `every quiz item sourceCode exists in sources registry`() {
        val sources = load("sources.json") { json.decodeFromString<List<SeedSource>>(it) }
        val sourceCodes = sources.map { it.code }.toSet()
        val quizItems = load("quiz_items.json") { json.decodeFromString<List<SeedQuizItem>>(it) }

        quizItems.forEach { item ->
            assertTrue(
                "Quiz item ${item.id} references unknown source code: ${item.sourceCode}",
                item.sourceCode in sourceCodes
            )
        }
    }

    @Test
    fun `every quiz item correctIndex is within options bounds`() {
        val quizItems = load("quiz_items.json") { json.decodeFromString<List<SeedQuizItem>>(it) }
        quizItems.forEach { item ->
            assertTrue(
                "Quiz item ${item.id} has correctIndex ${item.correctIndex} out of bounds for ${item.options.size} options",
                item.correctIndex in item.options.indices
            )
        }
    }

    @Test
    fun `every figure has non-empty altText and licenseString`() {
        val figures = load("figures.json") { json.decodeFromString<List<SeedFigure>>(it) }
        figures.forEach { figure ->
            assertTrue("Figure ${figure.id} is missing altText", figure.altText.isNotBlank())
            assertTrue("Figure ${figure.id} is missing licenseString", figure.licenseString.isNotBlank())
        }
    }

    @Test
    fun `every figureRef in a lesson body resolves to a real figure`() {
        val lessons = load("lessons.json") { json.decodeFromString<List<SeedLesson>>(it) }
        val figures = load("figures.json") { json.decodeFromString<List<SeedFigure>>(it) }
        val figureIds = figures.map { it.id }.toSet()

        lessons.forEach { lesson ->
            lesson.bodyBlocks
                .filter { it.type == "figureRef" }
                .forEach { block ->
                    val figureId = block.figureId
                    assertTrue(
                        "Lesson ${lesson.id} references missing figureId: $figureId",
                        figureId != null && figureId in figureIds
                    )
                }
        }
    }

    @Test
    fun `every chapter belongs to a real module`() {
        val modules = load("modules.json") { json.decodeFromString<List<SeedModule>>(it) }
        val moduleIds = modules.map { it.id }.toSet()
        val chapters = load("chapters.json") { json.decodeFromString<List<SeedChapter>>(it) }

        chapters.forEach { chapter ->
            assertTrue(
                "Chapter ${chapter.id} references unknown moduleId: ${chapter.moduleId}",
                chapter.moduleId in moduleIds
            )
        }
    }

    @Test
    fun `every lesson belongs to a real chapter`() {
        val chapters = load("chapters.json") { json.decodeFromString<List<SeedChapter>>(it) }
        val chapterIds = chapters.map { it.id }.toSet()
        val lessons = load("lessons.json") { json.decodeFromString<List<SeedLesson>>(it) }

        lessons.forEach { lesson ->
            assertTrue(
                "Lesson ${lesson.id} references unknown chapterId: ${lesson.chapterId}",
                lesson.chapterId in chapterIds
            )
        }
    }

    /**
     * Catches content that exists structurally (a Module or Chapter row)
     * but has nothing underneath it — this renders as a dead-end card/
     * accordion section in the UI rather than a build failure, so it's
     * easy to ship by accident. Flagged explicitly in Phase 8 QA Section 2
     * ("no orphaned content") as a gap in the original v1 test suite.
     */
    @Test
    fun `every module has at least one chapter`() {
        val modules = load("modules.json") { json.decodeFromString<List<SeedModule>>(it) }
        val chapters = load("chapters.json") { json.decodeFromString<List<SeedChapter>>(it) }
        val modulesWithChapters = chapters.map { it.moduleId }.toSet()

        val orphanedModules = modules.filter { it.id !in modulesWithChapters }
        assertTrue(
            "Modules with no chapters (dead-end cards in the UI): " +
                orphanedModules.joinToString { it.id },
            orphanedModules.isEmpty()
        )
    }

    @Test
    fun `every chapter has at least one lesson`() {
        val chapters = load("chapters.json") { json.decodeFromString<List<SeedChapter>>(it) }
        val lessons = load("lessons.json") { json.decodeFromString<List<SeedLesson>>(it) }
        val chaptersWithLessons = lessons.map { it.chapterId }.toSet()

        val orphanedChapters = chapters.filter { it.id !in chaptersWithLessons }
        assertTrue(
            "Chapters with no lessons (dead-end accordion sections in the UI): " +
                orphanedChapters.joinToString { it.id },
            orphanedChapters.isEmpty()
        )
    }

    @Test
    fun `every case sourceCode exists in sources registry`() {
        val sources = load("sources.json") { json.decodeFromString<List<SeedSource>>(it) }
        val sourceCodes = sources.map { it.code }.toSet()
        val cases = load("cases.json") { json.decodeFromString<List<SeedCase>>(it) }

        cases.forEach { case ->
            assertTrue(
                "Case ${case.id} references unknown source code: ${case.sourceCode}",
                case.sourceCode in sourceCodes
            )
        }
    }

    @Test
    fun `every case decision point has matching options and rationale counts`() {
        val cases = load("cases.json") { json.decodeFromString<List<SeedCase>>(it) }
        cases.forEach { case ->
            assertTrue(
                "Case ${case.id} decision point has ${case.decisionPoint.options.size} options " +
                    "but ${case.decisionPoint.rationaleByOption.size} rationales — these must match 1:1",
                case.decisionPoint.options.size == case.decisionPoint.rationaleByOption.size
            )
        }
    }

    @Test
    fun `every case relatedLessonId resolves to a real lesson`() {
        val lessons = load("lessons.json") { json.decodeFromString<List<SeedLesson>>(it) }
        val lessonIds = lessons.map { it.id }.toSet()
        val cases = load("cases.json") { json.decodeFromString<List<SeedCase>>(it) }

        cases.forEach { case ->
            case.relatedLessonIds.forEach { lessonId ->
                assertTrue(
                    "Case ${case.id} references unknown lessonId: $lessonId",
                    lessonId in lessonIds
                )
            }
        }
    }
}
