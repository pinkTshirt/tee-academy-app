package com.teeacademy.app.data.repository

import com.teeacademy.app.data.local.dao.*
import com.teeacademy.app.data.local.entity.QuizAttemptEntity
import com.teeacademy.app.domain.model.*
import com.teeacademy.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GlossaryRepositoryImpl @Inject constructor(
    private val glossaryDao: GlossaryDao
) : GlossaryRepository {
    override fun getAllTerms(): Flow<List<GlossaryTerm>> =
        glossaryDao.getAllTerms().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<GlossaryTerm>> =
        glossaryDao.search(query).map { list -> list.map { it.toDomain() } }
}

class QuizRepositoryImpl @Inject constructor(
    private val quizItemDao: QuizItemDao,
    private val quizAttemptDao: QuizAttemptDao
) : QuizRepository {
    override fun getQuizItemsForLesson(lessonId: String): Flow<List<QuizItem>> =
        quizItemDao.getQuizItemsForLesson(lessonId).map { list -> list.map { it.toDomain() } }

    override suspend fun getRandomQuizItems(count: Int): List<QuizItem> =
        quizItemDao.getRandomQuizItems(count).map { it.toDomain() }

    override suspend fun getRandomQuizItemsForModule(moduleId: String, count: Int): List<QuizItem> =
        quizItemDao.getRandomQuizItemsForModule(moduleId, count).map { it.toDomain() }

    override suspend fun recordAttempt(attempt: QuizAttempt) {
        quizAttemptDao.insert(
            QuizAttemptEntity(
                quizItemId = attempt.quizItemId,
                selectedIndex = attempt.selectedIndex,
                isCorrect = attempt.isCorrect,
                attemptedAt = attempt.attemptedAt
            )
        )
    }

    override fun getAttemptsForItem(quizItemId: String): Flow<List<QuizAttempt>> =
        quizAttemptDao.getAttemptsForItem(quizItemId).map { list -> list.map { it.toDomain() } }
}

class CaseRepositoryImpl @Inject constructor(
    private val caseDao: CaseDao
) : CaseRepository {
    override fun getAllCases(): Flow<List<Case>> =
        caseDao.getAllCases().map { list -> list.map { it.toDomain() } }

    override fun getCaseById(id: String): Flow<Case?> =
        caseDao.getCaseById(id).map { it?.toDomain() }
}

class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao
) : SourceRepository {
    override fun getAllSources(): Flow<List<Source>> =
        sourceDao.getAllSources().map { list -> list.map { it.toDomain() } }

    override suspend fun getSourceByCode(code: String): Source? =
        sourceDao.getSourceByCode(code)?.toDomain()
}
