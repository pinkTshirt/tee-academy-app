package com.teeacademy.app.domain.repository

import com.teeacademy.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ModuleRepository {
    fun getAllModules(): Flow<List<Module>>
    fun getModuleById(id: String): Flow<Module?>
    fun getChaptersForModule(moduleId: String): Flow<List<Chapter>>
}

interface LessonRepository {
    fun getLessonsForChapter(chapterId: String): Flow<List<Lesson>>
    fun getLessonById(id: String): Flow<Lesson?>
    fun search(query: String): Flow<List<Lesson>>
}

interface FigureRepository {
    fun getFiguresForLesson(lessonId: String): Flow<List<Figure>>
    fun getFiguresForCase(caseId: String): Flow<List<Figure>>
    fun getFigureById(id: String): Flow<Figure?>
}

interface VideoRepository {
    fun getVideosForLesson(lessonId: String): Flow<List<Video>>
    fun getVideoById(id: String): Flow<Video?>
}

interface GlossaryRepository {
    fun getAllTerms(): Flow<List<GlossaryTerm>>
    fun search(query: String): Flow<List<GlossaryTerm>>
}

interface QuizRepository {
    fun getQuizItemsForLesson(lessonId: String): Flow<List<QuizItem>>
    suspend fun getRandomQuizItems(count: Int): List<QuizItem>
    suspend fun getRandomQuizItemsForModule(moduleId: String, count: Int): List<QuizItem>
    suspend fun recordAttempt(attempt: QuizAttempt)
    fun getAttemptsForItem(quizItemId: String): Flow<List<QuizAttempt>>
}

interface CaseRepository {
    fun getAllCases(): Flow<List<Case>>
    fun getCaseById(id: String): Flow<Case?>
}

interface SourceRepository {
    fun getAllSources(): Flow<List<Source>>
    suspend fun getSourceByCode(code: String): Source?
}

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun isBookmarked(type: BookmarkEntityType, id: String): Flow<Boolean>
    suspend fun toggleBookmark(type: BookmarkEntityType, id: String)
}

interface NoteRepository {
    fun getNotesForLesson(lessonId: String): Flow<List<Note>>
    suspend fun saveNote(note: Note): Long
    suspend fun deleteNote(note: Note)
}

interface LessonProgressRepository {
    fun getProgress(lessonId: String): Flow<Boolean>
    suspend fun markOpened(lessonId: String, scrollPosition: Int = 0)
    suspend fun markCompleted(lessonId: String, completed: Boolean)
    suspend fun getMostRecentLessonId(): String?
    suspend fun getCompletionPercentForModule(moduleId: String, totalLessons: Int): Int
}

interface SearchRepository {
    fun search(query: String): Flow<List<SearchResult>>
}
