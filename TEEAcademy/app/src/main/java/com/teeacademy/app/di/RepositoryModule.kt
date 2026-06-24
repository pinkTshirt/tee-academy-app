package com.teeacademy.app.di

import com.teeacademy.app.data.repository.*
import com.teeacademy.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindModuleRepository(impl: ModuleRepositoryImpl): ModuleRepository

    @Binds @Singleton
    abstract fun bindLessonRepository(impl: LessonRepositoryImpl): LessonRepository

    @Binds @Singleton
    abstract fun bindFigureRepository(impl: FigureRepositoryImpl): FigureRepository

    @Binds @Singleton
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

    @Binds @Singleton
    abstract fun bindGlossaryRepository(impl: GlossaryRepositoryImpl): GlossaryRepository

    @Binds @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds @Singleton
    abstract fun bindCaseRepository(impl: CaseRepositoryImpl): CaseRepository

    @Binds @Singleton
    abstract fun bindSourceRepository(impl: SourceRepositoryImpl): SourceRepository

    @Binds @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds @Singleton
    abstract fun bindLessonProgressRepository(impl: LessonProgressRepositoryImpl): LessonProgressRepository

    @Binds @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}
