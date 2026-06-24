package com.teeacademy.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.teeacademy.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE entityType = :type AND entityId = :id LIMIT 1")
    fun getBookmark(type: String, id: String): Flow<BookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)
}

@Dao
interface HighlightDao {
    @Query("SELECT * FROM highlights WHERE lessonId = :lessonId")
    fun getHighlightsForLesson(lessonId: String): Flow<List<HighlightEntity>>

    @Insert
    suspend fun insert(highlight: HighlightEntity): Long

    @Delete
    suspend fun delete(highlight: HighlightEntity)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE lessonId = :lessonId ORDER BY updatedAt DESC")
    fun getNotesForLesson(lessonId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}

@Dao
interface QuizAttemptDao {
    @Insert
    suspend fun insert(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempts WHERE quizItemId = :quizItemId ORDER BY attemptedAt DESC")
    fun getAttemptsForItem(quizItemId: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT COUNT(*) FROM quiz_attempts WHERE isCorrect = 1")
    suspend fun getCorrectCount(): Int

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    suspend fun getTotalCount(): Int
}

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    fun getProgress(lessonId: String): Flow<LessonProgressEntity?>

    @Query("SELECT * FROM lesson_progress ORDER BY lastOpenedAt DESC LIMIT 1")
    fun getMostRecentlyOpened(): Flow<LessonProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: LessonProgressEntity)

    @Query(
        "SELECT COUNT(*) FROM lesson_progress p INNER JOIN lessons l ON p.lessonId = l.id " +
        "INNER JOIN chapters c ON l.chapterId = c.id " +
        "WHERE c.moduleId = :moduleId AND p.isCompleted = 1"
    )
    suspend fun getCompletedCountForModule(moduleId: String): Int
}

@Dao
interface AppMetaDao {
    @Query("SELECT value FROM app_meta WHERE key = :key")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: AppMetaEntity)
}
