package com.teeacademy.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teeacademy.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules ORDER BY orderIndex")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE id = :id")
    fun getModuleById(id: String): Flow<ModuleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modules: List<ModuleEntity>)

    @Query("DELETE FROM modules")
    suspend fun clearAll()
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE moduleId = :moduleId ORDER BY orderIndex")
    fun getChaptersForModule(moduleId: String): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapters: List<ChapterEntity>)

    @Query("DELETE FROM chapters")
    suspend fun clearAll()
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE chapterId = :chapterId ORDER BY orderIndex")
    fun getLessonsForChapter(chapterId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    fun getLessonById(id: String): Flow<LessonEntity?>

    @Query(
        "SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' " +
        "OR bodyBlocksJson LIKE '%' || :query || '%' OR objective LIKE '%' || :query || '%'"
    )
    fun searchLessons(query: String): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Query("DELETE FROM lessons")
    suspend fun clearAll()
}

@Dao
interface FigureDao {
    @Query("SELECT * FROM figures WHERE lessonId = :lessonId ORDER BY orderInLesson")
    fun getFiguresForLesson(lessonId: String): Flow<List<FigureEntity>>

    @Query("SELECT * FROM figures WHERE caseId = :caseId ORDER BY orderInLesson")
    fun getFiguresForCase(caseId: String): Flow<List<FigureEntity>>

    @Query("SELECT * FROM figures WHERE id = :id")
    fun getFigureById(id: String): Flow<FigureEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(figures: List<FigureEntity>)

    @Query("DELETE FROM figures")
    suspend fun clearAll()
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE lessonId = :lessonId")
    fun getVideosForLesson(lessonId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id")
    fun getVideoById(id: String): Flow<VideoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoEntity>)

    @Query("DELETE FROM videos")
    suspend fun clearAll()
}

@Dao
interface GlossaryDao {
    @Query("SELECT * FROM glossary_terms ORDER BY term")
    fun getAllTerms(): Flow<List<GlossaryTermEntity>>

    @Query("SELECT * FROM glossary_terms WHERE term LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<GlossaryTermEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<GlossaryTermEntity>)

    @Query("DELETE FROM glossary_terms")
    suspend fun clearAll()
}

@Dao
interface QuizItemDao {
    @Query("SELECT * FROM quiz_items WHERE lessonId = :lessonId")
    fun getQuizItemsForLesson(lessonId: String): Flow<List<QuizItemEntity>>

    @Query("SELECT * FROM quiz_items WHERE id IN (:ids)")
    suspend fun getQuizItemsByIds(ids: List<String>): List<QuizItemEntity>

    @Query("SELECT * FROM quiz_items ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuizItems(count: Int): List<QuizItemEntity>

    @Query(
        "SELECT q.* FROM quiz_items q INNER JOIN lessons l ON q.lessonId = l.id " +
        "WHERE l.chapterId IN (SELECT id FROM chapters WHERE moduleId = :moduleId) " +
        "ORDER BY RANDOM() LIMIT :count"
    )
    suspend fun getRandomQuizItemsForModule(moduleId: String, count: Int): List<QuizItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QuizItemEntity>)

    @Query("DELETE FROM quiz_items")
    suspend fun clearAll()
}

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY id")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    fun getCaseById(id: String): Flow<CaseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CaseEntity>)

    @Query("DELETE FROM cases")
    suspend fun clearAll()
}

@Dao
interface SourceDao {
    @Query("SELECT * FROM sources ORDER BY reliabilityTier, code")
    fun getAllSources(): Flow<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE code = :code")
    suspend fun getSourceByCode(code: String): SourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<SourceEntity>)

    @Query("DELETE FROM sources")
    suspend fun clearAll()
}
