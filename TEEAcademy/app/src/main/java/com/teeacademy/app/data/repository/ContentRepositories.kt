package com.teeacademy.app.data.repository

import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.domain.model.*
import com.teeacademy.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ModuleRepositoryImpl @Inject constructor(
    private val moduleDao: ModuleDao,
    private val chapterDao: ChapterDao,
    private val lessonProgressDao: LessonProgressDao
) : ModuleRepository {

    override fun getAllModules(): Flow<List<Module>> =
        moduleDao.getAllModules().map { list -> list.map { it.toDomain() } }
        // Completion % is computed lazily per-module via getCompletionPercentForModule
        // (LessonProgressRepository) rather than joined here, to keep this query cheap;
        // ModuleViewModel combines the two flows for the home/module screens.

    override fun getModuleById(id: String): Flow<Module?> =
        moduleDao.getModuleById(id).map { it?.toDomain() }

    override fun getChaptersForModule(moduleId: String): Flow<List<Chapter>> =
        chapterDao.getChaptersForModule(moduleId).map { list -> list.map { it.toDomain() } }
}

class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao,
    private val lessonProgressDao: LessonProgressDao
) : LessonRepository {

    override fun getLessonsForChapter(chapterId: String): Flow<List<Lesson>> =
        lessonDao.getLessonsForChapter(chapterId).map { list -> list.map { it.toDomain() } }

    override fun getLessonById(id: String): Flow<Lesson?> =
        lessonDao.getLessonById(id).combine(lessonProgressDao.getProgress(id)) { entity, progress ->
            entity?.toDomain(isCompleted = progress?.isCompleted == true)
        }

    override fun search(query: String): Flow<List<Lesson>> =
        lessonDao.searchLessons(query).map { list -> list.map { it.toDomain() } }
}

class FigureRepositoryImpl @Inject constructor(
    private val figureDao: FigureDao
) : FigureRepository {
    override fun getFiguresForLesson(lessonId: String): Flow<List<Figure>> =
        figureDao.getFiguresForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override fun getFiguresForCase(caseId: String): Flow<List<Figure>> =
        figureDao.getFiguresForCase(caseId).map { list -> list.map { it.toDomain() } }

    override fun getFigureById(id: String): Flow<Figure?> =
        figureDao.getFigureById(id).map { it?.toDomain() }
}

class VideoRepositoryImpl @Inject constructor(
    private val videoDao: VideoDao
) : VideoRepository {
    override fun getVideosForLesson(lessonId: String): Flow<List<Video>> =
        videoDao.getVideosForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override fun getVideoById(id: String): Flow<Video?> =
        videoDao.getVideoById(id).map { it?.toDomain() }
}
