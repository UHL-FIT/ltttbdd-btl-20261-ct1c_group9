package com.example.studysync.data.repository

import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import com.example.studysync.data.local.StudyDao
import kotlinx.coroutines.flow.Flow

class OfflineStudyRepository(private val studyDao: StudyDao) : StudyRepository {
    override fun getAllSessionsStream(): Flow<List<ClassSession>> = studyDao.getAllSessions()

    override suspend fun insertSession(session: ClassSession) = studyDao.insertSession(session)

    override suspend fun deleteSession(session: ClassSession) = studyDao.deleteSession(session)

    override fun getAllExamsStream(): Flow<List<ExamReminder>> = studyDao.getAllExams()

    override suspend fun insertExam(exam: ExamReminder) = studyDao.insertExam(exam)

    override suspend fun deleteExam(exam: ExamReminder) = studyDao.deleteExam(exam)
}
