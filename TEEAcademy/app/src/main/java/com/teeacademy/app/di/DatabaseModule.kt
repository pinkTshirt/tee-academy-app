package com.teeacademy.app.di

import android.content.Context
import androidx.room.Room
import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // No destructive fallback in production; migrations should be added
            // explicitly as the schema evolves. Acceptable for v1 pre-release only:
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideModuleDao(db: AppDatabase): ModuleDao = db.moduleDao()
    @Provides fun provideChapterDao(db: AppDatabase): ChapterDao = db.chapterDao()
    @Provides fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()
    @Provides fun provideFigureDao(db: AppDatabase): FigureDao = db.figureDao()
    @Provides fun provideVideoDao(db: AppDatabase): VideoDao = db.videoDao()
    @Provides fun provideGlossaryDao(db: AppDatabase): GlossaryDao = db.glossaryDao()
    @Provides fun provideQuizItemDao(db: AppDatabase): QuizItemDao = db.quizItemDao()
    @Provides fun provideCaseDao(db: AppDatabase): CaseDao = db.caseDao()
    @Provides fun provideSourceDao(db: AppDatabase): SourceDao = db.sourceDao()
    @Provides fun provideBookmarkDao(db: AppDatabase): BookmarkDao = db.bookmarkDao()
    @Provides fun provideHighlightDao(db: AppDatabase): HighlightDao = db.highlightDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides fun provideQuizAttemptDao(db: AppDatabase): QuizAttemptDao = db.quizAttemptDao()
    @Provides fun provideLessonProgressDao(db: AppDatabase): LessonProgressDao = db.lessonProgressDao()
    @Provides fun provideAppMetaDao(db: AppDatabase): AppMetaDao = db.appMetaDao()
}
