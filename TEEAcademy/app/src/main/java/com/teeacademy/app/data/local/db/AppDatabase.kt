package com.teeacademy.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.data.local.entity.*

@Database(
    entities = [
        ModuleEntity::class,
        ChapterEntity::class,
        LessonEntity::class,
        FigureEntity::class,
        VideoEntity::class,
        GlossaryTermEntity::class,
        QuizItemEntity::class,
        CaseEntity::class,
        SourceEntity::class,
        BookmarkEntity::class,
        HighlightEntity::class,
        NoteEntity::class,
        QuizAttemptEntity::class,
        LessonProgressEntity::class,
        AppMetaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao
    abstract fun chapterDao(): ChapterDao
    abstract fun lessonDao(): LessonDao
    abstract fun figureDao(): FigureDao
    abstract fun videoDao(): VideoDao
    abstract fun glossaryDao(): GlossaryDao
    abstract fun quizItemDao(): QuizItemDao
    abstract fun caseDao(): CaseDao
    abstract fun sourceDao(): SourceDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun highlightDao(): HighlightDao
    abstract fun noteDao(): NoteDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun appMetaDao(): AppMetaDao

    companion object {
        const val DATABASE_NAME = "tee_academy.db"
    }
}
