package com.example.studysync.data.local

import androidx.room.*
import com.example.studysync.data.ClassSession
import com.example.studysync.data.ExamReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM class_sessions")
    fun getAllSessions(): Flow<List<ClassSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ClassSession)

    @Delete
    suspend fun deleteSession(session: ClassSession)

    @Query("SELECT * FROM exam_reminders ORDER BY daysUntil ASC")
    fun getAllExams(): Flow<List<ExamReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamReminder)

    @Delete
    suspend fun deleteExam(exam: ExamReminder)
}
