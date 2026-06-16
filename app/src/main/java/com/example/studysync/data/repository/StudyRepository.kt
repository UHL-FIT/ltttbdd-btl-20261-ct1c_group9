package com.example.studysync.data.repository

import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun getAllSessionsStream(): Flow<List<ClassSession>>
    suspend fun insertSession(session: ClassSession)
    suspend fun deleteSession(session: ClassSession)

    fun getAllExamsStream(): Flow<List<ExamReminder>>
    suspend fun insertExam(exam: ExamReminder)
    suspend fun deleteExam(exam: ExamReminder)
}
