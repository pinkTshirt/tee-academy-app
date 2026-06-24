package com.teeacademy.app.data.repository

import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.data.local.entity.*
import com.teeacademy.app.domain.model.*
import com.teeacademy.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> =
        bookmarkDao.getAllBookmarks().map { list -> list.map { it.toDomain() } }

    override fun isBookmarked(type: BookmarkEntityType, id: String): Flow<Boolean> =
        bookmarkDao.getBookmark(type.name.lowercase(), id).map { it != null }

    override suspend fun toggleBookmark(type: BookmarkEntityType, id: String) {
        val existing = bookmarkDao.getBookmark(type.name.lowercase(), id).first()
        if (existing != null) {
            bookmarkDao.delete(existing)
        } else {
            bookmarkDao.insert(
                BookmarkEntity(
                    entityType = type.name.lowercase(),
                    entityId = id,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotesForLesson(lessonId: String): Flow<List<Note>> =
        noteDao.getNotesForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveNote(note: Note): Long =
        noteDao.upsert(
            NoteEntity(
                id = note.id,
                lessonId = note.lessonId,
                highlightId = note.highlightId,
                text = note.text,
                createdAt = note.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        )

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(
            NoteEntity(note.id, note.lessonId, note.highlightId, note.text, note.createdAt, note.updatedAt)
        )
    }
}

class LessonProgressRepositoryImpl @Inject constructor(
    private val lessonProgressDao: LessonProgressDao
) : LessonProgressRepository {

    override fun getProgress(lessonId: String): Flow<Boolean> =
        lessonProgressDao.getProgress(lessonId).map { it?.isCompleted == true }

    override suspend fun markOpened(lessonId: String, scrollPosition: Int) {
        lessonProgressDao.upsert(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = lessonProgressDao.getProgress(lessonId).first()?.isCompleted == true,
                lastOpenedAt = System.currentTimeMillis(),
                scrollPosition = scrollPosition
            )
        )
    }

    override suspend fun markCompleted(lessonId: String, completed: Boolean) {
        val current = lessonProgressDao.getProgress(lessonId).first()
        lessonProgressDao.upsert(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = completed,
                lastOpenedAt = current?.lastOpenedAt ?: System.currentTimeMillis(),
                scrollPosition = current?.scrollPosition ?: 0
            )
        )
    }

    override suspend fun getMostRecentLessonId(): String? =
        lessonProgressDao.getMostRecentlyOpened().first()?.lessonId

    override suspend fun getCompletionPercentForModule(moduleId: String, totalLessons: Int): Int {
        if (totalLessons == 0) return 0
        val completed = lessonProgressDao.getCompletedCountForModule(moduleId)
        return ((completed.toFloat() / totalLessons) * 100).toInt()
    }
}

/**
 * Composite search across lessons, glossary terms, quiz items, and cases.
 * v1 strategy per Phase 4 Section 10: Room LIKE-based, ranked client-side.
 * Migrate to Room FTS4 if corpus exceeds ~500 entries (documented upgrade path).
 */
class SearchRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao,
    private val glossaryDao: GlossaryDao,
    private val caseDao: CaseDao
) : SearchRepository {

    override fun search(query: String): Flow<List<SearchResult>> {
        if (query.isBlank()) return combine(
            lessonDao.searchLessons(""), glossaryDao.search("")
        ) { _, _ -> emptyList() }

        return combine(
            lessonDao.searchLessons(query),
            glossaryDao.search(query),
            caseDao.getAllCases()
        ) { lessons, terms, cases ->
            val lessonResults = lessons.map {
                SearchResult(
                    entityType = "lesson",
                    entityId = it.id,
                    title = it.title,
                    breadcrumb = it.chapterId,
                    label = if (it.label == "MK") LessonLabel.MUST_KNOW else LessonLabel.ADVANCED
                )
            }
            val termResults = terms.map {
                SearchResult("glossary", it.id, it.term, "Glossary", null)
            }
            val caseResults = cases.filter {
                it.title.contains(query, ignoreCase = true) || it.presentation.contains(query, ignoreCase = true)
            }.map {
                SearchResult("case", it.id, it.title, "Case Library", null)
            }
            lessonResults + termResults + caseResults
        }
    }
}
