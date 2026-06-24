package com.teeacademy.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-generated content tables. The SeedLoader (data/seed/SeedLoader.kt)
 * never touches these tables on re-seed — only the content tables in
 * ContentEntities.kt are replaced when the bundled seed version bumps.
 */

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String, // "lesson"|"figure"|"video"|"case"|"quiz_item"
    val entityId: String,
    val createdAt: Long
)

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val startOffset: Int,
    val endOffset: Int,
    val createdAt: Long
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val highlightId: Long?,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizItemId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean,
    val lastOpenedAt: Long,
    val scrollPosition: Int = 0
)

/** Tracks bundled seed content version, drives re-seed logic in SeedLoader. */
@Entity(tableName = "app_meta")
data class AppMetaEntity(
    @PrimaryKey val key: String,
    val value: String
)
